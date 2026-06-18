package synera.centralis.api.chat.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import synera.centralis.api.chat.domain.model.aggregates.Group;
import synera.centralis.api.chat.domain.model.commands.*;
import synera.centralis.api.chat.domain.model.queries.*;
import synera.centralis.api.chat.domain.model.valueobjects.GroupVisibility;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;
import synera.centralis.api.chat.domain.services.GroupCommandService;
import synera.centralis.api.chat.domain.services.GroupQueryService;
import synera.centralis.api.chat.interfaces.rest.resources.*;
import synera.centralis.api.chat.interfaces.rest.transform.*;

import jakarta.validation.Valid;
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;
import synera.centralis.api.shared.domain.exceptions.UnauthorizedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * GroupController handles HTTP requests for Group operations.
 * Provides full CRUD operations for group management including member operations.
 */
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
@RestController
@RequestMapping(value = "/api/v1/groups", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Groups", description = "Group Management Endpoints")
public class GroupController {

    private final GroupCommandService groupCommandService;
    private final GroupQueryService groupQueryService;
    private final IamContextFacade iamContextFacade;

    public GroupController(GroupCommandService groupCommandService, GroupQueryService groupQueryService, synera.centralis.api.iam.interfaces.acl.IamContextFacade iamContextFacade) {
        this.groupCommandService = groupCommandService;
        this.groupQueryService = groupQueryService;
        this.iamContextFacade = iamContextFacade;
    }

    private CompanyId getCurrentCompanyId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        String username = authentication.getName();
        java.util.UUID companyId = iamContextFacade.fetchCompanyIdByUsername(username);
        if (companyId == null) {
            throw new UnauthorizedException("User not associated with any company");
        }
        return new CompanyId(companyId);
    }

    /**
     * Creates a new group.
     */
    @PostMapping
    @Operation(summary = "Create a new group", description = "Creates a new chat group with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Group created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GroupResource> createGroup(@Valid @RequestBody CreateGroupResource resource) {
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        var createGroupCommand = CreateGroupCommandFromResourceAssembler.toCommandFromResource(resource, companyId);
        var group = groupCommandService.handle(createGroupCommand);
        
        var groupResource = GroupResourceFromEntityAssembler.toResourceFromEntity(group);
        return new ResponseEntity<>(groupResource, HttpStatus.CREATED);
    }

    /**
     * Retrieves a group by its ID.
     */
    @GetMapping("/{groupId}")
    @Operation(summary = "Get group by ID", description = "Retrieves a specific group by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GroupResource> getGroupById(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId) {
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        var getGroupByIdQuery = new GetGroupByIdQuery(groupId, companyId);
        var group = groupQueryService.handle(getGroupByIdQuery);
        
        // Las conversaciones directas no se exponen por la API de grupos:
        // tienen su propio recurso en /api/v1/conversations.
        if (group.isEmpty() || group.get().isDirect()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        var groupResource = GroupResourceFromEntityAssembler.toResourceFromEntity(group.get());
        return new ResponseEntity<>(groupResource, HttpStatus.OK);
    }

    /**
     * Retrieves all groups for a specific user.
     */
    @GetMapping
    @Operation(summary = "Get groups by user", description = "Retrieves all groups that a user belongs to")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Groups retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<GroupResource>> getGroupsByUserId(
            @Parameter(description = "User ID", required = true) @RequestParam UUID userId) {
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        var getGroupsByMemberIdQuery = new GetGroupsByMemberIdQuery(new UserId(userId), companyId);
        List<Group> groups = groupQueryService.handle(getGroupsByMemberIdQuery);

        var groupResources = groups.stream()
                .filter(group -> !group.isDirect())
                .map(GroupResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        return new ResponseEntity<>(groupResources, HttpStatus.OK);
    }

    /**
     * Retrieves groups by visibility.
     */
    @GetMapping("/visibility/{visibility}")
    @Operation(summary = "Get groups by visibility", description = "Retrieves groups based on their visibility setting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Groups retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid visibility parameter"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<GroupResource>> getGroupsByVisibility(
            @Parameter(description = "Group visibility (PUBLIC/PRIVATE)", required = true) @PathVariable GroupVisibility visibility) {
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        var getAllGroupsQuery = new GetAllGroupsQuery(companyId);
        List<Group> allGroups = groupQueryService.handle(getAllGroupsQuery);
        List<Group> groups = allGroups.stream()
                .filter(group -> !group.isDirect())
                .filter(group -> group.getVisibility().equals(visibility))
                .toList();
        
        var groupResources = groups.stream()
                .map(GroupResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        return new ResponseEntity<>(groupResources, HttpStatus.OK);
    }

    /**
     * Updates a group's information.
     */
    @PutMapping("/{groupId}")
    @Operation(summary = "Update group", description = "Updates group information including name, description, and image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group updated successfully"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GroupResource> updateGroup(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId,
            @Valid @RequestBody UpdateGroupResource resource) {
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        var updateGroupCommand = UpdateGroupCommandFromResourceAssembler.toCommandFromResource(groupId, resource, companyId);
        var updatedGroup = groupCommandService.handle(updateGroupCommand);
        
        var groupResource = GroupResourceFromEntityAssembler.toResourceFromEntity(updatedGroup);
        return new ResponseEntity<>(groupResource, HttpStatus.OK);
    }

    /**
     * Updates a group's visibility.
     */
    @PatchMapping("/{groupId}/visibility")
    @Operation(summary = "Update group visibility", description = "Changes the visibility setting of a group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group visibility updated successfully"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GroupResource> updateGroupVisibility(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId,
            @Valid @RequestBody UpdateGroupVisibilityResource resource) {
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        var updateVisibilityCommand = UpdateGroupVisibilityCommandFromResourceAssembler.toCommandFromResource(groupId, resource, companyId);
        var updatedGroup = groupCommandService.handle(updateVisibilityCommand);
        
        var groupResource = GroupResourceFromEntityAssembler.toResourceFromEntity(updatedGroup);
        return new ResponseEntity<>(groupResource, HttpStatus.OK);
    }

    /**
     * Adds a member to a group.
     */
    @PostMapping("/{groupId}/members")
    @Operation(summary = "Add member to group", description = "Adds a new member to an existing group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member added successfully"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or member already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GroupResource> addMemberToGroup(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId,
            @Valid @RequestBody AddMemberToGroupResource resource) {
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        var addMemberCommand = AddMemberToGroupCommandFromResourceAssembler.toCommandFromResource(groupId, resource, companyId);
        var updatedGroup = groupCommandService.handle(addMemberCommand);
        
        var groupResource = GroupResourceFromEntityAssembler.toResourceFromEntity(updatedGroup);
        return new ResponseEntity<>(groupResource, HttpStatus.OK);
    }

    /**
     * Removes a member from a group.
     */
    @DeleteMapping("/{groupId}/members")
    @Operation(summary = "Remove member from group", description = "Removes a member from an existing group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member removed successfully"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or member not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GroupResource> removeMemberFromGroup(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId,
            @Valid @RequestBody RemoveMemberFromGroupResource resource) {
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        var removeMemberCommand = RemoveMemberFromGroupCommandFromResourceAssembler.toCommandFromResource(groupId, resource, companyId);
        var updatedGroup = groupCommandService.handle(removeMemberCommand);
        
        var groupResource = GroupResourceFromEntityAssembler.toResourceFromEntity(updatedGroup);
        return new ResponseEntity<>(groupResource, HttpStatus.OK);
    }

    /**
     * Deletes a group.
     */
    @DeleteMapping("/{groupId}")
    @Operation(summary = "Delete group", description = "Permanently deletes a group and all its messages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Group deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteGroup(
            @Parameter(description = "Group ID", required = true) @PathVariable UUID groupId) {
        var companyId = getCurrentCompanyId();
        if (companyId == null) throw new UnauthorizedException("Company not found");

        var deleteGroupCommand = new DeleteGroupCommand(groupId, companyId);
        groupCommandService.handle(deleteGroupCommand);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
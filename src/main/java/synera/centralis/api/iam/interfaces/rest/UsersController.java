package synera.centralis.api.iam.interfaces.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import synera.centralis.api.iam.domain.model.queries.GetAllUsersQuery;
import synera.centralis.api.iam.domain.model.queries.GetUserByIdQuery;
import synera.centralis.api.iam.domain.services.UserCommandService;
import synera.centralis.api.iam.domain.services.UserQueryService;
import synera.centralis.api.iam.interfaces.rest.resources.UpdateUserResource;
import synera.centralis.api.iam.interfaces.rest.resources.UserResource;
import synera.centralis.api.iam.interfaces.rest.transform.UpdateUserCommandFromResourceAssembler;
import synera.centralis.api.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;

import synera.centralis.api.company.interfaces.acl.CompanyContextFacade;
import synera.centralis.api.company.interfaces.rest.resources.JoinCompanyResource;

/**
 * This class is a REST controller that exposes the users resource.
 * It includes the following operations:
 * - GET /api/v1/users: returns all the users
 * - GET /api/v1/users/{userId}: returns the user with the given id
 **/
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Available User Endpoints")
public class UsersController {
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final CompanyContextFacade companyContextFacade;

    public UsersController(UserQueryService userQueryService, UserCommandService userCommandService, CompanyContextFacade companyContextFacade) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
        this.companyContextFacade = companyContextFacade;
    }

    /**
     * This method returns all the users.
     * @return a list of user resources
     * @see UserResource
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Get all the users available in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")})
    public ResponseEntity<List<UserResource>> getAllUsers() {
        var getAllUsersQuery = new GetAllUsersQuery();
        var users = userQueryService.handle(getAllUsersQuery);
        var userResources = users.stream().map(UserResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(userResources);
    }

    /**
     * This method returns the user with the given id.
     * @param userId the user id
     * @return the user resource with the given id
     * @throws RuntimeException if the user is not found
     * @see UserResource
     */
    @GetMapping(value = "/{userId}")
    @Operation(summary = "Get user by id", description = "Get the user with the given id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "User not found."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")})
    public ResponseEntity<UserResource> getUserById(@PathVariable UUID userId) {
        var getUserByIdQuery = new GetUserByIdQuery(userId);
        var user = userQueryService.handle(getUserByIdQuery);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(userResource);
    }

    /**
     * Updates a user with the given id.
     * @param userId the user id.
     * @param updateUserResource the user data to update.
     * @return the updated user resource or a bad request response if the user cannot be updated.
     */
    @Operation(summary = "Updates a user", description = "Updates a user with the given data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @PutMapping("/{userId}")
    public ResponseEntity<UserResource> updateUser(@PathVariable UUID userId, @RequestBody UpdateUserResource updateUserResource) {
        var updateUserCommand = UpdateUserCommandFromResourceAssembler.toCommandFromResource(userId, updateUserResource);
        var updatedUser = userCommandService.handle(updateUserCommand);
        if (updatedUser.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(updatedUser.get());
        return ResponseEntity.ok(userResource);
    }

    /**
     * Assigns a company to a user.
     * @param userId the user id.
     * @param assignCompanyResource the resource containing the company id.
     * @return the updated user resource.
     */
    @Operation(summary = "Assign company to user", description = "Assigns an existing user to a company (Admin only usually)")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company assigned"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @PutMapping("/{userId}/company")
    public ResponseEntity<UserResource> assignCompany(@PathVariable UUID userId, @RequestBody synera.centralis.api.iam.interfaces.rest.resources.AssignCompanyResource assignCompanyResource) {
        var command = new synera.centralis.api.iam.domain.model.commands.AssignUserToCompanyCommand(userId, new synera.centralis.api.shared.domain.model.valueobjects.CompanyId(assignCompanyResource.companyId()));
        var updatedUser = userCommandService.handle(command);
        if (updatedUser.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(updatedUser.get());
        return ResponseEntity.ok(userResource);
    }

    /**
     * Joins a company using a 6-character code.
     * @param joinCompanyResource the resource containing the join code.
     * @return the updated user resource.
     */
    @Operation(summary = "Join a company", description = "Allows a user to join a company using a 6-character code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully joined the company"),
            @ApiResponse(responseCode = "400", description = "Invalid join code"),
            @ApiResponse(responseCode = "404", description = "Company not found for the given code"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @PostMapping("/me/company/join")
    public ResponseEntity<UserResource> joinCompany(@RequestBody JoinCompanyResource joinCompanyResource) {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = authentication.getName();
        var getUserQuery = new synera.centralis.api.iam.domain.model.queries.GetUserByUsernameQuery(username);
        var userOptional = userQueryService.handle(getUserQuery);
        
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }

        UUID companyId = companyContextFacade.fetchCompanyIdByJoinCode(joinCompanyResource.joinCode());
        if (companyId == null) {
            return ResponseEntity.notFound().build();
        }

        var command = new synera.centralis.api.iam.domain.model.commands.AssignUserToCompanyCommand(
                userOptional.get().getId(), 
                new synera.centralis.api.shared.domain.model.valueobjects.CompanyId(companyId)
        );
        var updatedUser = userCommandService.handle(command);
        
        if (updatedUser.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(updatedUser.get());
        return ResponseEntity.ok(userResource);
    }
}

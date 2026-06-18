package synera.centralis.api.company.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import synera.centralis.api.company.domain.model.commands.DeleteSpaceCommand;
import synera.centralis.api.company.domain.model.queries.GetAllSpacesQuery;
import synera.centralis.api.company.domain.model.queries.GetSpaceByIdQuery;
import synera.centralis.api.company.domain.services.SpaceCommandService;
import synera.centralis.api.company.domain.services.SpaceQueryService;
import synera.centralis.api.company.interfaces.rest.resources.CreateSpaceResource;
import synera.centralis.api.company.interfaces.rest.resources.SpaceResource;
import synera.centralis.api.company.interfaces.rest.resources.UpdateSpaceResource;
import synera.centralis.api.company.interfaces.rest.transform.CreateSpaceCommandFromResourceAssembler;
import synera.centralis.api.company.interfaces.rest.transform.SpaceResourceFromEntityAssembler;
import synera.centralis.api.company.interfaces.rest.transform.UpdateSpaceCommandFromResourceAssembler;
import synera.centralis.api.event.interfaces.acl.EventContextFacade;
import synera.centralis.api.iam.infrastructure.authorization.sfs.utils.SecurityUtils;
import synera.centralis.api.shared.domain.exceptions.ResourceNotFoundException;
import synera.centralis.api.shared.domain.exceptions.UnauthorizedException;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Manages company-level Spaces (rooms). Mutations are restricted to the company's
 * manager (or a system admin); reads are open to any authenticated company user.
 * Everything is scoped to the caller's company.
 */
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping(value = "/api/v1/spaces", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Spaces", description = "Company Space (room) Management Endpoints")
public class SpaceController {

    private final SpaceCommandService spaceCommandService;
    private final SpaceQueryService spaceQueryService;
    private final EventContextFacade eventContextFacade;

    public SpaceController(SpaceCommandService spaceCommandService, SpaceQueryService spaceQueryService,
                           EventContextFacade eventContextFacade) {
        this.spaceCommandService = spaceCommandService;
        this.spaceQueryService = spaceQueryService;
        this.eventContextFacade = eventContextFacade;
    }

    private CompanyId getCurrentCompanyId() {
        CompanyId companyId = SecurityUtils.getCurrentCompanyId();
        if (companyId == null || companyId.companyId() == null) {
            throw new UnauthorizedException("User is not associated with a company");
        }
        return companyId;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Create a space", description = "Creates a new room for the caller's company")
    public ResponseEntity<SpaceResource> createSpace(@Valid @RequestBody CreateSpaceResource resource) {
        var companyId = getCurrentCompanyId();
        var command = CreateSpaceCommandFromResourceAssembler.toCommandFromResource(resource, companyId);
        var space = spaceCommandService.handle(command);
        return new ResponseEntity<>(SpaceResourceFromEntityAssembler.toResourceFromEntity(space), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List spaces",
            description = "Lists the company's rooms. When 'date' is provided, each room is annotated with its availability for that day.")
    public ResponseEntity<List<SpaceResource>> getSpaces(
            @Parameter(description = "Optional calendar day (YYYY-MM-DD) to compute availability")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var companyId = getCurrentCompanyId();
        var spaces = spaceQueryService.handle(new GetAllSpacesQuery(companyId));

        if (date == null) {
            var resources = spaces.stream()
                    .map(SpaceResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            return ResponseEntity.ok(resources);
        }

        Set<UUID> bookedSpaceIds = eventContextFacade.findBookedSpaceIdsOnDate(companyId, date);
        var resources = spaces.stream()
                .map(space -> SpaceResourceFromEntityAssembler.toResourceFromEntity(
                        space, !bookedSpaceIds.contains(space.getId())))
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a space by ID")
    public ResponseEntity<SpaceResource> getSpaceById(@PathVariable UUID id) {
        var companyId = getCurrentCompanyId();
        var space = spaceQueryService.handle(new GetSpaceByIdQuery(id, companyId))
                .orElseThrow(() -> new ResourceNotFoundException("Space not found with ID: " + id));
        return ResponseEntity.ok(SpaceResourceFromEntityAssembler.toResourceFromEntity(space));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Update a space")
    public ResponseEntity<SpaceResource> updateSpace(@PathVariable UUID id, @Valid @RequestBody UpdateSpaceResource resource) {
        var companyId = getCurrentCompanyId();
        var command = UpdateSpaceCommandFromResourceAssembler.toCommandFromResource(id, resource, companyId);
        var space = spaceCommandService.handle(command);
        return ResponseEntity.ok(SpaceResourceFromEntityAssembler.toResourceFromEntity(space));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Delete a space", description = "Deletes a room. Blocked while the room has upcoming bookings.")
    public ResponseEntity<Void> deleteSpace(@PathVariable UUID id) {
        var companyId = getCurrentCompanyId();
        spaceCommandService.handle(new DeleteSpaceCommand(id, companyId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

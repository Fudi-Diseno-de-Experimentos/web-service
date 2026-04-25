package synera.centralis.api.profile.interfaces.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import synera.centralis.api.profile.domain.model.queries.GetAllProfilesQuery;
import synera.centralis.api.profile.domain.model.queries.GetProfileByIdQuery;
import synera.centralis.api.profile.domain.model.queries.GetProfileByUserIdQuery;
import synera.centralis.api.profile.domain.model.valueobjects.UserId;
import synera.centralis.api.profile.domain.services.ProfileCommandService;
import synera.centralis.api.profile.domain.services.ProfileQueryService;
import synera.centralis.api.profile.interfaces.rest.resources.CreateProfileResource;
import synera.centralis.api.profile.interfaces.rest.resources.ProfileResource;
import synera.centralis.api.profile.interfaces.rest.resources.UpdateProfileResource;
import synera.centralis.api.profile.interfaces.rest.transform.CreateProfileCommandFromResourceAssembler;
import synera.centralis.api.profile.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import synera.centralis.api.profile.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;

/**
 * Profile controller
 * REST controller for profile operations
 */
@RestController
@RequestMapping(value = "/api/v1/profiles")
@Tag(name = "Profiles", description = "Profile Management Endpoints")
public class ProfileController {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    public ProfileController(ProfileCommandService profileCommandService, ProfileQueryService profileQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new profile", description = "Create a new user profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Profile created successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileResource.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                content = @Content),
        @ApiResponse(responseCode = "409", description = "User already has a profile",
                content = @Content)
    })
    public ResponseEntity<ProfileResource> createProfile(
            @Parameter(description = "Profile creation request", required = true)
            @Valid @RequestBody CreateProfileResource resource) {
        
        var command = CreateProfileCommandFromResourceAssembler.toCommandFromResource(resource);
        var profile = profileCommandService.handle(command);
        
        if (profile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
        return new ResponseEntity<>(profileResource, HttpStatus.CREATED);
    }

    @GetMapping("/{profileId}")
    @Operation(summary = "Get profile by ID", description = "Retrieve a profile by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileResource.class))),
        @ApiResponse(responseCode = "404", description = "Profile not found",
                content = @Content)
    })
    public ResponseEntity<ProfileResource> getProfileById(
            @Parameter(description = "Profile ID", required = true)
            @PathVariable UUID profileId) {
        
        var query = new GetProfileByIdQuery(profileId);
        var profile = profileQueryService.handle(query);
        
        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
        return ResponseEntity.ok(profileResource);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get profile by user ID", description = "Retrieve a profile by its associated user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileResource.class))),
        @ApiResponse(responseCode = "404", description = "Profile not found",
                content = @Content)
    })
    public ResponseEntity<ProfileResource> getProfileByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID userId) {
        
        var query = new GetProfileByUserIdQuery(new UserId(userId));
        var profile = profileQueryService.handle(query);
        
        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
        return ResponseEntity.ok(profileResource);
    }

    @GetMapping
    @Operation(summary = "Get all profiles", description = "Retrieve all profiles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profiles retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileResource.class)))
    })
    public ResponseEntity<List<ProfileResource>> getAllProfiles() {
        var query = new GetAllProfilesQuery();
        var profiles = profileQueryService.handle(query);
        
        var profileResources = profiles.stream()
                .map(ProfileResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        return ResponseEntity.ok(profileResources);
    }

    @PutMapping("/{profileId}")
    @Operation(summary = "Update profile", description = "Update an existing profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileResource.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Profile not found",
                content = @Content)
    })
    public ResponseEntity<ProfileResource> updateProfile(
            @Parameter(description = "Profile ID", required = true)
            @PathVariable UUID profileId,
            @Parameter(description = "Profile update request", required = true)
            @Valid @RequestBody UpdateProfileResource resource) {
        
        var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(profileId, resource);
        var profile = profileCommandService.handle(command);
        
        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
        return ResponseEntity.ok(profileResource);
    }
}
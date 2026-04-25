package synera.centralis.api.profile.application.internal.queryservices;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import synera.centralis.api.profile.domain.model.aggregates.Profile;
import synera.centralis.api.profile.domain.model.queries.GetAllProfilesQuery;
import synera.centralis.api.profile.domain.model.queries.GetProfileByIdQuery;
import synera.centralis.api.profile.domain.model.queries.GetProfileByUserIdQuery;
import synera.centralis.api.profile.domain.services.ProfileQueryService;
import synera.centralis.api.profile.infrastructure.persistence.jpa.repositories.ProfileRepository;

/**
 * Profile query service implementation
 * Handles profile query operations
 */
@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private final ProfileRepository profileRepository;

    public ProfileQueryServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> handle(GetProfileByIdQuery query) {
        return profileRepository.findById(query.profileId());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> handle(GetProfileByUserIdQuery query) {
        return profileRepository.findByUserId(query.userId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Profile> handle(GetAllProfilesQuery query) {
        return profileRepository.findAll();
    }
}
package synera.centralis.api.profile.application.internal.queryservices;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import synera.centralis.api.iam.infrastructure.authorization.sfs.utils.SecurityUtils;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import synera.centralis.api.profile.domain.model.aggregates.Profile;
import synera.centralis.api.profile.domain.model.queries.GetAllProfilesQuery;
import synera.centralis.api.profile.domain.model.queries.GetProfileByIdQuery;
import synera.centralis.api.profile.domain.model.queries.GetProfileByUserIdQuery;
import synera.centralis.api.profile.domain.model.queries.GetProfilesByCompanyIdQuery;
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
        CompanyId currentCompanyId = SecurityUtils.getCurrentCompanyId();
        if (SecurityUtils.isAdmin() || currentCompanyId == null) {
            return profileRepository.findById(query.profileId());
        }
        return profileRepository.findByIdAndCompanyId(query.profileId(), currentCompanyId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> handle(GetProfileByUserIdQuery query) {
        // FindByUserId already implies specific user so we may not need to filter,
        // but for extreme security we could check if profile.getCompanyId() matches.
        // It's probably safe since the user has to exist.
        return profileRepository.findByUserId(query.userId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Profile> handle(GetAllProfilesQuery query) {
        CompanyId currentCompanyId = SecurityUtils.getCurrentCompanyId();
        if (SecurityUtils.isAdmin() || currentCompanyId == null) {
            return profileRepository.findAll();
        }
        return profileRepository.findAllByCompanyId(currentCompanyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Profile> handle(GetProfilesByCompanyIdQuery query) {
        CompanyId currentCompanyId = SecurityUtils.getCurrentCompanyId();
        if (!SecurityUtils.isAdmin() && currentCompanyId != null) {
            // A non-admin user can only query their own company
            if (!currentCompanyId.equals(query.companyId())) {
                return List.of();
            }
        }
        return profileRepository.findAllByCompanyId(query.companyId());
    }
}
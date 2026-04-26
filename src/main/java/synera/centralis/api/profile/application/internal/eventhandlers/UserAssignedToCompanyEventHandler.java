package synera.centralis.api.profile.application.internal.eventhandlers;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import synera.centralis.api.profile.domain.model.valueobjects.UserId;
import synera.centralis.api.profile.infrastructure.persistence.jpa.repositories.ProfileRepository;
import synera.centralis.api.shared.domain.events.UserAssignedToCompanyEvent;

@Component
public class UserAssignedToCompanyEventHandler {

    private final ProfileRepository profileRepository;

    public UserAssignedToCompanyEventHandler(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @EventListener
    public void on(UserAssignedToCompanyEvent event) {
        var userId = new UserId(event.userId());
        var profileOpt = profileRepository.findByUserId(userId);
        
        if (profileOpt.isPresent()) {
            var profile = profileOpt.get();
            profile.setCompanyId(event.companyId());
            profileRepository.save(profile);
        }
    }
}

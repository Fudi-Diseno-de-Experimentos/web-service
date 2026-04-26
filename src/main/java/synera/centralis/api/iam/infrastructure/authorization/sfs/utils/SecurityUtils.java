package synera.centralis.api.iam.infrastructure.authorization.sfs.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import synera.centralis.api.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

public class SecurityUtils {

    public static UserDetailsImpl getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return (UserDetailsImpl) authentication.getPrincipal();
        }
        return null;
    }

    public static CompanyId getCurrentCompanyId() {
        UserDetailsImpl currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getCompanyId() : null;
    }

    public static boolean isAdmin() {
        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser == null) return false;
        return currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}

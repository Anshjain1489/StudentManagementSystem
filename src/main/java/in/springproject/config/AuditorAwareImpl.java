package in.springproject.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of {@link AuditorAware} to provide the current authenticated user
 * for JPA auditing ({@code createdBy}, {@code updatedBy} fields).
 *
 * <p>Falls back to {@code "system"} when no authenticated user is present
 * or the principal is the anonymous user.</p>
 */
@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName().equals("anonymousUser")) {
            return Optional.of("system");
        }
        return Optional.of(authentication.getName());
    }
}

package synera.centralis.api.iam.application.internal.queryservices;

import synera.centralis.api.iam.domain.model.aggregates.User;
import synera.centralis.api.iam.domain.model.queries.GetAllUsersQuery;
import synera.centralis.api.iam.domain.model.queries.GetUserByIdQuery;
import synera.centralis.api.iam.domain.model.queries.GetUserByUsernameQuery;
import synera.centralis.api.iam.domain.services.UserQueryService;
import synera.centralis.api.iam.infrastructure.persistence.jpa.repositories.UserRepository;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link UserQueryService} interface.
 */
@Service
public class UserQueryServiceImpl implements UserQueryService {
    private final UserRepository userRepository;

    /**
     * Constructor.
     *
     * @param userRepository {@link UserRepository} instance.
     */
    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This method is used to handle {@link GetAllUsersQuery} query.
     * @param query {@link GetAllUsersQuery} instance.
     * @return {@link List} of {@link User} instances.
     * @see GetAllUsersQuery
     */
    @Override
    public List<User> handle(GetAllUsersQuery query) {
        return userRepository.findAll();
    }

    /**
     * This method is used to handle {@link GetUserByIdQuery} query.
     * @param query {@link GetUserByIdQuery} instance.
     * @return {@link Optional} of {@link User} instance.
     * @see GetUserByIdQuery
     */
    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId());
    }

    /**
     * This method is used to handle {@link GetUserByUsernameQuery} query.
     * @param query {@link GetUserByUsernameQuery} instance.
     * @return {@link Optional} of {@link User} instance.
     * @see GetUserByUsernameQuery
     */
    @Override
    public Optional<User> handle(GetUserByUsernameQuery query) {
        return userRepository.findByUsername(query.username());
    }

    /**
     * Get total count of users in the system
     * @return Total user count
     */
    @Override
    public long getTotalUserCount() {
        return userRepository.count();
    }
}

package it.safesiteguard.ms.loginms_ssguard.repositories;

import it.safesiteguard.ms.loginms_ssguard.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    List<User> findAll();
    Optional<User> findByUsername(String username);

    Optional<User> findById(String userID);

    List<User> findAllByIdIn(List<String> userIDs);

    List<User> findAllByRole(User.Role role);
}

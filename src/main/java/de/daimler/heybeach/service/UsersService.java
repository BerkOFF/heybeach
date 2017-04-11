package de.daimler.heybeach.service;

import de.daimler.heybeach.error.BackendException;
import de.daimler.heybeach.model.User;
import de.daimler.heybeach.persistence.UserEntityDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersService {
    @Autowired
    private UserEntityDAO entityService;

    public Optional<User> getUser(UUID id) throws BackendException {
        return Optional.ofNullable(entityService.find(id));
    }

    public Optional<User> getUser(String email) throws BackendException {
        return Optional.ofNullable(entityService.findByEMail(email));
    }

    public User createUser(User user) throws BackendException {
        if(!StringUtils.hasText(user.getEmail()) || !StringUtils.hasText(user.getPassword()) ||
                user.getRole() == null) {
            throw new IllegalArgumentException("Missing mandatory user information");
        }
        user.setId(UUID.randomUUID());
        entityService.create(user);
        return user;
    }

    public Optional<User> update(User user) throws BackendException {
        return Optional.ofNullable(entityService.update(user));
    }

    public boolean exists(String email) throws BackendException {
        return entityService.findByEMail(email) != null;
    }

    public List<UUID> fetchLikes(UUID id) throws BackendException {
        return entityService.fetchLikes(id);
    }
}

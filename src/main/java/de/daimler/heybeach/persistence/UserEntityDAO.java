package de.daimler.heybeach.persistence;

import de.daimler.heybeach.error.BackendException;
import de.daimler.heybeach.model.User;

import java.util.List;
import java.util.UUID;

public interface UserEntityDAO extends EntityDAO<User, UUID> {
    User findByEMail(String email) throws BackendException;
    List<UUID> fetchLikes(UUID id) throws BackendException;
}

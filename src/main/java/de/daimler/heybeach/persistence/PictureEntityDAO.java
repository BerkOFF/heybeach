package de.daimler.heybeach.persistence;

import de.daimler.heybeach.error.BackendException;
import de.daimler.heybeach.model.Picture;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PictureEntityDAO extends EntityDAO<Picture, UUID> {
    void updateHashtags(UUID id, Set<String> hashtags) throws BackendException;
    List<String> fetchHashtags(UUID id) throws BackendException;
    long countLikes(UUID id) throws BackendException;
    List<UUID> fetchLikes(UUID id) throws BackendException;
    boolean like(UUID pictureId, UUID userId) throws BackendException;
    boolean unlike(UUID pictureId, UUID userId) throws BackendException;
    List<UUID> findPictureByHashtag(String hashtag) throws BackendException;
}

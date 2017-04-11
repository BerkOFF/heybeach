package de.daimler.heybeach.service;

import de.daimler.heybeach.error.BackendException;
import de.daimler.heybeach.model.Picture;
import de.daimler.heybeach.model.PictureState;
import de.daimler.heybeach.persistence.PictureEntityDAO;
import de.daimler.heybeach.util.LRUCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class PicturesService {

    @Value("${app.pictures.path}")
    private String storePathPrefix;

    @Value("${cache.pictureToHashtags.size}")
    private int picToHashtagsCacheSize;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private PictureEntityDAO entityService;

    private LRUCache<UUID, Set<String>> pictureToHashtags;

    @PostConstruct
    public void init() {
        pictureToHashtags = new LRUCache<>(picToHashtagsCacheSize);
    }

    public Picture upload(Picture picture, Set<String> hashtags, InputStreamSource inputStreamSource) throws BackendException {
        picture.setId(UUID.randomUUID());
        picture.setState(PictureState.review);
        try {
            Files.copy(inputStreamSource.getInputStream(), Paths.get(storePathPrefix, picture.getId().toString()),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exc) {
            throw new BackendException("Unable to save file", exc);
        }
        entityService.create(picture);
        entityService.updateHashtags(picture.getId(), hashtags);
        return picture;
    }

    public Resource getResource(UUID id) throws BackendException {
        try {
            return resourceLoader.getResource("file:" + Paths.get(storePathPrefix, id.toString()));
        } catch (Exception exc) {
            throw new BackendException("Unable to load resource: " + id, exc);
        }
    }

    public Optional<Picture> getPicture(UUID id) throws BackendException {
        return Optional.ofNullable(entityService.find(id));
    }

    public Optional<Picture> update(Picture picture) throws BackendException {
        return Optional.ofNullable(entityService.update(picture));
    }

    public long countLikes(UUID id) throws BackendException {
        return entityService.countLikes(id);
    }

    public boolean like(UUID pictureId, UUID userId) throws BackendException {
        return entityService.like(pictureId, userId);
    }

    public boolean unlike(UUID pictureId, UUID userId) throws BackendException {
        return entityService.unlike(pictureId, userId);
    }

    public void updateHashtags(UUID id, Set<String> hashtags) throws BackendException {
        entityService.updateHashtags(id, hashtags);
        pictureToHashtags.put(id, hashtags);
    }

    public Set<String> fetchHashtags(UUID id) throws BackendException {
        Set<String> hashtags = pictureToHashtags.get(id);
        if(hashtags == null) {
            hashtags = new HashSet<>(entityService.fetchHashtags(id));
            pictureToHashtags.put(id, hashtags);
        }
        return hashtags;
    }


}

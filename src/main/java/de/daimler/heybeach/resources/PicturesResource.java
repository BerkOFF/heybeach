package de.daimler.heybeach.resources;

import de.daimler.heybeach.error.BackendException;
import de.daimler.heybeach.model.Picture;
import de.daimler.heybeach.model.PictureState;
import de.daimler.heybeach.model.UserRole;
import de.daimler.heybeach.response.PictureResponse;
import de.daimler.heybeach.service.PicturesService;
import de.daimler.heybeach.util.Helpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static de.daimler.heybeach.resources.PicturesResource.PATH;
import static de.daimler.heybeach.util.APIConstants.BASE_PATH;

@RestController
@RequestMapping(path = PATH)
public class PicturesResource {

    public final static String PATH = BASE_PATH + "pictures";

    @Autowired
    private PicturesService picturesService;

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('seller')")
    public ResponseEntity<Void> upload(@RequestParam Optional<Set<String>> hashtags, @RequestParam Double price,
                                  @RequestParam("file") MultipartFile file, UriComponentsBuilder ucBuilder) throws BackendException {
        Picture picture = new Picture();
        picture.setOwnerId(Helpers.getAuthUserId());
        picture.setMediaType(file.getContentType());
        picture.setPrice(price);
        picture = picturesService.upload(picture, hashtags.orElse(Collections.emptySet()), file);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path(PATH).path("/{id}").buildAndExpand(picture.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable("id") UUID id, HttpServletResponse response) throws BackendException {
        Optional<Picture> picture = picturesService.getPicture(id);
        Resource resource;
        if(picture.isPresent() &&
                (resource = picturesService.getResource(id)).exists()
                && canBeDisplayed(picture.get())) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(picture.get().getMediaType()));
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path = "/{id}")
    public ResponseEntity<PictureResponse> getDetails(@PathVariable UUID id, UriComponentsBuilder ucBuilder) throws BackendException {
        Optional<Picture> picture = picturesService.getPicture(id);
        if(picture.isPresent()) {
            PictureResponse response = Helpers.copyClone(PictureResponse.class, picture.get());
            response.setHref(ucBuilder.path(PATH).path("/{id}/download").buildAndExpand(response.getId()).toUriString());
            response.setLikesCount(picturesService.countLikes(response.getId()));
            response.setHashtags(picturesService.fetchHashtags(id));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path = "/{id}/like", method = RequestMethod.POST)
    @PreAuthorize("hasAnyAuthority('buyer', 'seller')")
    public ResponseEntity<Void> like(@PathVariable("id") UUID id) throws BackendException {
        if(picturesService.like(id, Helpers.getAuthUserId())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
    }

    @RequestMapping(path = "/{id}/unlike", method = RequestMethod.POST)
    @PreAuthorize("hasAnyAuthority('buyer', 'seller')")
    public ResponseEntity<Void> unlike(@PathVariable("id") UUID id) throws BackendException {
        if(picturesService.unlike(id, Helpers.getAuthUserId())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
    }

    @RequestMapping(path = "/{id}/modify", method = RequestMethod.POST)
    @PreAuthorize("hasAnyAuthority('seller', 'admin')")
    public ResponseEntity<Void> update(@PathVariable("id") UUID id,
                       @RequestParam(name = "hashtags") Optional<Set<String>> hashtags,
                       @RequestParam(name = "price") Optional<Double> price) throws BackendException {
        Optional<Picture> picture = picturesService.getPicture(id);
        if(picture.isPresent()) {
            boolean isAdmin = Helpers.checkAuthzForRole(UserRole.admin);
            boolean isOwner = picture.get().getOwnerId().equals(Helpers.getAuthUserId());
            if(isAdmin || isOwner) {
                if(hashtags.isPresent()) {
                    picturesService.updateHashtags(id, hashtags.get());
                }
                if(price.isPresent()) {
                    Picture update = new Picture();
                    update.setId(id);
                    update.setPrice(price.get());
                    picturesService.update(update);
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Void> update(@PathVariable("id") UUID id, @RequestBody Picture picture) throws BackendException {
        if(picturesService.getPicture(id).isPresent()) {
            picture.setId(id);
            picturesService.update(picture);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") UUID id) {
        //TODO implement
    }

    @RequestMapping
    public List<UUID> list(@RequestParam(name = "state", required = false) PictureState[] states,
                           @RequestParam(name = "owner", required = false) UUID ownerId,
                           @RequestParam(defaultValue = "100") int limit,
                           @RequestParam(defaultValue = "0") int offset) {
        return null;
    }

    private boolean canBeDisplayed(Picture picture) {
        return picture.getState().compareTo(PictureState.displayable) >=0 ||
                picture.getOwnerId().equals(Helpers.getAuthUserId()) || Helpers.checkAuthzForRole(UserRole.admin);
    }
}

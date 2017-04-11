package de.daimler.heybeach.resources;

import de.daimler.heybeach.error.BackendException;
import de.daimler.heybeach.model.User;
import de.daimler.heybeach.model.UserRole;
import de.daimler.heybeach.response.UserResponse;
import de.daimler.heybeach.service.UsersService;
import de.daimler.heybeach.util.Helpers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static de.daimler.heybeach.resources.UsersResource.PATH;
import static de.daimler.heybeach.util.APIConstants.BASE_PATH;

@RestController
@RequestMapping(path = PATH)
public class UsersResource {

    public final static String PATH = BASE_PATH + "users";

    @Autowired
    private UsersService usersService;

    @RequestMapping(path = "/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) throws BackendException {
        Optional<User> user = usersService.getUser(id);
        if (user.isPresent()) {
            UserResponse response = new UserResponse();
            BeanUtils.copyProperties(user.get(), response);
            response.setLikes(usersService.fetchLikes(id));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) throws BackendException {
        if (usersService.exists(user.getEmail())) {
            System.out.println("A User with email " + user.getEmail() + " already exist");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        if (user.getRole() == UserRole.admin && !Helpers.checkAuthzForRole(UserRole.admin)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        user = usersService.createUser(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path(PATH).path("/{id}").buildAndExpand(user.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateUser(@PathVariable UUID id, @RequestBody User update) throws BackendException {
        boolean isAdmin = Helpers.checkAuthzForRole(UserRole.admin);
        if (isAdmin || Objects.equals(Helpers.getAuthUserId(), id)) {
            if (!isAdmin) {
                //only admin can change role associated
                update.setRole(null);
            }
            update.setId(id);
            return usersService.update(update)
                    .map(user -> new ResponseEntity<Void>(HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}

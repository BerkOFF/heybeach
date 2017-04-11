package de.daimler.heybeach.resources;

import de.daimler.heybeach.model.Order;
import de.daimler.heybeach.model.OrderState;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.daimler.heybeach.resources.OrdersResource.PATH;
import static de.daimler.heybeach.util.APIConstants.BASE_PATH;

@RestController
@RequestMapping(path = PATH)
public class OrdersResource {
    public final static String PATH = BASE_PATH + "orders";

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('buyer')")
    public ResponseEntity<Void> createOrder(@RequestBody Order order) {
        return null;
    }

    @RequestMapping(path = "/{id}")
    public Order getDetails(@PathVariable("id") UUID id) {
        return null;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public Order update(@PathVariable("id") UUID id) {
        return null;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public void cancel(@PathVariable("id") UUID id) {
    }

    public List<UUID> list(@RequestParam("owner") Optional<UUID> ownerId,
                           @RequestParam("state") Optional<OrderState[]> states) {
        return null;
    }

    @RequestMapping(path = "/{id}/item/{itemId}", method = RequestMethod.PUT)
    public ResponseEntity<Void> addItem(@PathVariable("id") UUID id) {
        return null;
    }

    @RequestMapping(path = "/{id}/item/{itemId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeItem(@PathVariable("id") UUID id) {
        return null;
    }
}

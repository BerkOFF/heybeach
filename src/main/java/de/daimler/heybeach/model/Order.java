package de.daimler.heybeach.model;

import java.util.Map;
import java.util.UUID;

public class Order {
    private UUID id;
    private UUID userId;
    private String name;
    private String address;
    private OrderState state;
    private Map<UUID, Integer> items;
}

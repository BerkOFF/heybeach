package de.daimler.heybeach.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public class Picture {
    private UUID id;
    private UUID ownerId;
    private Double price;
    private PictureState state;
    @JsonIgnore
    private String mediaType;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public PictureState getState() {
        return state;
    }

    public void setState(PictureState state) {
        this.state = state;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}

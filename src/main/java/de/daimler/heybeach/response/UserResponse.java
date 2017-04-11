package de.daimler.heybeach.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.daimler.heybeach.model.User;

import java.util.List;
import java.util.UUID;

public class UserResponse extends User {
    private List<UUID> likes;

    public List<UUID> getLikes() {
        return likes;
    }

    public void setLikes(List<UUID> likes) {
        this.likes = likes;
    }

    @JsonIgnore
    public String getPassword() {
        return null;
    }
}

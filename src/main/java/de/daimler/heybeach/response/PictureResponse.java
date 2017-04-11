package de.daimler.heybeach.response;

import de.daimler.heybeach.model.Picture;

import java.util.Set;

public class PictureResponse extends Picture {
    private Set<String> hashtags;
    private String href;
    private long likesCount;

    public Set<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<String> hashtags) {
        this.hashtags = hashtags;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }
}

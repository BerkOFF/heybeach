package de.daimler.heybeach.persistence.jdbc;


import de.daimler.heybeach.error.BackendException;
import de.daimler.heybeach.model.Picture;
import de.daimler.heybeach.model.PictureState;
import de.daimler.heybeach.persistence.PictureEntityDAO;
import de.daimler.heybeach.util.Helpers;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
public class PicturesEntityDAOImpl extends JdbcDaoSupport implements PictureEntityDAO {

    private RowMapper<Picture> rowMapper = (rs, rowNum) -> {
        Picture picture = new Picture();
        picture.setId(UUID.fromString(rs.getString("id")));
        picture.setOwnerId(UUID.fromString(rs.getString("owner_id")));
        picture.setPrice(rs.getDouble("price"));
        picture.setState(PictureState.valueOf(rs.getString("state")));
        picture.setMediaType(rs.getString("media_type"));
        return picture;
    };

    @Override
    public Picture find(UUID id) throws BackendException {
        try {
            return getJdbcTemplate().queryForObject("select * from pictures where id = ?", rowMapper, id.toString());
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        } catch (Exception exc) {
            throw new BackendException("Unable to find picture", exc);
        }
    }

    @Override
    public void create(Picture entity) throws BackendException {
        try {
            getJdbcTemplate().update("insert into pictures(id, owner_id, price, state, media_type) values (?, ?, ?, ?, ?) ",
                    entity.getId().toString(), entity.getOwnerId().toString(), entity.getPrice(), entity.getState().name(), entity.getMediaType());
        } catch (Exception exc) {
            throw new BackendException("Unable to create picture", exc);
        }
    }

    @Override
    public Picture update(Picture entity) throws BackendException {
        Picture updated = find(entity.getId());
        Helpers.merge(entity, updated);
        int updatedCount = getJdbcTemplate().update("update pictures set ownerId = ?, price = ?, state = ?, media_type = ? where id = ?",
                updated.getOwnerId(), updated.getPrice(), updated.getState().name(), updated.getMediaType(), updated.getId());
        if(updatedCount == 0) {
            return null;
        }
        getJdbcTemplate().update("delete from user_like where user_id = ?", entity.getId());
        return updated;
    }

    @Override
    public boolean delete(UUID id) throws BackendException {
        try {
            getJdbcTemplate().update("delete from user_like where picture_id = ?", id.toString());
            return getJdbcTemplate().update("delete from pictures where id = ?", id.toString()) > 0;
        } catch (Exception exc) {
            throw new BackendException("Unable to delete picture");
        }
    }

    @Override
    public void updateHashtags(UUID id, Set<String> hashtags) throws BackendException {
        try {
            getJdbcTemplate().update("delete from hashtags where picture_id = ?", id.toString());
            getJdbcTemplate().batchUpdate("insert into hashtags(picture_id, hashtag) values (?, ?)",
                    hashtags.stream()
                            .map(hashtag -> new Object[] {id.toString(), hashtag})
                            .collect(Collectors.toList()));
        } catch (Exception exc) {
            throw new BackendException("Unable to update hashtags");
        }
    }

    @Override
    public List<String> fetchHashtags(UUID id) throws BackendException {
        try {
            return getJdbcTemplate().queryForList("select hashtag from hashtags where picture_id = ?",
                    String.class, id.toString());
        } catch (Exception exc) {
            throw new BackendException("Unable to fetch hashtags");
        }
    }

    @Override
    public long countLikes(UUID id) throws BackendException {
        return fetchLikes(id).size();
    }

    public List<UUID> fetchLikes(UUID id) throws BackendException {
        try {
            List<String> likes = getJdbcTemplate().queryForList("select user_id from user_like where picture_id = ?",
                    String.class, id.toString());
            return likes.stream().map(UUID::fromString).collect(Collectors.toList());
        } catch (Exception exc) {
            throw new BackendException("Unable to fetch likes");
        }
    }

    @Override
    public boolean like(UUID pictureId, UUID userId) throws BackendException {
        try {
            return getJdbcTemplate().update("insert into user_like(user_id, picture_id) values (?, ?)", userId.toString(), pictureId.toString()) > 0;
        } catch (Exception exc) {
            throw new BackendException("Unable to record like");
        }
    }

    @Override
    public boolean unlike(UUID pictureId, UUID userId) throws BackendException {
        try {
            return getJdbcTemplate().update("delete from user_like where user_id = ? and picture_id = ?", userId.toString(), pictureId.toString()) > 0;
        } catch (Exception exc) {
            throw new BackendException("Unable to remove like");
        }
    }

    @Override
    public List<UUID> findPictureByHashtag(String hashtag) throws BackendException {
        try {
            List<String> pictures = getJdbcTemplate().queryForList("select picure_id from hashtags where hashtag = ?",
                    String.class, hashtag);
            return pictures.stream().map(UUID::fromString).collect(Collectors.toList());
        } catch (Exception exc) {
            throw new BackendException("Unable to find by hashtag");
        }
    }


}

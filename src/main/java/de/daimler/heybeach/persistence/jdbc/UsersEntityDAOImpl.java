package de.daimler.heybeach.persistence.jdbc;


import de.daimler.heybeach.model.User;
import de.daimler.heybeach.model.UserRole;
import de.daimler.heybeach.error.BackendException;

import de.daimler.heybeach.persistence.UserEntityDAO;
import de.daimler.heybeach.util.Helpers;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
public class UsersEntityDAOImpl extends JdbcDaoSupport implements UserEntityDAO {

    private final RowMapper<User> rowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(UUID.fromString(rs.getString("id")));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        return user;
    };

    @Override
    public User find(UUID id) throws BackendException {
        try {
            return getJdbcTemplate().queryForObject("select * from users where id = ?", rowMapper, id.toString());
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        } catch (Exception exc) {
            throw new BackendException("Unable to find user", exc);
        }
    }

    @Override
    public void create(User entity) throws BackendException {
        try {
            getJdbcTemplate().update("insert into users(id, email, password, role) values (?, ?, ?, ?) ",
                    entity.getId().toString(), entity.getEmail(), entity.getPassword(), entity.getRole().name());
        } catch(Exception exc) {
            throw new BackendException("Unable to create user", exc);
        }
    }

    @Override
    public User update(User entity) throws BackendException {
        try {
            User updated = find(entity.getId());
            Helpers.merge(entity, updated);
            int updatedCount = getJdbcTemplate().update("update users set email = ?, password = ?, role = ? where id = ?",
                    updated.getEmail(), updated.getPassword(), updated.getRole().name(), updated.getId());
            if (updatedCount == 0) {
                return null;
            }
            return updated;
        } catch (Exception exc) {
            throw new BackendException("Unable to update user", exc);
        }
    }

    @Override
    public boolean delete(UUID id) throws BackendException {
        getJdbcTemplate().update("delete from user_like where user_id = ?", id.toString());
        return getJdbcTemplate().update("delete from users where user_id = ?", id.toString()) > 0;
    }

    @Override
    public User findByEMail(String email) throws BackendException {
        try {
            return getJdbcTemplate().queryForObject("select * from users where email = ?", rowMapper, email);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        } catch (Exception exc) {
            throw new BackendException("Unable to find user", exc);
        }
    }

    public List<UUID> fetchLikes(UUID id) throws BackendException {
        try {
            List<String> likes = getJdbcTemplate().queryForList("select picture_id from user_like where user_id = ?",
                    String.class, id.toString());
            return likes.stream().map(UUID::fromString).collect(Collectors.toList());
        } catch (Exception exc) {
            throw new BackendException("Unable to fetch likes", exc);
        }
    }
}

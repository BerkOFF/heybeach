package de.daimler.heybeach.persistence;

import de.daimler.heybeach.error.BackendException;

import java.io.Serializable;

public interface EntityDAO<E, I extends Serializable> {

    /**
     *
     * @param id primary key of entity
     * @return the and instance of an entity fetched from the data store or null if the entity does not exist
     * @throws NullPointerException when id is null
     * @throws BackendException when request to underlying storage fails go return normally
     *
     **/
    E find(final I id) throws BackendException;

    /**
     * #
     * @param entity the entity to be created in the underlying data store
     * @throws NullPointerException when entity is null
     * @throws BackendException when entity could not be created

     */
    void create(final E entity) throws BackendException;

    /**
     *
     * @param entity to be updated in the underlying data store
     * @return reference to an updated entity                  #
     * @throws NullPointerException when entity is null
     * @throws BackendException when entity could not be update
     */
    E update(final E entity) throws BackendException;

    /**
     *
     *
     *
     * @param id primary key of entity
     * @return deleted entity if found and deleted, null otherwise
     * @throws NullPointerException when id is null
     * @throws BackendException when entity could not be update
     */
    boolean delete(final I id) throws BackendException;
}

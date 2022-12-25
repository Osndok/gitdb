package github.osndok.gitdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.function.Predicate;

public
interface Transaction
{
    Date getStartTime();

    <T extends GitDbObject>
    Collection<UUID> listIds(Class<T> c);

    default
    <T extends GitDbObject>
    Collection<T> listObjects(Class<T> c)
    {
        return listObjects(c, listIds(c));
    }

    default
    <T extends GitDbObject>
    Collection<T> listObjects(Class<T> c, Iterable<UUID> uuids)
    {
        var results = new ArrayList<T>();
        for (UUID uuid : uuids)
        {
            T t = get(c, uuid);
            results.add(t);
        }
        return results;
    }

    /**
     * Behold the shame of v0. Nice and clean from the consumer side, but from the implementation side this
     * materializes every object in order to perform the test, O(n)... scanning every object.
     */
    default
    <T extends GitDbObject>
    Collection<T> search(Class<T> c, Predicate<T> test)
    {
        var results = new ArrayList<T>();
        for (T t : listObjects(c))
        {
            if (test.test(t))
            {
                results.add(t);
            }
        }
        return results;
    }

    /**
     * Causes the given object to be persisted into the database.
     */
    void save(GitDbObject object);

    /**
     * Similar to 'save', but less safe b/c it accepts objects from other transactions and those
     * previously saved under a different id.
     */
    void forceOverwrite(UUID id, GitDbObject object);

    default
    void delete(GitDbObject object)
    {
        delete(object.getClass(), object._db_id);
    }

    default
    <T extends GitDbObject>
    void delete(Class<T> c, UUID id)
    {
        delete(get(c, id));
    }

    void commit(String formatString, Object... args);

    void abort();

    default
    <T extends GitDbObject>
    T get(Class<T> c, String id)
    {
        return get(c, UUID.fromString(id));
    }

    default
    <T extends GitDbObject>
    T get(Class<T> c, UUID id)
    {
        return get(c, id.toString());
    }

    /**
     * Saves the object in question, or throws an exception if it has already been saved.
     */
    default
    UUID create(GitDbObject object)
    {
        if (object._db_id != null)
        {
            throw new IllegalStateException("Object has already been saved");
        }

        save(object);

        var id = object._db_id;
        assert id != null;
        return id;
    }

    /**
     * Saves the object in question, or throws if the object was not already saved.
     */
    default
    void update(GitDbObject object)
    {
        if (object._db_id == null)
        {
            throw new IllegalStateException("Attempting to update an object that was never persisted");
        }

        save(object);
    }

    /**
     * ADVANCED USAGE.
     *
     * Causes the database layer to allocate the given object without actually performing a write operation.
     * If used improperly, this might cause dangling references to unsaved objects.
     *
     * @bug Using this method will cause the wrong hook to be called when saving (update instead of create).
     * @bug Using this method will cause the create() method to errantly throw an exception.
     */
    void allocateId(GitDbObject object);
}

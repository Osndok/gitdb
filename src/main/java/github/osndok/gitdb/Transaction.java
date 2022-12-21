package github.osndok.gitdb;

import java.util.Date;
import java.util.UUID;

public
interface Transaction
{
    Date getStartTime();

    <T extends GitDbObject> Iterable<UUID> list(Class<T> c);

    /**
     * Causes the given object to be persisted into the database.
     */
    void save(GitDbObject object);

    void delete(GitDbObject object);

    void commit(String message);
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
}

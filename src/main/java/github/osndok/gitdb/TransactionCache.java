package github.osndok.gitdb;

import java.util.*;

class TransactionCache
{
    final
    Map<UUID, GitDbObject> objects = new HashMap<>();

    <T extends GitDbObject>
    T get(Class<T> c, UUID id)
    {
        return (T)objects.get(id);
    }

    void put(GitDbObject object)
    {
        objects.put(object._db_id, object);
    }

    Collection<GitDbObject> values()
    {
        return objects.values();
    }

    public
    void remove(final GitDbObject object)
    {
        objects.remove(object._db_id);
    }
}

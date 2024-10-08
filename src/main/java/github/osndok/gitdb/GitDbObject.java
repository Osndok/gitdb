package github.osndok.gitdb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * All objects to be persisted in the database must extend this class.
 */
public abstract
class GitDbObject
{
    UUID _db_id;
    UUID _db_transaction_id;

    @JsonIgnore
    WeakReference<Transaction> _db_transaction;

    @JsonIgnore
    String _db_json_data_as_fetched;

    /**
     * If null, this object has not been persisted into the database; otherwise, it represents
     * an identifier that can be used to re-fetch this object later. Once saved into a database
     * transaction, or loaded from disk, this field will be non-null.
     *
     * Would be 'final', but it helps downstream unit tests to be able to override it.
     */
    public
    UUID _db_id()
    {
        return _db_id;
    }

    private String _db_id_short_memo;

    /**
     * If saved to the database, this will return the first 8 characters of the UUID (the first "segment"),
     * for more convenient (but less precise) reference to this object that, depending on the situation,
     * may be more suitable for presentation to a human. If the object has not been persisted, this method
     * will return null.
     *
     * Would be 'final', but it helps downstream unit tests to be able to override it.
     */
    public
    String _db_id_short()
    {
        if (_db_id_short_memo == null && _db_id != null)
        {
            _db_id_short_memo = _db_id.toString().substring(0, 8);
        }

        return _db_id_short_memo;
    }

    @Override
    public
    String toString()
    {
        var className = getClass().getSimpleName();

        if (_db_id == null)
        {
            return className + "{unsaved}";
        }
        else
        {
            return className + "{" +
                   "_db_id=" + _db_id +
                   '}';
        }
    }

    /**
     * @return This same object, befor
     * @todo no way to 'freeze' the object in java
     */
    public <T>
    T _as_fetched_from_db(Class<T> t)
    {
        if (!getClass().equals(t))
        {
            var message = String.format("This is a %s object, not a %s", getClass(), t);
            throw new IllegalArgumentException(message);
        }

        if (_db_json_data_as_fetched == null)
        {
            return null;
        }

        var transaction = _db_transaction.get();

        if (transaction == null)
        {
            throw new IllegalStateException("transaction is no longer reachable");
        }

        var objectMapper = transaction.getObjectMapper();

        try
        {
            return objectMapper.readValue(_db_json_data_as_fetched, t);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }
}

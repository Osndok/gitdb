package github.osndok;

import java.util.UUID;

/**
 * All objects to be persisted in the database must extend this class.
 */
public abstract
class GitDbObject
{
    UUID _db_id;
    UUID _db_transaction_id;

    /**
     * If null, this object has not been persisted into the database; otherwise, it represents
     * an identifier that can be used to re-fetch this object later. Once saved into a database
     * transaction, or loaded from disk, this field will be non-null.
     */
    public final
    UUID _db_id()
    {
        return _db_id;
    }
}

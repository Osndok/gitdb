package github.osndok.gitdb.hooks;

import github.osndok.gitdb.Database;
import github.osndok.gitdb.GitDbObject;
import github.osndok.gitdb.Transaction;

public abstract
class GitDbReactiveObjectBase
        extends GitDbObject
        implements GitDbReactiveObject
{

    @Override
    public
    void beforeCreate(final Database database, final Transaction transaction)
    {

    }

    @Override
    public
    void beforeUpdate(final Database database, final Transaction transaction)
    {

    }

    @Override
    public
    void beforeDelete(final Database database, final Transaction transaction)
    {

    }

    @Override
    public
    void beforeTransactionCommit(final Database database, final Transaction transaction)
    {

    }

    @Override
    public
    void beforeTransactionAbort(final Database database, final Transaction transaction)
    {

    }

    @Override
    public
    void onCreated(final Database database, final Transaction transaction)
    {

    }

    @Override
    public
    void onLoaded(final Database database, final Transaction transaction)
    {

    }

    @Override
    public
    void onUpdated(final Database database, final Transaction transaction)
    {

    }

    @Override
    public
    void onDeleted(final Database database, final Transaction transaction)
    {

    }

    @Override
    public
    void onTransactionCommitted(final Database database, final Transaction transaction)
    {

    }

    @Override
    public
    void onTransactionAborted(final Database database, final Transaction transaction)
    {

    }
}

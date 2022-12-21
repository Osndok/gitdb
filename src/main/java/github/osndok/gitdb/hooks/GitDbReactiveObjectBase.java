package github.osndok.gitdb.hooks;

import github.osndok.gitdb.Database;
import github.osndok.gitdb.GitDbObject;

public abstract
class GitDbReactiveObjectBase
        extends GitDbObject
        implements GitDbReactiveObject
{
    @Override
    public
    void beforeCreate(final Database database)
    {

    }

    @Override
    public
    void beforeUpdate(final Database database)
    {

    }

    @Override
    public
    void beforeDelete(final Database database)
    {

    }

    @Override
    public
    void beforeTransactionCommit(final Database database)
    {

    }

    @Override
    public
    void beforeTransactionAbort(final Database database)
    {

    }

    @Override
    public
    void onCreated(final Database database)
    {

    }

    @Override
    public
    void onLoaded(final Database database)
    {

    }

    @Override
    public
    void onUpdated(final Database database)
    {

    }

    @Override
    public
    void onDeleted(final Database database)
    {

    }

    @Override
    public
    void onTransactionCommitted(final Database database)
    {

    }

    @Override
    public
    void onTransactionAborted(final Database database)
    {

    }
}

package github.osndok.gitdb.hooks;

import github.osndok.gitdb.Database;
import github.osndok.gitdb.GitDbObject;

public abstract
class GitDbReactiveObject
        extends GitDbObject
        implements IGitDbReactiveObject
{
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

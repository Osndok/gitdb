package github.osndok.gitdb.hooks;

import github.osndok.gitdb.Database;

public
interface IGitDbReactiveObject
{
    void onCreated(Database database);
    void onLoaded(Database database);
    void onUpdated(Database database);
    void onDeleted(Database database);
    void onTransactionCommitted(Database database);
    void onTransactionAborted(Database database);
}

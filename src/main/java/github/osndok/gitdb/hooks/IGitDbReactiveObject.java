package github.osndok.gitdb.hooks;

import github.osndok.gitdb.Database;

public
interface IGitDbReactiveObject
{
    void beforeCreate(Database database);
    void beforeUpdate(Database database);
    void beforeDelete(Database database);
    void beforeTransactionCommit(Database database);
    void beforeTransactionAbort(Database database);
    void onCreated(Database database);
    void onLoaded(Database database);
    void onUpdated(Database database);
    void onDeleted(Database database);
    void onTransactionCommitted(Database database);
    void onTransactionAborted(Database database);
}

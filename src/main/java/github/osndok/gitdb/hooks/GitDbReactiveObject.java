package github.osndok.gitdb.hooks;

import github.osndok.gitdb.Database;

/**
 * If a database object implements this interface, it becomes "self-aware", and will have various methods run
 * at key points in the object's lifecycle. Since most of the time an object only wants one or two hooks,
 * consider using GitDbReactiveObject as a base class, which allows you to override just the hooks you want
 * to use (as it has empty implementations of all these).
 */
public
interface GitDbReactiveObject
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

package github.osndok.gitdb.hooks;

import github.osndok.gitdb.Database;
import github.osndok.gitdb.GitDbObject;
import github.osndok.gitdb.Transaction;

/**
 * If a database object implements this interface, it becomes "self-aware", and will have various methods run
 * at key points in the object's lifecycle. Since most of the time an object only wants one or two hooks,
 * consider using GitDbReactiveObject as a base class, which allows you to override just the hooks you want
 * to use (as it has empty implementations of all these).
 */
public
interface GitDbReactiveObject
{
    void beforeCreate(Database database, Transaction transaction);
    void beforeUpdate(Database database, Transaction transaction);
    void beforeDelete(Database database, Transaction transaction);
    void beforeTransactionCommit(Database database, Transaction transaction);
    void beforeTransactionAbort(Database database, Transaction transaction);
    <T extends GitDbObject>
    void beforeMutate(Database database, Transaction transaction, Class<T> newClass);
    void onCreated(Database database, Transaction transaction);
    void onLoaded(Database database, Transaction transaction);
    void onUpdated(Database database, Transaction transaction);
    void onDeleted(Database database, Transaction transaction);
    void onTransactionCommitted(Database database, Transaction transaction);
    void onTransactionAborted(Database database, Transaction transaction);
    <T extends GitDbObject>
    void onMutate(Database database, Transaction transaction, Class<T> newClass);
}

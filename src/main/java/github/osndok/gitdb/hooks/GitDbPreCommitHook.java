package github.osndok.gitdb.hooks;

import github.osndok.gitdb.Database;
import github.osndok.gitdb.Transaction;

public
interface GitDbPreCommitHook
{
    void onCommitInProgress(Database database, Transaction transaction, CommitInProgress commit);
}

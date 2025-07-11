package github.osndok.gitdb;

import github.osndok.gitdb.hooks.GitDbPreCommitHook;

public
interface Database
{
    Transaction startTransaction();
    void addPreCommitHook(GitDbPreCommitHook hook);
    boolean removePreCommitHook(GitDbPreCommitHook hook);
}

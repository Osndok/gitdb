package github.osndok.gitdb;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import github.osndok.gitdb.attach.Sha1AttachmentScheme;
import github.osndok.gitdb.hooks.GitDbReactiveObject;
import github.osndok.gitdb.pathing.BasicPathing;
import github.osndok.gitdb.serialization.DefaultGitDbDataFormats;
import org.buildobjects.process.ProcBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public
class SingleThreadedDatabase implements Database
{
    public final
    ObjectMapper objectMapper;

    final
    File gitRepo;

    final
    PathingScheme pathingScheme;

    final
    AttachmentScheme attachmentScheme;

    public
    SingleThreadedDatabase(final File gitRepo)
    {
        this.gitRepo = gitRepo;
        // TODO: Save/load these config from the repo, keeping compat, but using the best currently known by default.
        this.pathingScheme = new BasicPathing();
        this.objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build()
                .registerModule(new DefaultGitDbDataFormats());

        // Put each array value on its own line, for easier diffs and merges.
        var prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        objectMapper.setDefaultPrettyPrinter(prettyPrinter);

        this.attachmentScheme = new Sha1AttachmentScheme();
    }

    public
    SingleThreadedDatabase(
            final File gitRepo,
            final PathingScheme pathingScheme,
            final ObjectMapper objectMapper,
            final AttachmentScheme attachmentScheme
    )
    {
        this.gitRepo = gitRepo;
        this.pathingScheme = pathingScheme;
        this.objectMapper = objectMapper;
        this.attachmentScheme = attachmentScheme;
    }

    Transaction activeTransaction;

    @Override
    public
    Transaction startTransaction()
    {
        var retval = activeTransaction = new SingleThreadedTransaction();
        git().withArgs("stash").run();
        return retval;
    }

    /**
     * Exposed to allow easy tweaks (e.g. to the repo's user/email configuration).
     */
    public
    ProcBuilder git()
    {
        return new ProcBuilder("git").withWorkingDirectory(gitRepo);
    }

    public
    void initializeGitRepo(String message)
    {
        if (!gitRepo.isDirectory() && !gitRepo.mkdirs())
        {
            throw new RuntimeException("Unable to create directories: " + gitRepo);
        }

        git().withArgs("init").run();

        // b/c startTransaction() wants to blinkly run 'stash', which does not work on a repo w/o a commit...
        git().withArgs("commit", "--allow-empty", "--message", message).run();
    }

    public
    void selfTest()
    {
        // make sure there is a .git directory
        // make sure that 'user.name' and 'user.email' config options can be resolved
    }

    private
    class SingleThreadedTransaction implements Transaction
    {
        private final Date startTime = new Date();
        private final UUID transactionId = UUID.randomUUID();
        private final TransactionCache transactionCache = new TransactionCache();

        private
        void mustBeCurrentTransaction()
        {
            if (this != activeTransaction)
            {
                throw new IllegalStateException("This transaction has been abandoned, as another transaction has started");
            }
        }

        @Override
        public
        <T extends GitDbObject>
        T get(final Class<T> c, final UUID uuid)
        {
            mustBeCurrentTransaction();
            var existing = transactionCache.get(c, uuid);

            if (existing != null)
            {
                return existing;
            }

            var file = getFile(uuid, c);
            var object = fromJsonFile(c, file);

            if (object == null)
            {
                // todo: log/debug
                return null;
            }

            object._db_transaction_id = transactionId;
            object._db_id = uuid;

            transactionCache.put(object);

            if (object instanceof GitDbReactiveObject hook)
            {
                hook.onLoaded(SingleThreadedDatabase.this, this);
            }

            return object;
        }

        @Override
        public
        void allocateId(final GitDbObject object)
        {
            if (object._db_id != null)
            {
                throw new IllegalArgumentException("object already has already been assigned an id");
            }

            maybeAssignIds(object);
        }

        @Override
        public
        <T extends GitDbObject>
        void mutate(final GitDbObject object, final Class<T> newClass)
        {
            mustBeCurrentTransaction();

            var oldClass = object.getClass();

            var id = object._db_id;
            if (id == null)
            {
                throw new IllegalArgumentException("objects must be saved before they can be mutated");
            }

            if (object instanceof GitDbReactiveObject hook)
            {
                hook.beforeMutate(SingleThreadedDatabase.this, this, newClass);
            }

            var oldFile = getFile(id, oldClass);
            var newFile = getFile(id, newClass);
            var newParent = newFile.getParentFile();

            if (!newParent.isDirectory() && !newParent.mkdirs())
            {
                throw new RuntimeException("Unable to create directory: " + newParent);
            }

            git().withArgs("mv", oldFile.toString(), newFile.toString()).run();

            // We disown the object, so that it can't be used in other transactions, and is immediately refetchable.
            object._db_transaction_id = null;
            transactionCache.remove(object);

            if (object instanceof GitDbReactiveObject hook)
            {
                hook.onMutate(SingleThreadedDatabase.this, this, newClass);
            }
        }

        @Override
        public
        String putAttachment(final File file)
        {
            var fileId = attachmentScheme.store(gitRepo, file);
            var savedFile = attachmentScheme.locate(gitRepo, fileId);
            git().withArgs("add", savedFile.toString()).run();
            return fileId;
        }

        @Override
        public
        File getAttachment(final String fileId)
        {
            return attachmentScheme.locate(gitRepo, fileId);
        }

        @Override
        public
        Date getStartTime()
        {
            return startTime;
        }

        @Override
        public
        <T extends GitDbObject>
        Collection<UUID> listIds(final Class<T> c)
        {
            mustBeCurrentTransaction();
            var classId = pathingScheme.getClassId(gitRepo, c);
            return pathingScheme.listObjectIds(gitRepo, classId);
        }

        @Override
        public
        void save(final GitDbObject object)
        {
            var create = object._db_id == null;
            mustBeCurrentTransaction();
            maybeAssignIds(object);

            if (object instanceof GitDbReactiveObject hook)
            {
                if (create)
                {
                    hook.beforeCreate(SingleThreadedDatabase.this, this);
                }
                else
                {
                    hook.beforeUpdate(SingleThreadedDatabase.this, this);
                }
            }

            var file = pathingScheme.getObjectPath(gitRepo, object);
            writeJsonFile(object, file);
            git().withArgs("add", file.toString()).run();
            transactionCache.put(object);

            if (object instanceof GitDbReactiveObject hook)
            {
                if (create)
                {
                    hook.onCreated(SingleThreadedDatabase.this, this);
                }
                else
                {
                    hook.onUpdated(SingleThreadedDatabase.this, this);
                }
            }
        }

        @Override
        public
        void forceOverwrite(final UUID id, final GitDbObject object)
        {
            object._db_id = id;
            object._db_transaction_id = transactionId;

            if (object instanceof GitDbReactiveObject hook)
            {
                hook.beforeUpdate(SingleThreadedDatabase.this, this);
            }

            var file = pathingScheme.getObjectPath(gitRepo, object);
            writeJsonFile(object, file);
            git().withArgs("add", file.toString()).run();
            transactionCache.put(object);

            if (object instanceof GitDbReactiveObject hook)
            {
                hook.onUpdated(SingleThreadedDatabase.this, this);
            }
        }

        @Override
        public
        void delete(final GitDbObject object)
        {
            mustBeCurrentTransaction();
            maybeAssignIds(object);

            if (object instanceof GitDbReactiveObject hook)
            {
                hook.beforeDelete(SingleThreadedDatabase.this, this);
            }

            var file = pathingScheme.getObjectPath(gitRepo, object);
            git().withArgs("rm", "-f", file.toString()).run();
            transactionCache.put(object);

            if (object instanceof GitDbReactiveObject hook)
            {
                hook.onDeleted(SingleThreadedDatabase.this, this);
            }
        }

        @Override
        public
        void commit(final String formatString, Object... args)
        {
            mustBeCurrentTransaction();

            var message = String.format(formatString, args);

            for (GitDbObject value : transactionCache.values())
            {
                if (value instanceof GitDbReactiveObject hook)
                {
                    hook.beforeTransactionCommit(SingleThreadedDatabase.this, this);
                }
            }

            var date = Long.toString(startTime.getTime()/1000);

            git()
                    .withArgs("commit", "--message", message)
                    .withVar("GIT_AUTHOR_DATE", date)
                    .withVar("GIT_COMMITTER_DATE", date)
                    .run();

            // NOTE: We do not clear active transaction, so you can call commit() multiple times.

            for (GitDbObject value : transactionCache.values())
            {
                if (value instanceof GitDbReactiveObject hook)
                {
                    hook.onTransactionCommitted(SingleThreadedDatabase.this, this);
                }
            }
        }

        @Override
        public
        void abort()
        {
            // Allow for calling abort() multiple times.
            mustBeCurrentTransaction();

            for (GitDbObject value : transactionCache.values())
            {
                if (value instanceof GitDbReactiveObject hook)
                {
                    hook.beforeTransactionAbort(SingleThreadedDatabase.this, this);
                }
            }

            git().withArgs("reset", "--hard").run();
            // NOTE: We clear ourself from being the active transaction to protect objects in-memory from invalid UUIDs.
            activeTransaction = null;

            for (GitDbObject value : transactionCache.values())
            {
                if (value instanceof GitDbReactiveObject hook)
                {
                    hook.onTransactionAborted(SingleThreadedDatabase.this, this);
                }
            }
        }

        private
        void maybeAssignIds(final GitDbObject object)
        {
            if (object._db_transaction_id == null)
            {
                object._db_transaction_id = transactionId;
            }
            else
            if (object._db_transaction_id != transactionId)
            {
                throw new IllegalArgumentException("Cannot effect objects from one transaction in another"+
                                                   " (hint: multiple calls to commit() is okay)");
            }

            if (object._db_id == null)
            {
                object._db_id = UUID.randomUUID();
            }
        }

        <T extends GitDbObject>
        T fromJsonFile(final Class<T> c, final File file)
        {
            if (!file.exists())
            {
                // log/debug
                return null;
            }

            try
            {
                return objectMapper.readValue(file, c);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    <T extends GitDbObject>
    File getFile(final UUID uuid, final Class<T> c)
    {
        var classId = pathingScheme.getClassId(gitRepo, c);
        return pathingScheme.getObjectPath(gitRepo, classId, uuid);
    }

    private
    void writeJsonFile(final GitDbObject object, final File file)
    {
        var dir = file.getParentFile();

        if (!dir.isDirectory() && !dir.mkdirs())
        {
            throw new RuntimeException("Unable to create directory: " + dir);
        }

        try
        {
            objectMapper.writeValue(file, object);
            kludge_AppendTrailingNewline(file);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private
    void kludge_AppendTrailingNewline(final File file) throws IOException
    {
        try ( var out = new FileWriter(file, true))
        {
            out.append('\n');
        }
    }
}

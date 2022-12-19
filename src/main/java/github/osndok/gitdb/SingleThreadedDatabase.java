package github.osndok.gitdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.buildobjects.process.ProcBuilder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public
class SingleThreadedDatabase implements Database
{
    final
    ObjectMapper objectMapper = new ObjectMapper();

    final
    File gitRepo;

    final
    PathingScheme pathingScheme;

    public
    SingleThreadedDatabase(final File gitRepo, final PathingScheme pathingScheme)
    {
        this.gitRepo = gitRepo;
        this.pathingScheme = pathingScheme;
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    Transaction activeTransaction;

    @Override
    public
    Transaction startTransaction()
    {
        var retval = activeTransaction = new SingleThreadedTransaction();
        git().withArgs("stash");
        return retval;
    }

    ProcBuilder git()
    {
        return new ProcBuilder("git").withWorkingDirectory(gitRepo);
    }

    public
    void initializeGitRepo()
    {
        if (!gitRepo.isDirectory() && !gitRepo.mkdirs())
        {
            throw new RuntimeException("Unable to create directories: " + gitRepo);
        }

        git().withArgs("init").run();
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

            var classId = pathingScheme.getClassId(gitRepo, c);
            var file = pathingScheme.getObjectPath(gitRepo, classId, uuid);
            var object = fromJsonFile(c, file);

            if (object == null)
            {
                // todo: log/debug
                return null;
            }

            object._db_transaction_id = transactionId;
            object._db_id = uuid;

            transactionCache.put(object);

            return object;
        }

        @Override
        public
        <T extends GitDbObject>
        Iterable<UUID> list(final Class<T> c)
        {
            mustBeCurrentTransaction();
            var classId = pathingScheme.getClassId(gitRepo, c);
            return pathingScheme.listObjectIds(gitRepo, classId);
        }

        @Override
        public
        void save(final GitDbObject object)
        {
            mustBeCurrentTransaction();
            maybeAssignIds(object);
            var file = pathingScheme.getObjectPath(gitRepo, object);
            writeJsonFile(object, file);
            git().withArgs("add", file.toString()).run();
            transactionCache.put(object);
        }

        @Override
        public
        void delete(final GitDbObject object)
        {
            mustBeCurrentTransaction();
            maybeAssignIds(object);
            var file = pathingScheme.getObjectPath(gitRepo, object);
            git().withArgs("rm", "-f", file.toString()).run();
            transactionCache.put(object);
        }

        @Override
        public
        void commit(final String message)
        {
            mustBeCurrentTransaction();
            git().withArgs("commit", "--message", message).run();
            // NOTE: We do not clear active transaction, so you can call commit() multiple times.
        }

        @Override
        public
        void abort()
        {
            // Allow for calling abort() multiple times.
            mustBeCurrentTransaction();
            git().withArgs("reset", "--hard").run();
            // NOTE: We clear ourself from being the active transaction to protect objects in-memory from invalid UUIDs.
            activeTransaction = null;
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
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}

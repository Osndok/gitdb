package github.osndok.gitdb.pathing;

import github.osndok.gitdb.GitDbObject;
import github.osndok.gitdb.PathingScheme;
import github.osndok.gitdb.util.IdentifierSplitter;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import static github.osndok.gitdb.util.FileUtil.notNull;

/**
 * This pathing scheme groups multiple "facets" of a hyperobject together which is identified by a single UUID.
 * This might be counter-intuitive for most use cases, as instead of "object 123 is a User", we have "object 123
 * can be a User, but is also an X, Y, and Z". So instead of having "all your users in the Users directory", you
 * have a bunch of UUID directories, some of which might contain a "User.json" file.
 */
public
class HyperObjectPathingScheme implements PathingScheme
{
    private final
    IdentifierSplitter identifierSplitter = IdentifierSplitter.optimizedForThousands();

    @Override
    public
    File getObjectPath(final File repoDir, final String classId, final UUID uuid)
    {
        if (uuid == null)
        {
            throw new IllegalStateException("db object has no id assigned");
        }

        var splitPath = identifierSplitter.split(uuid.toString()).toString();

        var basename = String.format("%s/%s.json", splitPath, classId);
        return new File(repoDir, basename);
    }

    @Override
    public
    <T extends GitDbObject>
    String getClassId(final File gitRepo, final Class<T> c)
    {
        return c.getSimpleName();
    }

    @Override
    public
    Collection<UUID> listObjectIds(final File repoDir, final String classId)
    {
        throw new UnsupportedOperationException("unimplemented: would need to search all object directories");
    }

    @Override
    public
    Collection<String> listClassIds(final File repoDir, final UUID uuid)
    {
        var splitPath = identifierSplitter.split(uuid.toString()).toString();

        var objectDirectory = new File(repoDir, splitPath);

        var ids = new HashSet<String>();

        for (File file : notNull(objectDirectory.listFiles()))
        {
            if (file.isDirectory())
            {
                var subDirectoryOrJsonFile = file.getName();
                accumulateClassIds(ids, file, subDirectoryOrJsonFile);
            }
        }

        return ids;
    }

    private
    void accumulateClassIds(final HashSet<String> ids, final File dir, final String nameSoFar)
    {
        for (File file : notNull(dir.listFiles()))
        {
            var name = file.getName();
            if (file.isDirectory())
            {
                accumulateClassIds(ids, file, nameSoFar + name);
            }

            if (file.isFile() && name.endsWith(".json"))
            {
                // todo: test me
                var classId = name.substring(0, name.length()-".json".length());
                ids.add(classId);
            }
        }
    }

}

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
 * Goal is to use identifier splitter to make more git-update-friendly directory structures in case
 * there are a large number of objects. This pathing scheme groups objects of particular types together
 * (which is what most might expect): for example, all of your users are in the 'Users' directory.
 */
public
class ClassGroupsPathingScheme implements PathingScheme
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

        var basename = String.format("%s/%s.json", classId, splitPath);
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
    Iterable<String> listClassIds(final File repoDir)
    {
        var dirs = new HashSet<String>();
        for (File file : notNull(repoDir.listFiles()))
        {
            var name = file.getName();

            if (file.isDirectory() && name.indexOf('.') < 0)
            {
                dirs.add(name);
            }
        }

        return dirs;
    }

    @Override
    public
    Collection<UUID> listObjectIds(final File repoDir, final String classId)
    {
        var classDir = new File(repoDir, classId);

        var ids = new HashSet<UUID>();

        for (File file : notNull(classDir.listFiles()))
        {
            if (file.isDirectory())
            {
                var name = file.getName();
                accumulateObjectIds(ids, file, name);
            }
        }

        return ids;
    }

    private
    void accumulateObjectIds(final HashSet<UUID> ids, final File dir, final String nameSoFar)
    {
        for (File file : notNull(dir.listFiles()))
        {
            var name = file.getName();
            if (file.isDirectory())
            {
                accumulateObjectIds(ids, file, nameSoFar + name);
            }

            if (file.isFile() && name.endsWith(".json"))
            {
                ids.add(toUuid(nameSoFar + name));
            }
        }
    }

    private
    UUID toUuid(final String s)
    {
        var sb = new StringBuilder();
        int n = 0;
        for (char c : s.toCharArray())
        {
            // in the form 8-4-4-4-12
            if (n == 8+4+4+4+12)
            {
                break;
            }

            if (n == 8 || n == 8+4 || n == 8+4+4 || n == 8+4+4+4)
            {
                sb.append('-');
                sb.append(c);
            }
            else
            {
                sb.append(c);
            }
            n++;
        }

        var uuidString = sb.toString();
        return UUID.fromString(uuidString);
    }
}

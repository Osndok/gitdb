package github.osndok.gitdb.pathing;

import github.osndok.gitdb.GitDbObject;
import github.osndok.gitdb.PathingScheme;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import static github.osndok.gitdb.util.FileUtil.notNull;

@Deprecated
public
class StupidlySimplePathing
        implements PathingScheme
{
    @Override
    public
    File getObjectPath(final File repoDir, final String classId, final UUID uuid)
    {
        if (uuid == null)
        {
            throw new IllegalStateException("db object has no id assigned");
        }

        var basename = String.format("%s/%s", classId, uuid);
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
        var dir = new File(repoDir, classId);

        var ids = new HashSet<UUID>();

        for (File file : notNull(dir.listFiles()))
        {
            var name = file.getName();

            if (file.isFile() && name.indexOf('.') < 0)
            {
                ids.add(UUID.fromString(name));
            }
        }

        return ids;
    }
}

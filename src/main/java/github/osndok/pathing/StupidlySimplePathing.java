package github.osndok.pathing;

import github.osndok.GitDbObject;
import github.osndok.PathingScheme;

import java.io.File;
import java.util.HashSet;
import java.util.UUID;

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
    <T extends GitDbObject> String getClassId(final File gitRepo, final Class<T> c)
    {
        return c.getSimpleName();
    }

    @Override
    public
    Iterable<String> listClassIds(final File repoDir)
    {
        var dirs = new HashSet<String>();
        for (File file : repoDir.listFiles())
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
    Iterable<UUID> listObjectIds(final File repoDir, final String classId)
    {
        var dir = new File(repoDir, classId);

        var ids = new HashSet<UUID>();

        for (File file : dir.listFiles())
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

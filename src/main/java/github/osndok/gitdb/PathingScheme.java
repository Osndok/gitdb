package github.osndok.gitdb;

import java.io.File;
import java.util.UUID;

public
interface PathingScheme
{
    /**
     * @param repoDir - the root of the git repo
     * @param classId
     * @param uuid    - a uuid of a gitdb object that may or may not be present in the repo
     * @return a path where an object with that uuid would be located, if it were in the repo
     */
    File getObjectPath(File repoDir, final String classId, UUID uuid);

    <T extends GitDbObject>
    String getClassId(File gitRepo, Class<T> c);

    Iterable<String> listClassIds(File repoDir);

    Iterable<UUID> listObjectIds(File repoDir, String classId);

    default File getObjectPath(File repoDir, GitDbObject object)
    {
        var classId = getClassId(repoDir, object.getClass());
        var uuid = object._db_id();
        return getObjectPath(repoDir, classId, uuid);
    }
}

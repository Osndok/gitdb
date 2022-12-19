package github.osndok;

public
interface PathingScheme
{
    /**
     * @param object - with an _db_id
     * @return a path that the object would be located at
     */
    String getObjectPath(GitDbObject object);
}

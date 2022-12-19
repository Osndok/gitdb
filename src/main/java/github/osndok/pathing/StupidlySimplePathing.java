package github.osndok.pathing;

import github.osndok.GitDbObject;
import github.osndok.PathingScheme;

public
class StupidlySimplePathing
        implements PathingScheme
{
    @Override
    public
    String getObjectPath(final GitDbObject object)
    {
        var name = object.getClass().getSimpleName();
        var uuid = object._db_id();

        if (uuid == null)
        {
            throw new IllegalStateException("db object has no id assigned");
        }

        return String.format("%s/%s", name, uuid);
    }
}

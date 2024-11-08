package github.osndok.gitdb.hyper;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final
class Directed
        implements Tuple
{
    // For a directed tuple, the order matters!
    public final List<UUID> orderedUuids;

    /**
     * @param ordered A list of UUIDs (in object, string, or facet-Class form)
     *               in tuple-order (e.g. "A" comes first in: A -> B).
     */
    public
    Directed(final UUID... ordered)
    {
        this.orderedUuids = Arrays.asList(ordered);
    }

    /**
     * @param ordered A list of UUIDs (in object, string, or facet-Class form)
     *               in tuple-order (e.g. "A" comes first in: A -> B).
     */
    public
    Directed(final List<UUID> ordered)
    {
        this.orderedUuids = ordered;
    }
}

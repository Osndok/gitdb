package github.osndok.gitdb.hyper;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

public final
class Undirected
        implements Tuple
{
    // For a directed tuple, the order does not matter, so let's remember to sort them.
    public final Collection<UUID> needsSorting;

    /**
     * @param needsSorting A collection of UUIDs (in object, string, or facet-Class form).
     */
    public
    Undirected(final UUID... needsSorting)
    {
        this.needsSorting = Arrays.asList(needsSorting);
    }

    /**
     * @param needsSorting A collection of UUIDs (in object, string, or facet-Class form).
     */
    public
    Undirected(final Collection<UUID> needsSorting)
    {
        this.needsSorting = needsSorting;
    }
}

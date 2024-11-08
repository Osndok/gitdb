package github.osndok.gitdb.hyper;

import java.util.UUID;

public
interface HyperRepo
{
    FacetClassIdentifier getFacetClassIdentifier();

    UUID allocateUuid();
    UUID getOrCreateTupleId(Tuple tuple);

    HyperObject get(UUID uuid);

    Iterable<UUID> listFacets(UUID objectId);
    Iterable<UUID> listObjectsHavingFacet(UUID facetClassId);

    /**
     * Get a particular facet of an object. Useful for simplifying code when there are some inherent expectations
     * or assumptions regarding the data/structure for a given object/id.
     *
     * @param objectId
     * @param t
     * @return
     * @param <T>
     */
    default
    <T>
    T getFacet(UUID objectId, Class<T> t)
    {
        var facetClassId = getFacetClassIdentifier().getUuidForClass(t);
        return getFacet(objectId, facetClassId, t);
    }

    <T>
    T getFacet(UUID objectId, UUID facetClassId, Class<T> t);

    default
    void saveFacet(UUID objectId, Object data)
    {
        var facetClassId = getFacetClassIdentifier().getUuidForClass(data.getClass());
        saveFacet(objectId, facetClassId, data);
    }

    void saveFacet(UUID objectId, UUID facetClassId, Object data);
}

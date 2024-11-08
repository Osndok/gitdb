package github.osndok.gitdb.hyper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public
interface HyperObject
{
    UUID getUuid();
    HyperRepo getHyperRepo();

    default
    <T>
    T getFacet(Class<T> t)
    {
        return getHyperRepo().getFacet(getUuid(), t);
    }

    default
    void saveFacet(Object data)
    {
        getHyperRepo().saveFacet(getUuid(), data);
    }

    default
    void saveFacet(UUID facetClassId, Object data)
    {
        getHyperRepo().saveFacet(getUuid(), facetClassId, data);
    }

    default
    Iterable<UUID> listFacets()
    {
        return getHyperRepo().listFacets(getUuid());
    }

    default
    UUID getCommonLinkageId(Class<?> t, Enum<?>... qualifiers)
    {
        var identifier = getHyperRepo().getFacetClassIdentifier();

        var thisId = getUuid();
        var listId = identifier.getUuidForClass(List.class);
        var facetClassId = identifier.getUuidForClass(t);

        var list = new ArrayList<UUID>();
        list.add(thisId);
        list.add(listId);
        list.add(facetClassId);

        for (Enum<?> qualifier : qualifiers)
        {
            var qualifierId = identifier.getUuidForEnum(qualifier);
            list.add(qualifierId);
        }

        return getHyperRepo().getOrCreateTupleId(new Undirected(list));
    }

    /**
     * A convenience method for class-simple related object types.
     * Why multiple? Because if it can always be zero, and this way
     * we reduce code and avoid nulls. Just use the only/single
     * method if you know there is only one.
     */
    default
    List<UUID> getConnecting(Class<?> t, Enum<?>... qualifiers)
    {
        var linkageId = getCommonLinkageId(t, qualifiers);
        // It will probably deserialize as a list of strings, so convert them to UUIDs.
        var stringFormUuids = (List<String>)getHyperRepo().getFacet(linkageId, List.class);
        return stringFormUuids.stream().map(UUID::fromString).collect(Collectors.toUnmodifiableList());
    }

    // TODO: Instead of writing add/remove/move here, we should probably make a hyperobject-facet-backed list?
    @Deprecated
    default
    <T>
    void addConnecting(Class<T> tClass, UUID itemToAdd, Enum<?>... qualifiers)
    {
        var stringifiedUuidToAdd = itemToAdd.toString();
        var linkageId = getCommonLinkageId(tClass, qualifiers);
        // It will probably deserialize as a list of strings, so convert them to UUIDs.
        var stringFormUuids = getHyperRepo().getFacet(linkageId, List.class);
        stringFormUuids.add(stringifiedUuidToAdd);
        getHyperRepo().saveFacet(linkageId, stringFormUuids);
    }

    // TODO: Instead of writing add/remove/move here, we should probably make a hyperobject-facet-backed list?
    @Deprecated
    default
    <T>
    void removeConnecting(Class<T> tClass, UUID itemToRemove, Enum<?>... qualifiers)
    {
        var stringifiedUuidToRemove = itemToRemove.toString();
        var linkageId = getCommonLinkageId(tClass, qualifiers);
        // It will probably deserialize as a list of strings, so convert them to UUIDs.
        var stringFormUuids = getHyperRepo().getFacet(linkageId, List.class);
        if (!stringFormUuids.remove(stringifiedUuidToRemove))
        {
            throw new IllegalStateException("UUID not present to remove: " + stringifiedUuidToRemove);
        }
        getHyperRepo().saveFacet(linkageId, stringFormUuids);
    }

}

package github.osndok.gitdb.hyper;

import java.util.Map;
import java.util.UUID;

/**
 * Converts between UUIDs and facet classes (aka Schemas).
 */
public
interface FacetClassIdentifier
{
    // Used to 'prime' the system to avoid reflection at runtime.
    // ... if the given class is an ENUM, it will observe the enum constants too.
    void observeClass(Class<?> aClass);

    UUID getUuidForClass(Class<?> aClass);
    Class<?> getClassForUuid(UUID uuid);
    Map<Class<?>, UUID> getObservedUuidsByClass();
    Map<UUID, Class<?>> getObservedClassesByUuid();

    UUID getUuidForEnum(Enum<?> aEnum);
    Map<Class<?>, UUID> getObservedUuidsByEnum();
    Map<UUID, Class<?>> getObservedEnumsByUuid();
}

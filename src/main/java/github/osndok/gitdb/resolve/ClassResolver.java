package github.osndok.gitdb.resolve;

import github.osndok.gitdb.GitDbObject;

/**
 * Ordinarily, the database system has no indication of what object types it might be called upon
 * to load and store. Therefore, this interface is intended to be used in future hypothetical
 * situations where the database subsystem needs to operate on class types dynamically, but only
 * has a list of class identifiers from the pathing schema to operate from.
 */
public
interface ClassResolver
{
    /**
     * @param classIdentifier - The simple name (or full/canonical name) of an entity; depending on the pathing scheme.
     * @return The class that corresponds to the given classIdentifier.
     * @param <T> constraining the class to database objects
     */
    <T extends GitDbObject>
    Class<T> getDatabaseModelClass(String classIdentifier);
}

package github.osndok.gitdb.resolve;

import github.osndok.gitdb.GitDbObject;

/**
 * Useful when all the data model objects are in one package.
 * TODO: Completely untested.
 */
public
class SinglePackageClassResolver implements ClassResolver
{

    private final Package _package;
    private final ClassLoader classLoader;
    private final Module module;

    SinglePackageClassResolver(Class<? extends GitDbObject> example)
    {
        this._package = example.getPackage();
        this.classLoader = example.getClassLoader();
        this.module = example.getModule();
    }

    @Override
    public
    <T extends GitDbObject>
    Class<T> getDatabaseModelClass(final String classIdentifier)
    {
        var className = String.format("%s.%s", _package.getName(), classIdentifier);
        try
        {
            return (Class<T>) classLoader.loadClass(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }
}

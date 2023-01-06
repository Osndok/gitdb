package github.osndok.gitdb.attach;

public
enum AttachmentSchemeId
{
    POLY,
    DROP,
    SHA1,
    SHA224,
    ;

    public
    String getPrettyIdentifier()
    {
        return name().toLowerCase();
    }
}

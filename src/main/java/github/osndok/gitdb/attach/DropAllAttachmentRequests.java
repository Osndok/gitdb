package github.osndok.gitdb.attach;

import github.osndok.gitdb.AttachmentScheme;

import java.io.File;

public
class DropAllAttachmentRequests
       implements AttachmentScheme
{
    @Override
    public
    AttachmentSchemeId getId()
    {
        return AttachmentSchemeId.DROP;
    }

    @Override
    public
    String store(final File gitRepo, final File file)
    {
        return null;
    }

    @Override
    public
    File locate(final File gitRepo, final String fileId)
    {
        return null;
    }
}

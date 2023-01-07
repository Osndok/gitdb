package github.osndok.gitdb.attach;

import github.osndok.gitdb.AttachmentScheme;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public
class PolyAttachmentScheme implements AttachmentScheme
{
    private final
    AttachmentScheme preferredScheme;

    private final
    Map<String,AttachmentScheme> schemesByUriPrefix = new HashMap<>();

    public
    PolyAttachmentScheme(final AttachmentScheme... schemes)
    {
        this.preferredScheme = schemes[0];

        for (AttachmentScheme scheme : schemes)
        {
            schemesByUriPrefix.put(getUriPrefix(scheme), scheme);
        }
    }

    public static
    String getUriPrefix(final AttachmentScheme scheme)
    {
        return scheme.getId().getPrettyIdentifier() + ":";
    }

    @Override
    public
    AttachmentSchemeId getId()
    {
        return AttachmentSchemeId.POLY;
    }

    @Override
    public
    String store(final File gitRepo, final File file, final String fileExtension)
    {
        var scheme = preferredScheme;
        var result = scheme.store(gitRepo, file, fileExtension);
        var expectedPrefix = getUriPrefix(scheme);
        if (result != null && !result.startsWith(expectedPrefix))
        {
            throw new RuntimeException(scheme + " did not produce a valid uri");
        }
        return null;
    }

    @Override
    public
    File locate(final File gitRepo, final String fileId)
    {
        int colon = fileId.indexOf(':');
        if (colon <= 0)
        {
            throw new IllegalArgumentException(fileId);
        }
        var prefix = fileId.substring(0, colon + 1);
        var scheme = schemesByUriPrefix.get(prefix);
        if (scheme == null)
        {
            throw new IllegalArgumentException("Unsupported attachment scheme: '" + prefix + "'");
        }
        return scheme.locate(gitRepo, fileId);
    }
}

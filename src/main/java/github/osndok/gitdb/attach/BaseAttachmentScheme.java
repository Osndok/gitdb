package github.osndok.gitdb.attach;

import github.osndok.gitdb.AttachmentScheme;
import github.osndok.gitdb.util.IdentifierSplitter;
import org.buildobjects.process.ProcBuilder;

import java.io.File;

public abstract
class BaseAttachmentScheme implements AttachmentScheme
{
    abstract
    String hashFileContents(File file);

    final
    IdentifierSplitter identifierSplitter = IdentifierSplitter.optimizedForThousands();

    @Override
    public
    String store(final File gitRepo, final File externalFile)
    {
        var scheme = getId();
        var hash = hashFileContents(externalFile);
        var fileId = scheme.getPrettyIdentifier() + ":" + hash;
        var internalFile = locate(gitRepo, fileId);
        var parent = internalFile.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs())
        {
            throw new RuntimeException("Unable to create directories: "+parent);
        }
        new ProcBuilder("cp", externalFile.toString(), internalFile.toString()).run();
        new ProcBuilder("chmod", "400", internalFile.toString()).run();
        return fileId;
    }

    // This basic implementation basically just translates the colon of the URI into a '/'.
    @Override
    public
    File locate(final File gitRepo, final String fileId)
    {
        int colon = fileId.indexOf(':');
        var beforeColon = fileId.substring(0, colon);
        var afterColon = fileId.substring(colon + 1);
        var afterColonSplit = identifierSplitter.split(afterColon).toString();
        var relativePath = String.format("%s/%s", beforeColon, afterColonSplit);
        return new File(gitRepo, relativePath);
    }
}

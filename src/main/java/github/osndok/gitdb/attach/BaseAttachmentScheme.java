package github.osndok.gitdb.attach;

import github.osndok.gitdb.AttachmentScheme;
import github.osndok.gitdb.util.FileUtil;
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
        var extension = FileUtil.getExtension(externalFile, "");
        var fileId = String.format("%s:%s:%s", scheme.getPrettyIdentifier(), extension, hash);
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
        var bits = fileId.split(":");
        var schemeName = bits[0];
        var fileExtension = bits[1];
        var hasFileExtension = !fileExtension.isEmpty();
        var hash = bits[2];
        var splitHash = identifierSplitter.split(hash).toString();
        var relativePath = hasFileExtension
                ? String.format("%s/%s.%s", schemeName, splitHash, fileExtension)
                : String.format("%s/%s", schemeName, splitHash);
        return new File(gitRepo, relativePath);
    }
}

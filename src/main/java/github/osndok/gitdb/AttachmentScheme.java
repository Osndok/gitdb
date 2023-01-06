package github.osndok.gitdb;

import github.osndok.gitdb.attach.AttachmentSchemeId;

import java.io.File;

public
interface AttachmentScheme
{
    AttachmentSchemeId getId();

    /**
     * @param gitRepo - the repo into which the file should be stored
     * @param file - the file (outside of the repo) which should be embedded into the repo
     * @return a fully qualified attachment uri that can be used to locate & read the file later
     */
    String store(File gitRepo, File file);

    /**
     * @param gitRepo - the repo into which the attachment was previously stored
     * @param fileId - the fully qualified attachment uri that was produced by this scheme
     * @return a read-only file (which may be inside of the repo) corresponding to that id, or null if it does not exist
     */
    File locate(File gitRepo, String fileId);
}

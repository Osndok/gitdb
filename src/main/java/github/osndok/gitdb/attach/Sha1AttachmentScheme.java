package github.osndok.gitdb.attach;

import github.osndok.gitdb.util.Hexadecimal;

import java.io.*;
import java.security.MessageDigest;

public
class Sha1AttachmentScheme extends BaseAttachmentScheme
{
    @Override
    public
    AttachmentSchemeId getId()
    {
        return AttachmentSchemeId.SHA1;
    }

    @Override
    String hashFileContents(final File file)
    {
        try
        {
            var sha1 = MessageDigest.getInstance("SHA-1");
            try (var input = new FileInputStream(file))
            {

                byte[] buffer = new byte[8192];
                int len = input.read(buffer);

                while (len != -1)
                {
                    sha1.update(buffer, 0, len);
                    len = input.read(buffer);
                }

                return Hexadecimal.stringFromBytes(sha1.digest());
            }
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}

package github.osndok.gitdb.util;

import java.io.File;

public
class FileUtil
{
    public static
    String getExtension(File file)
    {
        var name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf <= 1) {
            return null;
        }
        return name.substring(lastIndexOf + 1);
    }
}

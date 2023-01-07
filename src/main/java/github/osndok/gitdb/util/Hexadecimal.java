package github.osndok.gitdb.util;

public
class Hexadecimal
{
    public static
    String stringFromBytes(byte... bytes)
    {
        var sb = new StringBuilder();

        for (byte b : bytes)
        {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}

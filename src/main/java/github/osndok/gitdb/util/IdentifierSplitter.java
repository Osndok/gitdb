package github.osndok.gitdb.util;

import java.util.UUID;

public
class IdentifierSplitter
{
    final int numGroups;
    final int groupSize;
    final char groupSeparator;

    IdentifierSplitter(final int numGroups, final int groupSize, final char groupSeparator)
    {
        this.numGroups = numGroups;
        this.groupSize = groupSize;
        this.groupSeparator = groupSeparator;
    }

    public static
    IdentifierSplitter optimizedForThousands()
    {
        return new IdentifierSplitter(1, 2, '/');
    }

    public static
    IdentifierSplitter optimizedForMillions()
    {
        return new IdentifierSplitter(2, 2, '/');
    }

    public static
    UUID toUuid(final String s)
    {
        var sb = new StringBuilder();
        int n = 0;
        for (char c : s.toCharArray())
        {
            // in the form 8-4-4-4-12
            if (n == 8+4+4+4+12)
            {
                break;
            }

            if (n == 8 || n == 8+4 || n == 8+4+4 || n == 8+4+4+4)
            {
                sb.append('-');
                sb.append(c);
            }
            else
            {
                sb.append(c);
            }
            n++;
        }

        var uuidString = sb.toString();
        return UUID.fromString(uuidString);
    }

    public
    StringBuilder split(String input)
    {
        int numInGroup = 0;
        int numGroups = 0;
        boolean complete = false;

        var sb = new StringBuilder();

        for (char c : input.toCharArray())
        {
            if (ignoredCharacter(c))
            {
                continue;
            }

            sb.append(c);

            if (complete)
            {
                continue;
            }

            numInGroup++;
            if (numInGroup >= groupSize)
            {
                sb.append(groupSeparator);
                numInGroup = 0;
                numGroups++;
                if (numGroups >= this.numGroups)
                {
                    complete = true;
                }
            }
        }

        return sb;
    }

    boolean ignoredCharacter(final char c)
    {
        return c == '-';
    }
}

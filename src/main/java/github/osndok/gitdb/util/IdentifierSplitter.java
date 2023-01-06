package github.osndok.gitdb.util;

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

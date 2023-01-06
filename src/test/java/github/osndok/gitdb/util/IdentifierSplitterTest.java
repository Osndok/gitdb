package github.osndok.gitdb.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class IdentifierSplitterTest
{
    private
    IdentifierSplitter underTest;

    @Test
    void split_thousands()
    {
        underTest = IdentifierSplitter.optimizedForThousands();
        _case("1707f145-2ee5-4139-9564-53dfb46582cf", "17/07f1452ee54139956453dfb46582cf");
        _case("6dbb1ef5-6e47-40fe-83f5-e056b31be5cc", "6d/bb1ef56e4740fe83f5e056b31be5cc");
        _case("f0898ce3-d19a-4265-ae48-4ba10e7b553b", "f0/898ce3d19a4265ae484ba10e7b553b");
        _case("6e13c8be-dd84-4f1b-a08b-8753ab02dce0", "6e/13c8bedd844f1ba08b8753ab02dce0");
        _case("1289d186ecce4a6d182e1bd451d08b2291aae1e0", "12/89d186ecce4a6d182e1bd451d08b2291aae1e0");
        _case("9b00aea4efa334a6019c1967e3720df60aa830e8", "9b/00aea4efa334a6019c1967e3720df60aa830e8");
        _case("6754f794e649e4073df1ccf9795bee9c78e69332", "67/54f794e649e4073df1ccf9795bee9c78e69332");
        _case("276d0aa44d950bfa2c42e53a47369777db1af163", "27/6d0aa44d950bfa2c42e53a47369777db1af163");
    }

    @Test
    void split_millions()
    {
        underTest = IdentifierSplitter.optimizedForMillions();
        _case("1707f145-2ee5-4139-9564-53dfb46582cf", "17/07/f1452ee54139956453dfb46582cf");
        _case("6dbb1ef5-6e47-40fe-83f5-e056b31be5cc", "6d/bb/1ef56e4740fe83f5e056b31be5cc");
        _case("f0898ce3-d19a-4265-ae48-4ba10e7b553b", "f0/89/8ce3d19a4265ae484ba10e7b553b");
        _case("6e13c8be-dd84-4f1b-a08b-8753ab02dce0", "6e/13/c8bedd844f1ba08b8753ab02dce0");
        _case("1289d186ecce4a6d182e1bd451d08b2291aae1e0", "12/89/d186ecce4a6d182e1bd451d08b2291aae1e0");
        _case("9b00aea4efa334a6019c1967e3720df60aa830e8", "9b/00/aea4efa334a6019c1967e3720df60aa830e8");
        _case("6754f794e649e4073df1ccf9795bee9c78e69332", "67/54/f794e649e4073df1ccf9795bee9c78e69332");
        _case("276d0aa44d950bfa2c42e53a47369777db1af163", "27/6d/0aa44d950bfa2c42e53a47369777db1af163");
    }

    private
    void _case(final String input, final String expectedOutput)
    {
        var output = underTest.split(input).toString();
        assertThat(output).isEqualTo(expectedOutput);
    }
}

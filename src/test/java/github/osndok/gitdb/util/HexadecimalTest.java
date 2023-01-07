package github.osndok.gitdb.util;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

class HexadecimalTest
{
    @Test
    void stringFromBytes()
    {
        assertThat(bs(0x00000000)).isEqualTo("00000000");
        assertThat(bs(0x00010203)).isEqualTo("00010203");
        assertThat(bs(0x01020304)).isEqualTo("01020304");
        assertThat(bs(0x12345678)).isEqualTo("12345678");
        assertThat(bs(0x9ABCDEF0)).isEqualTo("9abcdef0");
        assertThat(bs(0xFFFFFFFF)).isEqualTo("ffffffff");
    }

    private
    String bs(final int i)
    {
        var bytes = ByteBuffer.allocate(4).putInt(i).array();
        return Hexadecimal.stringFromBytes(bytes);
    }
}

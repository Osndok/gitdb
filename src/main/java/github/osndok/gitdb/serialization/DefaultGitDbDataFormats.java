package github.osndok.gitdb.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Duration;

public
class DefaultGitDbDataFormats
    extends SimpleModule
{
    public
    DefaultGitDbDataFormats()
    {
        addSerializer(Duration.class, new StdSerializer<>(Duration.class)
        {
            @Override
            public
            void serialize(
                    final Duration duration,
                    final JsonGenerator jsonGenerator,
                    final SerializerProvider serializerProvider
            ) throws IOException
            {
                if (duration == null)
                {
                    jsonGenerator.writeNull();
                }
                else
                {
                    jsonGenerator.writeString(duration.toString());
                }
            }
        });

        addDeserializer(Duration.class, new StdDeserializer<>(Duration.class)
        {
            @Override
            public
            Duration deserialize(
                    final JsonParser jsonParser,
                    final DeserializationContext deserializationContext
            ) throws
              IOException,
              JacksonException
            {
                var s = jsonParser.getValueAsString();
                if (s == null)
                {
                    return null;
                }
                else
                {
                    return Duration.parse(s);
                }
            }
        });
    }

}

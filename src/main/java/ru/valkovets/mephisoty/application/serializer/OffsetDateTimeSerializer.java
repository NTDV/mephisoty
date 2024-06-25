package ru.valkovets.mephisoty.application.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeSerializer extends JsonSerializer<OffsetDateTime> {
private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss XXX");

@Override
public void serialize(final OffsetDateTime value, final JsonGenerator jsonGenerator,
                      final SerializerProvider serializerProvider) throws IOException {
    if (value == null) throw new IOException("OffsetDateTime argument is null.");
    jsonGenerator.writeString(DATE_TIME_FORMATTER.format(value));
}

}

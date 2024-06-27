package ru.valkovets.mephisoty.application.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {
private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss XXX");

@Override
public OffsetDateTime deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext)
throws IOException {
    final String dateAsString = jsonParser.getText();
    if (dateAsString == null) throw new IOException("OffsetDateTime argument is null.");
    return OffsetDateTime.parse(dateAsString, DATE_TIME_FORMATTER);
}
}


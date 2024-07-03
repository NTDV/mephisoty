package ru.valkovets.mephisoty.api.lazydata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTableFilterMetaData;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTableOperatorFilterMetaData;

import java.io.IOException;

public class DataTableFilterMetaDeserializer extends JsonDeserializer<Object> {
    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        final ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        final ObjectNode root = mapper.readTree(jp);

        final Class<? extends Object> instanceClass;
        if(root.isValueNode()) instanceClass = String.class;
        else if (root.has("operator")) instanceClass = DataTableOperatorFilterMetaData.class;
        else if (root.has("value")) instanceClass = DataTableFilterMetaData.class;
        else return null;

        return mapper.treeToValue(root, instanceClass);
    }
}

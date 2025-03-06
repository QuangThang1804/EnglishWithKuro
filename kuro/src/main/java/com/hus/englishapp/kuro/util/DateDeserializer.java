package com.hus.englishapp.kuro.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

public class DateDeserializer extends StdDeserializer<Date> {

    private static final String[] DATE_FORMATS = new String[]{
            "dd/MM/yyyy",
            "dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm",
            "dd/MM/yyyy HH",
            "dd-MM-yyyy",
            "dd-MM-yyyy HH:mm:ss",
            "dd-MM-yyyy HH:mm",
            "dd-MM-yyyy HH"
    };

    public DateDeserializer() {
        this(null);
    }

    public DateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JacksonException {
        JsonNode node = parser.getCodec().readTree(parser);
        final String value = node.textValue();

        try {
            return DateUtils.parseDateStrictly(value, DATE_FORMATS);
        } catch (ParseException e) {
            throw new JsonParseException(parser, "Error occurs when parsing date: " + value + ". Supported formats: " + Arrays.toString(DATE_FORMATS));
        }
    }
}

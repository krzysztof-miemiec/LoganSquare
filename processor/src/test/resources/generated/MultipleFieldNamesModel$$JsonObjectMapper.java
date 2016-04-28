package com.bluelinelabs.logansquare.processor;

import com.bluelinelabs.logansquare.JsonMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;

@SuppressWarnings("unsafe,unchecked")
public final class MultipleFieldNamesModel$$JsonObjectMapper extends JsonMapper<MultipleFieldNamesModel> {
    @Override
    public MultipleFieldNamesModel parse(JsonParser jsonParser) throws IOException {
        MultipleFieldNamesModel instance = new MultipleFieldNamesModel();
        if (jsonParser.getCurrentToken() == null) {
            jsonParser.nextToken();
        }
        if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
            jsonParser.skipChildren();
            return null;
        }
        jsonParser.
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();
            jsonParser.nextToken();
            parseField(instance, fieldName, jsonParser);
            jsonParser.skipChildren();
        }
        return instance;
    }

    @Override
    public void parseField(MultipleFieldNamesModel instance, String fieldName, JsonParser jsonParser) throws IOException {
        if ("possible_double_name_1".equals(fieldName) || "possible_double_name_2".equals(fieldName)) {
            instance.testDouble = jsonParser.getValueAsDouble();
        } else if ("possible_float_name_1".equals(fieldName) || "possible_float_name_2".equals(fieldName)){
            instance.testFloat = (float)jsonParser.getValueAsDouble();
        } else if ("possible_int_name_1".equals(fieldName) || "possible_int_name_2".equals(fieldName)){
            instance.testInt = jsonParser.getValueAsInt();
        } else if ("possible_long_name_1".equals(fieldName) || "possible_long_name_2".equals(fieldName)){
            instance.testLong = jsonParser.getValueAsLong();
        } else if ("possible_string_name_1".equals(fieldName) || "possible_string_name_2".equals(fieldName)){
            instance.testString = jsonParser.getValueAsString(null);
        }
    }

    @Override
    public void serialize(MultipleFieldNamesModel object, JsonGenerator jsonGenerator, boolean writeStartAndEnd) throws IOException {
        if (writeStartAndEnd) {
            jsonGenerator.writeStartObject();
        }
        jsonGenerator.writeNumberField("possible_double_name_1", object.testDouble);
        jsonGenerator.writeNumberField("possible_float_name_1", object.testFloat);
        jsonGenerator.writeNumberField("possible_int_name_1", object.testInt);
        jsonGenerator.writeNumberField("possible_long_name_1", object.testLong);
        if (object.testString != null) {
            jsonGenerator.writeStringField("possible_string_name_1", object.testString);
        }
        if (writeStartAndEnd) {
            jsonGenerator.writeEndObject();
        }
    }
}
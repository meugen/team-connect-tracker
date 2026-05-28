package com.ua.teamconnect.tracker.client;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ua.teamconnect.tracker.model.entity.Holiday;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class HolidaysDeserializer extends JsonDeserializer<HolidaysDeserializer.HolidaysList> {

    @Override
    public HolidaysList deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
        var node = parser.getCodec().readTree(parser);
        var responseNode = node.get("response");
        if (responseNode == null || !responseNode.isObject())
            throw new JsonParseException(parser, "Expected 'response' field to be an object");
        var holidaysNode = responseNode.get("holidays");
        if (holidaysNode == null || !holidaysNode.isArray())
            throw new JsonParseException(parser, "Expected 'holidays' field to be an array");
        var holidays = ((ArrayNode) holidaysNode).elements();
        return new HolidaysList(parse(holidays));
    }

    private Set<Holiday> parse(Iterator<JsonNode> iterator) {
        var result = new TreeSet<>(Comparator.comparing(Holiday::getId));
        while (iterator.hasNext()) {
            var node = iterator.next();
            var name = node.get("name").asText("");
            var description = node.get("description").asText("");
            var datetime = node.get("date").get("iso").asText("");
            var date = datetime.split("T")[0];
            var urlIdNode = node.get("urlid");
            var urlId = urlIdNode.isNull()
                ? Integer.toString(Objects.hash(name, description, date))
                : urlIdNode.asText() + "-" + date; // The same urlid for holidays in different years

            var holiday = new Holiday();
            holiday.setId(urlId);
            holiday.setName(name);
            holiday.setDescription(description);
            holiday.setDate(LocalDate.parse(date));
            result.add(holiday);
        }
        return result;
    }

    public record HolidaysList(Set<Holiday> holidays) {

        static final HolidaysList EMPTY = new HolidaysList(Set.of());
    }
}

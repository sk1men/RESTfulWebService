package items.gson;

import com.google.gson.*;
import items.model.Item;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ItemConverter implements JsonSerializer<Item>, JsonDeserializer<Item> {
    private static final String ITEM_FIELD = "item";
    private static final String ID_FIELD = "id";
    private static final String TIMESTAMP_FIELD = "timestamp";

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject itemHolder = json.getAsJsonObject();
            JsonObject itemElement = itemHolder.get(ITEM_FIELD).getAsJsonObject();

            long id = itemElement.get(ID_FIELD).getAsLong();
            String timestampSerializedValue = itemElement.get(TIMESTAMP_FIELD).getAsString();
            ZonedDateTime timestamp = TIMESTAMP_FORMATTER.parse(timestampSerializedValue, ZonedDateTime::from);

            return new Item(id, timestamp);
        } catch (Exception e) {
            throw new JsonParseException("Can't deserialize " + json + " to Item class");
        }
    }

    @Override
    public JsonElement serialize(Item item, Type typeOfSrc, JsonSerializationContext context) {
        try {
            JsonObject itemHolderElement = new JsonObject();
            JsonObject itemElement = new JsonObject();
            itemElement.add(ID_FIELD, new JsonPrimitive(item.getId()));
            itemElement.add(TIMESTAMP_FIELD, new JsonPrimitive(TIMESTAMP_FORMATTER.format(item.getTimestamp())));
            itemHolderElement.add(ITEM_FIELD, itemElement);
            return itemHolderElement;
        } catch (Exception e) {
            throw new JsonParseException("Can't serialize " + item, e);
        }
    }
}

package items.model;

import java.time.ZonedDateTime;
import java.util.Objects;


public class Item {

    private final long id;
    private final ZonedDateTime timestamp;

    public Item(long id, ZonedDateTime timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id &&
                timestamp.equals(item.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                '}';
    }
}


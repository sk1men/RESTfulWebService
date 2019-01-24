package items.dao;

import items.model.Item;

import java.time.Duration;
import java.util.List;

public interface ItemDao {
    boolean contains(Item item);

    Item save(Item item);

    void deleteItemsLessThan(Duration duration);

    void deleteOldItemsUntilSizeWillBe(Integer size);

    boolean delete(Item item);

    List<Item> getLastItemsForDuration(Duration duration);

    List<Item> getLastItems(Integer size);
}

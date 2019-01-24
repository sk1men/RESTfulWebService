package items.service;

import items.model.Item;

import java.util.List;

public interface ItemService {

    void save(Item item);

    List<Item> getLastItems();

}

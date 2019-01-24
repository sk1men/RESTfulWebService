package items.dao;

import com.google.common.flogger.FluentLogger;
import items.model.Item;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Repository
public class MemoryDao implements ItemDao {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private NavigableMap<ZonedDateTime, Set<Item>> itemMap = new ConcurrentSkipListMap<>();


    @Override
    public boolean contains(Item item) {
        Set<Item> itemsSet = itemMap.get(item.getTimestamp());
        if (Objects.nonNull(itemsSet) && itemsSet.contains(item)) {
            logger.atInfo().log("There is " + item.getId() + " in  itemMap. ");
            return true;
        } else {
            logger.atInfo().log("itemMap does not contain " + item + ". ");
            return false;
        }
    }

    @Override
    public Item save(Item item) {
        Set<Item> itemsSet = itemMap.get(item.getTimestamp());
        if (Objects.isNull(itemsSet)) {
            itemsSet = new CopyOnWriteArraySet<>();
            itemMap.put(item.getTimestamp(), itemsSet);
        }
        boolean result = itemsSet.add(item);
        if (result) {
            logger.atInfo().log("The item %s was successfully saved.", item);
        } else {
            logger.atInfo().log("The item %s has already existed.", item);
        }
        return item;
    }

    @Override
    public void deleteItemsLessThan(Duration duration) {
        if (itemMap.isEmpty()) {
            logger.atInfo().log("ItemMap is empty. ");
            return;
        }
        ZonedDateTime lowestTimeToKeep = ZonedDateTime.now().minusNanos(duration.toNanos());
        ZonedDateTime theOldestKey = itemMap.firstKey();

        while (!itemMap.isEmpty() && theOldestKey.compareTo(lowestTimeToKeep) < 0) {
            itemMap.remove(theOldestKey);
            logger.atInfo().log("All items for key %s were deleted.", theOldestKey);
            theOldestKey = itemMap.firstKey();
        }
    }

    @Override
    public void deleteOldItemsUntilSizeWillBe(Integer keepSize) {
        if (itemMap.isEmpty()) {
            logger.atInfo().log("ItemMap is empty. ");
            return;
        }

        int keepCounter = 0;
        for (Map.Entry<ZonedDateTime, Set<Item>> entry : itemMap.descendingMap().entrySet()) {
            Set<Item> itemSet = entry.getValue();
            if (keepCounter > keepSize) {
                itemMap.remove(entry.getKey());
                logger.atInfo().log("All items for key %s were deleted.", entry.getKey());
            } else if (keepCounter + entry.getValue().size() < keepSize) {
                keepCounter += entry.getValue().size();
            } else {
                for (Item item : itemSet) {
                    keepCounter++;
                    if (keepCounter > keepSize) {
                        delete(item);
                    }
                }
            }
        }
    }


    @Override
    public boolean delete(Item item) {
        boolean result = false;
        Set<Item> ItemsSet = itemMap.get(item.getTimestamp());
        if (Objects.nonNull(ItemsSet)) {
            result = ItemsSet.remove(item);
            if (result) {
                logger.atInfo().log("The item %s was successfully removed", item);
            } else {
                logger.atInfo().log("Can't delete item %s because it was not found", item);
            }
        }
        return result;
    }

    @Override
    public List<Item> getLastItemsForDuration(Duration duration) {
        ZonedDateTime startZoneDateTime = ZonedDateTime.now().minusNanos(duration.toNanos());
        NavigableMap<ZonedDateTime, Set<Item>> tailMap = itemMap.tailMap(startZoneDateTime, true);

        List<Item> result = new LinkedList<>();
        for (Map.Entry<ZonedDateTime, Set<Item>> entry : tailMap.descendingMap().entrySet()) {
            Set<Item> items = entry.getValue();
            result.addAll(items);
        }

        return result;
    }

    @Override
    public List<Item> getLastItems(Integer desiredSize) {
        List<Item> result = new LinkedList<>();
        for (Map.Entry<ZonedDateTime, Set<Item>> entry : itemMap.descendingMap().entrySet()) {
            Set<Item> items = entry.getValue();

            for (Item item : items) {
                result.add(item);
                if (result.size() == desiredSize) {
                    return result;
                }
            }
        }

        return result;
    }
}

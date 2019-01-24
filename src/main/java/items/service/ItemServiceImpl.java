package items.service;

import com.google.common.flogger.FluentLogger;
import items.config.ItemConfig;
import items.dao.ItemDao;
import items.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private static final int CLEANUP_INTERVALS = 5 * 1000;

    private final ItemDao itemDao;
    private final ItemConfig itemConfig;

    public ItemServiceImpl(@Autowired ItemDao itemDao, @Autowired ItemConfig itemConfig) {
        this.itemDao = itemDao;
        this.itemConfig = itemConfig;
    }

    @Override
    public void save(Item item) {
        /*if(itemDao.contains(item)) {
            throw new Exception("Item has already existed");
        }*/
        itemDao.save(item);
    }

    @Override
    public List<Item> getLastItems() {
        List<Item> lastItemsForDuration = itemDao.getLastItemsForDuration(itemConfig.getDuration());
        if (lastItemsForDuration.size() >= itemConfig.getSize()) {
            return lastItemsForDuration;
        } else {
            return itemDao.getLastItems(itemConfig.getSize());
        }
    }

    @Scheduled(fixedDelay = CLEANUP_INTERVALS)
    public void delete() {
        Duration desiredSeconds = itemConfig.getDuration();
        List<Item> lastItemsForDuration = itemDao.getLastItemsForDuration(desiredSeconds);
        if (lastItemsForDuration.size() >= itemConfig.getSize()) {
            logger.atInfo().log("Cleaning items older than %s ms", desiredSeconds.toMillis());
            itemDao.deleteItemsLessThan(desiredSeconds);
        } else {
            logger.atInfo().log("Cleaning items until size is %s", itemConfig.getSize());
            itemDao.deleteOldItemsUntilSizeWillBe(itemConfig.getSize());
        }
    }
}

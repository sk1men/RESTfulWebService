package items.service;

import items.config.ItemConfig;
import items.dao.ItemDao;
import items.model.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ItemServiceImplTest {

    @Mock
    private ItemDao itemDao;
    @Mock
    private ItemConfig itemConfig;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    public void testSaving() {
        Item item = createItem(1, ZonedDateTime.now());

        itemService.save(item);

        verify(itemDao).save(item);
    }


    @Test
    public void testGettingLastItemsForSize() {
        Duration duration = Duration.ofSeconds(1);
        int desiredSize = 3;

        List<Item> durationItems = Arrays.asList(
                createItem(1, ZonedDateTime.now()),
                createItem(2, ZonedDateTime.now().minusSeconds(1))
        );
        List<Item> sizeItems = Arrays.asList(
                createItem(1, ZonedDateTime.now()),
                createItem(2, ZonedDateTime.now().minusSeconds(1)),
                createItem(3, ZonedDateTime.now().minusSeconds(2))
        );

        when(itemDao.getLastItemsForDuration(duration)).thenReturn(durationItems);
        when(itemDao.getLastItems(desiredSize)).thenReturn(sizeItems);
        when(itemConfig.getSize()).thenReturn(desiredSize);
        when(itemConfig.getDuration()).thenReturn(duration);

        List<Item> actualItems = itemService.getLastItems();

        assertEquals(sizeItems, actualItems);
    }

    @Test
    public void testGettingLastItemsForDuration() {

        Duration duration = Duration.ofSeconds(1);
        List<Item> durationItems = Arrays.asList(
                createItem(1, ZonedDateTime.now()),
                createItem(2, ZonedDateTime.now().minusSeconds(1))
        );

        when(itemDao.getLastItemsForDuration(duration)).thenReturn(durationItems);
        when(itemConfig.getSize()).thenReturn(1);
        when(itemConfig.getDuration()).thenReturn(duration);

        List<Item> actualItems = itemService.getLastItems();

        assertEquals(durationItems, actualItems);
    }

    @Test
    public void testDeletingDataOlderThanDuration() {

        Duration duration = Duration.ofSeconds(1);
        List<Item> items = Arrays.asList(
                createItem(1, ZonedDateTime.now()),
                createItem(2, ZonedDateTime.now().minusSeconds(1))
        );

        when(itemDao.getLastItemsForDuration(duration)).thenReturn(items);
        when(itemConfig.getSize()).thenReturn(1);
        when(itemConfig.getDuration()).thenReturn(duration);

        itemService.delete();

        verify(itemDao).deleteItemsLessThan(duration);
    }

    @Test
    public void testDeletingDataForSize() {
        Duration duration = Duration.ofSeconds(1);
        int desiredSize = 2;
        List<Item> items = Collections.singletonList(createItem(1, ZonedDateTime.now()));

        when(itemDao.getLastItemsForDuration(duration)).thenReturn(items);
        when(itemConfig.getSize()).thenReturn(desiredSize);
        when(itemConfig.getDuration()).thenReturn(duration);

        itemService.delete();

        verify(itemDao).deleteOldItemsUntilSizeWillBe(desiredSize);
    }

    private Item createItem(long id, ZonedDateTime timestamp) {
        return new Item(id, timestamp);
    }
}
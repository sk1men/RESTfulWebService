package items.dao;

import items.model.Item;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MemoryDaoTest {
    private MemoryDao memoryDao = new MemoryDao();

    private Item item1;
    private Item item2;
    private Item item3;
    private Item item4;

    //same ZonedDateTime
    private Item item5;
    private Item item6;


    @Before
    public void setUp() {
        item1 = new Item(1, ZonedDateTime.now().minusNanos(2_500_000_001L));
        item2 = new Item(2, ZonedDateTime.now().minusNanos(2_000_000_001L));
        item3 = new Item(3, ZonedDateTime.now().minusNanos(1_500_000_001L));
        item4 = new Item(3, ZonedDateTime.now().minusNanos(1_000_000_001L));

        ZonedDateTime sameTimestamp = ZonedDateTime.now().minusNanos(500_000_001L);
        item5 = new Item(5, sameTimestamp);
        item6 = new Item(6, sameTimestamp);
    }

    @Test
    public void testSavingReturnsCorrectSavedElement() {
        assertEquals(item1, memoryDao.save(item1));
    }

    @Test
    public void testDeletingReturnsTrueIfItemWasFoundAndDeleted() {
        memoryDao.save(item1);

        assertTrue(memoryDao.delete(item1));
    }

    @Test
    public void testDeletingReturnsFalseIfItemWasNotFound() {
        assertFalse(memoryDao.delete(item1));
    }

    @Test
    public void testingContainReturnsFalseAfterDeletingItem() {
        memoryDao.save(item1);
        memoryDao.delete(item1);

        assertFalse(memoryDao.contains(item1));
    }

    @Test
    public void testingDeleteSingleOldItemsUntilSizeWillBeGivenValue() {
        memoryDao.save(item1);
        memoryDao.save(item2);
        memoryDao.save(item3);
        memoryDao.save(item4);
        memoryDao.save(item5);

        memoryDao.deleteOldItemsUntilSizeWillBe(4);

        assertFalse(memoryDao.contains(item1));
        assertTrue(memoryDao.contains(item2));
        assertTrue(memoryDao.contains(item3));
        assertTrue(memoryDao.contains(item4));
        assertTrue(memoryDao.contains(item5));
    }

    @Test
    public void testingDeleteOldItemsUntilSizeWillBeGivenValueWhenThereIsMultipleItemsForDataTimeKey() {
        memoryDao.save(item1);
        memoryDao.save(item2);
        memoryDao.save(item3);
        memoryDao.save(item4);
        memoryDao.save(item5);
        memoryDao.save(item6);

        memoryDao.deleteOldItemsUntilSizeWillBe(4);

        assertFalse(memoryDao.contains(item1));
        assertFalse(memoryDao.contains(item2));

        assertTrue(memoryDao.contains(item3));
        assertTrue(memoryDao.contains(item4));
        assertTrue(memoryDao.contains(item5));
        assertTrue(memoryDao.contains(item6));
    }

    @Test
    public void testingDeleteMultipleOldItemsUntilSizeWillBeGivenValue() {
        memoryDao.save(item1);
        memoryDao.save(item2);
        memoryDao.save(item3);
        memoryDao.save(item4);
        memoryDao.save(item5);

        memoryDao.deleteOldItemsUntilSizeWillBe(3);

        assertFalse(memoryDao.contains(item1));
        assertFalse(memoryDao.contains(item2));

        assertTrue(memoryDao.contains(item3));
        assertTrue(memoryDao.contains(item4));
        assertTrue(memoryDao.contains(item5));
    }

    @Test
    public void testingDeleteItemsUntilSizeWillBeGivenValueWhenStorageIsEmpty() {
        memoryDao.deleteOldItemsUntilSizeWillBe(2);

        assertEquals(Collections.emptyList(), memoryDao.getLastItems(2));
    }

    @Test
    public void testDeletingItemsLessThanGivenDurationWhenStorageIsEmpty() {
        Duration twoSec = Duration.ofSeconds(2);
        memoryDao.deleteItemsLessThan(twoSec);

        assertEquals(Collections.emptyList(), memoryDao.getLastItems(2));
    }

    @Test
    public void testDeletingItemsLessThanGivenDuration() {

        Duration twoSec = Duration.ofSeconds(2);
        memoryDao.save(item1);
        memoryDao.save(item2);
        memoryDao.save(item3);
        memoryDao.save(item4);
        memoryDao.save(item5);

        memoryDao.deleteItemsLessThan(twoSec);

        assertFalse(memoryDao.contains(item1));
        assertFalse(memoryDao.contains(item2));

        assertTrue(memoryDao.contains(item3));
        assertTrue(memoryDao.contains(item4));
        assertTrue(memoryDao.contains(item5));
    }

    @Test
    public void testGettingLastItemsForGivenSizeWhenSizeIsEqualToSizeOfStorage() {
        List<Item> expectedItemList = Arrays.asList(item5, item4, item3, item2, item1);

        for (Item item : expectedItemList) {
            memoryDao.save(item);
        }

        assertEquals(expectedItemList, memoryDao.getLastItems(5));
    }

    @Test
    public void testGettingLastItemsForGivenSizeWhenSizeIsGreaterThanSizeOfStorage() {
        List<Item> expectedItemList = Arrays.asList(item5, item4, item3, item2, item1);

        for (Item item : expectedItemList) {
            memoryDao.save(item);
        }

        assertEquals(expectedItemList, memoryDao.getLastItems(10));
    }

    @Test
    public void testGettingLastItemsForGivenSizeWhenSizeIsLessThanSizeOfStorage() {
        memoryDao.save(item1);
        memoryDao.save(item2);
        memoryDao.save(item3);
        memoryDao.save(item4);
        memoryDao.save(item5);

        List<Item> expectedItemList = Arrays.asList(item5, item4, item3);

        List<Item> actualItemList = memoryDao.getLastItems(3);

        assertEquals(expectedItemList, actualItemList);
    }

    @Test
    public void testGettingLastItemsForGivenDurationWhenResultIsAllItemsInStorage() {

        List<Item> expectedItemList = Arrays.asList(item5, item4, item3, item2, item1);

        expectedItemList.forEach(memoryDao::save);

        assertEquals(expectedItemList, memoryDao.getLastItemsForDuration(Duration.ofSeconds(6)));
    }

    @Test
    public void testGettingLastItemsForGivenDurationWhenResultIsSmallerThanSizeOfStorage() {

        memoryDao.save(item1);
        memoryDao.save(item2);
        memoryDao.save(item3);
        memoryDao.save(item4);
        memoryDao.save(item5);

        List<Item> expectedItemList = Collections.singletonList(item5);

        assertEquals(expectedItemList, memoryDao.getLastItemsForDuration(Duration.ofSeconds(1)));
    }


}
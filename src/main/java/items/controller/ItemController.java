package items.controller;


import com.google.common.flogger.FluentLogger;
import items.model.Item;
import items.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/items")
public class ItemController {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final ItemService itemService;

    public ItemController(@Autowired ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Lists available items.
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    List<Item> listItem() {
        logger.atInfo().log("Reading available items");
        return itemService.getLastItems();
    }

    /**
     * Creates the item.
     */
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity postItem(@RequestBody Item item) {
        logger.atInfo().log("Saving the item %s", item);
        itemService.save(item);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}

package com.ebicep.warlords.database.repositories.illusionvendor.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "Illusion_Vendor_Weekly_Shop")
public class IllusionVendorWeeklyShop {

    public static IllusionVendorWeeklyShop currentIllusionVendorWeeklyShop;
    public static final Map<String, PurchasableItem> ITEM_COSTS = new HashMap<>() {{
        put("RANDOM_ALPHA_ITEM", new PurchasableItem(25L));
        put("RANDOM_ALPHA_ITEM_2", new PurchasableItem(25L));
        put("RANDOM_BETA_ITEM", new PurchasableItem(50L));
        put("RANDOM_BETA_ITEM_2", new PurchasableItem(50L));
    }};

    public static void loadWeeklyIllusionVendor() {
        ChatUtils.MessageType.WEEKLY_BLESSINGS.sendMessage("Loading Weekly Illusion Vendor - " + DatabaseTiming.RESET_WEEKLY.get());
        if (DatabaseTiming.RESET_WEEKLY.get()) {
            currentIllusionVendorWeeklyShop = createNewShop();
            onInitialize();
            createNewWeeklyBlessings();
        } else {
            Warlords.newChain()
                    .asyncFirst(() -> DatabaseManager.illusionVendorService.findAll())
                    .syncLast(weeklyBlessings -> {
                        if (weeklyBlessings.isEmpty()) {
                            currentIllusionVendorWeeklyShop = createNewShop();
                            createNewWeeklyBlessings();
                        } else {
                            currentIllusionVendorWeeklyShop = weeklyBlessings.get(weeklyBlessings.size() - 1);
                        }
                        onInitialize();
                    })
                    .execute();
        }
    }

    private static IllusionVendorWeeklyShop createNewShop() {
        IllusionVendorWeeklyShop weeklyShop = new IllusionVendorWeeklyShop();
        weeklyShop.newItems = generateNewItems();
        return weeklyShop;
    }

    private static Map<String, NewItem> generateNewItems() {
        Map<String, NewItem> generatedItems = new HashMap<>();
        generatedItems.put("RANDOM_ALPHA_ITEM", NewItemsUtils.generateRandomItem(NewItemTier.COMMON));
        generatedItems.put("RANDOM_ALPHA_ITEM_2", NewItemsUtils.generateRandomItem(NewItemTier.COMMON));
        generatedItems.put("RANDOM_BETA_ITEM", NewItemsUtils.generateRandomItem(NewItemTier.RARE));
        generatedItems.put("RANDOM_BETA_ITEM_2", NewItemsUtils.generateRandomItem(NewItemTier.RARE));
        return generatedItems;
    }

    private static void onInitialize() {
        IllusionVendorWeeklyShop weeklyShop = currentIllusionVendorWeeklyShop;
        boolean migratedToNewItems = weeklyShop.ensureNewItems();
        ChatUtils.MessageType.ILLUSION_VENDOR.sendMessage("Initialized Illusion Vendor - " + weeklyShop);
        if (migratedToNewItems && weeklyShop.id != null) {
            weeklyShop.persistNewItems();
        }
    }

    private static void createNewWeeklyBlessings() {
        Warlords.newChain()
                .async(() -> DatabaseManager.illusionVendorService.create(currentIllusionVendorWeeklyShop))
                .execute();
    }

    @Id
    protected String id;
    private Instant week = DateUtil.getResetDateCurrentWeek();
    private Map<String, NewItem> newItems;

    public IllusionVendorWeeklyShop() {
    }

    private synchronized boolean ensureNewItems() {
        if (newItems != null
                && newItems.size() == ITEM_COSTS.size()
                && newItems.keySet().containsAll(ITEM_COSTS.keySet())
                && !newItems.containsValue(null)) {
            return false;
        }
        newItems = generateNewItems();
        return true;
    }

    private void persistNewItems() {
        if (!DatabaseManager.enabled || DatabaseManager.illusionVendorService == null || id == null) {
            return;
        }
        Warlords.newChain()
                .async(() -> DatabaseManager.illusionVendorService.update(this))
                .execute();
    }

    @Override
    public String toString() {
        return "IllusionVendorWeeklyShop{" +
                "week=" + week +
                ", newItems=" + newItems +
                '}';
    }

    public synchronized Map<String, NewItem> getNewItems() {
        if (ensureNewItems()) {
            persistNewItems();
        }
        return newItems;
    }

    public static class PurchasableItem {
        private final long cost;

        PurchasableItem(long cost) {
            this.cost = cost;
        }

        public long getCost() {
            return cost;
        }
    }
}

package com.ebicep.warlords.database.repositories.illusionvendor.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.ItemType;
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
        put("RANDOM_BETA_ITEM", new PurchasableItem(50L));
    }};

    public static void loadWeeklyIllusionVendor() {
        ChatUtils.MessageTypes.WEEKLY_BLESSINGS.sendMessage("Loading Weekly Illusion Vendor - " + DatabaseTiming.RESET_WEEKLY.get());
        if (DatabaseTiming.RESET_WEEKLY.get()) {
            currentIllusionVendorWeeklyShop = new IllusionVendorWeeklyShop();
            onInitialize();
            createNewWeeklyBlessings();
        } else {
            Warlords.newChain()
                    .asyncFirst(() -> DatabaseManager.illusionVendorService.findAll())
                    .syncLast(weeklyBlessings -> {
                        if (weeklyBlessings.isEmpty()) {
                            currentIllusionVendorWeeklyShop = new IllusionVendorWeeklyShop();
                            createNewWeeklyBlessings();
                        } else {
                            currentIllusionVendorWeeklyShop = weeklyBlessings.get(weeklyBlessings.size() - 1);
                        }
                        onInitialize();
                    })
                    .execute();
        }
    }

    private static void onInitialize() {
        ChatUtils.MessageTypes.ILLUSION_VENDOR.sendMessage("Initialized Illusion Vendor - " + currentIllusionVendorWeeklyShop);
    }

    private static void createNewWeeklyBlessings() {
        Warlords.newChain()
                .async(() -> DatabaseManager.illusionVendorService.create(currentIllusionVendorWeeklyShop))
                .execute();
    }

    @Id
    protected String id;
    private Instant week = DateUtil.getResetDateLatestMonday();
    private Map<String, AbstractItem> items = new HashMap<>() {{
        put("RANDOM_ALPHA_ITEM", ItemType.getRandom().createBasic(ItemTier.ALPHA));
        put("RANDOM_BETA_ITEM", ItemType.getRandom().createBasic(ItemTier.BETA));
    }};

    public IllusionVendorWeeklyShop() {
    }

    @Override
    public String toString() {
        return "IllusionVendorWeeklyShop{" +
                "week=" + week +
                ", items=" + items +
                '}';
    }

    public Map<String, AbstractItem> getItems() {
        return items;
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

package com.ebicep.warlords.pve.journal;

import com.ebicep.warlords.guilds.GuildSpendable;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.ExpSpendable;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.consumables.vials.Vial;
import com.ebicep.warlords.pve.items.types.SpendableRandomItem;
import com.ebicep.warlords.pve.items.types.fixeditems.FixedItems;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.newitems.SpendableRandomNewItem;
import com.ebicep.warlords.pve.newitems.gems.Gem;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ResourceDiscovery {

    public enum Category {
        CURRENCIES("Currencies", Material.GOLD_NUGGET, NamedTextColor.YELLOW),
        MOB_DROPS("Mob Drops", Material.ZOMBIE_HEAD, NamedTextColor.GREEN),
        GEMS("Gems", Material.EMERALD, NamedTextColor.AQUA),
        VIALS("Vials", Material.HONEY_BOTTLE, NamedTextColor.LIGHT_PURPLE),
        GUILD("Guild", Material.GOLD_INGOT, NamedTextColor.GOLD),
        EXPERIENCE("Experience", Material.EXPERIENCE_BOTTLE, NamedTextColor.GREEN),
        ITEMS("Items", Material.ITEM_FRAME, NamedTextColor.BLUE),
        ;

        public static final Category[] VALUES = values();

        public final String displayName;
        public final Material icon;
        public final TextColor textColor;

        Category(String displayName, Material icon, TextColor textColor) {
            this.displayName = displayName;
            this.icon = icon;
            this.textColor = textColor;
        }
    }

    public record Entry(Spendable spendable, Category category, List<String> sources) {
        public Entry {
            sources = List.copyOf(sources);
        }
    }

    private static final List<Entry> ENTRIES;

    static {
        List<Entry> entries = new ArrayList<>();
        for (Currencies currency : Currencies.VALUES) {
            entries.add(new Entry(currency, Category.CURRENCIES, sourcesFor(currency)));
        }
        for (MobDrop drop : MobDrop.VALUES) {
            entries.add(new Entry(drop, Category.MOB_DROPS, sourcesFor(drop)));
        }
        for (Gem gem : Gem.VALUES) {
            entries.add(new Entry(gem, Category.GEMS, sourcesFor(gem)));
        }
        for (Vial vial : Vial.VALUES) {
            entries.add(new Entry(vial, Category.VIALS, sourcesFor(vial)));
        }
        for (GuildSpendable guildSpendable : GuildSpendable.VALUES) {
            entries.add(new Entry(guildSpendable, Category.GUILD, sourcesFor(guildSpendable)));
        }
        for (ExpSpendable expSpendable : ExpSpendable.VALUES) {
            entries.add(new Entry(expSpendable, Category.EXPERIENCE, sourcesFor(expSpendable)));
        }
        for (FixedItems fixedItem : FixedItems.values()) {
            entries.add(new Entry(fixedItem, Category.ITEMS, sourcesFor(fixedItem)));
        }
        for (SpendableRandomItem randomItem : SpendableRandomItem.VALUES) {
            entries.add(new Entry(randomItem, Category.ITEMS, sourcesFor(randomItem)));
        }
        for (SpendableRandomNewItem randomNewItem : SpendableRandomNewItem.VALUES) {
            entries.add(new Entry(randomNewItem, Category.ITEMS, sourcesFor(randomNewItem)));
        }
        ENTRIES = Collections.unmodifiableList(entries);
    }

    private ResourceDiscovery() {
    }

    public static List<Entry> entries() {
        return ENTRIES;
    }

    public static List<Entry> entries(Category category) {
        return ENTRIES.stream()
                      .filter(entry -> entry.category() == category)
                      .toList();
    }

    private static List<String> sourcesFor(Currencies currency) {
        return switch (currency) {
            case COIN -> List.of("Wave Defense", "Onslaught", "Event Wave Defense", "Anomaly", "Raid", "Bounties", "Pouches");
            case SYNTHETIC_SHARD -> List.of("Onslaught", "Anomaly", "Weapon Salvage", "Supply Drops");
            case LEGEND_FRAGMENTS -> List.of("Wave Defense", "Onslaught", "Raid", "Pouches");
            case ILLUSION_SHARD -> List.of("Wave Defense", "Onslaught");
            case FAIRY_ESSENCE -> List.of("Supply Drops", "Level Rewards", "Bounties", "Event Shops");
            case COMMON_STAR_PIECE, RARE_STAR_PIECE, EPIC_STAR_PIECE -> List.of("Supply Drops");
            case LEGENDARY_STAR_PIECE -> List.of("Onslaught", "Supply Drops");
            case ASCENDANT_STAR_PIECE -> List.of("Wave Defense", "Raid");
            case VOID_STAR_PIECE -> List.of("Cryptic Conquest Vendor");
            case SUPPLY_DROP_TOKEN -> List.of("Onslaught");
            case SKILL_BOOST_MODIFIER -> List.of("Supply Drops", "Level Rewards", "Event Shops");
            case EVENT_POINTS_BOLTARO, EVENT_POINTS_NARMER, EVENT_POINTS_MITHRA, EVENT_POINTS_ILLUIMINA,
                 EVENT_POINTS_GARDEN_OF_HESPERIDES, EVENT_POINTS_LIBRARY_ARCHIVES -> List.of("Event Wave Defense");
            case TITLE_TOKEN_JUGGERNAUT, TITLE_TOKEN_PHARAOHS_REVENGE, TITLE_TOKEN_SPIDERS_BURROW,
                 TITLE_TOKEN_BANE_OF_IMPURITIES, TITLE_TOKEN_GARDEN_OF_HESPERIDES, TITLE_TOKEN_LIBRARY_ARCHIVES ->
                    List.of("Event Shops", "Event Leaderboards");
            case LIMIT_BREAKER -> List.of("Wave Defense", "Raid");
            case MYSTERIOUS_TOKEN -> List.of("Vendors");
            case CELESTIAL_BRONZE -> List.of("Item Crafting");
            case SCRAP_METAL -> List.of("Item Salvage");
            case ASCENDANT_SHARD -> List.of("Wave Defense", "Raid");
            case PRESTIGE_ORB -> List.of("Spec Prestige");
            case ETHEREUM_CRYSTAL -> List.of("Anomaly", "Raid");
            case ASCENDANT_SCROLL -> List.of("Ascendant Vendor");
            case ITEM_LOCK_SCROLL -> List.of("Raid", "Ascendant Vendor");
            case CRYPTIC_CONQUEST_KEY -> List.of("Ascendant Vendor");
            case ARCHEMEDIAN_FRAGMENT -> List.of("Cryptic Conquest");
            case SOVEREIGN_TOWER_KEY -> List.of("Ascendant Vendor");
            case VEILKEEPER_INSIGNIA -> List.of("Prestige Vendor");
        };
    }

    private static List<String> sourcesFor(MobDrop drop) {
        return switch (drop) {
            case ZENITH_STAR -> List.of("Onslaught", "Wave Defense");
            case AWAKENED_ABILITY_SCROLL -> List.of("Hidden Mob Drops");
        };
    }

    private static List<String> sourcesFor(Gem gem) {
        return List.of("Raid");
    }

    private static List<String> sourcesFor(Vial vial) {
        return List.of("Vial Inventory");
    }

    private static List<String> sourcesFor(GuildSpendable guildSpendable) {
        return switch (guildSpendable) {
            case GUILD_COIN -> List.of("Wave Defense", "Onslaught", "Event Wave Defense", "Raid");
            case GUILD_EXPERIENCE -> List.of("Wave Defense", "Onslaught", "Event Wave Defense", "Raid");
        };
    }

    private static List<String> sourcesFor(ExpSpendable expSpendable) {
        return switch (expSpendable) {
            case SPEC -> List.of("Wave Defense", "Onslaught", "Event Wave Defense", "Anomaly", "Raid");
        };
    }

    private static List<String> sourcesFor(FixedItems fixedItem) {
        return switch (fixedItem) {
            case SHAWL_OF_MITHRA, SPIDER_GAUNTLET, DISASTER_FRAGMENT -> List.of("Event Wave Defense");
        };
    }

    private static List<String> sourcesFor(SpendableRandomItem randomItem) {
        return List.of("Wave Defense", "Onslaught", "Event Wave Defense");
    }

    private static List<String> sourcesFor(SpendableRandomNewItem randomNewItem) {
        return switch (randomNewItem) {
            case COMMON, RARE, EPIC, SOVEREIGN, LEGENDARY -> List.of("Wave Defense", "Onslaught", "Event Wave Defense", "Anomaly");
            case ASCENDANT -> List.of("Wave Defense", "Anomaly");
        };
    }
}

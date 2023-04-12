package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemBucklerModifier;
import com.ebicep.warlords.pve.items.modifiers.ItemGauntletModifier;
import com.ebicep.warlords.pve.items.modifiers.ItemModifier;
import com.ebicep.warlords.pve.items.modifiers.ItemTomeModifier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.SpecialItems;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unchecked")
public enum ItemType {


    NONE("All",
            new ItemStack(Material.BARRIER)
    ) {
        @Override
        public <R extends Enum<R> & ItemModifier> R[] getBlessings() {
            return null;
        }

        @Override
        public <R extends Enum<R> & ItemModifier> R[] getCurses() {
            return null;
        }
    },
    GAUNTLET("Gauntlet",
            SkullUtils.getSkullFrom(SkullID.IRON_FIST)
    ) {
        @Override
        public <R extends Enum<R> & ItemModifier> R[] getBlessings() {
            return (R[]) ItemGauntletModifier.Blessings.VALUES;
        }

        @Override
        public <R extends Enum<R> & ItemModifier> R[] getCurses() {
            return (R[]) ItemGauntletModifier.Curses.VALUES;
        }
    },
    TOME("Tome",
            SkullUtils.getSkullFrom(SkullID.ENCHANTMENT_BOOK)
    ) {
        @Override
        public <R extends Enum<R> & ItemModifier> R[] getBlessings() {
            return (R[]) ItemTomeModifier.Blessings.VALUES;
        }

        @Override
        public <R extends Enum<R> & ItemModifier> R[] getCurses() {
            return (R[]) ItemTomeModifier.Curses.VALUES;
        }
    },
    BUCKLER("Buckler",
            SkullUtils.getSkullFrom(SkullID.GOOGLE_HOME_MINI)
    ) {
        @Override
        public <R extends Enum<R> & ItemModifier> R[] getBlessings() {
            return (R[]) ItemBucklerModifier.Blessings.VALUES;
        }

        @Override
        public <R extends Enum<R> & ItemModifier> R[] getCurses() {
            return (R[]) ItemBucklerModifier.Curses.VALUES;
        }
    },

    ;

    public static final ItemType[] VALUES = values();
    public static final ItemType[] VALID_VALUES = {GAUNTLET, TOME, BUCKLER};
    public final String name;
    public final ItemStack skull;

    public static ItemType getRandom() {
        return VALID_VALUES[ThreadLocalRandom.current().nextInt(VALID_VALUES.length)];
    }

    ItemType(String name, ItemStack skull) {
        this.name = name;
        this.skull = skull;
    }

    public ItemType next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public AbstractItem createBasic(ItemTier tier) {
        switch (tier) {
            case ALPHA:
            case BETA:
                return new BasicItem(this, tier);
            case GAMMA:
                return SpecialItems.GAMMA_ITEMS[ThreadLocalRandom.current().nextInt(SpecialItems.GAMMA_ITEMS.length)].create.get();
            case DELTA:
                return SpecialItems.DELTA_ITEMS[ThreadLocalRandom.current().nextInt(SpecialItems.DELTA_ITEMS.length)].create.get();
        }
        ChatUtils.MessageTypes.WARLORDS.sendErrorMessage("Invalid item tier creation: " + tier.name);
        return null;
    }

    public BasicItem createBasicInherited(ItemTier tier, Set<BasicStatPool> statPool) {
        return new BasicItem(this, tier, statPool);
    }

    public abstract <R extends Enum<R> & ItemModifier> R[] getBlessings();

    public abstract <R extends Enum<R> & ItemModifier> R[] getCurses();
}

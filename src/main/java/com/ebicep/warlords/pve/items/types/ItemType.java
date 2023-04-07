package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemBucklerModifier;
import com.ebicep.warlords.pve.items.modifiers.ItemGauntletModifier;
import com.ebicep.warlords.pve.items.modifiers.ItemModifier;
import com.ebicep.warlords.pve.items.modifiers.ItemTomeModifier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unchecked")
public enum ItemType {

    GAUNTLET("Gauntlet"
    ) {
        @Override
        public BasicItem createBasic(ItemTier tier) {
            return new BasicItem(this, tier);
        }

        @Override
        public <R extends Enum<R> & ItemModifier> R[] getBlessings() {
            return (R[]) ItemGauntletModifier.Blessings.VALUES;
        }

        @Override
        public <R extends Enum<R> & ItemModifier> R[] getCurses() {
            return (R[]) ItemGauntletModifier.Curses.VALUES;
        }
    },
    TOME("Tome"
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
    BUCKLER("Buckler"
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
    public final String name;

    public static ItemType getRandom() {
        return VALUES[ThreadLocalRandom.current().nextInt(VALUES.length)];
    }

    ItemType(String name) {
        this.name = name;
    }

    public BasicItem createBasic(ItemTier tier) {
        return new BasicItem(this, tier);
    }

    public BasicItem createBasicInherited(ItemTier tier, Set<BasicStatPool> statPool) {
        return new BasicItem(this, tier, statPool);
    }

    public abstract <R extends Enum<R> & ItemModifier> R[] getBlessings();

    public abstract <R extends Enum<R> & ItemModifier> R[] getCurses();
}

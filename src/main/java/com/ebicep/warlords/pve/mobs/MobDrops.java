package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum MobDrops implements Spendable {

    ZENITH_STAR(
            "Zenith Star",
            ChatColor.WHITE,
            new ItemStack(Material.NETHER_STAR)
    ),
    CELESTIAL_BRONZE(
            "Celestial Bronze",
            ChatColor.GOLD,
            new ItemStack(Material.MONSTER_EGG, 1, (short) 101)
    ) {
        @Override
        public boolean pluralIncludeS() {
            return false;
        }
    },

    ;

    public static final MobDrops[] VALUES = values();

    public final String name;
    public final ChatColor chatColor;
    public final ItemStack item;

    MobDrops(String name, ChatColor chatColor, ItemStack item) {
        this.name = name;
        this.chatColor = chatColor;
        this.item = item;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ChatColor getChatColor() {
        return chatColor;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public void addToPlayer(DatabasePlayer databasePlayer, long amount) {
        databasePlayer.getPveStats().addMobDrops(this, amount);
    }

    @Override
    public Long getFromPlayer(DatabasePlayer databasePlayer) {
        return databasePlayer.getPveStats().getMobDrops(this);
    }

}

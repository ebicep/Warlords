package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum MobDrops implements Spendable {

    ZENITH_STAR(
            "Zenith Star",
            ChatColor.WHITE,
            new ItemStack(Material.NETHER_STAR)
    ),

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
    public void addToPlayer(DatabasePlayer databasePlayer, long amount) {
        databasePlayer.getPveStats().addMobDrops(this, amount);
    }

    @Override
    public Long getFromPlayer(DatabasePlayer databasePlayer) {
        return databasePlayer.getPveStats().getMobDrops(this);
    }

    @Override
    public String getCostColoredName(long cost) {
        return chatColor.toString() + NumberFormat.addCommas(cost) + " " + name + (cost == 1 || !pluralIncludeS() ? "" : "s");
    }

    public boolean pluralIncludeS() {
        return true;
    }

}

package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum MobDrop implements Spendable {

    ZENITH_STAR(
            "Zenith Star",
            NamedTextColor.WHITE,
            new ItemStack(Material.NETHER_STAR)
    ),

    ;

    public static final MobDrop[] VALUES = values();

    public final String name;
    public final NamedTextColor textColor;
    public final ItemStack item;

    MobDrop(String name, NamedTextColor textColor, ItemStack item) {
        this.name = name;
        this.textColor = textColor;
        this.item = item;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public NamedTextColor getTextColor() {
        return textColor;
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

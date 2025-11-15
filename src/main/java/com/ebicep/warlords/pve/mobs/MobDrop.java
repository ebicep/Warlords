package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum MobDrop implements Spendable {

    ZENITH_STAR(
            "Zenith Star",
            TextColor.color(210, 210, 210),
            new ItemStack(Material.WIND_CHARGE),
            false
    ),
    AWAKENED_ABILITY_SCROLL(
            "Awakened Ability Scroll",
            TextColor.color(120, 170, 100),
            new ItemStack(Material.FLOWER_BANNER_PATTERN),
            true
    ),

    ;

    public static final MobDrop[] VALUES = values();

    public final String name;
    public final TextColor textColor;
    public final ItemStack item;
    private final boolean isHidden;

    MobDrop(String name, TextColor textColor, ItemStack item, boolean isHidden) {
        this.name = name;
        this.textColor = textColor;
        this.item = item;
        this.isHidden = isHidden;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TextColor getTextColor() {
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

    public boolean isHidden() {
        return isHidden;
    }
}

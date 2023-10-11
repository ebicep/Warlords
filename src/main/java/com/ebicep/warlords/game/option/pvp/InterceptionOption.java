package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class InterceptionOption implements Option {

    public static final ItemStack COMPASS = new ItemBuilder(Material.COMPASS)
            .name(Component.text("Point Information", NamedTextColor.GREEN))
            .unbreakable()
            .get();

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        player.getInventory().setItem(8, COMPASS);
    }

}

package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.CompassTargetMarker;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import javax.annotation.Nonnull;

public class CompassOption implements Option {

    private static final Component FLAG_FINDER = Component.text("Flag Finder", NamedTextColor.GREEN);
    private static final Component POINT_INFORMATION = Component.text("Point Information", NamedTextColor.GREEN);

    public static CompassOption flagOption() {
        return new CompassOption(FLAG_FINDER);
    }

    public static CompassOption pointInformationOption() {
        return new CompassOption(POINT_INFORMATION);
    }

    private final ItemStack compassItem;
    private final int compassSlot = 8;
    private final int updatePeriod = 20;

    public CompassOption(Component compassName) {
        this.compassItem = new ItemBuilder(Material.COMPASS)
                .name(compassName)
                .unbreakable()
                .meta(CompassMeta.class, compassMeta -> {
                            compassMeta.setLodestoneTracked(false);
                            compassMeta.setLodestone(null);
                        }
                )
                .glow(false)
                .get();
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {

            @Override
            public void run() {
                game.warlordsPlayers().forEach(warlordsEntity -> {
                    if (!(warlordsEntity.getEntity() instanceof Player player)) {
                        return;
                    }
                    CompassTargetMarker target = warlordsEntity.getCompassTarget();
                    ItemStack item = player.getInventory().getItem(compassSlot);
                    if (target != null && item != null && item.getItemMeta() instanceof CompassMeta compassMeta) {
                        compassMeta.setLodestone(target.getLocation());
                        item.setItemMeta(compassMeta);
                    }
                });
            }

        }.runTaskTimer(0, updatePeriod);
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        player.getInventory().setItem(compassSlot, compassItem);
    }

}

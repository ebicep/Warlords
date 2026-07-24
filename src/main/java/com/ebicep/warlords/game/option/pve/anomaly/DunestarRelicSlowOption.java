package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class DunestarRelicSlowOption implements Option {

    private static final String MODIFIER_NAME = "Dunestar Relic Carrier";
    private static final float SPEED_REDUCTION = -20;
    private static final String RELIC_NAME = "Dunestar Relic";

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                game.warlordsPlayers().forEach(warlordsPlayer -> {
                    if (!(warlordsPlayer.getEntity() instanceof Player player)) {
                        return;
                    }
                    if (isHoldingRelic(player.getInventory().getItem(8))) {
                        warlordsPlayer.addSpeedModifier(
                                warlordsPlayer,
                                MODIFIER_NAME,
                                SPEED_REDUCTION,
                                2
                        );
                    } else {
                        removeSlow(warlordsPlayer);
                    }
                });
            }
        }.runTaskTimer(0, 1);
    }

    private boolean isHoldingRelic(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.HEART_OF_THE_SEA || !itemStack.hasItemMeta()) {
            return false;
        }
        return RELIC_NAME.equals(PlainTextComponentSerializer.plainText().serialize(itemStack.getItemMeta().displayName()));
    }

    private void removeSlow(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getSpeed().removeModifier(MODIFIER_NAME);
    }

    @Override
    public void onGameEnding(@Nonnull Game game) {
        game.warlordsPlayers().forEach(this::removeSlow);
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        game.warlordsPlayers().forEach(this::removeSlow);
    }
}

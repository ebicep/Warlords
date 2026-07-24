package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.pve.CurrencyOnEventOption;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class DunestarCurrencyOption extends CurrencyOnEventOption {

    private static final int INSIGNIA_PER_ENEMY_KILL = 250;
    private static final int INSIGNIA_PER_CHECKPOINT = 50_000;
    private static final int CHECKPOINT_COUNT = 2;

    private boolean checkpointRewardGranted;

    public DunestarCurrencyOption() {
        onKill(INSIGNIA_PER_ENEMY_KILL);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGameWin(WarlordsGameTriggerWinEvent event) {
        if (checkpointRewardGranted || event.getGame().getOption(DunestarEscortOption.class).isEmpty()) {
            return;
        }

        DunestarEscortOption escortOption = event.getGame().getOption(DunestarEscortOption.class).getFirst();
        if (!escortOption.isCompleted()) {
            return;
        }

        checkpointRewardGranted = true;
        int checkpointsReached = Math.min(CHECKPOINT_COUNT, escortOption.getObjectivesCompleted());
        int insignia = checkpointsReached * INSIGNIA_PER_CHECKPOINT;
        if (insignia == 0) {
            return;
        }

        event.getGame().warlordsPlayers().forEach(warlordsPlayer -> {
            warlordsPlayer.addCurrency(insignia);
            warlordsPlayer.sendMessage(Component.text("Checkpoint reward: ", NamedTextColor.GREEN)
                    .append(Component.text("❂ " + NumberFormat.addCommas(insignia), NamedTextColor.GOLD)));
        });
    }
}

package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.CurrencyOnEventOption;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class DunestarCurrencyOption extends CurrencyOnEventOption {

    private static final int INSIGNIA_PER_ENEMY_KILL = 250;
    private static final int INSIGNIA_PER_CHECKPOINT = 50_000;

    public DunestarCurrencyOption() {
        onKill(INSIGNIA_PER_ENEMY_KILL);
    }

    public static void grantCheckpointReward(Game game) {
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            warlordsPlayer.addCurrency(INSIGNIA_PER_CHECKPOINT);
            warlordsPlayer.sendMessage(Component.text("Checkpoint reward: ", NamedTextColor.GREEN)
                    .append(Component.text("❂ " + NumberFormat.addCommas(INSIGNIA_PER_CHECKPOINT), NamedTextColor.GOLD)));
        });
    }
}

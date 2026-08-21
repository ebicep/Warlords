package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.CurrencyOnEventOption;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class OpexCurrencyOption extends CurrencyOnEventOption {

    private static final int INSIGNIA_PER_ENEMY_KILL = 250;
    private static final int INSIGNIA_PER_RELIC = 50_000;

    public OpexCurrencyOption() {
        onKill(INSIGNIA_PER_ENEMY_KILL);
    }

    public static void grantRelicReward(Game game) {
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            warlordsPlayer.addCurrency(INSIGNIA_PER_RELIC);
            warlordsPlayer.sendMessage(Component.text("Relic reward: ", NamedTextColor.GREEN)
                    .append(Component.text("❂ " + NumberFormat.addCommas(INSIGNIA_PER_RELIC), NamedTextColor.GOLD)));
        });
    }
}

package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.CurrencyOnEventOption;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class EndlessParadoxCurrencyOption extends CurrencyOnEventOption {

    private static final int INSIGNIA_PER_ENEMY_KILL = 250;
    private static final int INSIGNIA_PER_FRAGMENT = 10_000;
    private static final int INSIGNIA_PER_BOSS = 50_000;

    public EndlessParadoxCurrencyOption() {
        onKill(INSIGNIA_PER_ENEMY_KILL);
    }

    public static void grantFragmentReward(Game game) {
        grantReward(game, "Fragment restored: ", INSIGNIA_PER_FRAGMENT);
    }

    public static void grantBossReward(Game game) {
        grantReward(game, "Timeline restored: ", INSIGNIA_PER_BOSS);
    }

    private static void grantReward(Game game, String prefix, int amount) {
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            warlordsPlayer.addCurrency(amount);
            warlordsPlayer.sendMessage(Component.text(prefix, NamedTextColor.GREEN)
                    .append(Component.text("❂ " + NumberFormat.addCommas(amount), NamedTextColor.GOLD)));
        });
    }
}

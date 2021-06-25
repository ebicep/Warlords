package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.entity.Player;

public class Inferno extends AbstractAbility {

    public Inferno() {
        super("Inferno", 0, 0, 46.98f, 0, 30, 30
        );
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Combust into a molten inferno,\n" +
                "§7increasing your Crit Chance by §c30%\n" +
                "§7and your Crit Multiplier by §c30%§7. Lasts\n" +
                "§618 §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.setInferno(18);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.inferno.activation", 2, 1);
        }
    }
}

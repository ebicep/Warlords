package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.ActionBarStats;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Inferno extends AbstractAbility {

    public Inferno() {
        super("Inferno", 0, 0, 3, 0, 30, 30,
                "§7Combust into a molten inferno,\n" +
                        "§7increasing your Crit Chance by §c30%\n" +
                        "§7and your Crit Multiplier by §c30%§7. Lasts\n" +
                        "§618 §7seconds.");
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.setInferno(18);

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "mage.inferno.activation", 1, 1);
        }
    }
}

package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.entity.Player;

public class Earthliving extends AbstractAbility {

    public Earthliving() {
        super("Earthliving Weapon", 0, 0, 16, 30, 25, 240,
                "§7Imbue your weapon with the power of the\n" +
                "§7Earth, causing each of your melee attacks\n" +
                "§7to have a §e40% §7chance to heal you and §e2\n" +
                "§7nearby allies for §a240% §7weapon damage.\n" +
                "§7Lasts §68 §7seconds.\n" + "\n" +
                "§7The first hit is guaranteed to activate Earthliving.");
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.subtractEnergy(energyCost);
        warlordsPlayer.setEarthlivingDuration(8);
        warlordsPlayer.setFirstProc(true);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.earthlivingweapon.activation", 2, 1);
        }
    }
}



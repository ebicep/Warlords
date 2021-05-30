package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.entity.Player;

public class ArcaneShield extends AbstractAbility {

    public ArcaneShield() {
        super("Arcane Shield", 0, 0, 32, 40, 0, 0,
                "§7Surround yourself with arcane\n" +
                "§7energy, creating a shield that will\n" +
                "§7absorb up to %dynamic.value% §7(§e50% §7of your maximum\n" +
                "§7health) incoming damage. Lasts §66 §7seconds.");
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.setArcaneShield(6);
        warlordsPlayer.setArcaneShieldHealth((int) (warlordsPlayer.getMaxHealth() * .5));
        warlordsPlayer.subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.arcaneshield.activation", 2, 1);
        }
    }
}

package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ArcaneShield extends AbstractAbility {

    public int maxShieldHealth;

    public ArcaneShield() {
        super("Arcane Shield", 0, 0, 31.32f, 40, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Surround yourself with arcane\n" +
                "§7energy, creating a shield that will\n" +
                "§7absorb up to §e" + maxShieldHealth + " §7(§e50% §7of your maximum\n" +
                "§7health) incoming damage. Lasts §66 §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.setArcaneShield(6);
        warlordsPlayer.setArcaneShieldHealth((int) (warlordsPlayer.getMaxHealth() * .5));
        warlordsPlayer.subtractEnergy(energyCost);
        ((EntityLiving) ((CraftPlayer) player).getHandle()).setAbsorptionHearts(20);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.arcaneshield.activation", 2, 1);
        }
    }
}

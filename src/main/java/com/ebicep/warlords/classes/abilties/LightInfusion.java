package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LightInfusion extends AbstractAbility {

    public LightInfusion(float cooldown) {
        super("Light Infusion", 0, 0, cooldown, -120, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7You become infused with light,\n" +
                "§7restoring §a120 §7energy and\n" +
                "§7increasing your movement speed by\n" +
                "§e40% §7for §63 §7seconds";
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.getSpeed().addSpeedModifier("Infusion", 40, 3 * 20);
        warlordsPlayer.setInfusion(3);
        warlordsPlayer.subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.infusionoflight.activation", 2, 1);
        }

        for (int i = 0; i < 10; i++) {
            Location particleLoc = player.getLocation().add(0, 1.5, 0);
            ParticleEffect.SPELL.display(1F, 0F, 1F, 0.3F, 5, particleLoc, 500);
        }
    }
}

package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LightInfusion extends AbstractAbility {

    public LightInfusion(int cooldown, String description) {
        super("Light Infusion", 0, 0, cooldown, -120, 0, 0, description);
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.getSpeed().changeCurrentSpeed("Infusion", 40, 3 * 20, "BASE");
        warlordsPlayer.setInfusion(3 * 20);
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

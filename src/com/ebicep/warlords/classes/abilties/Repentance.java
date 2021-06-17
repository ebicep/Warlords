package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Repentance extends AbstractAbility {

    public Repentance() {
        super("Repentance", 0, 0, 32, 20, 0, 0,
                "§7Taking damage empowers your damaging\n" +
                "§7abilities and melee hits, restoring health\n" +
                "§7and energy based on §c10 §7+ §c10% §7of the\n" +
                "§7damage you've recently took. Lasts §612 §7seconds.");
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.setRepentanceDuration(12);
        warlordsPlayer.setRepentanceCounter(warlordsPlayer.getRepentanceCounter() + 2000);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.barrieroflight.impact", 2, 1.35f);
        }

        Location playerLoc = player.getLocation();
        Location particleLoc = playerLoc.clone();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double angle = j / 10D * Math.PI * 2;
                double width = 1;
                particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                particleLoc.setY(playerLoc.getY() + i / 5D);
                particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255,255,255), particleLoc, 500);
            }
        }
    }
}

package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Matrix4d;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class LastStand extends AbstractAbility {

    public LastStand() {
        super("Last Stand", 0, 0, 56.38f, 40, 0, 0
        );
    }

    @Override
    public void updateDescription() {
        description = "§7Enter a defensive stance,\n" +
                "§7reducing all damage you take by\n" +
                "§c50% §7for §612 §7seconds and also\n" +
                "§7reduces all damage nearby allies take\n" +
                "§7by §c40% §7for §66 §7seconds. You are\n" +
                "§chealed §7for the amount of damage\n" +
                "§7prevented on allies.";
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.setLastStandedBy(warlordsPlayer);
        warlordsPlayer.setLastStandDuration(12);
        PlayerFilter.entitiesAround(warlordsPlayer, 4, 4, 4)
            .aliveTeammatesOfExcludingSelf(warlordsPlayer)
            .forEach((nearPlayer) -> {
                nearPlayer.setLastStandDuration(6);
                nearPlayer.setLastStandedBy(warlordsPlayer);
                player.sendMessage("you last standed " + nearPlayer.getName());
            });
        warlordsPlayer.subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.laststand.activation", 2, 1);
        }

        Location loc = player.getEyeLocation();
        loc.setPitch(0);
        loc.setYaw(0);
        Matrix4d matrix = new Matrix4d();
        for (int i = 0; i < 3; i++) {
            loc.setYaw(loc.getYaw() + 360F / 3F);
            matrix.updateFromLocation(loc);
            for (int c = 0; c < 20; c++) {
                double angle = c / 20D * Math.PI * 2;
                double width = 1.2;
                double distance = 3;

                ParticleEffect.FLAME.display(0, 0, 0, 0, 1,
                        matrix.translateVector(player.getWorld(), distance, Math.sin(angle) * width, Math.cos(angle) * width), 500);
            }

            for (int c = 0; c < 10; c++) {
                double angle = c / 10D * Math.PI * 2;
                double width = 0.6;
                double distance = 3;

                ParticleEffect.REDSTONE.display(0, 0, 0, 0, 1,
                        matrix.translateVector(player.getWorld(), distance, Math.sin(angle) * width, Math.cos(angle) * width), 500);
            }
        }
    }
}

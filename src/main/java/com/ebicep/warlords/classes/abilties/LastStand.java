package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.ClassesSkillBoosts;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.Matrix4d;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class LastStand extends AbstractAbility {

    private final int selfDuration = 12;
    private final int allyDuration = 6;
    private final int radius = 7;

    public LastStand() {
        super("Last Stand", 0, 0, 56.38f, 40, 0, 0
        );
    }

    @Override
    public void updateDescription(Player player) {
        int selfReduction = Classes.getSelectedBoost(player) == ClassesSkillBoosts.LAST_STAND ? 60 : 50;
        int allyReduction = Classes.getSelectedBoost(player) == ClassesSkillBoosts.LAST_STAND ? 50 : 40;
        description = "§7Enter a defensive stance,\n" +
                "§7reducing all damage you take by\n" +
                "§c" + selfReduction + "% §7for §6" + selfDuration + " §7seconds and also\n" +
                "§7reduces all damage nearby allies take\n" +
                "§7by §c" + allyReduction + "% §7for §6" + allyDuration + " §7seconds. You are\n" +
                "§7healed §7for the amount of damage\n" +
                "§7prevented on allies." +
                "\n\n" +
                "§7Has a maximum range of §e" + radius + " §7blocks.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        LastStand tempLastStand = new LastStand();
        wp.getCooldownManager().addCooldown(name, LastStand.this.getClass(), tempLastStand, "LAST", selfDuration, wp, CooldownTypes.BUFF);
        PlayerFilter.entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .forEach((nearPlayer) -> {
                    //green line thingy
                    Location lineLocation = player.getLocation().clone().add(0, 1, 0);
                    lineLocation.setDirection(lineLocation.toVector().subtract(nearPlayer.getLocation().add(0, 1, 0).toVector()).multiply(-1));
                    for (int i = 0; i < Math.floor(player.getLocation().distance(nearPlayer.getLocation())) * 2; i++) {
                        ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0.35F, 1, lineLocation, 500);
                        lineLocation.add(lineLocation.getDirection().multiply(.5));
                    }
                    nearPlayer.getCooldownManager().addCooldown(name, LastStand.this.getClass(), tempLastStand, "LAST", allyDuration, wp, CooldownTypes.BUFF);
                    player.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.GRAY + " Your Last Stand is now protecting " + ChatColor.YELLOW + nearPlayer.getName() + ChatColor.GRAY + "!");
                    nearPlayer.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.GRAY + " " + player.getName() + "'s " + ChatColor.YELLOW + "Last Stand" + ChatColor.GRAY + " is now protecting you for §66 §7seconds!");
                });

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

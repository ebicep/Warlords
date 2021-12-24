package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.ClassesSkillBoosts;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Berserk extends AbstractAbility {

    private final int duration = 18;
    private final int speedBuff = 30;

    public Berserk() {
        super("Berserk", 0, 0, 46.98f, 30, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7You go into a berserker rage,\n" +
                "§7increasing your damage by §c30% §7and\n" +
                "§7movement speed by §e" + speedBuff + "%§7. While active,\n" +
                "§7you also take §c10% §7more damage.\n" + "§7Lasts §6" + duration + " §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        double cooldown = Classes.getSelectedBoost(player) == ClassesSkillBoosts.BERSERK ? 37.5 : 46.98;
        setCooldown((float) cooldown);

        wp.subtractEnergy(energyCost);
        wp.getSpeed().addSpeedModifier("Berserk", speedBuff, duration * 20, "BASE");
        wp.getCooldownManager().addCooldown(name, Berserk.this.getClass(), new Berserk(), "BERS", duration, wp, CooldownTypes.BUFF);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.berserk.activation", 2, 1);
        }
        wp.getGame().getGameTasks().put(
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!wp.getCooldownManager().getCooldown(Berserk.class).isEmpty()) {
                            Location location = player.getLocation();
                            location.add(0, 2.1, 0);
                            ParticleEffect.VILLAGER_ANGRY.display(0, 0, 0, 0.1F, 1, location, 500);
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 3),
                System.currentTimeMillis()
        );
    }
}

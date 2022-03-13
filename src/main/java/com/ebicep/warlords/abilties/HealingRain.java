package com.ebicep.warlords.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Overheal;
import com.ebicep.warlords.effects.circle.AreaEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;


public class HealingRain extends AbstractAbility {

    private int duration = 12;
    private int radius = 8;

    public HealingRain() {
        super("Healing Rain", 100, 125, 52.85f, 50, 25, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Conjure rain at targeted\n" +
                "location that will restore §a" + format(minDamageHeal) + "\n" +
                "§7- §a" + format(maxDamageHeal) + " §7health every 0.5 seconds\n" +
                "to allies. Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "You may move Healing Rain to your location\n" +
                "using your SNEAK key." +
                "\n\n" +
                "§7Healing Rain can overheal allies for up to\n" +
                "§a10% §7of their max health as bonus health\n" +
                "§7for §6" + Overheal.OVERHEAL_DURATION + " §7seconds.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        if (player.getTargetBlock((Set<Material>) null, 25).getType() == Material.AIR) return false;

        Location location = player.getTargetBlock((Set<Material>) null, 25).getLocation().clone();

        wp.subtractEnergy(energyCost);
        wp.getCooldownManager().addRegularCooldown(name, "RAIN", HealingRain.class, new HealingRain(), wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, duration * 20);
        wp.getSpec().getOrange().setCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));

        Utils.playGlobalSound(location, "mage.healingrain.impact", 2, 1);

        CircleEffect circleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                location,
                radius,
                new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE),
                new AreaEffect(5, ParticleEffect.CLOUD).particlesPerSurface(0.025),
                new AreaEffect(5, ParticleEffect.DRIP_WATER).particlesPerSurface(0.025)
        );

        BukkitTask task = wp.getGame().registerGameTask(circleEffect::playEffects, 0, 1);

        location.add(0, 1, 0);

        BukkitTask rainSneakAbility = new GameRunnable(wp.getGame()) {
            boolean wasSneaking = false;

            @Override
            public void run() {
                if (!wp.getGame().isFrozen()) {
                    if (wp.isAlive() && wp.isSneaking() && !wasSneaking) {
                        wp.playSound(wp.getLocation(), "mage.timewarp.teleport", 2, 1.35f);
                        wp.sendMessage(WarlordsPlayer.RECEIVE_ARROW + " §7You moved your §aHealing Rain §7to your current location.");
                        location.setX(wp.getLocation().getX());
                        location.setY(wp.getLocation().getY());
                        location.setZ(wp.getLocation().getZ());
                    }

                    wasSneaking = wp.isSneaking();
                }
            }
        }.runTaskTimer(0, 0);

        new GameRunnable(wp.getGame()) {
            int counter = 0;
            int timeLeft = duration;

            @Override
            public void run() {
                if (!wp.getGame().isFrozen()) {
                    if (counter % 10 == 0) {
                        for (WarlordsPlayer teammateInRain : PlayerFilter
                                .entitiesAround(location, radius, radius, radius)
                                .aliveTeammatesOf(wp)
                        ) {
                            teammateInRain.addHealingInstance(
                                    wp,
                                    name,
                                    minDamageHeal,
                                    maxDamageHeal,
                                    critChance,
                                    critMultiplier,
                                    false,
                                    false);

                            if (teammateInRain != wp) {
                                teammateInRain.getCooldownManager().removeCooldown(Overheal.OVERHEAL_MARKER);
                                teammateInRain.getCooldownManager().addRegularCooldown("Overheal",
                                        "OVERHEAL", Overheal.class, Overheal.OVERHEAL_MARKER, wp, CooldownTypes.BUFF, cooldownManager -> {
                                        }, Overheal.OVERHEAL_DURATION * 20);
                            }
                        }

                        if (timeLeft < 0) {
                            this.cancel();
                            task.cancel();
                            rainSneakAbility.cancel();
                        }
                    }

                    if (counter % 20 == 0) {
                        timeLeft--;
                    }

                    counter--;
                }
            }

        }.runTaskTimer(0, 0);

        return true;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

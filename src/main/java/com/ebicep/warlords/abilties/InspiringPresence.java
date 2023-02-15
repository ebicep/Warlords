package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class InspiringPresence extends AbstractAbility {

    public int playersHit = 0;

    protected List<WarlordsEntity> playersAffected = new ArrayList<>();
    protected double energyGivenFromStrikeAndPresence = 0;

    private int speedBuff = 30;
    private double radius = 10;
    private int duration = 12;
    private int energyPerSecond = 10;

    public InspiringPresence() {
        super("Inspiring Presence", 0, 0, 60f + 10.47f, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Your presence on the battlefield inspires your allies, increasing their energy regeneration by §e" + energyPerSecond +
                " §7per second and their movement by §e" + speedBuff + "% §7for §6" + duration + " §7seconds." +
                "\n\nHas a maximum range of §e" + format(radius) + " §7blocks.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        Utils.playGlobalSound(player.getLocation(), "paladin.inspiringpresence.activation", 2, 1);

        Runnable cancelSpeed = wp.addSpeedModifier(wp, "Inspiring Presence", speedBuff, duration * 20, "BASE");

        InspiringPresence tempPresence = new InspiringPresence();
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "PRES",
                InspiringPresence.class,
                tempPresence,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    cancelSpeed.run();
                    ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.PORTABLE_ENERGIZER);
                },
                duration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.5, 0);
                        World world = location.getWorld();
                        world.spawnParticle(Particle.SMOKE_NORMAL, location, 1, 0.3, 0.3, 0.3, 0.02, null, true);
                        world.spawnParticle(Particle.SPELL, location, 2, 0.3, 0.3, 0.3, 0.5, null, true);
                    }
                })
        ) {
            @Override
            public float addEnergyGainPerTick(float energyGainPerTick) {
                tempPresence.addEnergyGivenFromStrikeAndPresence(energyPerSecond / 20d);
                return energyGainPerTick + energyPerSecond / 20f;
            }
        });

        if (pveUpgrade) {
            resetCooldowns(wp);
        }

        for (WarlordsEntity presenceTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
            playersHit++;
            tempPresence.getPlayersAffected().add(presenceTarget);
            if (pveUpgrade) {
                resetCooldowns(presenceTarget);
            }
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN +
                    ChatColor.GRAY + " Your Inspiring Presence inspired " +
                    ChatColor.YELLOW + presenceTarget.getName() +
                    ChatColor.GRAY + "!"
            );

            Runnable cancelAllySpeed = presenceTarget.addSpeedModifier(wp, "Inspiring Presence", speedBuff, duration * 20, "BASE");
            presenceTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "PRES",
                    InspiringPresence.class,
                    tempPresence,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                        cancelAllySpeed.run();
                    },
                    duration * 20
            ) {
                @Override
                public float addEnergyGainPerTick(float energyGainPerTick) {
                    tempPresence.addEnergyGivenFromStrikeAndPresence(energyPerSecond / 20d);
                    return energyGainPerTick + energyPerSecond / 20f;
                }
            });
        }

        return true;
    }

    public void addEnergyGivenFromStrikeAndPresence(double energyGivenFromStrikeAndPresence) {
        this.energyGivenFromStrikeAndPresence += energyGivenFromStrikeAndPresence;
    }

    private void resetCooldowns(WarlordsEntity we) {
        we.getRedAbility().subtractCooldown(30);
        we.getPurpleAbility().subtractCooldown(30);
        we.getBlueAbility().subtractCooldown(30);
        if (!we.getOrangeAbility().getName().equals("Inspiring Presence")) {
            we.getOrangeAbility().subtractCooldown(30);
        }
        we.updateItems();
    }

    public List<WarlordsEntity> getPlayersAffected() {
        return playersAffected;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getEnergyPerSecond() {
        return energyPerSecond;
    }

    public void setEnergyPerSecond(int energyPerSecond) {
        this.energyPerSecond = energyPerSecond;
    }


    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getEnergyGivenFromStrikeAndPresence() {
        return energyGivenFromStrikeAndPresence;
    }

    public int getSpeedBuff() {
        return speedBuff;
    }

    public void setSpeedBuff(int speedBuff) {
        this.speedBuff = speedBuff;
    }
}
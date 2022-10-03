package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class InspiringPresence extends AbstractAbility {
    private boolean pveUpgrade = false;

    private int speedBuff = 30;
    private double radius = 10;
    protected int playersHit = 0;
    private int duration = 12;
    private int energyPerSecond = 10;

    private List<WarlordsEntity> playersAffected = new ArrayList<>();
    private double energyGivenFromStrikeAndPresence = 0;

    public InspiringPresence() {
        super("Inspiring Presence", 0, 0, 60f + 10.47f, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Your presence on the battlefield\n" +
                "§7inspires your allies, increasing\n" +
                "§7their energy regeneration by §e" + energyPerSecond + "\n" +
                "§7per second and their movement\n" +
                "§7by §e" + speedBuff + "% §7for §6" + duration + " §7seconds." +
                "\n\n" +
                "§7Has a maximum range of §e" + radius + " §7blocks.";
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

        Runnable cancelSpeed = wp.getSpeed().addSpeedModifier("Inspiring Presence", speedBuff, duration * 20, "BASE");

        InspiringPresence tempPresence = new InspiringPresence();
        wp.getCooldownManager().addCooldown(new RegularCooldown<InspiringPresence>(
                name,
                "PRES",
                InspiringPresence.class,
                tempPresence,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    cancelSpeed.run();
                    ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.PORTABLE_ENERGIZER);
                },
                duration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.5, 0);
                        ParticleEffect.SMOKE_NORMAL.display(0.3F, 0.3F, 0.3F, 0.02F, 1, location, 500);
                        ParticleEffect.SPELL.display(0.3F, 0.3F, 0.3F, 0.5F, 2, location, 500);
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

            Runnable cancelAllySpeed = presenceTarget.getSpeed().addSpeedModifier("Inspiring Presence", speedBuff, duration * 20, "BASE");
            presenceTarget.getCooldownManager().addCooldown(new RegularCooldown<InspiringPresence>(
                    name,
                    "PRES",
                    InspiringPresence.class,
                    tempPresence,
                    wp,
                    CooldownTypes.ABILITY,
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

    private void resetCooldowns(WarlordsEntity we) {
        we.setRedCurrentCooldown(0);
        we.setPurpleCurrentCooldown(0);
        we.setBlueCurrentCooldown(0);
        if (!we.getOrangeAbility().getName().equals("Inspiring Presence")) {
            we.setOrangeCurrentCooldown(0);
        }
        we.updateItems();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<WarlordsEntity> getPlayersAffected() {
        return playersAffected;
    }

    public int getEnergyPerSecond() {
        return energyPerSecond;
    }

    public void setEnergyPerSecond(int energyPerSecond) {
        this.energyPerSecond = energyPerSecond;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void addEnergyGivenFromStrikeAndPresence(double energyGivenFromStrikeAndPresence) {
        this.energyGivenFromStrikeAndPresence += energyGivenFromStrikeAndPresence;
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
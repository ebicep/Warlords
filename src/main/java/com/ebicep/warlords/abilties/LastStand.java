package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class LastStand extends AbstractAbility {
    private final int radius = 7;
    protected int playersLastStanded = 0;
    private boolean pveUpgrade = false;
    private int selfDuration = 12;
    private int allyDuration = 6;
    private int selfDamageReductionPercent = 50;
    private int teammateDamageReductionPercent = 40;

    private float amountPrevented = 0;

    public LastStand() {
        super("Last Stand", 0, 0, 56.38f, 40);
    }

    public LastStand(int selfDamageReductionPercent, int teammateDamageReductionPercent) {
        super("Last Stand", 0, 0, 56.38f, 40);
        this.selfDamageReductionPercent = selfDamageReductionPercent;
        this.teammateDamageReductionPercent = teammateDamageReductionPercent;
    }

    @Override
    public void updateDescription(Player player) {
        description = "Enter a defensive stance, reducing all damage you take by §c" + selfDamageReductionPercent +
                "% §7for §6" + selfDuration + " §7seconds and also reduces all damage nearby allies take by §c" + teammateDamageReductionPercent +
                "% §7for §6" + allyDuration + " §7seconds. You are healed §7for the amount of damage prevented on allies." +
                "\n\nHas a maximum range of §e" + radius + " §7blocks.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Last Standed", "" + playersLastStanded));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "warrior.laststand.activation", 2, 1);

        LastStand tempLastStand = new LastStand(selfDamageReductionPercent, teammateDamageReductionPercent);
        wp.getCooldownManager().addCooldown(new RegularCooldown<LastStand>(
                name,
                "LAST",
                LastStand.class,
                tempLastStand,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.HARDENED_SCALES);
                },
                selfDuration * 20
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float afterValue = currentDamageValue * (100 - selfDamageReductionPercent) / 100f;
                tempLastStand.addAmountPrevented(currentDamageValue - afterValue);
                return afterValue;
            }
        });

        for (WarlordsEntity standTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
            playersLastStanded++;

            EffectUtils.playParticleLinkAnimation(wp.getLocation(), standTarget.getLocation(), ParticleEffect.VILLAGER_HAPPY);
            standTarget.getCooldownManager().addCooldown(new RegularCooldown<LastStand>(
                    name,
                    "LAST",
                    LastStand.class,
                    tempLastStand,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    allyDuration * 20
            ) {
                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * (100 - teammateDamageReductionPercent) / 100f;
                }

                @Override
                public void onShieldFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    tempLastStand.addAmountPrevented(currentDamageValue);
                    wp.addAbsorbed(currentDamageValue);
                    wp.addHealingInstance(
                            wp,
                            name,
                            currentDamageValue,
                            currentDamageValue,
                            isCrit ? 100 : 0,
                            100,
                            false,
                            true
                    );
                }

                @Override
                public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    tempLastStand.addAmountPrevented(currentDamageValue);
                    wp.addAbsorbed(currentDamageValue);
                    wp.addHealingInstance(
                            wp,
                            name,
                            currentDamageValue,
                            currentDamageValue,
                            isCrit ? 100 : 0,
                            100,
                            false,
                            false
                    );
                }
            });

            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN +
                    ChatColor.GRAY + " Your Last Stand is now protecting " +
                    ChatColor.YELLOW + standTarget.getName() +
                    ChatColor.GRAY + "!"
            );

            standTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN +
                    ChatColor.GRAY + " " + wp.getName() + "'s " +
                    ChatColor.YELLOW + "Last Stand" +
                    ChatColor.GRAY + " is now protecting you for §6" + allyDuration + " §7seconds!"
            );
        }

        if (pveUpgrade) {
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(wp, radius, radius, radius)
                    .aliveEnemiesOf(wp)
                    .closestFirst(wp)
            ) {
                EffectUtils.playSphereAnimation(wp.getLocation(), radius + 2, ParticleEffect.FLAME, 1);
                Utils.addKnockback(wp.getLocation(), we, -2.5, 0.2f);
            }
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
                        matrix.translateVector(player.getWorld(), distance, Math.sin(angle) * width, Math.cos(angle) * width), 500
                );
            }

            for (int c = 0; c < 10; c++) {
                double angle = c / 10D * Math.PI * 2;
                double width = 0.6;
                double distance = 3;

                ParticleEffect.REDSTONE.display(0, 0, 0, 0, 1,
                        matrix.translateVector(player.getWorld(), distance, Math.sin(angle) * width, Math.cos(angle) * width), 500
                );
            }
        }

        return true;
    }

    public void addAmountPrevented(float amountPrevented) {
        this.amountPrevented += amountPrevented;
    }

    public float getSelfDamageReduction() {
        return selfDamageReductionPercent;
    }

    public void setSelfDamageReductionPercent(int selfDamageReductionPercent) {
        this.selfDamageReductionPercent = selfDamageReductionPercent;
    }

    public float getTeammateDamageReduction() {
        return teammateDamageReductionPercent;
    }

    public void setTeammateDamageReductionPercent(int teammateDamageReductionPercent) {
        this.teammateDamageReductionPercent = teammateDamageReductionPercent;
    }

    public float getAmountPrevented() {
        return amountPrevented;
    }

    public int getSelfDuration() {
        return selfDuration;
    }

    public void setSelfDuration(int selfDuration) {
        this.selfDuration = selfDuration;
    }

    public int getAllyDuration() {
        return allyDuration;
    }

    public void setAllyDuration(int allyDuration) {
        this.allyDuration = allyDuration;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}

package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class LastStand extends AbstractAbility {

    private final int selfDuration = 12;
    private final int allyDuration = 6;
    private final int radius = 7;
    private int selfDamageReductionPercent = 50;
    private int teammateDamageReductionPercent = 40;

    public LastStand() {
        super("Last Stand", 0, 0, 56.38f, 40, 0, 0);
    }

    public LastStand(int selfDamageReductionPercent, int teammateDamageReductionPercent) {
        super("Last Stand", 0, 0, 56.38f, 40, 0, 0);
        this.selfDamageReductionPercent = selfDamageReductionPercent;
        this.teammateDamageReductionPercent = teammateDamageReductionPercent;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Enter a defensive stance,\n" +
                "§7reducing all damage you take by\n" +
                "§c" + selfDamageReductionPercent + "% §7for §6" + selfDuration + " §7seconds and also\n" +
                "§7reduces all damage nearby allies take\n" +
                "§7by §c" + teammateDamageReductionPercent + "% §7for §6" + allyDuration + " §7seconds. You are\n" +
                "§7healed §7for the amount of damage\n" +
                "§7prevented on allies." +
                "\n\n" +
                "§7Has a maximum range of §e" + radius + " §7blocks.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        LastStand tempLastStand = new LastStand(selfDamageReductionPercent, teammateDamageReductionPercent);
        Utils.playGlobalSound(player.getLocation(), "warrior.laststand.activation", 2, 1);

        wp.getCooldownManager().addCooldown(new RegularCooldown<LastStand>(
                name,
                "LAST",
                LastStand.class,
                tempLastStand,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                selfDuration * 20
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * getSelfDamageReduction();
            }
        });

        for (WarlordsPlayer standTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
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
                    return currentDamageValue * getTeammateDamageReduction();
                }

                @Override
                public void onShieldFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    wp.addAbsorbed(currentDamageValue);
                    wp.addHealingInstance(
                            wp,
                            name,
                            currentDamageValue,
                            currentDamageValue,
                            isCrit ? 100 : -1,
                            100,
                            false,
                            true
                    );
                }

                @Override
                public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    wp.addAbsorbed(currentDamageValue);
                    wp.addHealingInstance(
                            wp,
                            name,
                            currentDamageValue,
                            currentDamageValue,
                            isCrit ? 100 : -1,
                            100,
                            false,
                            false
                    );
                }
            });

            player.sendMessage(
                WarlordsPlayer.GIVE_ARROW_GREEN +
                        ChatColor.GRAY + " Your Last Stand is now protecting " +
                        ChatColor.YELLOW + standTarget.getName() +
                        ChatColor.GRAY + "!"
            );

            standTarget.sendMessage(
                    WarlordsPlayer.RECEIVE_ARROW_GREEN +
                            ChatColor.GRAY + " " + player.getName() + "'s " +
                            ChatColor.YELLOW + "Last Stand" +
                            ChatColor.GRAY + " is now protecting you for §6" + allyDuration + " §7seconds!"
            );
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

        return true;
    }

    public float getSelfDamageReduction() {
        return (100 - selfDamageReductionPercent) / 100f;
    }

    public void setSelfDamageReductionPercent(int selfDamageReductionPercent) {
        this.selfDamageReductionPercent = selfDamageReductionPercent;
    }

    public float getTeammateDamageReduction() {
        return (100 - selfDamageReductionPercent) / 100f;
    }

    public void setTeammateDamageReductionPercent(int teammateDamageReductionPercent) {
        this.teammateDamageReductionPercent = teammateDamageReductionPercent;
    }
}

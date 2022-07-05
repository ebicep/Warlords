package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TimeWarp extends AbstractAbility {
    private boolean pveUpgrade = false;
    protected int timesSuccessful = 0;

    private final int duration = 5;
    private int warpHealPercentage = 30;

    public TimeWarp() {
        super("Time Warp", 0, 0, 28.19f, 30, -1, 100);
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Successful", "" + timesSuccessful));

        return info;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Activate to place a time rune on\n" +
                "§7the ground. After §6" + duration + " §7seconds,\n" +
                "§7you will warp back to that location\n" +
                "§7and restore §a" + warpHealPercentage + "% §7of your health";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);
        Utils.playGlobalSound(player.getLocation(), "mage.timewarp.activation", 3, 1);

        Location warpLocation = wp.getLocation();
        List<Location> warpTrail = new ArrayList<>();
        wp.getCooldownManager().addRegularCooldown(
                name,
                "TIME",
                TimeWarp.class,
                new TimeWarp(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (wp.isDead() || wp.getGame().getState() instanceof EndState) return;
                    timesSuccessful++;

                    Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.teleport", 1, 1);

                    wp.addHealingInstance(
                            wp,
                            name,
                            wp.getMaxHealth() * (warpHealPercentage / 100f),
                            wp.getMaxHealth() * (warpHealPercentage / 100f),
                            -1,
                            100,
                            false,
                            false
                    );

                    wp.getEntity().teleport(warpLocation);
                    warpTrail.clear();
                },
                duration * 20,
                (cooldown, ticksLeft, counter) -> {
                    if (counter % 4 == 0) {
                        for (Location location : warpTrail) {
                            ParticleEffect.SPELL_WITCH.display(0.01f, 0, 0.01f, 0.001f, 1, location, 500);
                        }

                        warpTrail.add(wp.getLocation());
                        ParticleEffect.SPELL_WITCH.display(0.1f, 0, 0.1f, 0.001f, 4, warpLocation, 500);

                        int points = 6;
                        double radius = 0.5d;
                        for (int e = 0; e < points; e++) {
                            double angle = 2 * Math.PI * e / points;
                            Location point = warpLocation.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
                            ParticleEffect.CLOUD.display(0.1F, 0, 0.1F, 0.001F, 1, point, 500);
                        }
                    }
                }
        );

        if (pveUpgrade) {
            int startingBlocksTravelled = wp.getBlocksTravelledCM();
            wp.getSpeed().addSpeedModifier("Time Warp Speed", 20, duration * 20);
            wp.getCooldownManager().addCooldown(new RegularCooldown<TimeWarp>(
                    name,
                    null,
                    TimeWarp.class,
                    new TimeWarp(),
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    duration * 20
            ) {
                @Override
                public boolean distinct() {
                    return true;
                }

                @Override
                public int addCritChanceFromAttacker(WarlordsDamageHealingEvent event, int currentCritChance) {
                    if (event.getAbility().isEmpty() || event.getAbility().equals("Time Warp")) {
                        return currentCritChance;
                    }
                    return currentCritChance + ((event.getAttacker().getBlocksTravelledCM() - startingBlocksTravelled) / 100);
                }

                @Override
                public int addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, int currentCritMultiplier) {
                    if (event.getAbility().isEmpty() || event.getAbility().equals("Time Warp")) {
                        return currentCritMultiplier;
                    }
                    return currentCritMultiplier + ((event.getAttacker().getBlocksTravelledCM() - startingBlocksTravelled) / 100);
                }
            });
        }
        return true;
    }

    public void setWarpHealPercentage(int warpHealPercentage) {
        this.warpHealPercentage = warpHealPercentage;
    }

    public int getTimesSuccessful() {
        return timesSuccessful;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}

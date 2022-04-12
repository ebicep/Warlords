package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TimeWarp extends AbstractAbility {
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
    public boolean onActivate(WarlordsPlayer wp, Player player) {
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
                (cooldown, ticksLeft) -> {
                    if (ticksLeft % 4 == 0) {
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

        return true;
    }

    public void setWarpHealPercentage(int warpHealPercentage) {
        this.warpHealPercentage = warpHealPercentage;
    }

    public int getTimesSuccessful() {
        return timesSuccessful;
    }
}

package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class InspiringPresence extends AbstractAbility {
    protected int playersHit = 0;

    private int duration = 12;
    private final int speedBuff = 30;
    private final int radius = 10;
    private List<WarlordsPlayer> playersEffected = new ArrayList<>();

    public InspiringPresence() {
        super("Inspiring Presence", 0, 0, 60f + 10.47f, 0, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Your presence on the battlefield\n" +
                "§7inspires your allies, increasing\n" +
                "§7their energy regeneration by §e10\n" +
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
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        InspiringPresence tempPresence = new InspiringPresence();
        Utils.playGlobalSound(player.getLocation(), "paladin.inspiringpresence.activation", 2, 1);

        Runnable cancelSpeed = wp.getSpeed().addSpeedModifier("Inspiring Presence", speedBuff, duration * 20, "BASE");
        wp.getCooldownManager().addRegularCooldown(
                name,
                "PRES",
                InspiringPresence.class,
                tempPresence,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    cancelSpeed.run();
                },
                duration * 20
        );

        for (WarlordsPlayer presenceTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
            playersHit++;
            tempPresence.getPlayersEffected().add(presenceTarget);
            wp.sendMessage(
                WarlordsPlayer.GIVE_ARROW_GREEN +
                ChatColor.GRAY + " Your Inspiring Presence inspired " +
                ChatColor.YELLOW + presenceTarget.getName() +
                ChatColor.GRAY + "!"
            );

            Runnable cancelAllySpeed = presenceTarget.getSpeed().addSpeedModifier("Inspiring Presence", speedBuff, duration * 20, "BASE");
            presenceTarget.getCooldownManager().addRegularCooldown(
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
            );
        }

        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (wp.getCooldownManager().hasCooldown(tempPresence)) {
                    Location location = wp.getLocation();
                    location.add(0, 1.5, 0);
                    ParticleEffect.SMOKE_NORMAL.display(0.3F, 0.3F, 0.3F, 0.02F, 1, location, 500);
                    ParticleEffect.SPELL.display(0.3F, 0.3F, 0.3F, 0.5F, 2, location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 4);

        return true;
    }

    public float getDuration() {
        return duration;
    }

    public void decrementDuration() {
        this.duration -= .05;
    }

    public List<WarlordsPlayer> getPlayersEffected() {
        return playersEffected;
    }
}
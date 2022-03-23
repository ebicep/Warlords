package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class InspiringPresence extends AbstractAbility {

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
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        InspiringPresence tempPresence = new InspiringPresence();
        wp.getCooldownManager().addRegularCooldown(name, "PRES", InspiringPresence.class, tempPresence, wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, duration * 20);
        wp.getSpeed().addSpeedModifier("Inspiring Presence", speedBuff, duration * 20, "BASE");
        PlayerFilter.entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .forEach((nearPlayer) -> {
                    tempPresence.getPlayersEffected().add(nearPlayer);
                    wp.sendMessage(WarlordsPlayer.GIVE_ARROW_GREEN + ChatColor.GRAY + " Your Inspiring Presence inspired " + ChatColor.YELLOW + nearPlayer.getName() + ChatColor.GRAY + "!");
                    nearPlayer.getSpeed().addSpeedModifier("Inspiring Presence", speedBuff, duration * 20, "BASE");
                    nearPlayer.getCooldownManager().addRegularCooldown(name, "PRES", InspiringPresence.class, tempPresence, wp, CooldownTypes.ABILITY, cooldownManager -> {
                    }, duration * 20);
                });

        Utils.playGlobalSound(player.getLocation(), "paladin.inspiringpresence.activation", 2, 1);

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
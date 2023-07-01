package com.ebicep.warlords.abilities;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Consecrate extends AbstractAbility implements Duration {

    public int strikesBoosted = 0;
    public int playersHit = 0;

    private int strikeDamageBoost;
    private float radius;
    private Location location;
    private int tickDuration = 100;

    public Consecrate(float minDamageHeal, float maxDamageHeal, float energyCost, float critChance, float critMultiplier, int strikeDamageBoost, float radius) {
        super("Consecrate", minDamageHeal, maxDamageHeal, 7.83f, energyCost, critChance, critMultiplier);
        this.strikeDamageBoost = strikeDamageBoost;
        this.radius = radius;
    }

    public Consecrate(
            float minDamageHeal,
            float maxDamageHeal,
            float energyCost,
            float critChance,
            float critMultiplier,
            int strikeDamageBoost,
            float radius,
            Location location
    ) {
        super("Consecrate", minDamageHeal, maxDamageHeal, 7.83f, energyCost, critChance, critMultiplier);
        this.strikeDamageBoost = strikeDamageBoost;
        this.radius = radius;
        this.location = location;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Consecrate the ground below your feet, declaring it sacred. Enemies standing on it will take ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage per second and take "))
                               .append(Component.text(strikeDamageBoost + "%", NamedTextColor.RED))
                               .append(Component.text(" increased damage from your paladin strikes. Has a radius of "))
                               .append(Component.text(format(radius), NamedTextColor.YELLOW))
                               .append(Component.text(" blocks. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));
        info.add(new Pair<>("Strikes Boosted", "" + strikesBoosted));
        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);

        Location location = player.getLocation().clone();

        Utils.playGlobalSound(location, "paladin.consecrate.activation", 2, 1);
        CircleEffect circleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                location,
                radius,
                new CircumferenceEffect(Particle.VILLAGER_HAPPY, Particle.REDSTONE),
                new DoubleLineEffect(Particle.SPELL)
        );
        BukkitTask effectTask = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), circleEffect::playEffects, 0, 1);

        wp.getCooldownManager().addRegularCooldown(
                name,
                null,
                Consecrate.class,
                new Consecrate(minDamageHeal, maxDamageHeal, energyCost.getCurrentValue(), critChance, critMultiplier, strikeDamageBoost, radius, location),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    effectTask.cancel();
                },
                false,
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 20 == 0) {
                        PlayerFilter.entitiesAround(location, radius, 6, radius)
                                    .aliveEnemiesOf(wp)
                                    .forEach(enemy -> {
                                        playersHit++;
                                        enemy.addDamageInstance(
                                                wp,
                                                name,
                                                minDamageHeal,
                                                maxDamageHeal,
                                                critChance,
                                                critMultiplier
                                        );
                                    });
                    }
                })
        );

        return true;
    }

    public void addStrikesBoosted() {
        strikesBoosted++;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Location getLocation() {
        return location;
    }

    public int getStrikeDamageBoost() {
        return strikeDamageBoost;
    }

    public void setStrikeDamageBoost(int strikeDamageBoost) {
        this.strikeDamageBoost = strikeDamageBoost;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }
}

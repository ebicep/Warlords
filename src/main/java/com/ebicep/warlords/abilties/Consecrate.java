package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Consecrate extends AbstractAbility {
    protected int strikesBoosted = 0;
    protected int playersHit = 0;

    protected int strikeDamageBoost;
    protected float radius;
    protected Location location;

    private int duration = 5;

    public Consecrate(float minDamageHeal, float maxDamageHeal, float energyCost, float critChance, float critMultiplier, int strikeDamageBoost, float radius) {
        super("Consecrate", minDamageHeal, maxDamageHeal, 7.83f, energyCost, critChance, critMultiplier);
        this.strikeDamageBoost = strikeDamageBoost;
        this.radius = radius;
    }

    public Consecrate(float minDamageHeal, float maxDamageHeal, float energyCost, float critChance, float critMultiplier, int strikeDamageBoost, float radius, Location location) {
        super("Consecrate", minDamageHeal, maxDamageHeal, 7.83f, energyCost, critChance, critMultiplier);
        this.strikeDamageBoost = strikeDamageBoost;
        this.radius = radius;
        this.location = location;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Consecrate the ground below your\n" +
                "§7feet, declaring it sacred. Enemies\n" +
                "§7standing on it will take §c" + format(minDamageHeal) + " §7-\n" +
                "§c" + format(maxDamageHeal) + " §7damage per second and\n" +
                "§7take §c" + strikeDamageBoost + "% §7increased damage from\n" +
                "§7your paladin strikes. Lasts §65\n" +
                "§7seconds.";
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
                new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE),
                new DoubleLineEffect(ParticleEffect.SPELL)
        );
        BukkitTask effectTask = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), circleEffect::playEffects, 0, 1);

        wp.getCooldownManager().addRegularCooldown(
                name,
                null,
                Consecrate.class,
                new Consecrate(minDamageHeal, maxDamageHeal, energyCost, critChance, critMultiplier, strikeDamageBoost, radius, location),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    effectTask.cancel();
                },
                duration * 20,
                (cooldown, ticksLeft, counter) -> {
                    if (counter % 20 == 0) {
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
                                            critMultiplier,
                                            false);
                                });
                    }
                }
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractConsecrate extends AbstractAbility implements RedAbilityIcon, Duration, HitBox {

    public int strikesBoosted = 0;
    public int playersHit = 0;

    protected int strikeDamageBoost;
    protected FloatModifiable hitBox;
    protected int tickDuration;
    protected Location location;

    public AbstractConsecrate(
            float minDamageHeal,
            float maxDamageHeal,
            float energyCost,
            float critChance,
            float critMultiplier,
            int strikeDamageBoost,
            float hitBox,
            int duration
    ) {
        super("Consecrate", minDamageHeal, maxDamageHeal, 7.83f, energyCost, critChance, critMultiplier);
        this.strikeDamageBoost = strikeDamageBoost;
        this.hitBox = new FloatModifiable(hitBox);
        this.tickDuration = duration * 20;
    }

    public AbstractConsecrate(
            float minDamageHeal,
            float maxDamageHeal,
            float energyCost,
            float critChance,
            float critMultiplier,
            int strikeDamageBoost,
            float hitBox,
            int duration,
            Location location
    ) {
        super("Consecrate", minDamageHeal, maxDamageHeal, 7.83f, energyCost, critChance, critMultiplier);
        this.strikeDamageBoost = strikeDamageBoost;
        this.hitBox = new FloatModifiable(hitBox);
        this.location = location;
        this.tickDuration = duration * 20;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Consecrate the ground below your feet, declaring it sacred. Enemies standing on it will take ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage per second and take "))
                               .append(Component.text(strikeDamageBoost + "%", NamedTextColor.RED))
                               .append(Component.text(" increased damage from your paladin strikes. Has a radius of "))
                               .append(Component.text(format(hitBox.getCalculatedValue()), NamedTextColor.YELLOW))
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
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Location location = wp.getLocation().clone();

        Utils.playGlobalSound(location, "paladin.consecrate.activation", 2, 1);
        float radius = hitBox.getCalculatedValue();
        CircleEffect circleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                location,
                radius,
                new CircumferenceEffect(Particle.VILLAGER_HAPPY, Particle.REDSTONE),
                new DoubleLineEffect(Particle.SPELL)
        );

        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                null,
                AbstractConsecrate.class,
                createConsecrate(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                },
                false,
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 2 == 0) {
                        circleEffect.playEffects();
                    }
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
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (!event.getAbility().equals(getStrikeName()) || event.getFlags().contains(InstanceFlags.STRIKE_IN_CONS)) {
                    return currentDamageValue;
                }
                boolean insideCons = location.distanceSquared(event.getWarlordsEntity().getLocation()) < radius * radius;
                if (!insideCons) {
                    return currentDamageValue;
                }
                event.getFlags().add(InstanceFlags.STRIKE_IN_CONS);
                addStrikesBoosted();
                return currentDamageValue * convertToMultiplicationDecimal(strikeDamageBoost);
            }
        });

        return true;
    }

    @Nonnull
    public abstract AbstractConsecrate createConsecrate();

    @Nonnull
    public abstract String getStrikeName();

    public void addStrikesBoosted() {
        strikesBoosted++;
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return hitBox;
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

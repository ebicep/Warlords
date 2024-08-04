package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BeaconOfLight extends AbstractBeaconAbility<BeaconOfLight, BeaconOfLight.BeaconOfLightData> implements Heals<BeaconOfLight.HealingValues>, AbilityStats<BeaconOfLight, BeaconOfLight.BeaconOfLightStats> {

    private final HealingValues healingValues = new HealingValues();
    private final BeaconOfLightStats stats = new BeaconOfLightStats();

    public BeaconOfLight() {
        super("Beacon of Light", 20, 40, 4, 20);
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public Component getBonusDescription() {
        return AbilityDescriptionBuilder
                .create("All allies within a ")
                .blocks(radius.getCalculatedValue())
                .text(" radius restore ")
                .heal(healingValues.beaconHealing)
                .text("  health every 2 seconds.")
                .build();
    }

    @Override
    public Class<BeaconOfLightData> getDataClass() {
        return BeaconOfLightData.class;
    }

    @Override
    public LineEffect getLineEffect(Location target) {
        return new LineEffect(target, Particle.REDSTONE, new Particle.DustOptions(Color.fromRGB(255, 255, 0), 1));
    }

    @Override
    public BeaconOfLightData getDataObject(WarlordsEntity wp, ArmorStand beacon, Location groundLocation, CircleEffect effect, float radius) {
        return new BeaconOfLightData(beacon, groundLocation, effect, radius);
    }

    @Override
    public String getAbbreviation() {
        return "LIGHT BEACON";
    }

    @Override
    public void whileActive(@Nonnull WarlordsEntity wp, RegularCooldown<BeaconOfLightData> cooldown, Integer ticksLeft, Integer ticksElapsed) {
        if (ticksElapsed % 40 == 0) {
            BeaconData beacon = cooldown.getCooldownObject();
            float rad = radius.getCalculatedValue();
            for (WarlordsEntity allyTarget : PlayerFilter
                    .entitiesAround(beacon.getGroundLocation(), rad, rad, rad)
                    .aliveTeammatesOf(wp)
            ) {
                allyTarget.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(wp)
                        .min(healingValues.beaconHealing.getMinValue() * (wp.getCooldownManager().hasCooldown(DivineBlessing.class) ? 1.5f : 1))
                        .max(healingValues.beaconHealing.getMaxValue() * (wp.getCooldownManager().hasCooldown(DivineBlessing.class) ? 1.5f : 1))
                        .crit(healingValues.beaconHealing)
                );
            }
        }
    }

    @Override
    public BeaconOfLightStats getAbilityStats() {
        return stats;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.RangedValueCritable beaconHealing = new Value.RangedValueCritable(170, 230, 25, 175);
        private final List<Value> values = List.of(beaconHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class BeaconOfLightData extends BeaconData {

        public BeaconOfLightData(ArmorStand beacon, Location groundLocation, CircleEffect effect, float radius) {
            super(beacon, groundLocation, effect, radius);
        }

    }

    public static class BeaconOfLightStats extends AbstractAbilityStats<BeaconOfLight, BeaconOfLightStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public BeaconOfLightStats merge(BeaconOfLightStats other, int multiplier) {
            BeaconOfLightStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<BeaconOfLightStats> getClazz() {
            return BeaconOfLightStats.class;
        }

        @Override
        public BeaconOfLightStats create() {
            return new BeaconOfLightStats();
        }
    }
}
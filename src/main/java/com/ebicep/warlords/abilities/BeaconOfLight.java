package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractBeaconAbility;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BeaconOfLight extends AbstractBeaconAbility<BeaconOfLight> implements Heals<BeaconOfLight.HealingValues> {

    private final HealingValues healingValues = new HealingValues();

    public BeaconOfLight() {
        this(null, null);
    }

    public BeaconOfLight(Location location, CircleEffect effect) {
        super("Beacon of Light", 20, 40, location, 4, 20, effect);
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Place a stationary beacon on the ground that lasts ")
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. All allies within a "))
                               .append(Component.text(radius.getCalculatedValue(), NamedTextColor.YELLOW))
                               .append(Component.text(" block radius restore "))
                               .append(Heals.formatHealing(healingValues.beaconHealing))
                               .append(Component.text(" health every 2 seconds." +
                                       "Only 2 beacons can be on the field at once (Including both Beacon of Light and Impair)."));
    }

    @Override
    public Component getBonusDescription() {
        return Component.text("All allies within a ")
                        .append(Component.text(radius.getCalculatedValue(), NamedTextColor.YELLOW))
                        .append(Component.text(" block radius restore "))
                        .append(Heals.formatHealing(healingValues.beaconHealing))
                        .append(Component.text("  health every 2 seconds."));
    }

    @Override
    public LineEffect getLineEffect(Location target) {
        return new LineEffect(target, Particle.REDSTONE, new Particle.DustOptions(Color.fromRGB(255, 255, 0), 1));
    }

    @Override
    public String getAbbreviation() {
        return "LIGHT BEACON";
    }

    @Override
    public Class<BeaconOfLight> getBeaconClass() {
        return BeaconOfLight.class;
    }

    @Override
    public BeaconOfLight getObject(WarlordsEntity warlordsEntity, Location groundLocation, CircleEffect effect) {
        return new BeaconOfLight(groundLocation, effect);
    }

    @Override
    public ArmorStand getCrystal() {
        return null;
    }

    @Override
    public void whileActive(@Nonnull WarlordsEntity wp, RegularCooldown<BeaconOfLight> cooldown, Integer ticksLeft, Integer ticksElapsed) {
        if (ticksElapsed % 40 == 0) {
            BeaconOfLight beacon = cooldown.getCooldownObject();
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
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        return info;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.RangedValueCritable beaconHealing = new Value.RangedValueCritable(170, 230, 25, 175);
        private final List<Value> values = List.of(beaconHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}

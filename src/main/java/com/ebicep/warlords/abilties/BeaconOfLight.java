package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractBeaconAbility;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class BeaconOfLight extends AbstractBeaconAbility<BeaconOfLight> {

    public BeaconOfLight() {
        this(null);
    }

    public BeaconOfLight(Location location) {
        super("Beacon of Light", 150, 150, 20, 60, 25, 175, location, 5, 30);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Place a stationary beacon on the ground that lasts ")
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. All allies within a " + radius + " block radius restore "))
                               .append(Component.text(minDamageHeal, NamedTextColor.GREEN))
                               .append(Component.text("  health every 2 seconds. You heal for double the amount. " +
                                       "Only 2 beacons can be on the field at once (Including both Beacon of Light and Impair)."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public LineEffect getLineEffect(Location target) {
        return new LineEffect(target, Particle.REDSTONE, new Particle.DustOptions(Color.fromRGB(255, 255, 0), 1));
    }

    @Override
    public String getAbbreviation() {
        return "LBEACON";
    }

    @Override
    public Class<BeaconOfLight> getBeaconClass() {
        return BeaconOfLight.class;
    }

    @Override
    public BeaconOfLight getObject(Location groundLocation) {
        return new BeaconOfLight(groundLocation);
    }

    @Override
    public Material getGlassMaterial() {
        return Material.GREEN_STAINED_GLASS;
    }


    @Override
    public void whileActive(@Nonnull WarlordsEntity wp, RegularCooldown<BeaconOfLight> cooldown, Integer ticksLeft, Integer ticksElapsed) {
        if (ticksElapsed % 40 == 0) {
            BeaconOfLight beacon = cooldown.getCooldownObject();
            for (WarlordsEntity allyTarget : PlayerFilter
                    .entitiesAround(beacon.getGroundLocation(), radius, radius, radius)
                    .aliveTeammatesOf(wp)
            ) {
                boolean isSelf = wp == allyTarget;
                allyTarget.addHealingInstance(
                        wp,
                        name,
                        minDamageHeal * (isSelf ? 2 : 1),
                        maxDamageHeal * (isSelf ? 2 : 1),
                        critChance,
                        critMultiplier,
                        false,
                        false
                );
            }
        }
    }


}

package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractBeaconAbility;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class BeaconOfImpair extends AbstractBeaconAbility<BeaconOfImpair> {

    private int damageIncrease = 20;

    public BeaconOfImpair() {
        this(null);
    }

    public BeaconOfImpair(Location location) {
        super("Beacon of Impair", 145, 224, 20, 80, 25, 200, location, 5, 30);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Place a stationary beacon on the ground that lasts ")
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. All allies within a " + radius + " block radius have their damage increased by "))
                               .append(Component.text(damageIncrease + "%", NamedTextColor.RED))
                               .append(Component.text(". Enemies in range have their damage dealt reduced by "))
                               .append(Component.text(minDamageHeal, NamedTextColor.GREEN))
                               .append(Component.text("  health every 2 seconds. You heal for double the amount. " +
                                       "Only 2 beacons can be on the field at once (Including both Beacon of Light and Impair)."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public String getAbbreviation() {
        return "IBEACON";
    }

    @Override
    public Class<BeaconOfImpair> getBeaconClass() {
        return BeaconOfImpair.class;
    }

    @Override
    public BeaconOfImpair getObject(Location groundLocation) {
        return new BeaconOfImpair(groundLocation);
    }

    @Override
    public Material getGlassMaterial() {
        return Material.RED_STAINED_GLASS;
    }

    @Override
    public LineEffect getLineEffect(Location target) {
        return new LineEffect(target, Particle.REDSTONE, new Particle.DustOptions(Color.fromRGB(255, 165, 0), 1));
    }

    @Override
    public void whileActive(@Nonnull WarlordsEntity wp, RegularCooldown<BeaconOfImpair> cooldown, Integer ticksLeft, Integer ticksElapsed) {
        if (ticksElapsed % 40 == 0) {

        }
    }


}

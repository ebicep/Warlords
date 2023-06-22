package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractBeaconAbility;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.List;

public class BeaconOfShadow extends AbstractBeaconAbility<BeaconOfShadow> {

    private int critMultiplierReducedTo = 100;
    private int darknessTickDuration = 160;

    public BeaconOfShadow() {
        this(null, null);
    }

    public BeaconOfShadow(Location location, CircleEffect effect) {
        super("Beacon of Shadow", 0, 0, 15, 40, 0, 0, location, 8, 20, effect);
    }

    @Override
    public Component getBonusDescription() {
        return Component.text("All enemies within a ")
                        .append(Component.text(radius, NamedTextColor.YELLOW))
                        .append(Component.text(" block radius have their critical multiplier reduced to "))
                        .append(Component.text(critMultiplierReducedTo + "%", NamedTextColor.RED))
                        .append(Component.text(" and receive the Darkness effect for "))
                        .append(Component.text(format(darknessTickDuration / 20f), NamedTextColor.GOLD))
                        .append(Component.text(" seconds. Only one beacon can be present on the field at once."));
    }

    @Override
    public LineEffect getLineEffect(Location target) {
        return new LineEffect(target, Particle.REDSTONE, new Particle.DustOptions(Color.fromRGB(255, 75, 0), 1));
    }

    @Override
    public String getAbbreviation() {
        return "BEACON";
    }

    @Override
    public Class<BeaconOfShadow> getBeaconClass() {
        return BeaconOfShadow.class;
    }

    @Override
    public BeaconOfShadow getObject(Location groundLocation, CircleEffect effect) {
        return new BeaconOfShadow(groundLocation, effect);
    }

    @Override
    public void whileActive(@Nonnull WarlordsEntity wp, RegularCooldown<BeaconOfShadow> cooldown, Integer ticksLeft, Integer ticksElapsed) {
        if (ticksElapsed % 5 == 0) {
            BeaconOfShadow beacon = cooldown.getCooldownObject();
            int rad = beacon.getRadius();
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(beacon.getGroundLocation(), rad, rad, rad)
                    .aliveEnemiesOf(wp)
            ) {
                enemy.getCooldownManager().removeCooldownByObject(this);
                enemy.getCooldownManager().addCooldown(new RegularCooldown<>(
                        name,
                        null,
                        BeaconOfShadow.class,
                        beacon,
                        wp,
                        CooldownTypes.ABILITY,
                        cooldownManager -> {
                        },
                        6 // a little longer to make sure theres no gaps in the effect
                ) {
                    @Override
                    public float setCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
                        return critMultiplierReducedTo;
                    }
                });
                PotionEffect potionEffect = enemy.getEntity().getPotionEffect(PotionEffectType.DARKNESS);
                if (potionEffect == null) {
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, darknessTickDuration, 0, true, false));
                }
            }
        }
    }

    @Override
    public Material getGlassMaterial() {
        return Material.RED_STAINED_GLASS;
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    public int getCritMultiplierReducedTo() {
        return critMultiplierReducedTo;
    }

    public void setCritMultiplierReducedTo(int critMultiplierReducedTo) {
        this.critMultiplierReducedTo = critMultiplierReducedTo;
    }
}
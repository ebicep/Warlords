package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractStrike;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsStrikeEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class AvengersStrike extends AbstractStrike {

    public float energyStole = 0;

    private float energySteal = 10;

    public AvengersStrike() {
        super("Avenger's Strike", 359, 485, 0, 90, 25, 185);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Strike the targeted enemy player, causing")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text("damage and removing "))
                               .append(Component.text(format(energySteal), NamedTextColor.YELLOW))
                               .append(Component.text(" energy."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));
        info.add(new Pair<>("Energy Removed", "" + Math.round(energyStole)));

        return info;
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "paladin.paladinstrike.activation", 2, 1);
        randomHitEffect(location, 5, 255, 0, 0);
        location.getWorld().spawnParticle(
                Particle.SPELL,
                location,
                4,
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                1,
                null,
                true
        );
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        float multiplier = 1;
        float healthDamage = 0;
        if (nearPlayer instanceof WarlordsNPC) {
            if (pveMasterUpgrade) {
                if (((WarlordsNPC) nearPlayer).getMobTier() == MobTier.BASE) {
                    multiplier = 1.4f;
                }

                if (((WarlordsNPC) nearPlayer).getMobTier() == MobTier.ELITE) {
                    healthDamage = nearPlayer.getMaxHealth() * 0.005f;
                }
            }
        }
        if (healthDamage < DamageCheck.MINIMUM_DAMAGE) {
            healthDamage = DamageCheck.MINIMUM_DAMAGE;
        }
        if (healthDamage > DamageCheck.MAXIMUM_DAMAGE) {
            healthDamage = DamageCheck.MAXIMUM_DAMAGE;
        }
        AtomicReference<Float> minDamage = new AtomicReference<>(minDamageHeal);
        AtomicReference<Float> maxDamage = new AtomicReference<>(maxDamageHeal);
        Optional<Consecrate> consecrate = getStandingOnConsecrate(wp, nearPlayer);
        consecrate.ifPresent(cons -> {
            wp.doOnStaticAbility(Consecrate.class, Consecrate::addStrikesBoosted);
            minDamage.getAndUpdate(value -> value * (1 + cons.getStrikeDamageBoost() / 100f));
            maxDamage.getAndUpdate(value -> value * (1 + cons.getStrikeDamageBoost() / 100f));
        });
        Optional<WarlordsDamageHealingFinalEvent> finalEvent = nearPlayer.addDamageInstance(
                wp,
                name,
                (minDamage.get() * multiplier) + (pveMasterUpgrade ? healthDamage : 0),
                (maxDamage.get() * multiplier) + (pveMasterUpgrade ? healthDamage : 0),
                critChance,
                critMultiplier,
                consecrate.isPresent() ? EnumSet.of(InstanceFlags.STRIKE_IN_CONS) : EnumSet.noneOf(InstanceFlags.class)
        );

        if (pveMasterUpgrade) {
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(nearPlayer, 4, 4, 4)
                    .aliveEnemiesOf(wp)
                    .closestFirst(nearPlayer)
                    .excluding(nearPlayer)
                    .limit(2)
            ) {
                AtomicReference<Float> minDamageSlash = new AtomicReference<>(minDamageHeal);
                AtomicReference<Float> maxDamageSlash = new AtomicReference<>(maxDamageHeal);
                Optional<Consecrate> consecrateSlash = getStandingOnConsecrate(wp, we);
                consecrateSlash.ifPresent(cons -> {
                    wp.doOnStaticAbility(Consecrate.class, Consecrate::addStrikesBoosted);
                    minDamageSlash.getAndUpdate(value -> value * (1 + cons.getStrikeDamageBoost() / 100f));
                    maxDamageSlash.getAndUpdate(value -> value * (1 + cons.getStrikeDamageBoost() / 100f));
                });
                we.addDamageInstance(
                        wp,
                        "Avenger's Slash",
                        ((minDamageSlash.get() * multiplier) + (pveMasterUpgrade ? healthDamage : 0)) * 0.5f,
                        ((maxDamageSlash.get() * multiplier) + (pveMasterUpgrade ? healthDamage : 0)) * 0.5f,
                        critChance,
                        critMultiplier,
                        consecrateSlash.isPresent() ? EnumSet.of(InstanceFlags.STRIKE_IN_CONS) : EnumSet.noneOf(InstanceFlags.class)
                );
                Bukkit.getPluginManager().callEvent(new WarlordsStrikeEvent(wp, this, we));
            }
        }

        energyStole += nearPlayer.subtractEnergy(energySteal, true);
        return true;
    }

    public float getEnergySteal() {
        return energySteal;
    }

    public void setEnergySteal(float energySteal) {
        this.energySteal = energySteal;
    }
}

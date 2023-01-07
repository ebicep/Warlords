package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.abilties.internal.DamageCheck;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class AvengersStrike extends AbstractStrikeBase {

    public float energyStole = 0;

    private float energySteal = 10;

    public AvengersStrike() {
        super("Avenger's Strike", 359, 485, 0, 90, 25, 185);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Strike the targeted enemy player, causing" + formatRangeDamage(minDamageHeal, maxDamageHeal) +
                "damage and removing §e" + format(energySteal) + " §7energy.";
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
        ParticleEffect.SPELL.display(
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                1,
                4,
                location.clone().add(0, 1, 0),
                500
        );
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        float multiplier = 1;
        float healthDamage = 0;
        if (nearPlayer instanceof WarlordsNPC) {
            if (pveUpgrade) {
                switch (((WarlordsNPC) nearPlayer).getMobTier()) {
                    case BASE:
                        multiplier = 1.4f;
                        break;
                    case ELITE:
                        multiplier = 1.2f;
                        break;
                }

                if (((WarlordsNPC) nearPlayer).getMob().getMobTier() == MobTier.ELITE) {
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
            minDamage.getAndUpdate(value -> value *= (1 + cons.getStrikeDamageBoost() / 100f));
            maxDamage.getAndUpdate(value -> value *= (1 + cons.getStrikeDamageBoost() / 100f));
        });
        Optional<WarlordsDamageHealingFinalEvent> finalEvent = nearPlayer.addDamageInstance(
                wp,
                name,
                (minDamage.get() * multiplier) + (pveUpgrade ? healthDamage : 0),
                (maxDamage.get() * multiplier) + (pveUpgrade ? healthDamage : 0),
                critChance,
                critMultiplier,
                false,
                consecrate.isPresent() ? EnumSet.of(InstanceFlags.STRIKE_IN_CONS) : EnumSet.noneOf(InstanceFlags.class)
        );
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

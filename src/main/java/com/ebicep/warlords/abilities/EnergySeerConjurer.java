package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractEnergySeer;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsEnergyUseEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.conjurer.EnergySeerBranchConjurer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.List;

public class EnergySeerConjurer extends AbstractEnergySeer<EnergySeerConjurer> {

    protected int energyUsed = 0;
    private int damageIncrease = 10;

    @Override
    public Component getBonus() {
        return Component.text("increase your damage by ")
                        .append(Component.text(damageIncrease + "%", NamedTextColor.RED));
    }

    @Override
    public Class<EnergySeerConjurer> getEnergySeerClass() {
        return EnergySeerConjurer.class;
    }

    @Override
    public EnergySeerConjurer getObject() {
        return new EnergySeerConjurer();
    }

    @Override
    public RegularCooldown<EnergySeerConjurer> getBonusCooldown(@Nonnull WarlordsEntity wp) {
        return new RegularCooldown<>(
                name,
                "SEER",
                getEnergySeerClass(),
                getObject(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {

                },
                bonusDuration
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * convertToMultiplicationDecimal(damageIncrease);
            }
        };
    }

    @Override
    protected void onEnd(WarlordsEntity wp, EnergySeerConjurer cooldownObject) {
        wp.addEnergy(wp, "Replicating Sight", cooldownObject.energyUsed);
        EffectUtils.displayParticle(
                Particle.REDSTONE,
                wp.getLocation().add(0, 1.2, 0),
                3,
                0.3,
                0.2,
                0.3,
                0,
                new Particle.DustOptions(Color.fromRGB(255, 255, 0), 2)
        );
    }

    @Override
    protected void onEnergyUsed(WarlordsEntity wp, WarlordsEnergyUseEvent.Post event, EnergySeerConjurer cooldownObjet) {
        if (!pveMasterUpgrade2) {
            return;
        }
        WarlordsEntity warlordsEntity = event.getWarlordsEntity();
        if (warlordsEntity.isEnemy(wp) || warlordsEntity.equals(wp)) {
            return;
        }
        float amount = event.getEnergyUsed() * .1f;
        cooldownObjet.energyUsed += amount;

    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new EnergySeerBranchConjurer(abilityTree, this);
    }

    @Override
    protected void heal(WarlordsEntity wp, float energyUsed) {
        wp.addInstance(InstanceBuilder
                .healing()
                .ability(this)
                .source(wp)
                .value(energyUsed * healingValues.seerHealingMultiplier.getValue())
        );
    }

    public int getDamageIncrease() {
        return damageIncrease;
    }

    public void setDamageIncrease(int damageIncrease) {
        this.damageIncrease = damageIncrease;
    }

    public HealingValues getHealValues() {
        return healingValues;
    }

    private final HealingValues healingValues = new HealingValues();

    public static class HealingValues implements Value.ValueHolder {

        private final Value.SetValue seerHealingMultiplier = new Value.SetValue(4);
        private final List<Value> values = List.of(seerHealingMultiplier);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}

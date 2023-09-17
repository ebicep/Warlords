package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractEnergySeer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsEnergyUsedEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.conjurer.EnergySeerBranchConjurer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nonnull;

public class EnergySeerConjurer extends AbstractEnergySeer<EnergySeerConjurer> {

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
    protected void onEnergyUsed(WarlordsEntity wp, WarlordsEnergyUsedEvent event) {
        if (!pveMasterUpgrade2) {
            return;
        }
        WarlordsEntity warlordsEntity = event.getWarlordsEntity();
        if (warlordsEntity.isEnemy(wp) || warlordsEntity.equals(wp)) {
            return;
        }
        float energyUsed = event.getEnergyUsed();
        if (energyUsed <= 0) {
            return;
        }
        wp.addEnergy(wp, "Replicating Sight", energyUsed * .1f);
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new EnergySeerBranchConjurer(abilityTree, this);
    }

    public int getDamageIncrease() {
        return damageIncrease;
    }

    public void setDamageIncrease(int damageIncrease) {
        this.damageIncrease = damageIncrease;
    }
}

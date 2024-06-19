package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractEnergySeer;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.luminary.EnergySeerBranchLuminary;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.List;

public class EnergySeerLuminary extends AbstractEnergySeer<EnergySeerLuminary> implements PurpleAbilityIcon, Heals<EnergySeerLuminary.HealingValues> {

    private final HealingValues healingValues = new HealingValues();
    private int healingIncrease = 20;

    @Override
    public Component getBonus() {
        return Component.text("increase your healing by ")
                        .append(Component.text(healingIncrease + "%", NamedTextColor.GREEN));
    }

    @Override
    public Class<EnergySeerLuminary> getEnergySeerClass() {
        return EnergySeerLuminary.class;
    }

    @Override
    public EnergySeerLuminary getObject() {
        return new EnergySeerLuminary();
    }

    @Override
    public RegularCooldown<EnergySeerLuminary> getBonusCooldown(@Nonnull WarlordsEntity wp) {
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
            public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                return healingIncrease * convertToMultiplicationDecimal(healingIncrease);
            }
        };
    }

    @Override
    protected void onEnd(WarlordsEntity wp, EnergySeerLuminary cooldownObject) {
        if (pveMasterUpgrade2) {
            PlayerFilter.entitiesAround(wp, 10, 10, 10)
                        .aliveTeammatesOfExcludingSelf(wp)
                        .forEach(warlordsEntity -> {
                            MercifulHex.giveMercifulHex(wp, warlordsEntity);
                            EffectUtils.playParticleLinkAnimation(warlordsEntity.getLocation(), wp.getLocation(), Particle.VILLAGER_HAPPY, 1, 1.25, -1);
                        });
        }
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

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new EnergySeerBranchLuminary(abilityTree, this);
    }

    public int getHealingIncrease() {
        return healingIncrease;
    }

    public void setHealingIncrease(int healingIncrease) {
        this.healingIncrease = healingIncrease;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.SetValue seerHealingMultiplier = new Value.SetValue(4);
        private final List<Value> values = List.of(seerHealingMultiplier);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}

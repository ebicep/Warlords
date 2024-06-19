package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractEnergySeer;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.sentinel.EnergySeerBranchSentinel;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class EnergySeerSentinel extends AbstractEnergySeer<EnergySeerSentinel> {

    private final HealingValues healingValues = new HealingValues();
    private int damageResistance = 2;

    @Override
    public Component getBonus() {
        return Component.text("your Fortifying Hexes gain an additional ")
                        .append(Component.text(damageResistance + "%", NamedTextColor.YELLOW))
                        .append(Component.text(" damage resistance"));
    }

    @Override
    public Class<EnergySeerSentinel> getEnergySeerClass() {
        return EnergySeerSentinel.class;
    }

    @Override
    public EnergySeerSentinel getObject() {
        return new EnergySeerSentinel();
    }

    @Override
    public RegularCooldown<EnergySeerSentinel> getBonusCooldown(@Nonnull WarlordsEntity wp) {
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
            protected Listener getListener() {
                return new Listener() {
                    @EventHandler
                    public void onCooldownAdd(WarlordsAddCooldownEvent event) {
                        AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                        if (!Objects.equals(cooldown.getFrom(), wp)) {
                            return;
                        }
                        if (cooldown.getCooldownObject() instanceof FortifyingHex fortifyingHex) {
                            fortifyingHex.getDamageReduction().addAdditiveModifier(name, damageResistance, getTicksLeft());
                        }
                    }
                };
            }
        };
    }

    @Override
    protected void onEnd(WarlordsEntity wp, EnergySeerSentinel cooldownObject) {
        PlayerFilter.playingGame(wp.getGame())
                    .teammatesOf(wp)
                    .forEach(teammate -> {
                        new CooldownFilter<>(teammate, RegularCooldown.class)
                                .filterCooldownFrom(wp)
                                .filterCooldownClassAndMapToObjectsOfClass(FortifyingHex.class)
                                .forEach(fortifyingHex -> fortifyingHex.getDamageReduction().addAdditiveModifier(name, damageResistance, bonusDuration));
                    });
        if (pveMasterUpgrade2) {
            PlayerFilter.entitiesAround(wp, 10, 10, 10)
                        .aliveTeammatesOfExcludingSelf(wp)
                        .forEach(warlordsEntity -> {
                            warlordsEntity.getCooldownManager().addCooldown(getBonusCooldown(wp));
                            EffectUtils.playParticleLinkAnimation(warlordsEntity.getLocation(), wp.getLocation(), Particle.FALLING_HONEY, 1, 1, -1);
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
        return new EnergySeerBranchSentinel(abilityTree, this);
    }

    public int getDamageResistance() {
        return damageResistance;
    }

    public void setDamageResistance(int damageResistance) {
        this.damageResistance = damageResistance;
    }

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

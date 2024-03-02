package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractEnergySeer;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.sentinel.EnergySeerBranchSentinel;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;

import javax.annotation.Nonnull;

public class EnergySeerSentinel extends AbstractEnergySeer<EnergySeerSentinel> {

    private int damageResistance = 10;

    @Override
    public Component getBonus() {
        return Component.text("gain ")
                        .append(Component.text(damageResistance + "%", NamedTextColor.YELLOW))
                        .append(Component.text(" damage reduction"));
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
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 - damageResistance / 100f);
            }
        };
    }

    @Override
    protected void onEnd(WarlordsEntity wp, EnergySeerSentinel cooldownObject) {
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
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new EnergySeerBranchSentinel(abilityTree, this);
    }

    public int getDamageResistance() {
        return damageResistance;
    }

    public void setDamageResistance(int damageResistance) {
        this.damageResistance = damageResistance;
    }
}

package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.AbstractArcaneShield;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.cryomancer.ArcaneShieldBranchCryomancer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ArcaneShieldCryomancer extends AbstractArcaneShield {

    public ArcaneShieldCryomancer() {
        super(AbstractAbilityBuilder.create("arcaneShieldCryomancer").pvp());
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 1);
        Shield shield = new Shield(name, getMaxShieldHealth());
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "ARCA",
                Shield.class,
                shield,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (pveMasterUpgrade) {
                        Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 0.5f);
                        EffectUtils.strikeLightning(wp.getLocation(), false);
                        for (WarlordsEntity we : PlayerFilter
                                .entitiesAround(wp, 6, 6, 6)
                                .aliveEnemiesOf(wp)
                                .closestFirst(wp)
                        ) {
                            we.setStunTicks(6 * 20);
                        }
                    }
                    if (pveMasterUpgrade2) {
                        List<AbstractAbility> abilities = wp.getAbilities();
                        if (abilities.isEmpty()) {
                            return;
                        }
                        AbstractAbility rightClick = abilities.getFirst();
                        FloatModifiable.FloatModifier modifier = rightClick.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER,
                                "Arcane Energy", -.25f
                        );
                        wp.updateItem(rightClick);
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Arcane Energy",
                                "ARC",
                                ArcaneShield.class,
                                new ArcaneShield(),
                                wp,
                                CooldownTypes.ABILITY,
                                cooldownManager2 -> {
                                    modifier.forceEnd();
                                    wp.updateItem(rightClick);
                                },
                                6 * 20,
                                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                    if (ticksElapsed % 3 == 0) {
                                        EffectUtils.displayParticle(Particle.ELECTRIC_SPARK, wp.getLocation().add(0, 1, 0), 10, .4, .4, .4, 0);
                                    }
                                })
                        ));
                    }
                },
                cooldownManager -> {
                    if (shield.isBroken()) {
                        Bukkit.getPluginManager().callEvent(new ArcaneShield.WarlordsArcaneShieldBrokenEvent(wp));
                        getAbilityStats().timesBroken++;
                    }
                    getAbilityStats().totalAbsorbed += shield.getMaxShieldHealth() - Math.max(0, shield.getShieldHealth());
                },
                getTickDuration(),
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.5, 0);
                        EffectUtils.displayParticle(Particle.CLOUD, location, 2, 0.15, 0.3, 0.15, 0.01);
                        EffectUtils.displayParticle(Particle.FIREWORK, location, 1, 0.3, 0.3, 0.3, 0.0001);
                        EffectUtils.displayParticle(Particle.WITCH, location, 1, 0.3, 0.3, 0.3, 0);
                    }
                })
        ) {
            @Override
            public PlayerNameData addPrefixFromOther() {
                return PlayerNameData.shieldHealth(shield, we -> we.isTeammate(wp), NamedTextColor.YELLOW);
            }
        });

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ArcaneShieldBranchCryomancer(abilityTree, this);
    }
}

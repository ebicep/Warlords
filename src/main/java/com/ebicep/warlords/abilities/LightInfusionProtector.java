package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.AbstractLightInfusion;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.protector.LightInfusionBranchProtector;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Particle;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Collections;

public class LightInfusionProtector extends AbstractLightInfusion {

    public LightInfusionProtector() {
        super(AbstractAbilityBuilder.create("lightInfusionProtector").pvp());
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        wp.addEnergy(wp, name, energyGiven);
        Utils.playGlobalSound(wp.getLocation(), "paladin.infusionoflight.activation", 2, 1);
        wp.addSpeedModifier(wp, name, speedBuff, tickDuration);
        wp.getCooldownManager().addRegularCooldown(name, "INF", LightInfusionProtector.class, null, wp, CooldownTypes.ABILITY, cooldownManager -> {
                }, cooldownManager -> {
            wp.getSpeed().removeModifier(name);
                }, tickDuration, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        wp.getWorld().spawnParticle(Particle.EFFECT, wp.getLocation().add(0, 1.2, 0), 2, 0.3, 0.1, 0.3, 0.2, null, true);
                    }
                })
        );
        if (pveMasterUpgrade) {
            for (HolyRadianceProtector holyRadiance : wp.getAbilitiesMatching(HolyRadianceProtector.class)) {
                holyRadiance.setCurrentCooldown(0);
            }
            RegularCooldown<LightInfusionProtector> ornamentOfLightCooldown = new RegularCooldown<>("Ornament of Light",
                    "ORNA",
                    LightInfusionProtector.class,
                    null,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    4 * 20,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 2 == 0) {
                            wp.getSpeed().removeNegativeModifiers();
                            wp.getCooldownManager().removeDebuffCooldowns();
                        }
                    })
            ) {
                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * 0.1f;
                }
            };
            wp.addKnockbackModifier(wp, "Ornament of Light", -50, ornamentOfLightCooldown);
            wp.getCooldownManager().addCooldown(ornamentOfLightCooldown);
        } else if (pveMasterUpgrade2) {
            wp.getCooldownManager().addCooldown(new RegularCooldown<>("Chiron Light", "CHIRON", LightInfusionProtector.class, null, wp, CooldownTypes.BUFF, cooldownManager -> {
            }, tickDuration
            ) {

                @Override
                public float modifyHealingFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                    if (event.getCause().equals("Protector's Strike")) {
                        return currentHealValue * 1.25f;
                    }
                    return currentHealValue;
                }
            });
            for (WarlordsEntity infusionTarget : PlayerFilter.entitiesAround(wp, 5, 5, 5).aliveTeammatesOfExcludingSelf(wp)) {
                playCastEffect(infusionTarget);
                infusionTarget.getSpeed().removeNegativeModifiers();
                infusionTarget.getCooldownManager().removeDebuffCooldowns();
                infusionTarget.addSpeedModifier(wp, "Chiron Light", speedBuff, tickDuration);
                infusionTarget.getCooldownManager()
                              .addCooldown(new RegularCooldown<>("Chiron Light", "CHIRON", LightInfusionProtector.class, null, wp, CooldownTypes.ABILITY, cooldownManager -> {
                              }, 4 * 20
                              ) {

                                  @Override
                                  protected Listener getListener() {
                                      return CooldownManager.getDefaultDebuffImmunityListener(infusionTarget);
                                  }
                              });
            }
        }
        playCastEffect(wp);
        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new LightInfusionBranchProtector(abilityTree, this);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
    }

}

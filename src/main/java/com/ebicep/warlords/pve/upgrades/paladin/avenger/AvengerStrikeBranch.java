package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilities.AvengersStrike;
import com.ebicep.warlords.player.ingame.CalculateSpeed;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.PlayerFilter;

import java.util.Collections;

public class AvengerStrikeBranch extends AbstractUpgradeBranch<AvengersStrike> {

    float energySteal = ability.getEnergySteal();

    @Override
    public void runOnce() {
        ability.getMinDamageHeal().addMultiplicativeModifierAdd("PvE", .3f);
        ability.getMaxDamageHeal().addMultiplicativeModifierAdd("PvE", .3f);
    }

    public AvengerStrikeBranch(AbilityTree abilityTree, AvengersStrike ability) {
        super(abilityTree, ability);
        WarlordsPlayer warlordsPlayer = abilityTree.getWarlordsPlayer();

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability, 5f)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Energy Steal";
                    }

                    @Override
                    public void run(float value) {
                        ability.setEnergySteal(energySteal + value);
                    }
                }, 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addUpgradeHitBox(ability, 0.25f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Avenger's Slash",
                "Avenger's Strike - Master Upgrade",
                """
                        -5 Additional energy cost.

                        Avenger's Strike hits 2 additional enemies for 50% of the original strike damage.

                        Deal 40% more damage against level 3 enemies or below and deal 0.5% max health damage against level 4 and 5 enemies.""",
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -5);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Avenging Strike",
                "Avenger's Strike - Master Upgrade",
                """
                        Strike crit chance is increased by 15%.
                                                
                        If there are at least 2 enemies within 20 blocks, strike damage is increased by 25% and movement speed is increased by 20%.
                                                
                        If there are fewer, strike damage is further increased by 50%.
                        """,
                50000,
                () -> {
                    ability.setCritChance(ability.getCritChance() + 15);
                    CalculateSpeed calculateSpeed = warlordsPlayer.getSpeed();
                    CalculateSpeed.Modifier modifier = new CalculateSpeed.Modifier(
                            warlordsPlayer,
                            "Avenging Strike",
                            0,
                            Integer.MAX_VALUE,
                            Collections.emptyList(),
                            false
                    );
                    warlordsPlayer.addSpeedModifier(modifier);
                    warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                            "Avenging Strike",
                            null,
                            AvengerStrikeBranch.class,
                            null,
                            warlordsPlayer,
                            CooldownTypes.MASTERY,
                            cooldownManager -> {
                            },
                            false,
                            (cooldown, ticksElapsed) -> {
                                if (ticksElapsed % 20 == 0) {
                                    long enemiesNearBy = PlayerFilter.entitiesAround(warlordsPlayer, 20, 20, 20)
                                                                     .aliveEnemiesOf(warlordsPlayer)
                                                                     .stream()
                                                                     .count();
                                    float oldModifier = modifier.getModifier();
                                    if (enemiesNearBy >= 2 && oldModifier != 20) {
                                        modifier.setModifier(20);
                                        calculateSpeed.setChanged(true);
                                    } else if (oldModifier != 0) {
                                        modifier.setModifier(0);
                                        calculateSpeed.setChanged(true);
                                    }
                                }
                            }
                    ));
                }
        );
    }
}

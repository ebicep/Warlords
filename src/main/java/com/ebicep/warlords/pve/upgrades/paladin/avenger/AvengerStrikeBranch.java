package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilities.AvengersStrike;
import com.ebicep.warlords.player.ingame.CalculateSpeed;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.util.warlords.PlayerFilter;

import java.util.Collections;

public class AvengerStrikeBranch extends AbstractUpgradeBranch<AvengersStrike> {

    float minDamage;
    float maxDamage;
    float energyCost = ability.getEnergyCost();
    float energySteal = ability.getEnergySteal();
    double hitbox = ability.getHitbox();

    public AvengerStrikeBranch(AbilityTree abilityTree, AvengersStrike ability) {
        super(abilityTree, ability);
        WarlordsPlayer warlordsPlayer = abilityTree.getWarlordsPlayer();
        if (warlordsPlayer.isInPve()) {
            ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.3f);
            ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.3f);
        }
        minDamage = ability.getMinDamageHeal();
        maxDamage = ability.getMaxDamageHeal();

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+7.5% Damage\n+7.5 Energy steal",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.075f);
                    ability.setMaxDamageHeal(maxDamage * 1.075f);
                    ability.setEnergySteal(energySteal + 7.5f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+15% Damage\n+15 Energy steal",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                    ability.setEnergySteal(energySteal + 15);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+22.5% Damage\n+22.5 Energy steal",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.225f);
                    ability.setMaxDamageHeal(maxDamage * 1.225f);
                    ability.setEnergySteal(energySteal + 22.5f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+30% Damage\n+30 Energy steal",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                    ability.setEnergySteal(energySteal + 30);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-2.5 Energy cost\n+0.25 Blocks strike hit radius",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 2.5f);
                    ability.setHitbox(hitbox + 0.25);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-5 Energy cost\n+0.5 Blocks strike hit radius",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 5);
                    ability.setHitbox(hitbox + 0.5);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-7.5 Energy cost\n+0.75 Blocks strike hit radius",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 7.5f);
                    ability.setHitbox(hitbox + 0.75);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-10 Energy cost\n+1 Blocks strike hit radius",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                    ability.setHitbox(hitbox + 1);
                }
        ));

        masterUpgrade = new Upgrade(
                "Avenger's Slash",
                "Avenger's Strike - Master Upgrade",
                """
                        -5 Additional energy cost.

                        Avenger's Strike hits 2 additional enemies for 50% of the original strike damage.

                        Deal 40% more damage against level 3 enemies or below and deal 0.5% max health damage against level 4 and 5 enemies.""",
                50000,
                () -> {
                    ability.setEnergyCost(ability.getEnergyCost() - 5);
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
                                    float oldModifier = modifier.modifier;
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

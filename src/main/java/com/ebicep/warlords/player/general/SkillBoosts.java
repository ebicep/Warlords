package com.ebicep.warlords.player.general;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractConsecrate;
import com.ebicep.warlords.abilities.internal.AbstractGroundSlam;
import com.ebicep.warlords.abilities.internal.AbstractSeismicWave;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.function.Consumer;

public enum SkillBoosts {

    FIREBALL("Fireball",
            Component.text("Increase the damage of Fireball by 10% and increase the direct hit damage bonus by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Fireball by ", NamedTextColor.GREEN)
                     .append(Component.text("10%", NamedTextColor.RED))
                     .append(Component.text(" and increase the direct hit damage bonus by ", NamedTextColor.GREEN))
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            Fireball.class,
            abstractAbility -> {
                if (abstractAbility instanceof Fireball fireball) {
                    fireball.getDamageValues()
                            .getFireballDamage()
                            .forEachValue(floatModifiable -> {
                                floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .1f);
                            });
                    fireball.setDirectHitMultiplier(fireball.getDirectHitMultiplier() + 20);
                }
            }
    ),
    FLAME_BURST("Flame Burst",
            Component.text("Increase the damage of Flame Burst by 25% and reduce the energy cost by 40.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Flame Burst by ", NamedTextColor.GREEN)
                     .append(Component.text("25% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the energy cost by ", NamedTextColor.GREEN))
                     .append(Component.text("40", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            FlameBurst.class,
            abstractAbility -> {
                if (abstractAbility instanceof FlameBurst flameBurst) {
                    flameBurst.getDamageValues()
                              .getFlameBurstDamage()
                              .forEachValue(floatModifiable -> {
                                  floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .25f);
                              });
                    abstractAbility.getEnergyCost().addAdditiveModifier("Skill Boost", -40);
                }
            }
    ),
    TIME_WARP_PYROMANCER("Time Warp",
            Component.text("Increase the healing of Time Warp by 10% and reduce the cooldown by 50%.", NamedTextColor.GRAY),
            Component.text("Increase the healing of Time Warp by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("50%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            TimeWarpPyromancer.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarpPyromancer timeWarp) {
                    timeWarp.setWarpHealPercentage(timeWarp.getWarpHealPercentage() + 10);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .5f);
                }
            }
    ),
    ARCANE_SHIELD_PYROMANCER("Arcane Shield",
            Component.text("Remove the energy cost of Arcane Shield and reduce the cooldown by 30%.", NamedTextColor.GRAY),
            Component.text("Remove the energy cost of Arcane Shield and reduce the cooldown by ", NamedTextColor.GREEN)
                     .append(Component.text("30%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    abstractAbility.getEnergyCost().addAdditiveModifier("Skill Boost", -40);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                }
            }
    ),
    INFERNO("Inferno",
            Component.text("Increase the Crit Multiplier bonus of Inferno by 60%.", NamedTextColor.GRAY),
            Component.text("Increase the Crit Multiplier bonus of Inferno by ", NamedTextColor.GREEN)
                     .append(Component.text("60%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            Inferno.class,
            abstractAbility -> {
                if (abstractAbility instanceof Inferno inferno) {
                    inferno.setCritMultiplierIncrease(inferno.getCritMultiplierIncrease() + 60);
                }
            }
    ),
    FROST_BOLT("Frostbolt",
            Component.text("Increase the damage of Frostbolt by 20% and increase the slowness by 5%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Frostbolt by ", NamedTextColor.GREEN)
                     .append(Component.text("20% ", NamedTextColor.RED))
                     .append(Component.text("and increase the slowness by ", NamedTextColor.GREEN))
                     .append(Component.text("5%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            FrostBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof FrostBolt frostBolt) {
                    frostBolt.getDamageValues()
                             .getBoltDamage()
                             .forEachValue(floatModifiable -> {
                                 floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                             });
                    frostBolt.setSlowness(frostBolt.getSlowness() + 5);
                }
            }
    ),
    FREEZING_BREATH("Freezing Breath",
            Component.text("Increase the damage of Freezing Breath by 20% and reduce the cooldown by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Freezing Breath by ", NamedTextColor.GREEN)
                     .append(Component.text("20% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            FreezingBreath.class,
            abstractAbility -> {
                if (abstractAbility instanceof FreezingBreath freezingBreath) {
                    freezingBreath.getDamageValues()
                                  .getFreezingBreathDamage()
                                  .forEachValue(floatModifiable -> {
                                      floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                                  });
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    TIME_WARP_CRYOMANCER("Time Warp",
            Component.text("Reduce the cooldown of Time Warp by 40%.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Time Warp by ", NamedTextColor.GREEN)
                     .append(Component.text("40%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            TimeWarpCryomancer.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarpCryomancer) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .6f);
                }
            }
    ),
    ARCANE_SHIELD_CRYOMANCER("Arcane Shield",
            Component.text("Reduce the cooldown of Arcane Shield by 30%.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Arcane Shield by ", NamedTextColor.GREEN)
                     .append(Component.text("30%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                }
            }
    ),
    ICE_BARRIER("Ice Barrier",
            Component.text("Increase the damage reduction of Ice Barrier by 5% and increase the duration by 2 seconds.", NamedTextColor.GRAY),
            Component.text("Increase the damage reduction of Ice Barrier by ", NamedTextColor.GREEN)
                     .append(Component.text("5% ", NamedTextColor.RED))
                     .append(Component.text("and increase the duration by ", NamedTextColor.GREEN))
                     .append(Component.text("2 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            IceBarrier.class,
            abstractAbility -> {
                if (abstractAbility instanceof IceBarrier iceBarrier) {
                    iceBarrier.setDamageReductionPercent(iceBarrier.getDamageReductionPercent() + 5);
                    iceBarrier.setTickDuration(iceBarrier.getTickDuration() + 40);
                }
            }
    ),
    WATER_BOLT("Water Bolt",
            Component.text("Increase the healing of Water Bolt by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the healing of Water Bolt by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            WaterBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof WaterBolt waterBolt) {
                    waterBolt.getHealValues()
                             .getBoltHealing()
                             .forEachValue(floatModifiable -> {
                                 floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                             });
                }
            }
    ),
    WATER_BREATH("Water Breath",
            Component.text("Increase the healing of Water Breath by 15% and reduce the energy cost by 30.", NamedTextColor.GRAY),
            Component.text("Increase the healing of Water Breath by ", NamedTextColor.GREEN)
                     .append(Component.text("15% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the energy cost by ", NamedTextColor.GREEN))
                     .append(Component.text("30", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            WaterBreath.class,
            abstractAbility -> {
                if (abstractAbility instanceof WaterBreath waterBreath) {
                    waterBreath.getHealValues()
                               .getBreathHealing()
                               .forEachValue(floatModifiable -> {
                                   floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .15f);
                               });
                    abstractAbility.getEnergyCost().addAdditiveModifier("Skill Boost", -30);
                }
            }
    ),
    TIME_WARP_AQUAMANCER("Time Warp",
            Component.text("Increase the duration of Time Warp by 3 seconds.", NamedTextColor.GRAY),
            Component.text("Increase the duration of Time Warp by ", NamedTextColor.GREEN)
                     .append(Component.text("3", NamedTextColor.RED))
                     .append(Component.text(" seconds.", NamedTextColor.GREEN)),
            TimeWarpAquamancer.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarpAquamancer timeWarpAquamancer) {
                    timeWarpAquamancer.setTickDuration(timeWarpAquamancer.getTickDuration() + 60);
                }
            }
    ),
    ARCANE_SHIELD_AQUAMANCER("Arcane Shield",
            Component.text("Remove the energy cost of Arcane Shield and reduce the cooldown by 30%.", NamedTextColor.GRAY),
            Component.text("Remove the energy cost of Arcane Shield and reduce the cooldown by ", NamedTextColor.GREEN)
                     .append(Component.text("30%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    abstractAbility.getEnergyCost().addAdditiveModifier("Skill Boost", -40);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                }
            }
    ),
    HEALING_RAIN("Healing Rain",
            Component.text("Increase the duration of Healing Rain by 4 seconds and reduce the cooldown by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the duration of Healing Rain by ", NamedTextColor.GREEN)
                     .append(Component.text("4 ", NamedTextColor.RED))
                     .append(Component.text("seconds and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            HealingRain.class,
            abstractAbility -> {
                if (abstractAbility instanceof HealingRain healingRain) {
                    healingRain.setTickDuration(healingRain.getTickDuration() + 80);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    WOUNDING_STRIKE_BERSERKER("Wounding Strike",
            Component.text("Increase the damage of Wounding Strike by 10% and reduce the energy cost by 10.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Wounding Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the energy cost by ", NamedTextColor.GREEN))
                     .append(Component.text("10", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            WoundingStrikeBerserker.class,
            abstractAbility -> {
                if (abstractAbility instanceof WoundingStrikeBerserker woundingStrikeBerserker) {
                    woundingStrikeBerserker.getDamageValues()
                                           .getStrikeDamage()
                                           .forEachValue(floatModifiable -> {
                                               floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .1f);
                                           });
                    abstractAbility.getEnergyCost().addAdditiveModifier("Skill Boost", -10);
                }
            }
    ),
    SEISMIC_WAVE_BERSERKER("Seismic Wave",
            Component.text("Increase the damage of Seismic Wave by 15% and reduce the cooldown by 25%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Seismic Wave by ", NamedTextColor.GREEN)
                     .append(Component.text("15% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("25%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            SeismicWaveBerserker.class,
            abstractAbility -> {
                if (abstractAbility instanceof SeismicWaveBerserker seismicWaveBerserker) {
                    seismicWaveBerserker.getDamageValues()
                                        .getWaveDamage()
                                        .forEachValue(floatModifiable -> {
                                            floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .15f);
                                        });
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                }
            }
    ),
    GROUND_SLAM_BERSERKER("Ground Slam",
            Component.text("Increase the damage of Ground Slam by 35% and reduce the cooldown by 10%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Ground Slam by ", NamedTextColor.GREEN)
                     .append(Component.text("35% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            GroundSlamBerserker.class,
            abstractAbility -> {
                if (abstractAbility instanceof GroundSlamBerserker groundSlamBerserker) {
                    groundSlamBerserker.getDamageValues()
                                       .getSlamDamage()
                                       .forEachValue(floatModifiable -> {
                                           floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .35f);
                                       });
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .9f);
                }
            }
    ),
    BLOOD_LUST("Blood Lust",
            Component.text("Reduce the cooldown of Blood Lust by 30% and increase damage converted to healing by 5%.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Blood Lust by ", NamedTextColor.GREEN)
                     .append(Component.text("30% ", NamedTextColor.RED))
                     .append(Component.text("and increase damage converted to healing by ", NamedTextColor.GREEN))
                     .append(Component.text("5%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            BloodLust.class,
            abstractAbility -> {
                if (abstractAbility instanceof BloodLust bloodLust) {
                    bloodLust.setDamageConvertPercent(bloodLust.getDamageConvertPercent() + 5);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                }
            }
    ),
    BERSERK("Berserk",
            Component.text("Increase the damage bonus of Berserk by 15% and increase the speed by 10%.", NamedTextColor.GRAY),
            Component.text("Increase the damage bonus of Berserk by ", NamedTextColor.GREEN)
                     .append(Component.text("15% ", NamedTextColor.RED))
                     .append(Component.text("and increase the speed by ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            Berserk.class,
            abstractAbility -> {
                if (abstractAbility instanceof Berserk berserk) {
                    berserk.setDamageIncrease(berserk.getDamageIncrease() + 15);
                    berserk.setSpeedBuff(berserk.getSpeedBuff() + 10);
                }
            }
    ),
    WOUNDING_STRIKE_DEFENDER("Wounding Strike",
            Component.text("Increase the damage of Wounding Strike by 10% and increase wounding by 25%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Wounding Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and increase wounding by ", NamedTextColor.GREEN))
                     .append(Component.text("25%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            WoundingStrikeDefender.class,
            abstractAbility -> {
                if (abstractAbility instanceof WoundingStrikeDefender woundingStrikeDefender) {
                    woundingStrikeDefender.getDamageValues()
                                          .getStrikeDamage()
                                          .forEachValue(floatModifiable -> {
                                              floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .1f);
                                          });
                    woundingStrikeDefender.setWounding(woundingStrikeDefender.getWounding() + 25);
                }
            }
    ),
    SEISMIC_WAVE_DEFENDER("Seismic Wave",
            Component.text("Increase the knockback of Seismic Wave by 35% and reduce the cooldown by 25%.", NamedTextColor.GRAY),
            Component.text("Increase the knockback of Seismic Wave by ", NamedTextColor.GREEN)
                     .append(Component.text("35% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("", NamedTextColor.GREEN))
                     .append(Component.text("25%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            SeismicWaveDefender.class,
            abstractAbility -> {
                if (abstractAbility instanceof AbstractSeismicWave seismicWave) {
                    seismicWave.setVelocity(1.8f);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                }
            }
    ),
    GROUND_SLAM_DEFENDER("Ground Slam",
            Component.text("Increase the knockback of Ground Slam by 10% and reduce the cooldown by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the knockback of Ground Slam by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            GroundSlamDefender.class,
            abstractAbility -> {
                if (abstractAbility instanceof AbstractGroundSlam groundSlam) {
                    groundSlam.setVelocity(1.35f);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    INTERVENE("Intervene",
            Component.text("Increase the cast and break range of Intervene by 5 blocks and increase the max amount of damage absorbed by 400.", NamedTextColor.GRAY),
            Component.text("Increase the cast and break range of Intervene by ", NamedTextColor.GREEN)
                     .append(Component.text("5 ", NamedTextColor.RED))
                     .append(Component.text("blocks and increase the max amount of damage absorbed by ", NamedTextColor.GREEN))
                     .append(Component.text("400", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            Intervene.class,
            abstractAbility -> {
                if (abstractAbility instanceof Intervene intervene) {
                    intervene.setMaxDamagePrevented(intervene.getMaxDamagePrevented() + 400);
                    intervene.setRadius(intervene.getRadius() + 5);
                    intervene.setBreakRadius(intervene.getBreakRadius() + 5);
                }
            }
    ),
    LAST_STAND("Last Stand",
            Component.text("Increase the damage reduction of Last Stand by 5% and reduce the cooldown by 10%.", NamedTextColor.GRAY),
            Component.text("Increase the damage reduction of Last Stand by ", NamedTextColor.GREEN)
                     .append(Component.text("5% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            LastStand.class,
            abstractAbility -> {
                if (abstractAbility instanceof LastStand lastStand) {
                    lastStand.setSelfDamageReductionPercent((int) (lastStand.getSelfDamageReduction() + 5));
                    lastStand.setTeammateDamageReductionPercent((int) (lastStand.getTeammateDamageReduction() + 5));
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .9f);
                }
            }
    ),
    CRIPPLING_STRIKE("Crippling Strike",
            Component.text("Increase crippling by 10% and increase the additional reduction per strike by 5%.", NamedTextColor.GRAY),
            Component.text("Increase crippling by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and increase the additional reduction per strike by ", NamedTextColor.GREEN))
                     .append(Component.text("5%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            CripplingStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof CripplingStrike cripplingStrike) {
                    cripplingStrike.setCripple(cripplingStrike.getCripple() + 10);
                    cripplingStrike.setCripplePerStrike(cripplingStrike.getCripplePerStrike() + 5);
                }
            }
    ),
    RECKLESS_CHARGE("Reckless Charge",
            Component.text("Increase the immobilize duration of Reckless Charge by 0.3 seconds and reduce the cooldown by 30%.", NamedTextColor.GRAY),
            Component.text("Increase the immobilize duration of Reckless Charge by ", NamedTextColor.GREEN)
                     .append(Component.text("0.3 ", NamedTextColor.RED))
                     .append(Component.text("seconds and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("30%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            RecklessCharge.class,
            abstractAbility -> {
                if (abstractAbility instanceof RecklessCharge recklessCharge) {
                    recklessCharge.setStunTimeInTicks(recklessCharge.getStunTimeInTicks() + 6);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                }
            }
    ),
    GROUND_SLAM_REVENANT("Ground Slam",
            Component.text("Reduce the cooldown of Ground Slam by 40%.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Ground Slam by ", NamedTextColor.GREEN)
                     .append(Component.text("40%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            GroundSlamRevenant.class,
            abstractAbility -> {
                if (abstractAbility instanceof AbstractGroundSlam) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .6f);
                }
            }
    ),
    ORBS_OF_LIFE("Orbs of Life",
            Component.text("Increase the healing of Orbs of Life by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the healing of Orbs of Life by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            OrbsOfLife.class,
            abstractAbility -> {
                if (abstractAbility instanceof OrbsOfLife orbsOfLife) {
                    orbsOfLife.getHealValues()
                              .getOrbHealing()
                              .forEachValue(floatModifiable -> {
                                  floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                              });
                }
            }
    ),
    UNDYING_ARMY("Undying Army",
            Component.text("Reduce the damage of Undying Army after dying by 5% and increase the duration by 5 seconds.", NamedTextColor.GRAY),
            Component.text("Reduce the damage of Undying Army after dying by ", NamedTextColor.GREEN)
                     .append(Component.text("5% ", NamedTextColor.RED))
                     .append(Component.text("and increase the duration by ", NamedTextColor.GREEN))
                     .append(Component.text("5 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            UndyingArmy.class,
            abstractAbility -> {
                if (abstractAbility instanceof UndyingArmy undyingArmy) {
                    undyingArmy.setTickDuration(undyingArmy.getTickDuration() + 100);
                    undyingArmy.setMaxHealthDamage(undyingArmy.getMaxHealthDamage() - 5);
                }
            }
    ),
    AVENGER_STRIKE("Avenger's Strike",
            Component.text("Increase the damage of Avenger's Strike by 15% and increase the energy steal by 5.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Avenger's Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("15% ", NamedTextColor.RED))
                     .append(Component.text("and increase the energy steal by ", NamedTextColor.GREEN))
                     .append(Component.text("5", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            AvengersStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof AvengersStrike avengersStrike) {
                    avengersStrike.getDamageValues()
                                  .getStrikeDamage()
                                  .forEachValue(floatModifiable -> {
                                      floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .15f);
                                  });
                    avengersStrike.setEnergySteal(avengersStrike.getEnergySteal() + 5);
                }
            }
    ),
    CONSECRATE_AVENGER("Consecrate",
            Component.text("Increase the damage of Consecrate by 35% and remove the energy cost.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Consecrate by ", NamedTextColor.GREEN)
                     .append(Component.text("35%", NamedTextColor.RED))
                     .append(Component.text(" and remove the energy cost.", NamedTextColor.GREEN)),
            ConsecrateAvenger.class,
            abstractAbility -> {
                if (abstractAbility instanceof ConsecrateAvenger consecrateAvenger) {
                    abstractAbility.getEnergyCost().addAdditiveModifier("Skill Boost", -50);
                    consecrateAvenger.getDamageValues()
                                     .getConsecrateDamage()
                                     .forEachValue(floatModifiable -> {
                                         floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .35f);
                                     });
                }
            }
    ),
    LIGHT_INFUSION_AVENGER("Light Infusion",
            Component.text("Increase the energy restored by Light Infusion by 40 and reduce the cooldown by 25%.", NamedTextColor.GRAY),
            Component.text("Increase the energy restored by Light Infusion by ", NamedTextColor.GREEN)
                     .append(Component.text("40 ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("25%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            LightInfusionAvenger.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusionAvenger lightInfusion) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                    lightInfusion.setEnergyGiven(lightInfusion.getEnergyGiven() + 40);
                }
            }
    ),
    HOLY_RADIANCE_AVENGER("Holy Radiance",
            Component.text("Reduce the cooldown of Holy Radiance by 20% and increase the energy drain of Avenger's Mark by 100%.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Holy Radiance by ", NamedTextColor.GREEN)
                     .append(Component.text("20% ", NamedTextColor.RED))
                     .append(Component.text("and increase the energy drain of Avenger's Mark by ", NamedTextColor.GREEN))
                     .append(Component.text("100%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            HolyRadianceAvenger.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceAvenger holyRadiance) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                    holyRadiance.setEnergyDrainPerSecond(holyRadiance.getEnergyDrainPerSecond() * 2);
                }
            }
    ),
    AVENGERS_WRATH("Avenger's Wrath",
            Component.text("Increase the energy per second of Avenger's Wrath by 10 and increase the duration by 5 seconds.", NamedTextColor.GRAY),
            Component.text("Increase the energy per second of Avenger's Wrath by ", NamedTextColor.GREEN)
                     .append(Component.text("10 ", NamedTextColor.RED))
                     .append(Component.text("and increase the duration by ", NamedTextColor.GREEN))
                     .append(Component.text("5 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            AvengersWrath.class,
            abstractAbility -> {
                if (abstractAbility instanceof AvengersWrath avengersWrath) {
                    avengersWrath.setTickDuration(avengersWrath.getTickDuration() + 100);
                    avengersWrath.setEnergyPerSecond(avengersWrath.getEnergyPerSecond() + 10);
                }
            }
    ),
    CRUSADER_STRIKE("Crusader's Strike",
            Component.text("Increase the damage of Crusader's Strike by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Crusader's Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            CrusadersStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof CrusadersStrike crusadersStrike) {
                    crusadersStrike.getDamageValues()
                                   .getStrikeDamage()
                                   .forEachValue(floatModifiable -> {
                                       floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                                   });
                }
            }
    ),
    CONSECRATE_CRUSADER("Consecrate",
            Component.text("Increase the damage of Consecrate by 35% and remove the energy cost.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Consecrate by ", NamedTextColor.GREEN)
                     .append(Component.text("35%", NamedTextColor.RED))
                     .append(Component.text(" and remove the energy cost.", NamedTextColor.GREEN)),
            ConsecrateCrusader.class,
            abstractAbility -> {
                if (abstractAbility instanceof ConsecrateCrusader consecrateCrusader) {
                    abstractAbility.getEnergyCost().addAdditiveModifier("Skill Boost", -50);
                    consecrateCrusader.getDamageValues()
                                      .getConsecrateDamage()
                                      .forEachValue(floatModifiable -> {
                                          floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .35f);
                                      });
                }
            }
    ),
    LIGHT_INFUSION_CRUSADER("Light Infusion",
            Component.text("Reduce the cooldown of Light Infusion by 35% and increase the speed duration by 3 seconds.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Light Infusion by ", NamedTextColor.GREEN)
                     .append(Component.text("35% ", NamedTextColor.RED))
                     .append(Component.text("and increase the speed duration by ", NamedTextColor.GREEN))
                     .append(Component.text("3 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            LightInfusionCrusader.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusionCrusader lightInfusion) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .65f);
                    lightInfusion.setTickDuration(lightInfusion.getTickDuration() + 60);
                }
            }
    ),
    HOLY_RADIANCE_CRUSADER("Holy Radiance",
            Component.text("Reduce the cooldown of Holy Radiance by 25%, increase the duration of Crusader's Mark by 4 seconds and increase the speed by 15%.",
                    NamedTextColor.GRAY
            ),
            Component.text("Reduce the cooldown of Holy Radiance by ", NamedTextColor.GREEN)
                     .append(Component.text("25%", NamedTextColor.RED))
                     .append(Component.text(", increase the duration of Crusader's Mark by ", NamedTextColor.GREEN))
                     .append(Component.text("4 ", NamedTextColor.RED))
                     .append(Component.text("seconds and speed bonus by ", NamedTextColor.GREEN))
                     .append(Component.text("15%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            HolyRadianceCrusader.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceCrusader holyRadiance) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                    holyRadiance.setMarkDuration(holyRadiance.getMarkDuration() + 4);
                    holyRadiance.setMarkSpeed(holyRadiance.getMarkSpeed() + 15);
                }
            }
    ),
    INSPIRING_PRESENCE("Inspiring Presence",
            Component.text("Reduce the cooldown of Inspiring Presence by 25% and increase the speed by 10%.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Inspiring Presence by ", NamedTextColor.GREEN)
                     .append(Component.text("25% ", NamedTextColor.RED))
                     .append(Component.text("and increase the speed by ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            InspiringPresence.class,
            abstractAbility -> {
                if (abstractAbility instanceof InspiringPresence inspiringPresence) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                    inspiringPresence.setSpeedBuff(inspiringPresence.getSpeedBuff() + 10);
                }
            }
    ),
    PROTECTOR_STRIKE("Protector's Strike",
            Component.text("Increase the damage converted to healing for allies of Protector's Strike by 15%", NamedTextColor.GRAY),
            Component.text("Increase the damage converted to healing for allies of Protector's Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("15%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            ProtectorsStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof ProtectorsStrike protectorsStrike) {
                    protectorsStrike.setAllyHealing(protectorsStrike.getAllyHealing() + 15);
                }
            }
    ),
    CONSECRATE_PROTECTOR("Consecrate",
            Component.text("Increase the range of Consecrate by 2 blocks and reduce the cooldown by 30%.", NamedTextColor.GRAY),
            Component.text("Increase the range of Consecrate by ", NamedTextColor.GREEN)
                     .append(Component.text("2 ", NamedTextColor.RED))
                     .append(Component.text("blocks and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("30%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            ConsecrateProtector.class,
            abstractAbility -> {
                if (abstractAbility instanceof AbstractConsecrate consecrate) {
                    consecrate.getHitBoxRadius().addAdditiveModifier("Skill Boost", 2);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                }
            }
    ),
    LIGHT_INFUSION_PROTECTOR("Light Infusion",
            Component.text("Reduce the cooldown of Light Infusion by 35% and increase the speed duration by 3 seconds.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Light Infusion by ", NamedTextColor.GREEN)
                     .append(Component.text("35% ", NamedTextColor.RED))
                     .append(Component.text("and increase the speed duration by ", NamedTextColor.GREEN))
                     .append(Component.text("3 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            LightInfusionProtector.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusionProtector lightInfusion) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .65f);
                    lightInfusion.setTickDuration(lightInfusion.getTickDuration() + 60);
                }
            }
    ),
    HOLY_RADIANCE_PROTECTOR("Holy Radiance",
            Component.text("Increase the healing of Holy Radiance by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the healing of Holy Radiance by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            HolyRadianceProtector.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceProtector holyRadianceProtector) {
                    holyRadianceProtector.getHealValues()
                                         .getRadianceHealing()
                                         .forEachValue(floatModifiable -> {
                                             floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                                         });
                }
            }
    ),
    HAMMER_OF_LIGHT("Hammer of Light",
            Component.text("Increase the healing of Hammer of Light by 25% and reduce the cooldown by 25%.", NamedTextColor.GRAY),
            Component.text("Increase the healing of Hammer of Light by ", NamedTextColor.GREEN)
                     .append(Component.text("25% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("25%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            HammerOfLight.class,
            abstractAbility -> {
                if (abstractAbility instanceof HammerOfLight hammerOfLight) {
                    hammerOfLight.getHealValues()
                                 .getHammerHealing()
                                 .forEachValue(floatModifiable -> {
                                     floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .25f);
                                 });
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                }
            }
    ),
    LIGHTNING_BOLT("Lightning Bolt",
            Component.text("Increase the damage of Lightning Bolt by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Lightning Bolt by ", NamedTextColor.GREEN)
                     .append(Component.text("20%.", NamedTextColor.RED)),
            LightningBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightningBolt lightningBolt) {
                    lightningBolt.getDamageValues()
                                 .getBoltDamage()
                                 .forEachValue(floatModifiable -> {
                                     floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                                 });
                }
            }
    ),
    CHAIN_LIGHTNING("Chain Lightning",
            Component.text("Increase the damage of Chain Lightning by 20% and reduce the cooldown by 15%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Chain Lightning by ", NamedTextColor.GREEN)
                     .append(Component.text("20% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("15%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            ChainLightning.class,
            abstractAbility -> {
                if (abstractAbility instanceof ChainLightning chainLightning) {
                    chainLightning.getDamageValues()
                                  .getChainDamage()
                                  .forEachValue(floatModifiable -> {
                                      floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .25f);
                                  });
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .85f);
                }
            }
    ),
    WINDFURY_WEAPON("Windfury Weapon",
            Component.text("Increase the damage of Windfury Weapon by 30% and increase the proc chance by 10%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Windfury Weapon by ", NamedTextColor.GREEN)
                     .append(Component.text("30% ", NamedTextColor.RED))
                     .append(Component.text("and increase the proc chance by ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            WindfuryWeapon.class,
            abstractAbility -> {
                if (abstractAbility instanceof WindfuryWeapon windfuryWeapon) {
                    windfuryWeapon.setProcChance(windfuryWeapon.getProcChance() + 10);
                    windfuryWeapon.setWeaponDamage(windfuryWeapon.getWeaponDamage() + 30);
                }
            }
    ),
    LIGHTNING_ROD("Lightning Rod",
            Component.text("Reduce the cooldown of Lightning Rod by 40%.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Lightning Rod by ", NamedTextColor.GREEN)
                     .append(Component.text("40%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            LightningRod.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightningRod) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .6f);
                }
            }
    ),
    CAPACITOR_TOTEM("Capacitor Totem",
            Component.text("Increase the damage of Capacitor Totem by 30% and reduce the cooldown by 15%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Capacitor Totem by ", NamedTextColor.GREEN)
                     .append(Component.text("30% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("15%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            CapacitorTotem.class,
            abstractAbility -> {
                if (abstractAbility instanceof CapacitorTotem capacitorTotem) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .85f);
                    capacitorTotem.getDamageValues()
                                  .getTotemDamage()
                                  .forEachValue(floatModifiable -> {
                                      floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .3f);
                                  });
                }
            }
    ),
    FALLEN_SOULS("Fallen Souls",
            Component.text("Increase the damage of Fallen Souls by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Fallen Souls by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            FallenSouls.class,
            abstractAbility -> {
                if (abstractAbility instanceof FallenSouls fallenSouls) {
                    fallenSouls.getDamageValues()
                               .getFallenSoulDamage()
                               .forEachValue(floatModifiable -> {
                                   floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                               });
                }
            }
    ),
    SPIRIT_LINK("Spirit Link",
            Component.text("Increase the damage of Spirit Link by 25% and increase the speed duration by 0.5 seconds.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Spirit Link by ", NamedTextColor.GREEN)
                     .append(Component.text("25% ", NamedTextColor.RED))
                     .append(Component.text("and increase the speed duration by ", NamedTextColor.GREEN))
                     .append(Component.text("0.5 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            SpiritLink.class,
            abstractAbility -> {
                if (abstractAbility instanceof SpiritLink spiritLink) {
                    spiritLink.getDamageValues()
                              .getLinkDamage()
                              .forEachValue(floatModifiable -> {
                                  floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .25f);
                              });
                    spiritLink.setSpeedDuration(spiritLink.getSpeedDuration() + 0.5);
                }
            }
    ),
    SOULBINDING_WEAPON("Soulbinding Weapon",
            Component.text("Increase the duration of binds by 2 seconds.", NamedTextColor.GRAY),
            Component.text("Increase the duration of binds by ", NamedTextColor.GREEN)
                     .append(Component.text("2 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            Soulbinding.class,
            abstractAbility -> {
                if (abstractAbility instanceof Soulbinding soulbinding) {
                    soulbinding.setBindDuration(soulbinding.getBindDuration() + 40);
                }
            }
    ),
    REPENTANCE("Repentance",
            Component.text("Increase the damage converted by 5% and reduce the cooldown by 10%.", NamedTextColor.GRAY),
            Component.text("Increase the damage converted by ", NamedTextColor.GREEN)
                     .append(Component.text("5% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            Repentance.class,
            abstractAbility -> {
                if (abstractAbility instanceof Repentance repentance) {
                    repentance.setDamageConvertPercent(repentance.getDamageConvertPercent() + 5);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .9f);
                }
            }
    ),
    DEATHS_DEBT("Death's Debt",
            Component.text("Increase the range of Death's Debt by 5 blocks and reduce the delayed damage inflicted by 40%.", NamedTextColor.GRAY),
            Component.text("Increase the range of Death's Debt by", NamedTextColor.GREEN)
                     .append(Component.text("5 ", NamedTextColor.RED))
                     .append(Component.text("blocks and reduce the delayed damage inflicted by ", NamedTextColor.GREEN))
                     .append(Component.text("40%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            DeathsDebt.class,
            abstractAbility -> {
                if (abstractAbility instanceof DeathsDebt deathsDebt) {
                    deathsDebt.setRespiteRadius(deathsDebt.getRespiteRadius() + 5);
                    deathsDebt.setDebtRadius(deathsDebt.getDebtRadius() + 5);
                    deathsDebt.setDelayedDamageTaken(deathsDebt.getDelayedDamageTaken() - 40);
                }
            }
    ),
    EARTHEN_SPIKE("Earthen Spike",
            Component.text("Increase the damage of Earthen Spike by 15% and increase the speed by 30%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Earthen Spike by ", NamedTextColor.GREEN)
                     .append(Component.text("15% ", NamedTextColor.RED))
                     .append(Component.text("and increase the speed by ", NamedTextColor.GREEN))
                     .append(Component.text("30%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            EarthenSpike.class,
            abstractAbility -> {
                if (abstractAbility instanceof EarthenSpike earthenSpike) {
                    earthenSpike.getDamageValues()
                                .getSpikeDamage()
                                .forEachValue(floatModifiable -> {
                                    floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .15f);
                                });
                    earthenSpike.setSpeed(earthenSpike.getSpeed() * 1.3f);
                }
            }
    ),
    BOULDER("Boulder",
            Component.text("Increase the damage you deal with Boulder by 25%.", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Boulder by ", NamedTextColor.GREEN)
                     .append(Component.text("25%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            Boulder.class,
            abstractAbility -> {
                if (abstractAbility instanceof Boulder boulder) {
                    boulder.getDamageValues()
                           .getBoulderDamage()
                           .forEachValue(floatModifiable -> {
                               floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .25f);
                           });
                }
            }
    ),
    EARTHLIVING_WEAPON("Earthliving Weapon",
            Component.text("Increase the proc chance by of Earthliving Weapon by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the proc chance by of Earthliving Weapon by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            EarthlivingWeapon.class,
            abstractAbility -> {
                if (abstractAbility instanceof EarthlivingWeapon earthlivingWeapon) {
                    earthlivingWeapon.setProcChance(earthlivingWeapon.getProcChance() + 20);
                }
            }
    ),
    CHAIN_HEAL("Chain Heal",
            Component.text("Increase the healing of Chain Heal by 30%.", NamedTextColor.GRAY),
            Component.text("Increase the healing of Chain Heal by ", NamedTextColor.GREEN)
                     .append(Component.text("30%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            ChainHeal.class,
            abstractAbility -> {
                if (abstractAbility instanceof ChainHeal chainHeal) {
                    chainHeal.getHealValues()
                             .getChainHealing()
                             .forEachValue(floatModifiable -> {
                                 floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .3f);
                             });
                }
            }
    ),
    HEALING_TOTEM("Healing Totem",
            Component.text("Increase the healing of Healing Totem by 25% and reduce the cooldown by 25%.", NamedTextColor.GRAY),
            Component.text("Increase the healing of Healing Totem by ", NamedTextColor.GREEN)
                     .append(Component.text("25% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("25%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            HealingTotem.class,
            abstractAbility -> {
                if (abstractAbility instanceof HealingTotem healingTotem) {
                    healingTotem.getHealValues()
                                .getTotemHealing()
                                .forEachValue(floatModifiable -> {
                                    floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .25f);
                                });
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                }
            }
    ),
    JUDGEMENT_STRIKE("Judgement Strike",
            Component.text("Increase the damage of Judgement Strike by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Judgement Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            JudgementStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof JudgementStrike judgementStrike) {
                    judgementStrike.getDamageValues()
                                   .getStrikeDamage()
                                   .forEachValue(floatModifiable -> {
                                       floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                                   });
                }
            }
    ),
    INCENDIARY_CURSE("Incendiary Curse",
            Component.text("Reduce the cooldown of Incendiary Curse by 35% and increase the blind duration by 0.5 seconds.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Incendiary Curse by ", NamedTextColor.GREEN)
                     .append(Component.text("35% ", NamedTextColor.RED))
                     .append(Component.text("and increase the blind duration by ", NamedTextColor.GREEN))
                     .append(Component.text("0.5 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            IncendiaryCurse.class,
            abstractAbility -> {
                if (abstractAbility instanceof IncendiaryCurse incendiaryCurse) {
                    incendiaryCurse.setBlindDurationInTicks(incendiaryCurse.getBlindDurationInTicks() + 10);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .65f);
                }
            }
    ),
    BLINDING_ASSAULT("Shadow Step",
            Component.text("Reduce the cooldown of Shadow Step by 40% and grant temporary fall damage immunity.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Shadow Step by ", NamedTextColor.GREEN)
                     .append(Component.text("40% ", NamedTextColor.RED))
                     .append(Component.text("and grant temporary fall damage immunity.", NamedTextColor.GREEN)),
            ShadowStep.class,
            abstractAbility -> {
                if (abstractAbility instanceof ShadowStep shadowStep) {
                    shadowStep.setFallDamageNegation(1000);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .6f);
                }
            }
    ),
    SOUL_SWITCH("Soul Switch",
            Component.text("Reduce the cooldown by Soul Switch by 50% and increase the range by 2 blocks.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown by Soul Switch by ", NamedTextColor.GREEN)
                     .append(Component.text("50% ", NamedTextColor.RED))
                     .append(Component.text("and increase the range by ", NamedTextColor.GREEN))
                     .append(Component.text("2 ", NamedTextColor.RED))
                     .append(Component.text("blocks.", NamedTextColor.GREEN)),
            SoulSwitch.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoulSwitch soulSwitch) {
                    soulSwitch.getHitBoxRadius().addAdditiveModifier("Skill Boost", 2);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .5f);
                }
            }
    ),
    ORDER_OF_EVISCERATE("Order of Eviscerate",
            Component.text("Increase the duration of Order of Eviscerate by 4 seconds and reduce the cooldown by 30%.", NamedTextColor.GRAY),
            Component.text("Increase the duration of Order of Eviscerate by ", NamedTextColor.GREEN)
                     .append(Component.text("4 ", NamedTextColor.RED))
                     .append(Component.text("seconds and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("30%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            OrderOfEviscerate.class,
            abstractAbility -> {
                if (abstractAbility instanceof OrderOfEviscerate orderOfEviscerate) {
                    orderOfEviscerate.setTickDuration(orderOfEviscerate.getTickDuration() + 80);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                }
            }
    ),
    RIGHTEOUS_STRIKE("Righteous Strike",
            Component.text("Increase the damage of Righteous Strike by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Righteous Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            RighteousStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof RighteousStrike righteousStrike) {
                    righteousStrike.getDamageValues()
                                   .getStrikeDamage()
                                   .forEachValue(floatModifiable -> {
                                       floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                                   });
                }
            }
    ),
    SOUL_SHACKLE("Soul Shackle",
            Component.text("Reduce the cooldown of Soul Shackle by 20%.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Soul Shackle by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            SoulShackle.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoulShackle) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    HEART_TO_HEART("Heart to Heart",
            Component.text("Increase the healing of Heart to Heart by 300 and reduce the cooldown by 15%.", NamedTextColor.GRAY),
            Component.text("Increase the healing of Heart to Heart by ", NamedTextColor.GREEN)
                     .append(Component.text("300 ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("15%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            HeartToHeart.class,
            abstractAbility -> {
                if (abstractAbility instanceof HeartToHeart heartToHeart) {
                    heartToHeart.setHealthRestore(heartToHeart.getHealthRestore() + 300);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .85f);
                }
            }
    ),
    PRISM_GUARD("Prism Guard",
            Component.text("Increase the projectile damage reduction of Prism Guard by 15% and increase the healing by 300.", NamedTextColor.GRAY),
            Component.text("Increase the projectile damage reduction of Prism Guard by ", NamedTextColor.GREEN)
                     .append(Component.text("15% ", NamedTextColor.RED))
                     .append(Component.text("and increase the healing by ", NamedTextColor.GREEN))
                     .append(Component.text("300", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            PrismGuard.class,
            abstractAbility -> {
                if (abstractAbility instanceof PrismGuard prismGuard) {
                    prismGuard.setProjectileDamageReduction(prismGuard.getProjectileDamageReduction() + 15);
                    prismGuard.setBubbleHealing(prismGuard.getBubbleHealing() + 300);
                }
            }
    ),
    VINDICATE("Vindicate",
            Component.text("Increase the damage reduction of Vindicate by 10% and reduce the cooldown by 25%.", NamedTextColor.GRAY),
            Component.text("Increase the damage reduction of Vindicate by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("25%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            Vindicate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Vindicate vindicate) {
                    vindicate.setVindicateDamageReduction(vindicate.getVindicateDamageReduction() + 10);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .75f);
                }
            }
    ),
    IMPALING_STRIKE("Impaling Strike",
            Component.text("Increase the damage of Impaling Strike by 10% and increase the leech duration by 5 seconds.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Impaling Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and increase the leech duration by ", NamedTextColor.GREEN))
                     .append(Component.text("5 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            ImpalingStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof ImpalingStrike impalingStrike) {
                    impalingStrike.setLeechDuration(impalingStrike.getLeechDuration() + 5);
                    impalingStrike.getDamageValues()
                                  .getStrikeDamage()
                                  .forEachValue(floatModifiable -> {
                                      floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .1f);
                                  });
                }
            }
    ),
    SOOTHING_PUDDLE("Soothing Elixir",
            Component.text("Increase the healing of Soothing Elixir by 25%.", NamedTextColor.GRAY),
            Component.text("Increase the healing of Soothing Elixir by ", NamedTextColor.GREEN)
                     .append(Component.text("25%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            SoothingElixir.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoothingElixir soothingElixir) {
                    soothingElixir.getHealValues()
                                  .getElixirHealing()
                                  .forEachValue(floatModifiable -> {
                                      floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .25f);
                                  });
                }
            }
    ),
    VITALITY_CONCOCTION("Vitality Concoction",
            Component.text("Reduce the cooldown of Vitality Concoction by 30%.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Vitality Concoction by ", NamedTextColor.GREEN)
                     .append(Component.text("30%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            VitalityConcoction.class,
            abstractAbility -> {
                if (abstractAbility instanceof VitalityConcoction vitalityConcoction) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                }
            }
    ),
    REMEDIC_CHAINS("Remedic Chains",
            Component.text("Increase the healing of Remedic Chains by 10% and increase the link break range by 10 blocks.", NamedTextColor.GRAY),
            Component.text("Increase the healing of Remedic Chains by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and increase the link break range by ", NamedTextColor.GREEN))
                     .append(Component.text("10 ", NamedTextColor.RED))
                     .append(Component.text("blocks.", NamedTextColor.GREEN)),
            RemedicChains.class,
            abstractAbility -> {
                if (abstractAbility instanceof RemedicChains remedicChains) {
                    remedicChains.getHealValues()
                                 .getChainHealing()
                                 .forEachValue(floatModifiable -> {
                                     floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .1f);
                                 });
                    remedicChains.setLinkBreakRadius(remedicChains.getLinkBreakRadius() + 10);
                }
            }
    ),
    DRAINING_MIASMA("Draining Miasma",
            Component.text("Increase the leech duration of Draining Miasma by 5 seconds and reduce the cooldown by 30%.", NamedTextColor.GRAY),
            Component.text("Increase the leech duration of Draining Miasma by ", NamedTextColor.GREEN)
                     .append(Component.text("5 ", NamedTextColor.RED))
                     .append(Component.text("seconds and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("30%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            DrainingMiasma.class,
            abstractAbility -> {
                if (abstractAbility instanceof DrainingMiasma drainingMiasma) {
                    drainingMiasma.setLeechDuration(drainingMiasma.getLeechDuration() + 5);
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .7f);
                }
            }
    ),
    POISONOUS_HEX("Poisonous Hex",
            Component.text("Increase the damage over time inflicted by Poisonous Hex by 35% and the duration by 2 seconds.", NamedTextColor.GRAY),
            Component.text("Increase the damage over time inflicted by Poisonous Hex by ", NamedTextColor.GREEN)
                     .append(Component.text("35% ", NamedTextColor.RED))
                     .append(Component.text("and the duration by ", NamedTextColor.GREEN))
                     .append(Component.text("2 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            PoisonousHex.class,
            abstractAbility -> {
                if (abstractAbility instanceof PoisonousHex poisonousHex) {
                    poisonousHex.setDotMinDamage(poisonousHex.getDotMinDamage() * 1.35f);
                    poisonousHex.setDotMaxDamage(poisonousHex.getDotMaxDamage() * 1.35f);
                    poisonousHex.setTickDuration(poisonousHex.getTickDuration() + 20);
                }
            }
    ),
    SOULFIRE_BEAM("Soulfire Beam",
            Component.text("Increase the damage of Soulfire Beam by 20% and reduce the cooldown by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Soulfire Beam by ", NamedTextColor.GREEN)
                     .append(Component.text("20% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            SoulfireBeam.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoulfireBeam soulfireBeam) {
                    soulfireBeam.getDamageValues()
                                .getBeamDamage()
                                .forEachValue(floatModifiable -> {
                                    floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                                });
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    ENERGY_SEER_CONJURER("Energy Seer",
            Component.text("Increase the energy restored by Energy Seer by 40 and increase the damage bonus by 10%.", NamedTextColor.GRAY),
            Component.text("Increase the energy restored by Energy Seer by ", NamedTextColor.GREEN)
                     .append(Component.text("40 ", NamedTextColor.RED))
                     .append(Component.text("and increase the damage bonus by ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            EnergySeerConjurer.class,
            abstractAbility -> {
                if (abstractAbility instanceof EnergySeerConjurer energySeerConjurer) {
                    energySeerConjurer.setEnergyRestore(energySeerConjurer.getEnergyRestore() + 40);
                    energySeerConjurer.setDamageIncrease(energySeerConjurer.getDamageIncrease() + 10);
                }
            }
    ),
    CONTAGIOUS_FACADE("Contagious Facade",
            Component.text("Increased the damage reduction of Contagious Facade by 10%.", NamedTextColor.GRAY),
            Component.text("Increased the damage reduction of Contagious Facade by ", NamedTextColor.GREEN)
                     .append(Component.text("10%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            ContagiousFacade.class,
            abstractAbility -> {
                if (abstractAbility instanceof ContagiousFacade contagiousFacade) {
                    contagiousFacade.getDamageAbsorption().addAdditiveModifier("Skill Boost", 10);
                }
            }
    ),
    ASTRAL_PLAGUE("Astral Plague",
            Component.text("Reduce the cooldown of Astral Plague by 20%.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Astral Plague by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            AstralPlague.class,
            abstractAbility -> {
                if (abstractAbility instanceof AstralPlague astralPlague) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    FORTIFYING_HEX("Fortifying Hex",
            Component.text("Increase the damage of Fortifying Hex by 15% and increase the duration by 2 seconds.", NamedTextColor.GRAY),
            Component.text("Increase the damage of Fortifying Hex by ", NamedTextColor.GREEN)
                     .append(Component.text("15%", NamedTextColor.RED))
                     .append(Component.text("and the duration by ", NamedTextColor.GREEN))
                     .append(Component.text("2 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            FortifyingHex.class,
            abstractAbility -> {
                if (abstractAbility instanceof FortifyingHex fortifyingHex) {
                    fortifyingHex.getDamageValues()
                                 .getHexDamage()
                                 .forEachValue(floatModifiable -> {
                                     floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .15f);
                                 });
                    fortifyingHex.setTickDuration(fortifyingHex.getTickDuration() + 40);
                }
            }
    ),
    GUARDIAN_BEAM("Guardian Beam",
            Component.text("Increase the health of the shield granted by Guardian Beam by 17% multiplicatively and reduce the cooldown by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the health of the shield granted by Guardian Beam by ", NamedTextColor.GREEN)
                     .append(Component.text("17% ", NamedTextColor.RED))
                     .append(Component.text("multiplicatively and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            GuardianBeam.class,
            abstractAbility -> {
                if (abstractAbility instanceof GuardianBeam guardianBeam) {
                    List<Integer> shieldPercents = guardianBeam.getShieldPercents();
                    shieldPercents.replaceAll(integer -> (int) (integer * 1.17f));
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    ENERGY_SEER_SENTINEL("Energy Seer",
            Component.text("Increase the energy restored by Energy Seer by 40 and increase the damage reduction by 2%.\n", NamedTextColor.GRAY),
            Component.text("Increase the energy restored by Energy Seer by ", NamedTextColor.GREEN)
                     .append(Component.text("40 ", NamedTextColor.RED))
                     .append(Component.text("and increase the damage reduction by ", NamedTextColor.GREEN))
                     .append(Component.text("2%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            EnergySeerSentinel.class,
            abstractAbility -> {
                if (abstractAbility instanceof EnergySeerSentinel energySeerSentinel) {
                    energySeerSentinel.setEnergyRestore(energySeerSentinel.getEnergyRestore() + 40);
                    energySeerSentinel.setDamageResistance(energySeerSentinel.getDamageResistance() + 2);
                }
            }
    ),
    MYSTICAL_BARRIER("Mystical Barrier",
            Component.text("Increase the rune timer increase inflicted by Mystical Barrier by 0.25 seconds and increase the maximum shield health by 400.", NamedTextColor.GRAY),
            Component.text("Increase the rune timer increase inflicted by Mystical Barrier by ", NamedTextColor.GREEN)
                     .append(Component.text("0.25 ", NamedTextColor.RED))
                     .append(Component.text("seconds and increase the maximum shield health by", NamedTextColor.GREEN))
                     .append(Component.text("400", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            MysticalBarrier.class,
            abstractAbility -> {
                if (abstractAbility instanceof MysticalBarrier mysticalBarrier) {
                    mysticalBarrier.setRuneTimerIncrease(mysticalBarrier.getRuneTimerIncrease() + 0.25f);
                    mysticalBarrier.setShieldMaxHealth(mysticalBarrier.getShieldMaxHealth() + 400);
                }
            }
    ),
    SANCTUARY("Sanctuary",
            Component.text("Reduce the cooldown of Sanctuary by 20%.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Sanctuary by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            Sanctuary.class,
            abstractAbility -> {
                if (abstractAbility instanceof Sanctuary sanctuary) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    MERCIFUL_HEX("Merciful Hex",
            Component.text("Increase the healing over time healed by Merciful Hex by 35% and increase the duration by 2 seconds.", NamedTextColor.GRAY),
            Component.text("Increase the healing over time healed by Merciful Hex by ", NamedTextColor.GREEN)
                     .append(Component.text("35% ", NamedTextColor.RED))
                     .append(Component.text("and increase the duration by ", NamedTextColor.GREEN))
                     .append(Component.text("2 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            MercifulHex.class,
            abstractAbility -> {
                if (abstractAbility instanceof MercifulHex mercifulHex) {
                    mercifulHex.setDotMinHeal(mercifulHex.getDotMinHeal() * 1.35f);
                    mercifulHex.setDotMaxHeal(mercifulHex.getDotMaxHeal() * 1.35f);
                    mercifulHex.setTickDuration(mercifulHex.getTickDuration() + 20);
                }
            }
    ),
    RAY_OF_LIGHT("Ray of Light",
            Component.text("Increase the healing of Ray of Light by 20% and reduce the cooldown by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the healing of Ray of Light by ", NamedTextColor.GREEN)
                     .append(Component.text("20% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            RayOfLight.class,
            abstractAbility -> {
                if (abstractAbility instanceof RayOfLight rayOfLight) {
                    rayOfLight.getHealValues()
                              .getRayHealing()
                              .forEachValue(floatModifiable -> {
                                  floatModifiable.addMultiplicativeModifierAdd("Skill Boost", .2f);
                              });
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),
    ENERGY_SEER_LUMINARY("Energy Seer",
            Component.text("Increase the energy restored by Energy Seer by 40 and increase the healing modifier by 10%.\n", NamedTextColor.GRAY),
            Component.text("Increase the energy restored by Energy Seer by ", NamedTextColor.GREEN)
                     .append(Component.text("40 ", NamedTextColor.RED))
                     .append(Component.text("and increase the healing modifier by ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            EnergySeerLuminary.class,
            abstractAbility -> {
                if (abstractAbility instanceof EnergySeerLuminary energySeerLuminary) {
                    energySeerLuminary.setEnergyRestore(energySeerLuminary.getEnergyRestore() + 40);
                    energySeerLuminary.setHealingIncrease(energySeerLuminary.getHealingIncrease() + 10);
                }
            }
    ),
    SANCTIFIED_BEACON("Sanctified Beacon",
            Component.text("Increase the Crit Multiplier reduction inflicted by Sanctified Beacon by 10%.", NamedTextColor.GRAY),
            Component.text("Increase the Crit Multiplier reduction inflicted by Sanctified Beacon by ", NamedTextColor.GREEN)
                     .append(Component.text("30%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            SanctifiedBeacon.class,
            abstractAbility -> {
                if (abstractAbility instanceof SanctifiedBeacon sanctifiedBeacon) {
                    sanctifiedBeacon.setCritMultiplierReducedBy(sanctifiedBeacon.getCritMultiplierReducedBy() + 10);
                }
            }
    ),
    DIVINE_BLESSING("Divine Blessing",
            Component.text("Reduce the cooldown of Divine Blessing by 20%.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Divine Blessing by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            DivineBlessing.class,
            abstractAbility -> {
                if (abstractAbility instanceof DivineBlessing divineBlessing) {
                    abstractAbility.getCooldown().addMultiplicativeModifierMult("Skill Boost", .8f);
                }
            }
    ),

    ;

    public static final SkillBoosts[] VALUES = values();
    public final String name;
    public final TextComponent description;
    public final TextComponent selectedDescription;
    public final Class<?> ability;
    public final Consumer<AbstractAbility> applyBoost;

    SkillBoosts(String name, TextComponent description, TextComponent selectedDescription, Class<?> ability, Consumer<AbstractAbility> applyBoost) {
        this.name = name;
        this.description = description;
        this.selectedDescription = selectedDescription;
        this.ability = ability;
        this.applyBoost = applyBoost;
    }
}
package com.ebicep.warlords.player.general;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.function.Consumer;

public enum SkillBoosts {
    FIREBALL("Fireball",
            Component.text("Increases the damage you deal with Fireball by 20%", NamedTextColor.GRAY),
            Component.text("Increases the damage you deal with Fireball by ", NamedTextColor.GREEN).append(Component.text("20%", NamedTextColor.RED)),
            Fireball.class,
            abstractAbility -> {
                if (abstractAbility instanceof Fireball) {
                    abstractAbility.multiplyMinMax(1.2f);
                }
            }
    ),
    FLAME_BURST("Flame Burst",
            Component.text("Increases the damage you deal with Flame Burst by 25% and reduce the energy cost by 40", NamedTextColor.GRAY),
            Component.text("Increases the damage you deal with Flame Burst by ", NamedTextColor.GREEN)
                     .append(Component.text("25% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the energy cost by ", NamedTextColor.GREEN))
                     .append(Component.text("40", NamedTextColor.RED)),
            FlameBurst.class,
            abstractAbility -> {
                if (abstractAbility instanceof FlameBurst) {
                    abstractAbility.multiplyMinMax(1.25f);
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 40);
                }
            }
    ),
    TIME_WARP_PYROMANCER("Time Warp",
            Component.text("Increase the amount of health you restore with Time Warp by 10% and reduce the cooldown by 50%.", NamedTextColor.GRAY),
            Component.text("Increase the amount of health you restore with Time Warp by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("50%.", NamedTextColor.RED)),
            TimeWarpPyromancer.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarpPyromancer timeWarp) {
                    timeWarp.setWarpHealPercentage(40);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .5f);
                }
            }
    ),
    ARCANE_SHIELD_PYROMANCER("Arcane Shield",
            Component.text("Remove the energy cost of Arcane Shield and reduce the cooldown by 30%", NamedTextColor.GRAY),
            Component.text("Remove the energy cost of Arcane Shield and reduce the cooldown by ", NamedTextColor.GREEN)
                     .append(Component.text("30%", NamedTextColor.RED)),
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 40);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    INFERNO("Inferno",
            Component.text("Increase the Crit Multiplier bonus of Inferno by 60%", NamedTextColor.GRAY),
            Component.text("Increase the Crit Multiplier bonus of Inferno by ", NamedTextColor.GREEN).append(Component.text("60%", NamedTextColor.RED)),
            Inferno.class,
            abstractAbility -> {
                if (abstractAbility instanceof Inferno inferno) {
                    inferno.setCritMultiplierIncrease(inferno.getCritMultiplierIncrease() + 60);
                }
            }
    ),
    FROST_BOLT("Frostbolt",
            Component.text("Increases the damage you deal with Frostbolt by 20% and increase the slowness by 5%", NamedTextColor.GRAY),
            Component.text("Increases the damage you deal with Frostbolt by ", NamedTextColor.GREEN)
                     .append(Component.text("20% ", NamedTextColor.RED))
                     .append(Component.text("and increase the slowness by ", NamedTextColor.GREEN))
                     .append(Component.text("5%", NamedTextColor.RED)),
            FrostBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof FrostBolt frostBolt) {
                    abstractAbility.multiplyMinMax(1.2f);
                    frostBolt.setSlowness(frostBolt.getSlowness() + 5);
                }
            }
    ),
    FREEZING_BREATH("Freezing Breath",
            Component.text("Increase the damage you deal with Freezing Breath by 20% and reduce the cooldown by 20%", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Freezing Breath by ", NamedTextColor.GREEN)
                     .append(Component.text("20% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("20%", NamedTextColor.RED)),
            FreezingBreath.class,
            abstractAbility -> {
                if (abstractAbility instanceof FreezingBreath) {
                    abstractAbility.multiplyMinMax(1.2f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                }
            }
    ),
    TIME_WARP_CRYOMANCER("Time Warp",
            Component.text("Reduce the cooldown of Time Warp by 40%", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Time Warp by ", NamedTextColor.GREEN)
                     .append(Component.text("40%", NamedTextColor.RED)),
            TimeWarpCryomancer.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarpCryomancer) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    ARCANE_SHIELD_CRYOMANCER("Arcane Shield",
            Component.text("Reduce the cooldown of Arcane Shield by 30%", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Arcane Shield by ", NamedTextColor.GREEN)
                     .append(Component.text("30%", NamedTextColor.RED)),
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    ICE_BARRIER("Ice Barrier",
            Component.text("Increase the amount damage you reduce with Ice Barrier by 5% and increase the duration by 2 seconds", NamedTextColor.GRAY),
            Component.text("Increase the amount damage you reduce with Ice Barrier by ", NamedTextColor.GREEN)
                     .append(Component.text("5% ", NamedTextColor.RED))
                     .append(Component.text("and increase the duration by ", NamedTextColor.GREEN))
                     .append(Component.text("2 ", NamedTextColor.RED))
                     .append(Component.text("seconds", NamedTextColor.GREEN)),
            IceBarrier.class,
            abstractAbility -> {
                if (abstractAbility instanceof IceBarrier iceBarrier) {
                    iceBarrier.setDamageReductionPercent(55);
                    iceBarrier.setTickDuration(iceBarrier.getTickDuration() + 40);
                }
            }
    ),
    WATER_BOLT("Water Bolt",
            Component.text("Increases the amount of health you restore with Water Bolt by 20%", NamedTextColor.GRAY),
            Component.text("Increases the amount of health you restore with Water Bolt by ", NamedTextColor.GREEN).append(Component.text("20%", NamedTextColor.RED)),
            WaterBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof WaterBolt) {
                    abstractAbility.multiplyMinMax(1.2f);
                }
            }
    ),
    WATER_BREATH("Water Breath",
            Component.text("Increases the amount of health you restore with Water Breath by 15% and reduce the energy cost by 30", NamedTextColor.GRAY),
            Component.text("Increases the amount of health you restore with Water Breath by ", NamedTextColor.GREEN)
                     .append(Component.text("15% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the energy cost by ", NamedTextColor.GREEN))
                     .append(Component.text("30", NamedTextColor.RED)),
            WaterBreath.class,
            abstractAbility -> {
                if (abstractAbility instanceof WaterBreath) {
                    abstractAbility.multiplyMinMax(1.15f);
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 30);
                }
            }
    ),
    TIME_WARP_AQUAMANCER("Time Warp",
            Component.text("Reduce the cooldown of Time Warp by 50%", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Time Warp by ", NamedTextColor.GREEN)
                     .append(Component.text("50%", NamedTextColor.RED)),
            TimeWarpAquamancer.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarpAquamancer) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .5f);
                }
            }
    ),
    ARCANE_SHIELD_AQUAMANCER("Arcane Shield",
            Component.text("Remove the energy cost of Arcane Shield and reduce the cooldown by 30%", NamedTextColor.GRAY),
            Component.text("Remove the energy cost of Arcane Shield and reduce the cooldown by ", NamedTextColor.GREEN)
                     .append(Component.text("30%", NamedTextColor.RED)),
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 40);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    HEALING_RAIN("Healing Rain",
            Component.text("Increases the duration of Healing Rain by 4 seconds and reduce the cooldown by 20%", NamedTextColor.GRAY),
            Component.text("Increases the duration of Healing Rain by ", NamedTextColor.GREEN)
                     .append(Component.text("4 ", NamedTextColor.RED))
                     .append(Component.text("seconds and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("20%", NamedTextColor.RED)),
            HealingRain.class,
            abstractAbility -> {
                if (abstractAbility instanceof HealingRain healingRain) {
                    healingRain.setTickDuration(healingRain.getTickDuration() + 80);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                }
            }
    ),
    WOUNDING_STRIKE_BERSERKER("Wounding Strike",
            Component.text("Increase the damage you deal with Wounding Strike by 10% and reduce the energy cost by 10", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Wounding Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the energy cost by ", NamedTextColor.GREEN))
                     .append(Component.text("10", NamedTextColor.RED)),
            WoundingStrikeBerserker.class,
            abstractAbility -> {
                if (abstractAbility instanceof WoundingStrikeBerserker) {
                    abstractAbility.multiplyMinMax(1.1f);
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 10);
                }
            }
    ),
    SEISMIC_WAVE_BERSERKER("Seismic Wave",
            Component.text("Increase the damage you deal with Seismic Wave by 15% and reduce the cooldown by 25%", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Seismic Wave by ", NamedTextColor.GREEN)
                     .append(Component.text("15% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("25%", NamedTextColor.RED)),
            SeismicWave.class,
            abstractAbility -> {
                if (abstractAbility instanceof SeismicWave) {
                    abstractAbility.multiplyMinMax(1.15f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    GROUND_SLAM_BERSERKER("Ground Slam",
            Component.text("Increase the damage you deal with Ground Slam by 35% and reduce the cooldown by 10%", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Ground Slam by ", NamedTextColor.GREEN)
                     .append(Component.text("35% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED)),
            GroundSlam.class,
            abstractAbility -> {
                if (abstractAbility instanceof GroundSlam) {
                    abstractAbility.multiplyMinMax(1.35f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .9f);
                }
            }
    ),
    BLOOD_LUST("Blood Lust",
            Component.text("Reduce the cooldown of Blood Lust by 30% and increase damage converted to healing by 5%", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Blood Lust by ", NamedTextColor.GREEN)
                     .append(Component.text("30% ", NamedTextColor.RED))
                     .append(Component.text("and increase damage converted to healing by ", NamedTextColor.GREEN))
                     .append(Component.text("5%", NamedTextColor.RED)),
            BloodLust.class,
            abstractAbility -> {
                if (abstractAbility instanceof BloodLust bloodLust) {
                    bloodLust.setDamageConvertPercent(70);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    BERSERK("Berserk",
            Component.text("Increase the damage bonus of Berserk by 15% and increase the speed by 10%", NamedTextColor.GRAY),
            Component.text("Increase the damage bonus of Berserk by ", NamedTextColor.GREEN)
                     .append(Component.text("15% ", NamedTextColor.RED))
                     .append(Component.text("and increase the speed by ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED)),
            Berserk.class,
            abstractAbility -> {
                if (abstractAbility instanceof Berserk berserk) {
                    berserk.setDamageIncrease(berserk.getDamageIncrease() + 15);
                    berserk.setSpeedBuff(berserk.getSpeedBuff() + 10);
                }
            }
    ),
    WOUNDING_STRIKE_DEFENDER("Wounding Strike",
            Component.text("Increase the damage you deal with Wounding Strike by 10% and increase wounding by 25%", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Wounding Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and increase wounding by ", NamedTextColor.GREEN))
                     .append(Component.text("25%", NamedTextColor.RED)),
            WoundingStrikeDefender.class,
            abstractAbility -> {
                if (abstractAbility instanceof WoundingStrikeDefender strike) {
                    abstractAbility.multiplyMinMax(1.1f);
                    strike.setWounding(strike.getWounding() + 25);
                }
            }
    ),
    SEISMIC_WAVE_DEFENDER("Seismic Wave",
            Component.text("Increase the amount knockback you deal with Seismic Wave by 35% and reduce the cooldown by 25%", NamedTextColor.GRAY),
            Component.text("Increase the amount knockback you deal with Seismic Wave by ", NamedTextColor.GREEN)
                     .append(Component.text("35% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("", NamedTextColor.GREEN))
                     .append(Component.text("25%", NamedTextColor.RED)),
            SeismicWave.class,
            abstractAbility -> {
                if (abstractAbility instanceof SeismicWave seismicWave) {
                    seismicWave.setVelocity(1.8f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    GROUND_SLAM_DEFENDER("Ground Slam",
            Component.text("Increase the amount of knockback you deal with Ground Slam by 10% and reduce the cooldown by 20%", NamedTextColor.GRAY),
            Component.text("Increase the amount of knockback you deal with Ground Slam by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("20%", NamedTextColor.RED)),
            GroundSlam.class,
            abstractAbility -> {
                if (abstractAbility instanceof GroundSlam groundSlam) {
                    groundSlam.setVelocity(1.35f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                }
            }
    ),
    INTERVENE("Intervene",
            Component.text("Increase the cast and break radius of Intervene by 5 blocks and increase the max amount of damage you can absorb by 400", NamedTextColor.GRAY),
            Component.text("Increase the cast and break radius of Intervene by ", NamedTextColor.GREEN)
                     .append(Component.text("5 ", NamedTextColor.RED))
                     .append(Component.text("blocks and increase the max amount of damage you can absorb by ", NamedTextColor.GREEN))
                     .append(Component.text("400", NamedTextColor.RED)),
            Intervene.class,
            abstractAbility -> {
                if (abstractAbility instanceof Intervene intervene) {
                    intervene.setMaxDamagePrevented(4000);
                    intervene.setRadius(15);
                    intervene.setBreakRadius(20);
                }
            }
    ),
    LAST_STAND("Last Stand",
            Component.text("Increase the amount damage you reduce with Last Stand by 5% and reduce the cooldown by 10%", NamedTextColor.GRAY),
            Component.text("Increase the amount damage you reduce with Last Stand by ", NamedTextColor.GREEN)
                     .append(Component.text("5% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED)),
            LastStand.class,
            abstractAbility -> {
                if (abstractAbility instanceof LastStand lastStand) {
                    lastStand.setSelfDamageReductionPercent(55);
                    lastStand.setTeammateDamageReductionPercent(45);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .9f);
                }
            }
    ),
    CRIPPLING_STRIKE("Crippling Strike",
            Component.text("Increase the damage you reduce with Crippling Strike by 10% and increase the additional reduction per strike by 5%", NamedTextColor.GRAY),
            Component.text("Increase the damage you reduce with Crippling Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and increase the additional reduction per strike by ", NamedTextColor.GREEN))
                     .append(Component.text("5%", NamedTextColor.RED)),
            CripplingStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof CripplingStrike cripplingStrike) {
                    cripplingStrike.setCripple(cripplingStrike.getCripple() + 10);
                    cripplingStrike.setCripplePerStrike(cripplingStrike.getCripplePerStrike() + 5);
                }
            }
    ),
    RECKLESS_CHARGE("Reckless Charge",
            Component.text("Increase the immobilize duration of your Reckless Charge by 0.3 seconds and reduce the cooldown by 30%", NamedTextColor.GRAY),
            Component.text("Increase the immobilize duration of your Reckless Charge by ", NamedTextColor.GREEN)
                     .append(Component.text("0.3 ", NamedTextColor.RED))
                     .append(Component.text("seconds and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("30%", NamedTextColor.RED)),
            RecklessCharge.class,
            abstractAbility -> {
                if (abstractAbility instanceof RecklessCharge recklessCharge) {
                    recklessCharge.setStunTimeInTicks(16);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    GROUND_SLAM_REVENANT("Ground Slam",
            Component.text("Reduce the cooldown of Ground Slam by 40%", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Ground Slamby  ", NamedTextColor.GREEN)
                     .append(Component.text("40%", NamedTextColor.RED)),
            GroundSlam.class,
            abstractAbility -> {
                if (abstractAbility instanceof GroundSlam) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    ORBS_OF_LIFE("Orbs of Life",
            Component.text("Increases the amount of health you restore with Orbs of Life by 20%", NamedTextColor.GRAY),
            Component.text("Increases the amount of health you restore with Orbs of Life by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED)),
            OrbsOfLife.class,
            abstractAbility -> {
                if (abstractAbility instanceof OrbsOfLife) {
                    abstractAbility.multiplyMinMax(1.2f);
                }
            }
    ),
    UNDYING_ARMY("Undying Army",
            Component.text("Reduce the damage of Undying Army after dying by 5% and increase the duration by 5 seconds", NamedTextColor.GRAY),
            Component.text("Reduce the damage of Undying Army after dying by ", NamedTextColor.GREEN)
                     .append(Component.text("5% ", NamedTextColor.RED))
                     .append(Component.text("and increase the duration by ", NamedTextColor.GREEN))
                     .append(Component.text("5 ", NamedTextColor.RED))
                     .append(Component.text("seconds", NamedTextColor.GREEN)),
            UndyingArmy.class,
            abstractAbility -> {
                if (abstractAbility instanceof UndyingArmy undyingArmy) {
                    undyingArmy.setTickDuration(undyingArmy.getTickDuration() + 100);
                    undyingArmy.setMaxHealthDamage(undyingArmy.getMaxHealthDamage() - 5);
                }
            }
    ),
    AVENGER_STRIKE("Avenger's Strike",
            Component.text("Increase the damage you deal with Avenger's Strike by 15% and increase the energy you steal by 5", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Avenger's Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("15% ", NamedTextColor.RED))
                     .append(Component.text("and increase the energy you steal by ", NamedTextColor.GREEN))
                     .append(Component.text("5", NamedTextColor.RED)),
            AvengersStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof AvengersStrike avengersStrike) {
                    abstractAbility.multiplyMinMax(1.15f);
                    avengersStrike.setEnergySteal(avengersStrike.getEnergySteal() + 5);
                }
            }
    ),
    CONSECRATE_AVENGER("Consecrate",
            Component.text("Remove the energy cost of Consecrate and increase the damage by 35%", NamedTextColor.GRAY),
            Component.text("Remove the energy cost of Consecrate and increase the damage by ", NamedTextColor.GREEN).append(Component.text("35%", NamedTextColor.RED)),
            Consecrate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Consecrate) {
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 50);
                    abstractAbility.multiplyMinMax(1.35f);
                }
            }
    ),
    LIGHT_INFUSION_AVENGER("Light Infusion",
            Component.text("Reduce the cooldown of Light Infusion by 25% and increase the energy you restore by 40", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Light Infusion by ", NamedTextColor.GREEN)
                     .append(Component.text("25% ", NamedTextColor.RED))
                     .append(Component.text("and increase the energy you restore by ", NamedTextColor.GREEN))
                     .append(Component.text("40", NamedTextColor.RED)),
            LightInfusionAvenger.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusionAvenger lightInfusion) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                    lightInfusion.setEnergyGiven(lightInfusion.getEnergyGiven() + 40);
                }
            }
    ),
    HOLY_RADIANCE_AVENGER("Holy Radiance",
            Component.text("Reduce the cooldown of Holy Radiance by 20% and double the energy drain of Avenger's Mark", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Holy Radiance by ", NamedTextColor.GREEN)
                     .append(Component.text("20% ", NamedTextColor.RED))
                     .append(Component.text("and double the energy drain of Avenger's Mark", NamedTextColor.GREEN)),
            HolyRadianceAvenger.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceAvenger holyRadiance) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                    holyRadiance.setEnergyDrainPerSecond(holyRadiance.getEnergyDrainPerSecond() * 2);
                }
            }
    ),
    AVENGERS_WRATH("Avenger's Wrath",
            Component.text("Increase the energy per second of Avenger's Wrath by 10 and increase the duration by 5 seconds", NamedTextColor.GRAY),
            Component.text("Increase the energy per second of Avenger's Wrath by ", NamedTextColor.GREEN)
                     .append(Component.text("10 ", NamedTextColor.RED))
                     .append(Component.text("and increase the duration by ", NamedTextColor.GREEN))
                     .append(Component.text("5 ", NamedTextColor.RED))
                     .append(Component.text("seconds", NamedTextColor.GREEN)),
            AvengersWrath.class,
            abstractAbility -> {
                if (abstractAbility instanceof AvengersWrath avengersWrath) {
                    avengersWrath.setTickDuration(avengersWrath.getTickDuration() + 100);
                    avengersWrath.setEnergyPerSecond(avengersWrath.getEnergyPerSecond() + 10);
                }
            }
    ),
    CRUSADER_STRIKE("Crusader's Strike",
            Component.text("Increase the damage you deal with Crusader's Strike by 20%", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Crusader's Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED)),
            CrusadersStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof CrusadersStrike) {
                    abstractAbility.multiplyMinMax(1.2f);
                }
            }
    ),
    CONSECRATE_CRUSADER("Consecrate",
            Component.text("Remove the energy cost of Consecrate and increase the damage by 35%", NamedTextColor.GRAY),
            Component.text("Remove the energy cost of Consecrate and increase the damage by ", NamedTextColor.GREEN)
                     .append(Component.text("35%", NamedTextColor.RED)),
            Consecrate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Consecrate) {
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 50);
                    abstractAbility.multiplyMinMax(1.35f);
                }
            }
    ),
    LIGHT_INFUSION_CRUSADER("Light Infusion",
            Component.text("Reduce the cooldown of Light Infusion by 35% and increase the speed duration by 3 seconds", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Light Infusion by ", NamedTextColor.GREEN)
                     .append(Component.text("35% ", NamedTextColor.RED))
                     .append(Component.text("and increase the speed duration by ", NamedTextColor.GREEN))
                     .append(Component.text("3 ", NamedTextColor.RED))
                     .append(Component.text("seconds", NamedTextColor.GREEN)),
            LightInfusionCrusader.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusionCrusader lightInfusion) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .65f);
                    lightInfusion.setTickDuration(lightInfusion.getTickDuration() + 60);
                }
            }
    ),
    HOLY_RADIANCE_CRUSADER("Holy Radiance",
            Component.text("Reduce the cooldown of Holy Radiance by 25%, increase the duration of Crusader's Mark by 4 seconds and speed bonus by 15%", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Holy Radiance by ", NamedTextColor.GREEN)
                     .append(Component.text("25%", NamedTextColor.RED))
                     .append(Component.text(", increase the duration of Crusader's Mark by ", NamedTextColor.GREEN))
                     .append(Component.text("4 ", NamedTextColor.RED))
                     .append(Component.text("seconds and speed bonus by ", NamedTextColor.GREEN))
                     .append(Component.text("15%", NamedTextColor.RED)),
            HolyRadianceCrusader.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceCrusader holyRadiance) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                    holyRadiance.setMarkDuration(12);
                    holyRadiance.setMarkSpeed(holyRadiance.getMarkSpeed() + 15);
                }
            }
    ),
    INSPIRING_PRESENCE("Inspiring Presence",
            Component.text("Reduce the cooldown of Inspiring Presence by 25% and increase the speed by 10%", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Inspiring Presence by ", NamedTextColor.GREEN)
                     .append(Component.text("25% ", NamedTextColor.RED))
                     .append(Component.text("and increase the speed by ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED)),
            InspiringPresence.class,
            abstractAbility -> {
                if (abstractAbility instanceof InspiringPresence inspiringPresence) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                    inspiringPresence.setSpeedBuff(inspiringPresence.getSpeedBuff() + 10);
                }
            }
    ),
    PROTECTOR_STRIKE("Protector's Strike",
            Component.text("Increase the amount of damage you convert into healing for allies with Protector's Strike by 10% and heal 1 more ally.", NamedTextColor.GRAY),
            Component.text("Increase the amount of damage you convert into healing for allies with Protector's Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and heal ", NamedTextColor.GREEN))
                     .append(Component.text("1 ", NamedTextColor.RED))
                     .append(Component.text("more ally.", NamedTextColor.GREEN)),
            ProtectorsStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof ProtectorsStrike protectorsStrike) {
                    protectorsStrike.setMinConvert(protectorsStrike.getMinConvert() + 10);
                    protectorsStrike.setMaxConvert(protectorsStrike.getMaxConvert() + 10);
                    protectorsStrike.setMaxAllies(protectorsStrike.getMaxAllies() + 1);
                }
            }
    ),
    CONSECRATE_PROTECTOR("Consecrate",
            Component.text("Increases the range of Consecrate by 2 blocks and reduce the cooldown by 30%", NamedTextColor.GRAY),
            Component.text("Increases the range of Consecrate by ", NamedTextColor.GREEN)
                     .append(Component.text("2 ", NamedTextColor.RED))
                     .append(Component.text("blocks and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("30%", NamedTextColor.RED)),
            Consecrate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Consecrate consecrate) {
                    consecrate.setRadius(consecrate.getRadius() + 2);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    LIGHT_INFUSION_PROTECTOR("Light Infusion",
            Component.text("Reduce the cooldown of Light Infusion by 35% and increase the speed duration by 3 seconds", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Light Infusion by ", NamedTextColor.GREEN)
                     .append(Component.text("35% ", NamedTextColor.RED))
                     .append(Component.text("and increase the speed duration by ", NamedTextColor.GREEN))
                     .append(Component.text("3 ", NamedTextColor.RED))
                     .append(Component.text("seconds", NamedTextColor.GREEN)),
            LightInfusionProtector.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusionProtector lightInfusion) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .65f);
                    lightInfusion.setTickDuration(lightInfusion.getTickDuration() + 60);
                }
            }
    ),
    HOLY_RADIANCE_PROTECTOR("Holy Radiance",
            Component.text("Increases the amount of health you restore with Holy Radiance by 20%", NamedTextColor.GRAY),
            Component.text("Increases the amount of health you restore with Holy Radiance by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED)),
            HolyRadianceProtector.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceProtector) {
                    abstractAbility.multiplyMinMax(1.2f);
                }
            }
    ),
    HAMMER_OF_LIGHT("Hammer of Light",
            Component.text("Increases the amount of health you restore with Hammer of Light by 25% and reduce the cooldown by 25%", NamedTextColor.GRAY),
            Component.text("Increases the amount of health you restore with Hammer of Light by ", NamedTextColor.GREEN)
                     .append(Component.text("25% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("25%", NamedTextColor.RED)),
            HammerOfLight.class,
            abstractAbility -> {
                if (abstractAbility instanceof HammerOfLight) {
                    abstractAbility.multiplyMinMax(1.25f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    LIGHTNING_BOLT("Lightning Bolt",
            Component.text("Increase the damage you deal with Lightning Bolt by 20%", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Lightning Bolt by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED)),
            LightningBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightningBolt) {
                    abstractAbility.multiplyMinMax(1.2f);
                }
            }
    ),
    CHAIN_LIGHTNING("Chain Lightning",
            Component.text("Increase the damage you deal with Chain Lightning by 20% and reduce the cooldown by 15%", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Chain Lightning by ", NamedTextColor.GREEN)
                     .append(Component.text("20% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("15%", NamedTextColor.RED)),
            ChainLightning.class,
            abstractAbility -> {
                if (abstractAbility instanceof ChainLightning) {
                    abstractAbility.multiplyMinMax(1.25f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .85f);
                }
            }
    ),
    WINDFURY_WEAPON("Windfury Weapon",
            Component.text("Increase the damage you deal with Windfury Weapon by 30% and increase the proc chance by 10%", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Windfury Weapon by ", NamedTextColor.GREEN)
                     .append(Component.text("30% ", NamedTextColor.RED))
                     .append(Component.text("and increase the proc ", NamedTextColor.RED))
                     .append(Component.text("chance by ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED)),
            Windfury.class,
            abstractAbility -> {
                if (abstractAbility instanceof Windfury windfury) {
                    windfury.setProcChance(45);
                    windfury.setWeaponDamage(windfury.getWeaponDamage() + 30);
                }
            }
    ),
    LIGHTNING_ROD("Lightning Rod",
            Component.text("Reduce the cooldown of Lightning Rod by 40%", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Lightning Rod by ", NamedTextColor.GREEN)
                     .append(Component.text("40%", NamedTextColor.RED)),
            LightningRod.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightningRod) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    CAPACITOR_TOTEM("Capacitor Totem",
            Component.text("Increase the damage you deal with Capacitor Totem by 30% and reduce the cooldown by 15%", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Capacitor Totem by ", NamedTextColor.GREEN)
                     .append(Component.text("30% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("15%", NamedTextColor.RED)),
            CapacitorTotem.class,
            abstractAbility -> {
                if (abstractAbility instanceof CapacitorTotem) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .85f);
                    abstractAbility.multiplyMinMax(1.3f);
                }
            }
    ),
    FALLEN_SOULS("Fallen Souls",
            Component.text("Increase the damage you deal with Fallen Souls by 20%", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Fallen Souls by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED)),
            FallenSouls.class,
            abstractAbility -> {
                if (abstractAbility instanceof FallenSouls) {
                    abstractAbility.multiplyMinMax(1.2f);
                }
            }
    ),
    SPIRIT_LINK("Spirit Link",
            Component.text("Increase the damage you deal with Spirit Link by 25% and increase the speed duration by 0.5 seconds", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Spirit Link by ", NamedTextColor.GREEN)
                     .append(Component.text("25% ", NamedTextColor.RED))
                     .append(Component.text("and increase the speed duration by ", NamedTextColor.GREEN))
                     .append(Component.text("0.5 ", NamedTextColor.RED))
                     .append(Component.text("seconds", NamedTextColor.GREEN)),
            SpiritLink.class,
            abstractAbility -> {
                if (abstractAbility instanceof SpiritLink spiritLink) {
                    abstractAbility.multiplyMinMax(1.25f);
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
                    soulbinding.setBindDuration(4);
                }
            }
    ),
    REPENTANCE("Repentance",
            Component.text("Lower damage taken/dealt requirement by 500 and reduce the cooldown by 10%", NamedTextColor.GRAY),
            Component.text("Lower damage taken/dealt requirement by ", NamedTextColor.GREEN)
                     .append(Component.text("500 ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            Repentance.class,
            abstractAbility -> {
                if (abstractAbility instanceof Repentance repentance) {
                    repentance.setDamageConvertPercent(15);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .9f);
                }
            }
    ),
    DEATHS_DEBT("Death's Debt",
            Component.text("Increase the range of Death's Debt by 5 blocks and reduce the amount of delayed damage you take by 40%", NamedTextColor.GRAY),
            Component.text("Increase the range of Death's Debt by", NamedTextColor.GREEN)
                     .append(Component.text("5 ", NamedTextColor.RED))
                     .append(Component.text("blocks and reduce the amount of delayed damage you take by ", NamedTextColor.GREEN))
                     .append(Component.text("40%", NamedTextColor.RED)),
            DeathsDebt.class,
            abstractAbility -> {
                if (abstractAbility instanceof DeathsDebt deathsDebt) {
                    deathsDebt.setRespiteRadius(15);
                    deathsDebt.setDebtRadius(13);
                    deathsDebt.setSelfDamageInPercentPerSecond(.1f);
                }
            }
    ),
    EARTHEN_SPIKE("Earthen Spike",
            Component.text("Increase the damage you deal with Earthen Spike by 15% and increase the speed by 30%", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Earthen Spike by ", NamedTextColor.GREEN)
                     .append(Component.text("15% ", NamedTextColor.RED))
                     .append(Component.text("and increase the speed by ", NamedTextColor.GREEN))
                     .append(Component.text("30%", NamedTextColor.RED)),
            EarthenSpike.class,
            abstractAbility -> {
                if (abstractAbility instanceof EarthenSpike earthenSpike) {
                    abstractAbility.multiplyMinMax(1.15f);
                    earthenSpike.setSpeed(earthenSpike.getSpeed() * 1.3f);
                }
            }
    ),
    BOULDER("Boulder",
            Component.text("Increase the damage you deal with Boulder by 25%", NamedTextColor.GRAY),
            Component.text("Increase the damage you ", NamedTextColor.RED)
                     .append(Component.text("deal with Boulder by ", NamedTextColor.GREEN))
                     .append(Component.text("25%", NamedTextColor.RED)),
            Boulder.class,
            abstractAbility -> {
                if (abstractAbility instanceof Boulder) {
                    abstractAbility.multiplyMinMax(1.25f);
                }
            }
    ),
    EARTHLIVING_WEAPON("Earthliving Weapon",
            Component.text("Increase the proc chance by of Earthliving Weapon by 20%", NamedTextColor.GRAY),
            Component.text("Increase the proc chance by of Earthliving Weapon by ", NamedTextColor.GREEN).append(Component.text("20%", NamedTextColor.RED)),
            Earthliving.class,
            abstractAbility -> {
                if (abstractAbility instanceof Earthliving earthliving) {
                    earthliving.setProcChance(60);
                }
            }
    ),
    CHAIN_HEAL("Chain Heal",
            Component.text("Increases the amount of health you restore with Chain Heal by 30%", NamedTextColor.GRAY),
            Component.text("Increases the amount of health you restore with Chain Heal by ", NamedTextColor.GREEN).append(Component.text("30%", NamedTextColor.RED)),
            ChainHeal.class,
            abstractAbility -> {
                if (abstractAbility instanceof ChainHeal) {
                    abstractAbility.multiplyMinMax(1.3f);
                }
            }
    ),
    HEALING_TOTEM("Healing Totem",
            Component.text("Increase the amount of health you restore with Healing Totem by 25% and reduce the cooldown by 25%", NamedTextColor.GRAY),
            Component.text("Increase the amount of health you restore with Healing Totem by ", NamedTextColor.GREEN)
                     .append(Component.text("25% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("25%", NamedTextColor.RED)),
            HealingTotem.class,
            abstractAbility -> {
                if (abstractAbility instanceof HealingTotem) {
                    abstractAbility.multiplyMinMax(1.25f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    JUDGEMENT_STRIKE("Judgement Strike",
            Component.text("Increase the amount of damage you deal with Judgement Strike by 20%", NamedTextColor.GRAY),
            Component.text("Increase the amount of damage you deal with Judgement Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED)),
            JudgementStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof JudgementStrike) {
                    abstractAbility.multiplyMinMax(1.2f);
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
                    incendiaryCurse.setBlindDurationInTicks(50);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .65f);
                }
            }
    ),
    BLINDING_ASSAULT("Shadow Step",
            Component.text("Reduce the cooldown by Shadow Step by 40% and become temporarily immune to fall damage after leaping.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown by Shadow Step by ", NamedTextColor.GREEN)
                     .append(Component.text("40% ", NamedTextColor.RED))
                     .append(Component.text("and become temporarily immune to fall damage after leaping.", NamedTextColor.GREEN)),
            ShadowStep.class,
            abstractAbility -> {
                if (abstractAbility instanceof ShadowStep shadowStep) {
                    shadowStep.setFallDamageNegation(1000);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    SOUL_SWITCH("Soul Switch",
            Component.text("Reduce the cooldown by Soul Switch by 50% and increase the range by 2 blocks.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown by Soul Switch by ", NamedTextColor.GREEN)
                     .append(Component.text("50% ", NamedTextColor.RED))
                     .append(Component.text("and increase the range by ", NamedTextColor.GREEN))
                     .append(Component.text("2 ", NamedTextColor.RED))
                     .append(Component.text("blocks", NamedTextColor.GREEN)),
            SoulSwitch.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoulSwitch soulSwitch) {
                    soulSwitch.setRadius(15);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .5f);
                }
            }
    ),
    ORDER_OF_EVISCERATE("Order Of Eviscerate",
            Component.text("Increase the duration of Order Of Eviscerate by 4 seconds and reduce the cooldown by 30%.", NamedTextColor.GRAY),
            Component.text("Increase the duration of Order Of Eviscerate by ", NamedTextColor.GREEN)
                     .append(Component.text("4 ", NamedTextColor.RED))
                     .append(Component.text("seconds and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("30%.", NamedTextColor.RED)),
            OrderOfEviscerate.class,
            abstractAbility -> {
                if (abstractAbility instanceof OrderOfEviscerate orderOfEviscerate) {
                    orderOfEviscerate.setTickDuration(orderOfEviscerate.getTickDuration() + 80);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    RIGHTEOUS_STRIKE("Righteous Strike",
            Component.text("Increase the amount of damage you deal with Righteous Strike by 20%", NamedTextColor.GRAY),
            Component.text("Increase the amount of damage you deal with Righteous Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED)),
            RighteousStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof RighteousStrike) {
                    abstractAbility.multiplyMinMax(1.2f);
                }
            }
    ),
    SOUL_SHACKLE("Soul Shackle",
            Component.text("Reduce the cooldown of Soul Shackle by 20%", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Soul Shackle by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            SoulShackle.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoulShackle) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                }
            }
    ),
    HEART_TO_HEART("Heart To Heart",
            Component.text("Reduce the cooldown of Heart ot Heart by 30% and increase the amount of health you restore by 300", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Heart ot Heart by ", NamedTextColor.GREEN)
                     .append(Component.text("30% ", NamedTextColor.RED))
                     .append(Component.text("and increase the amount of health you restore by ", NamedTextColor.GREEN))
                     .append(Component.text("300", NamedTextColor.RED)),
            HeartToHeart.class,
            abstractAbility -> {
                if (abstractAbility instanceof HeartToHeart heartToHeart) {
                    heartToHeart.setHealthRestore(heartToHeart.getHealthRestore() + 300);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    PRISM_GUARD("Prism Guard",
            Component.text("Increase the damage reduction of Prism Guard by 15% and increase the amount of health you restore by 300", NamedTextColor.GRAY),
            Component.text("Increase the damage reduction of Prism Guard by ", NamedTextColor.GREEN)
                     .append(Component.text("15% ", NamedTextColor.RED))
                     .append(Component.text("and increase the amount of health you restore by ", NamedTextColor.GREEN))
                     .append(Component.text("300", NamedTextColor.RED)),
            PrismGuard.class,
            abstractAbility -> {
                if (abstractAbility instanceof PrismGuard prismGuard) {
                    prismGuard.setProjectileDamageReduction(75);
                    prismGuard.setBubbleHealing(prismGuard.getBubbleHealing() + 300);
                }
            }
    ),
    VINDICATE("Vindicate",
            Component.text("Increase the damage reduction of Vindicate by 10% and reduce the cooldown by 25%", NamedTextColor.GRAY),
            Component.text("Increase the damage reduction of Vindicate by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("25%", NamedTextColor.RED)),
            Vindicate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Vindicate vindicate) {
                    vindicate.setVindicateDamageReduction(40);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    IMPALING_STRIKE("Impaling Strike",
            Component.text("Increase the amount of damage you deal with Impaling Strike by 10% and increase the leech duration by 5 seconds.", NamedTextColor.GRAY),
            Component.text("Increase the amount of damage you deal with Impaling Strike by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and increase the leech duration by ", NamedTextColor.GREEN))
                     .append(Component.text("5 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            ImpalingStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof ImpalingStrike impalingStrike) {
                    impalingStrike.setLeechDuration(10);
                    abstractAbility.multiplyMinMax(1.1f);
                }
            }
    ),
    SOOTHING_PUDDLE("Soothing Elixir",
            Component.text("Increase the amount of health you restore with Soothing Elixir by 25%.", NamedTextColor.GRAY),
            Component.text("Increase the amount of health you restore with Soothing Elixir by ", NamedTextColor.GREEN)
                     .append(Component.text("25%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            SoothingElixir.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoothingElixir) {
                    abstractAbility.multiplyMinMax(1.25f);
                }
            }
    ),
    VITALITY_LIQUOR("Vitality Liquor",
            Component.text("Increase the amount of health you restore with Vitality Liquor by 15% and reduce the cooldown by 30%", NamedTextColor.GRAY),
            Component.text("Increase the amount of health you restore with Vitality Liquor by ", NamedTextColor.GREEN)
                     .append(Component.text("15% ", NamedTextColor.RED))
                     .append(Component.text("and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("30%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            VitalityLiquor.class,
            abstractAbility -> {
                if (abstractAbility instanceof VitalityLiquor vitalityLiquor) {
                    abstractAbility.multiplyMinMax(1.15f);
                    vitalityLiquor.setMinWaveHealing(vitalityLiquor.getMinWaveHealing() * 1.15f);
                    vitalityLiquor.setMaxWaveHealing(vitalityLiquor.getMaxWaveHealing() * 1.15f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    REMEDIC_CHAINS("Remedic Chains",
            Component.text("Increase the amount of health you restore with Remedic Chains by 10% and increase the link break radius by 10 blocks.", NamedTextColor.GRAY),
            Component.text("Increase the amount of health you restore with Remedic Chains by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and increase the link break radius by ", NamedTextColor.GREEN))
                     .append(Component.text("10 ", NamedTextColor.RED))
                     .append(Component.text("blocks.", NamedTextColor.GREEN)),
            RemedicChains.class,
            abstractAbility -> {
                if (abstractAbility instanceof RemedicChains remedicChains) {
                    abstractAbility.multiplyMinMax(1.1f);
                    remedicChains.setLinkBreakRadius(25);
                }
            }
    ),
    DRAINING_MIASMA("Draining Miasma",
            Component.text("Increase the leech duration of Draining Miasma by 5 seconds and reduce the cooldown by 30%", NamedTextColor.GRAY),
            Component.text("Increase the leech duration of Draining Miasma by ", NamedTextColor.GREEN)
                     .append(Component.text("5 ", NamedTextColor.RED))
                     .append(Component.text("seconds and reduce the cooldown by ", NamedTextColor.GREEN))
                     .append(Component.text("30%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            DrainingMiasma.class,
            abstractAbility -> {
                if (abstractAbility instanceof DrainingMiasma drainingMiasma) {
                    drainingMiasma.setLeechDuration(10);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
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
                    poisonousHex.setTickDuration(poisonousHex.getTickDuration() + 40);
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
                    soulfireBeam.multiplyMinMax(1.2f);
                    soulfireBeam.setCooldown(soulfireBeam.getCooldown() * .8f);
                }
            }
    ),
    ENERGY_SEER_CONJURER("Energy Seer",
            Component.text("Increase the energy restored and damage bonus granted after Energy Seer ends by 40 and 10%, respectively.", NamedTextColor.GRAY),
            Component.text("Increase the energy restored and damage bonus granted after Energy Seer ends by ", NamedTextColor.GREEN)
                     .append(Component.text("40 ", NamedTextColor.RED))
                     .append(Component.text("and ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED))
                     .append(Component.text(", respectively.", NamedTextColor.GREEN)),
            EnergySeerConjurer.class,
            abstractAbility -> {
                if (abstractAbility instanceof EnergySeerConjurer energySeerConjurer) {
                    energySeerConjurer.setEnergyRestore(energySeerConjurer.getEnergyRestore() + 40);
                    energySeerConjurer.setDamageIncrease(energySeerConjurer.getDamageIncrease() + 10);
                }
            }
    ),
    CONTAGIOUS_FACADE("Contagious Facade",
            Component.text("Increased the amount of damage you reduce with Contagious Facade by 5%.", NamedTextColor.GRAY),
            Component.text("Increased the amount of damage you reduce with Contagious Facade by ", NamedTextColor.GREEN)
                     .append(Component.text("5%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            ContagiousFacade.class,
            abstractAbility -> {
                if (abstractAbility instanceof ContagiousFacade contagiousFacade) {
                    contagiousFacade.setDamageAbsorption(contagiousFacade.getDamageAbsorption() + 5);
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
                    astralPlague.setCooldown(astralPlague.getCooldown() * .8f);
                }
            }
    ),
    FORTIFYING_HEX("Fortifying Hex",
            Component.text("Increase the damage you deal with Fortifying Hex by 20%.", NamedTextColor.GRAY),
            Component.text("Increase the damage you deal with Fortifying Hex by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            FortifyingHex.class,
            abstractAbility -> {
                if (abstractAbility instanceof FortifyingHex fortifyingHex) {
                    fortifyingHex.multiplyMinMax(1.2f);
                }
            }
    ),
    NOT_A_SHIELD("Not a Shield",
            Component.text("Reduce the cooldown of [] by 20% and increase range by 5 blocks.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of [] by ", NamedTextColor.GREEN)
                     .append(Component.text("20%", NamedTextColor.RED))
                     .append(Component.text("and increase range by ", NamedTextColor.GREEN))
                     .append(Component.text("5 ", NamedTextColor.RED))
                     .append(Component.text("blocks.", NamedTextColor.GREEN)),
            NotAShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof NotAShield notAShield) {
                    notAShield.setCooldown(notAShield.getCooldown() * .8f);
                    notAShield.setMaxDistance(notAShield.getMaxDistance() + 5);
                }
            }
    ),
    ENERGY_SEER_GUARDIAN("Energy Seer",
            Component.text("Increase the energy restored and damage reduction granted after Energy Seer ends by 40 and 10%, respectively.", NamedTextColor.GRAY),
            Component.text("Increase the energy restored and damage reduction granted after Energy Seer ends by ", NamedTextColor.GREEN)
                     .append(Component.text("40 ", NamedTextColor.RED))
                     .append(Component.text("and ", NamedTextColor.GREEN))
                     .append(Component.text("10%", NamedTextColor.RED))
                     .append(Component.text(", respectively.", NamedTextColor.GREEN)),
            EnergySeerGuardian.class,
            abstractAbility -> {
                if (abstractAbility instanceof EnergySeerGuardian energySeerGuardian) {
                    energySeerGuardian.setEnergyRestore(energySeerGuardian.getEnergyRestore() + 40);
                    energySeerGuardian.setDamageResistance(energySeerGuardian.getDamageResistance() + 10);
                }
            }
    ),
    SPIRITUAL_SHIELD("Spiritual Shield",
            Component.text("Increase the amount of time Spiritual Shield increases the rune timers by 0.25s.", NamedTextColor.GRAY),
            Component.text("Increase the amount of time Spiritual Shield increases the rune timers by ", NamedTextColor.GREEN)
                     .append(Component.text("0.25s", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            SpiritualShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof SpiritualShield spiritualShield) {
                    spiritualShield.setRuneTimerIncrease(spiritualShield.getRuneTimerIncrease() + 0.25f);
                }
            }
    ),
    SANCTUARY("Sanctuary",
            Component.text("Increase the amount of damage reflected by Sanctuary by 10% and the duration by 2 seconds.", NamedTextColor.GRAY),
            Component.text("Increase the amount of damage reflected by Sanctuary by ", NamedTextColor.GREEN)
                     .append(Component.text("10% ", NamedTextColor.RED))
                     .append(Component.text("and the duration by ", NamedTextColor.GREEN))
                     .append(Component.text("2 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            Sanctuary.class,
            abstractAbility -> {
                if (abstractAbility instanceof Sanctuary sanctuary) {
                    sanctuary.setDamageReflected(sanctuary.getDamageReflected() + 10);
                    sanctuary.setTickDuration(sanctuary.getTickDuration() + 40);
                }
            }
    ),
    MERCIFUL_HEX("Merciful Hex",
            Component.text("Increase the amount of health you restore to the first ally with Merciful Hex by 25%.", NamedTextColor.GRAY),
            Component.text("Increase the amount of health you restore to the first ally with Merciful Hex by ", NamedTextColor.GREEN)
                     .append(Component.text("25%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            MercifulHex.class,
            abstractAbility -> {
                if (abstractAbility instanceof MercifulHex mercifulHex) {
                    mercifulHex.multiplyMinMax(1.25f);
                }
            }
    ),
    BEACON_OF_LIGHT("Beacon of Light",
            Component.text("Reduce the cooldown of Beacon of Light by 30%.", NamedTextColor.GRAY),
            Component.text("Reduce the cooldown of Beacon of Light by ", NamedTextColor.GREEN)
                     .append(Component.text("30%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            BeaconOfLight.class,
            abstractAbility -> {
                if (abstractAbility instanceof BeaconOfLight beaconOfLight) {
                    beaconOfLight.setCooldown(beaconOfLight.getCooldown() * .7f);
                }
            }
    ),
    ENERGY_SEER_PRIEST("Energy Seer",
            Component.text("Increase the energy restored and critical chance granted after Energy Seer ends by 40 and 40%, respectively.", NamedTextColor.GRAY),
            Component.text("Increase the energy restored and critical chance granted after Energy Seer ends by ", NamedTextColor.GREEN)
                     .append(Component.text("40 ", NamedTextColor.RED))
                     .append(Component.text("and ", NamedTextColor.GREEN))
                     .append(Component.text("40%", NamedTextColor.RED))
                     .append(Component.text(", respectively.", NamedTextColor.GREEN)),
            EnergySeerPriest.class,
            abstractAbility -> {
                if (abstractAbility instanceof EnergySeerPriest energySeerPriest) {
                    energySeerPriest.setEnergyRestore(energySeerPriest.getEnergyRestore() + 40);
                    energySeerPriest.setCritChanceIncrease(energySeerPriest.getCritChanceIncrease() + 40);
                }
            }
    ),
    BEACON_OF_IMPAIR("Beacon of Impair",
            Component.text("Increase the Crit Multiplier reduction of Beacon of Impair by 40%.", NamedTextColor.GRAY),
            Component.text("Increase the Crit Multiplier reduction of Beacon of Impair by ", NamedTextColor.GREEN)
                     .append(Component.text("40%", NamedTextColor.RED))
                     .append(Component.text(".", NamedTextColor.GREEN)),
            BeaconOfImpair.class,
            abstractAbility -> {
                if (abstractAbility instanceof BeaconOfImpair beaconOfImpair) {
                    beaconOfImpair.setCritMultiplierReducedTo(beaconOfImpair.getCritMultiplierReducedTo() - 40);
                }
            }
    ),
    DIVINE_BLESSING("Divine Blessing",
            Component.text("Increase the duration of Divine Blessing by 2 seconds.", NamedTextColor.GRAY),
            Component.text("Increase the duration of Divine Blessing by ", NamedTextColor.GREEN)
                     .append(Component.text("2 ", NamedTextColor.RED))
                     .append(Component.text("seconds.", NamedTextColor.GREEN)),
            DivineBlessing.class,
            abstractAbility -> {
                if (abstractAbility instanceof DivineBlessing divineBlessing) {
                    divineBlessing.setTickDuration(divineBlessing.getTickDuration() + 40);
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
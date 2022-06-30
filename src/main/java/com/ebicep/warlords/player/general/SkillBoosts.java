package com.ebicep.warlords.player.general;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.abilties.internal.AbstractAbility;

import java.util.function.Consumer;

public enum SkillBoosts {
    FIREBALL("Fireball",
            "§7Increases the damage you\n§7deal with Fireball by 20%",
            "§aIncreases the damage you\n§adeal with Fireball by §c20%",
            Fireball.class,
            abstractAbility -> {
                if (abstractAbility instanceof Fireball) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    FLAME_BURST("Flame Burst",
            "§7Increases the damage you\n§7deal with Flame Burst by\n§720% and reduce the cooldown\n§7by 20%",
            "§aIncreases the damage you\n§adeal with Flame Burst by\n§c20% §aand reduce the cooldown\n§aby §c20%",
            FlameBurst.class,
            abstractAbility -> {
                if (abstractAbility instanceof FlameBurst) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * 0.8f);
                }
            }
    ),
    TIME_WARP_PYROMANCER("Time Warp",
            "§7Increase the amount of health\nyou restore with Time Warp by §710% §7and\n§7reduce the cooldown by 50%.",
            "§aIncrease the amount of health\n§ayou restore with Time Warp by §c10% §aand\n§areduce the cooldown by §c50%.",
            TimeWarp.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarp) {
                    ((TimeWarp) abstractAbility).setWarpHealPercentage(40);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .5f);
                }
            }
    ),
    ARCANE_SHIELD_PYROMANCER("Arcane Shield",
            "§7Increase the amount of health\n§7converted to shield by 10% and reduce\n§7the cooldown by 50%",
            "§aIncrease the amount of health\n§aconverted to shield by §c10% §aand reduce\n§athe cooldown by §c50%",
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    ArcaneShield arcaneShield = (ArcaneShield) abstractAbility;
                    arcaneShield.setShieldPercentage(60);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .5f);
                }
            }
    ),
    INFERNO("Inferno",
            "§7Increase the Crit Multiplier bonus of\nInferno by 70% but reduce the Crit\nChance bonus by 10%",
            "§aIncrease the Crit Multiplier bonus of\nInferno by §c70% §abut reduce the Crit\nChance bonus §aby §c10%",
            Inferno.class,
            abstractAbility -> {
                if (abstractAbility instanceof Inferno) {
                    ((Inferno) abstractAbility).setCritChanceIncrease(20);
                    ((Inferno) abstractAbility).setCritMultiplierIncrease(100);
                }
            }
    ),
    FROST_BOLT("Frostbolt",
            "§7Increases the damage you\n§7deal with Frostbolt by\n§720%",
            "§aIncreases the damage you\n§adeal with Frostbolt by\n§c20%",
            FrostBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof FrostBolt) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    FREEZING_BREATH("Freezing Breath",
            "§7Increase the damage you\n§7deal with Freezing Breath\n§7by 25% and reduce the cooldown\n§7by 15%",
            "§aIncrease the damage you\n§adeal with Freezing Breath\n§aby §c25% §aand reduce the cooldown\n§aby §c15%",
            FreezingBreath.class,
            abstractAbility -> {
                if (abstractAbility instanceof FreezingBreath) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .85f);
                }
            }
    ),
    TIME_WARP_CRYOMANCER("Time Warp",
            "§7Reduce the cooldown of Time Warp\n§7by 40%",
            "§aReduce the cooldown of Time Warp\n§aby §c40%",
            TimeWarp.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarp) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    ARCANE_SHIELD_CRYOMANCER("Arcane Shield",
            "§7Increase the amount of health\n§7converted to shield by 20%",
            "§aIncrease the amount of health\n§aconverted to shield by §c20%",
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    ArcaneShield arcaneShield = (ArcaneShield) abstractAbility;
                    arcaneShield.setShieldPercentage(70);
                }
            }
    ),
    ICE_BARRIER("Ice Barrier",
            "§7Increase the amount damage you\n§7reduce with Ice Barrier by\n§710% §7and reduce the cooldown by 20%",
            "§aIncrease the amount damage you\n§areduce with Ice Barrier by\n§c10% §aand reduce the cooldown by §c20%",
            IceBarrier.class,
            abstractAbility -> {
                if (abstractAbility instanceof IceBarrier) {
                    ((IceBarrier) abstractAbility).setDamageReductionPercent(60);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                }
            }
    ),
    WATER_BOLT("Water Bolt",
            "§7Increases the amount of\n§7health you restore with\n§7Water Bolt by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aWater Bolt by §c20%",
            WaterBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof WaterBolt) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    WATER_BREATH("Water Breath",
            "§7Increases the amount of\n§7health you restore with\n§7Water Breath by 25%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aWater Breath by §c25%",
            WaterBreath.class,
            abstractAbility -> {
                if (abstractAbility instanceof WaterBreath) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
                }
            }
    ),
    TIME_WARP_AQUAMANCER("Time Warp",
            "§7Reduce the cooldown of Time Warp\n§7by 40%",
            "§aReduce the cooldown of Time Warp\n§aby §c40%",
            TimeWarp.class,
            abstractAbility -> {
                if (abstractAbility instanceof TimeWarp) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    ARCANE_SHIELD_AQUAMANCER("Arcane Shield",
            "§7Increase the amount of health\n§7converted to shield by 10% and reduce\n§7the cooldown by 50%",
            "§aIncrease the amount of health\n§aconverted to shield by §c10% §aand reduce\n§athe cooldown by §c50%",
            ArcaneShield.class,
            abstractAbility -> {
                if (abstractAbility instanceof ArcaneShield) {
                    ArcaneShield arcaneShield = (ArcaneShield) abstractAbility;
                    arcaneShield.setShieldPercentage(60);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .5f);
                }
            }
    ),
    HEALING_RAIN("Healing Rain",
            "§7Increases the duration of\n§7Healing Rain by 4 seconds and\n§7reduce the cooldown by 20%",
            "§aIncreases the duration of\n§aHealing Rain by §c4 §aseconds and\n§areduce the cooldown by §c20%",
            HealingRain.class,
            abstractAbility -> {
                if (abstractAbility instanceof HealingRain) {
                    ((HealingRain) abstractAbility).setDuration(16);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                }
            }
    ),
    WOUNDING_STRIKE_BERSERKER("Wounding Strike",
            "§7Increase the damage you\n§7deal with Wounding Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Wounding Strike\n§aby §c20%",
            WoundingStrikeBerserker.class,
            abstractAbility -> {
                if (abstractAbility instanceof WoundingStrikeBerserker) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    SEISMIC_WAVE_BERSERKER("Seismic Wave",
            "§7Increase the damage you\n§7deal with Seismic Wave by\n§720% and reduce the cooldown\n§7by 25%",
            "§aIncrease the damage you\n§adeal with Seismic Wave by\n§c20% §aand reduce the cooldown\n§aby §c25%",
            SeismicWave.class,
            abstractAbility -> {
                if (abstractAbility instanceof SeismicWave) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    GROUND_SLAM_BERSERKER("Ground Slam",
            "§7Increase the damage you\n§7deal with Ground Slam by\n§725% §7and reduce the cooldown\nby §725%",
            "§aIncrease the damage you\n§adeal with Ground Slam by\n§c25% §aand reduce the cooldown\nby §c25%",
            GroundSlam.class,
            abstractAbility -> {
                if (abstractAbility instanceof GroundSlam) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    BLOOD_LUST("Blood Lust",
            "§7Increase the amount of damage\n§7you convert into healing with\n§7Blood Lust by 5% and reduce the\n§7cooldown by 25%",
            "§aIncrease the amount of damage\n§ayou convert into healing with\n§aBlood Lust by §c5% §aand reduce the\n§acooldown by §c25%",
            BloodLust.class,
            abstractAbility -> {
                if (abstractAbility instanceof BloodLust) {
                    ((BloodLust) abstractAbility).setDamageConvertPercent(70);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    BERSERK("Berserk",
            "§7Increase the damage bonus of Berserk\n§7by 20% but increase the damage you take\n§7by 5%",
            "§aIncrease the damage bonus of Berserk\n§aby §c20% §abut increase the damage you take\n§aby §c5%",
            Berserk.class,
            abstractAbility -> {
                if (abstractAbility instanceof Berserk) {
                    ((Berserk) abstractAbility).setDamageIncrease(50);
                    ((Berserk) abstractAbility).setDamageTakenIncrease(15);
                }
            }
    ),
    WOUNDING_STRIKE_DEFENDER("Wounding Strike",
            "§7Increase the damage you\n§7deal with Wounding Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Wounding Strike\n§aby §c20%",
            WoundingStrikeDefender.class,
            abstractAbility -> {
                if (abstractAbility instanceof WoundingStrikeDefender) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    SEISMIC_WAVE_DEFENDER("Seismic Wave",
            "§7Increase the amount knockback you\n§7deal with Seismic Wave by\n§735% and reduce the cooldown\n§7by 25%",
            "§aIncrease the amount knockback you\n§adeal with Seismic Wave by\n§c35% §aand reduce the cooldown\n§aby §c25%",
            SeismicWave.class,
            abstractAbility -> {
                if (abstractAbility instanceof SeismicWave) {
                    ((SeismicWave) abstractAbility).setVelocity(1.8f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    GROUND_SLAM_DEFENDER("Ground Slam",
            "§7Reduce the cooldown of Ground Slam\n§7by 25%",
            "§aReduce the cooldown of Ground Slam\n§aby §c25%",
            GroundSlam.class,
            abstractAbility -> {
                if (abstractAbility instanceof GroundSlam) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    INTERVENE("Intervene",
            "§7Increase the cast and break radius\n§7of Intervene by 5 blocks and increase\n§7the max amount of damage you can\n§7absorb by 400",
            "§aIncrease the cast and break radius\n§aof Intervene by §c5 §ablocks and increase\n§athe max amount of damage you can\n§aabsorb by §c400",
            Intervene.class,
            abstractAbility -> {
                if (abstractAbility instanceof Intervene) {
                    ((Intervene) abstractAbility).setMaxDamagePrevented(4000);
                    ((Intervene) abstractAbility).setRadius(15);
                    ((Intervene) abstractAbility).setBreakRadius(20);
                }
            }
    ),
    LAST_STAND("Last Stand",
            "§7Increase the amount damage you\n§7reduce with Last Stand by\n§75% §7and reduce the cooldown by 15%",
            "§aIncrease the amount damage you\n§areduce with Last Stand by\n§c5% §aand reduce the cooldown by §c15%",
            LastStand.class,
            abstractAbility -> {
                if (abstractAbility instanceof LastStand) {
                    ((LastStand) abstractAbility).setSelfDamageReductionPercent(55);
                    ((LastStand) abstractAbility).setTeammateDamageReductionPercent(45);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .85f);
                }
            }
    ),
    CRIPPLING_STRIKE("Crippling Strike",
            "§7Increase the damage you\n§7deal with Crippling Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Crippling Strike\n§aby §c20%",
            CripplingStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof CripplingStrike) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    RECKLESS_CHARGE("Reckless Charge",
            "§7Increase the immobilize duration\n§7of your Reckless Charge by\n§70.25 seconds and reduce the\n§7cooldown by 25%",
            "§aIncrease the immobilize duration\n§aof your Reckless Charge by\n§c0.25 §aseconds and reduce the\n§acooldown by §c25%",
            RecklessCharge.class,
            abstractAbility -> {
                if (abstractAbility instanceof RecklessCharge) {
                    ((RecklessCharge) abstractAbility).setStunTimeInTicks(15);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    GROUND_SLAM_REVENANT("Ground Slam",
            "§7Reduce the cooldown of Ground Slam\n§7by 40%",
            "§aReduce the cooldown of Ground Slam\n§aby §c40%",
            GroundSlam.class,
            abstractAbility -> {
                if (abstractAbility instanceof GroundSlam) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    ORBS_OF_LIFE("Orbs of Life",
            "§7Increases the amount of\n§7health you restore with\n§7Orbs of Life by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aOrbs of Life by §c20%",
            OrbsOfLife.class,
            abstractAbility -> {
                if (abstractAbility instanceof OrbsOfLife) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    UNDYING_ARMY("Undying Army",
            "§7Reduce the cooldown of Undying Army\nby 25% and increase the duration\nby 3 seconds",
            "§aReduce the cooldown of Undying Army\nby §c25% §aand increase the duration\nby §c3 §aseconds",
            UndyingArmy.class,
            abstractAbility -> {
                if (abstractAbility instanceof UndyingArmy) {
                    ((UndyingArmy) abstractAbility).setDuration(13);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    AVENGER_STRIKE("Avenger's Strike",
            "§7Increase the damage you\n§7deal with Avenger's Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Avenger's Strike\n§aby §c20%",
            AvengersStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof AvengersStrike) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    CONSECRATE_AVENGER("Consecrate",
            "§7Remove the energy cost\nof Consecrate and\nreduce the cooldown\nby 20%",
            "§aRemove the energy cost\nof Consecrate and\nreduce the cooldown\nby §c20%",
            Consecrate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Consecrate) {
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 50);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                }
            }
    ),
    LIGHT_INFUSION_AVENGER("Light Infusion",
            "§7Reduce the cooldown of Light Infusion\n§7by 35% and increase the speed\n§7duration by 2 seconds",
            "§aReduce the cooldown of Light Infusion\n§aby §c35% §aand increase the speed\n§aduration by §c2 §aseconds",
            LightInfusion.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusion) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .65f);
                    ((LightInfusion) abstractAbility).setDuration(5);
                }
            }
    ),
    HOLY_RADIANCE_AVENGER("Holy Radiance",
            "§7Reduce the cooldown of Holy Radiance\n§7by 40%",
            "§aReduce the cooldown of Holy Radiance\n§aby §c40%",
            HolyRadianceAvenger.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceAvenger) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    AVENGERS_WRATH("Avenger's Wrath",
            "§7Reduce the cooldown of Avenger's Wrath\n§7by 25% and increase the duration by\n§73 seconds",
            "§aReduce the cooldown of Avenger's Wrath\n§aby §c25% §aand increase the duration §aby\n§c3 §aseconds",
            AvengersWrath.class,
            abstractAbility -> {
                if (abstractAbility instanceof AvengersWrath) {
                    ((AvengersWrath) abstractAbility).setDuration(15);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    CRUSADER_STRIKE("Crusader's Strike",
            "§7Increase the damage you\n§7deal with Crusader's Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Crusader's Strike\n§aby §c20%",
            CrusadersStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof CrusadersStrike) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    CONSECRATE_CRUSADER("Consecrate",
            "§7Remove the energy cost\nof Consecrate and\n§7reduce the cooldown\n§7by 20%",
            "§aRemove the energy cost\nof Consecrate and\n§areduce the cooldown\n§aby §c20%",
            Consecrate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Consecrate) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                    abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() - 50);
                }
            }
    ),
    LIGHT_INFUSION_CRUSADER("Light Infusion",
            "§7Reduce the cooldown of Light Infusion\n§7by 30% and increase the speed\n§7duration by 3 seconds",
            "§aReduce the cooldown of Light Infusion\n§aby §c30% §aand increase the speed\n§aduration by §c3 §aseconds",
            LightInfusion.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusion) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                    ((LightInfusion) abstractAbility).setDuration(6);
                }
            }
    ),
    HOLY_RADIANCE_CRUSADER("Holy Radiance",
            "§7Reduce the cooldown of Holy Radiance\n§7by 25% and increase the duration\n§7of Crusader's Mark by 4 seconds",
            "§aReduce the cooldown of Holy Radiance\n§aby §c25% §aand increase the duration\n§aof Crusader's Mark by §c4 §aseconds",
            HolyRadianceCrusader.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceCrusader) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                    ((HolyRadianceCrusader) abstractAbility).setMarkDuration(12);
                }
            }
    ),
    INSPIRING_PRESENCE("Inspiring Presence",
            "§7Reduce the cooldown of Inspiring\n§7Presence by 30%",
            "§aReduce the cooldown of Inspiring\n§aPresence by §c30%",
            InspiringPresence.class,
            abstractAbility -> {
                if (abstractAbility instanceof InspiringPresence) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    PROTECTOR_STRIKE("Protector's Strike",
            "§7Increase the amount of damage\n§7you convert into healing for allies with\n§7Protector's Strike by 20%",
            "§aIncrease the amount of damage\n§ayou convert into healing for allies with\n§aProtector's Strike by §c20%",
            ProtectorsStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof ProtectorsStrike) {
                    ((ProtectorsStrike) abstractAbility).setMinConvert(90);
                    ((ProtectorsStrike) abstractAbility).setMaxConvert(120);
                }
            }
    ),
    CONSECRATE_PROTECTOR("Consecrate",
            "§7Increases the range of\n§7Consecrate by 2 blocks and\n§7reduce the cooldown\n§7by 40%",
            "§aIncreases the range of\n§aConsecrate by §c2 §ablocks and\n§areduce the cooldown\n§aby §c40%",
            Consecrate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Consecrate) {
                    ((Consecrate) abstractAbility).setRadius(6);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    LIGHT_INFUSION_PROTECTOR("Light Infusion",
            "§7Reduce the cooldown of Light Infusion\n§7by 35% and increase the speed\n§7duration by 2 seconds",
            "§aReduce the cooldown of Light Infusion\n§aby §c35% §aand increase the speed\n§aduration by §c2 §aseconds",
            LightInfusion.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightInfusion) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .65f);
                    ((LightInfusion) abstractAbility).setDuration(5);
                }
            }
    ),
    HOLY_RADIANCE_PROTECTOR("Holy Radiance",
            "§7Increases the amount of\n§7health you restore with\n§7Holy Radiance by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aHoly Radiance by §c20%",
            HolyRadianceProtector.class,
            abstractAbility -> {
                if (abstractAbility instanceof HolyRadianceProtector) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    HAMMER_OF_LIGHT("Hammer of Light",
            "§7Increases the amount of\n§7health you restore with\n§7Hammer of Light by 20% and\nreduce the cooldown by 25%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aHammer of Light by §c20% §aand\nreduce the cooldown by §c25%",
            HammerOfLight.class,
            abstractAbility -> {
                if (abstractAbility instanceof HammerOfLight) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    LIGHTNING_BOLT("Lightning Bolt",
            "§7Increase the damage you\n§7deal with Lightning Bolt by\n§720%",
            "§aIncrease the damage you\n§adeal with Lightning Bolt by\n§c20%",
            LightningBolt.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightningBolt) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    CHAIN_LIGHTNING("Chain Lightning",
            "§7Increase the damage you\n§7deal with Chain Lightning\n§7by 20% and reduce the cooldown\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Chain Lightning\n§aby §c20% §aand reduce the cooldown\n§aby §c20%",
            ChainLightning.class,
            abstractAbility -> {
                if (abstractAbility instanceof ChainLightning) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .8f);
                }
            }
    ),
    WINDFURY_WEAPON("Windfury Weapon",
            "§7Increase the damage you\n§7deal with Windfury Weapon\n§7by 20% and increase the proc\n§7chance by 20%",
            "§aIncrease the damage you\n§adeal with Windfury Weapon\n§aby §c20% §aand increase the proc\n§achance by §c20%",
            Windfury.class,
            abstractAbility -> {
                if (abstractAbility instanceof Windfury) {
                    ((Windfury) abstractAbility).setProcChance(55);
                }
            }
    ),
    LIGHTNING_ROD("Lightning Rod",
            "§7Reduce the cooldown of Lightning Rod\n§7by 40%",
            "§aReduce the cooldown of Lightning Rod\n§aby §c40%",
            LightningRod.class,
            abstractAbility -> {
                if (abstractAbility instanceof LightningRod) {
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    CAPACITOR_TOTEM("Capacitor Totem",
            "§7Increase the damage you\n§7deal with Capacitor Totem\n§7by 25% and increase the range\n§7by 3 blocks",
            "§aIncrease the damage you\n§adeal with Capacitor Totem\n§aby §c25% §aand increase the range\n§aby §c3 §ablocks",
            CapacitorTotem.class,
            abstractAbility -> {
                if (abstractAbility instanceof CapacitorTotem) {
                    ((CapacitorTotem) abstractAbility).setRadius(9);
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
                }
            }
    ),
    FALLEN_SOULS("Fallen Souls",
            "§7Increase the damage you\n§7deal with Fallen Souls by\n§720%",
            "§aIncrease the damage you\n§adeal with Fallen Souls by\n§c20%",
            FallenSouls.class,
            abstractAbility -> {
                if (abstractAbility instanceof FallenSouls) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    SPIRIT_LINK("Spirit Link",
            "§7Increase the damage you\n§7deal with Spirit Link by\n§725%",
            "§aIncrease the damage you\n§adeal with Spirit Link by\n§c25%",
            SpiritLink.class,
            abstractAbility -> {
                if (abstractAbility instanceof SpiritLink) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
                }
            }
    ),
    SOULBINDING_WEAPON("Soulbinding Weapon",
            "§7Increase the duration of\n§7binds by 1 second.",
            "§aIncrease the duration of\n§abinds by §c1 §asecond.",
            Soulbinding.class,
            abstractAbility -> {
                if (abstractAbility instanceof Soulbinding) {
                    ((Soulbinding) abstractAbility).setBindDuration(3);
                }
            }
    ),
    REPENTANCE("Repentance",
            "§7Increase the damage you\n§7convert by 5% and reduce the\n§7cooldown by 10%",
            "§aIncrease the damage you\n§aconvert by §c5% §aand reduce the\n§acooldown by §c10%",
            Repentance.class,
            abstractAbility -> {
                if (abstractAbility instanceof Repentance) {
                    ((Repentance) abstractAbility).setDamageConvertPercent(15);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .9f);
                }
            }
    ),
    DEATHS_DEBT("Death's Debt",
            "§7Increase the range of Death's Debt\n§7by 4 blocks and reduce the\n§7amount of delayed damage you take\n§7by 25%",
            "§aIncrease the range of Death's Debt\n§aby §c4 §ablocks and reduce the\n§aamount of delayed damage you take\n§aby §c25%",
            DeathsDebt.class,
            abstractAbility -> {
                if (abstractAbility instanceof DeathsDebt) {
                    ((DeathsDebt) abstractAbility).setRespiteRadius(14);
                    ((DeathsDebt) abstractAbility).setDebtRadius(12);
                    ((DeathsDebt) abstractAbility).setSelfDamageInPercentPerSecond(.125f);
                }
            }
    ),
    EARTHEN_SPIKE("Earthen Spike",
            "§7Increase the damage you\n§7deal with Earthen Spike by\n§720%",
            "§aIncrease the damage you\n§adeal with Earthen Spike by\n§c20%",
            EarthenSpike.class,
            abstractAbility -> {
                if (abstractAbility instanceof EarthenSpike) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    BOULDER("Boulder",
            "§7Increase the damage you\n§7deal with Boulder by 20%",
            "§aIncrease the damage you\n§adeal with Boulder by §c20%",
            Boulder.class,
            abstractAbility -> {
                if (abstractAbility instanceof Boulder) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    EARTHLIVING_WEAPON("Earthliving Weapon",
            "§7Increase the proc chance by\nof Earthliving Weapon§7 by 20%",
            "§aIncrease the proc chance by\nof Earthliving Weapon by §c20%",
            Earthliving.class,
            abstractAbility -> {
                if (abstractAbility instanceof Earthliving) {
                    ((Earthliving) abstractAbility).setProcChance(60);
                }
            }
    ),
    CHAIN_HEAL("Chain Heal",
            "§7Increases the amount of\n§7health you restore with\n§7Chain Heal by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aChain Heal by §c20%",
            ChainHeal.class,
            abstractAbility -> {
                if (abstractAbility instanceof ChainHeal) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    HEALING_TOTEM("Healing Totem",
            "§7Increase the amount of health you\n§7restore with Healing Totem\n§7by 20% and reduce the\n§7cooldown by 25%",
            "§aIncrease the amount of health you\n§arestore with Healing Totem\n§aby §c20% §aand reduce the\n§acooldown by §c25%",
            HealingTotem.class,
            abstractAbility -> {
                if (abstractAbility instanceof HealingTotem) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    JUDGEMENT_STRIKE("Judgement Strike",
            "§7Increase the amount of damage you\n§7deal with Judgement Strike\n§7by 20%",
            "§aIncrease the amount of damage you\n§adeal with Judgement Strike\n§aby §c20%",
            JudgementStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof JudgementStrike) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    INCENDIARY_CURSE("Incendiary Curse",
            "§7Reduce the cooldown of Incendiary Curse\nby 35% and increase the blind duration\nby 1.5 seconds.",
            "§aReduce the cooldown of Incendiary Curse\nby §c35% §aand increase the blind duration\nby §c1.5 §aseconds.",
            IncendiaryCurse.class,
            abstractAbility -> {
                if (abstractAbility instanceof IncendiaryCurse) {
                    ((IncendiaryCurse) abstractAbility).setBlindDurationInTicks(70);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .65f);
                }
            }
    ),
    BLINDING_ASSAULT("Shadow Step",
            "§7Reduce the cooldown by Shadow Step\nby 40% and become temporarily immune to\nfall damage after leaping.",
            "§aReduce the cooldown by Shadow Step\nby §c40% §aand become temporarily immune to\nfall damage after leaping.",
            ShadowStep.class,
            abstractAbility -> {
                if (abstractAbility instanceof ShadowStep) {
                    ((ShadowStep) abstractAbility).setFallDamageNegation(1000);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .6f);
                }
            }
    ),
    SOUL_SWITCH("Soul Switch",
            "§7Reduce the cooldown by Soul Switch\nby 50% and increase the range\nby 2 blocks.",
            "§aReduce the cooldown by Soul Switch\nby §c50% §aand increase the range\nby §c2 §ablocks",
            SoulSwitch.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoulSwitch) {
                    ((SoulSwitch) abstractAbility).setRadius(15);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .5f);
                }
            }
    ),
    ORDER_OF_EVISCERATE("Order Of Eviscerate",
            "§7Increase the duration of Order Of Eviscerate\nby 4 seconds and reduce the cooldown\nby 30%.",
            "§aIncrease the duration of Order Of Eviscerate\nby §c4 §aseconds and reduce the cooldown\nby §c30%.",
            OrderOfEviscerate.class,
            abstractAbility -> {
                if (abstractAbility instanceof OrderOfEviscerate) {
                    ((OrderOfEviscerate) abstractAbility).setDuration(12);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    RIGHTEOUS_STRIKE("Righteous Strike",
            "§7Increase the amount of damage you\n§7deal with Righteous Strike\n§7by 20%",
            "§aIncrease the amount of damage you\n§adeal with Righteous Strike\n§aby §c20%",
            RighteousStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof RighteousStrike) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.2f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.2f);
                }
            }
    ),
    SOUL_SHACKLE("Soul Shackle",
            "§7Reduce the cooldown of Soul Shackle\nby 15% and increase the silence\nduration by 0.5 seconds",
            "§aReduce the cooldown of Soul Shackle\nby §c15% §aand increase the silence\nduration by §c0.5 §aseconds",
            SoulShackle.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoulShackle) {
                    ((SoulShackle) abstractAbility).setMinSilenceDurationInTicks(50);
                    ((SoulShackle) abstractAbility).setMaxSilenceDurationInTicks(80);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .85f);
                }
            }
    ),
    HEART_TO_HEART("Heart To Heart",
            "§7Reduce the cooldown of Heart ot Heart\nby 30% and increase the amount of health\nyou restore by 300",
            "§aReduce the cooldown of Heart ot Heart\nby §c30% §aand increase the amount of health\nyou restore by §c300",
            HeartToHeart.class,
            abstractAbility -> {
                if (abstractAbility instanceof HeartToHeart) {
                    ((HeartToHeart) abstractAbility).setHealthRestore(((HeartToHeart) abstractAbility).getHealthRestore() + 300);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    PRISM_GUARD("Prism Guard",
            "§7Increase the damage reduction of Prism\nGuard by 15% and increase the amount of\nhealth you restore by 300",
            "§aIncrease the damage reduction of Prism\nGuard by §c15% §aand increase the amount of\nhealth you restore by §c300",
            PrismGuard.class,
            abstractAbility -> {
                if (abstractAbility instanceof PrismGuard) {
                    ((PrismGuard) abstractAbility).setDamageReduction(40);
                    ((PrismGuard) abstractAbility).setProjectileDamageReduction(75);
                    ((PrismGuard) abstractAbility).setBubbleHealing(((PrismGuard) abstractAbility).getBubbleHealing() + 300);
                }
            }
    ),
    VINDICATE("Vindicate",
            "§7Increase the damage reduction of\nVindicate by 10% and reduce the\ncooldown by 25%",
            "§aIncrease the damage reduction of\nVindicate by §c10% §aand reduce the\ncooldown by §c25%",
            Vindicate.class,
            abstractAbility -> {
                if (abstractAbility instanceof Vindicate) {
                    ((Vindicate) abstractAbility).setVindicateDamageReduction(40);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .75f);
                }
            }
    ),
    IMPALING_STRIKE("Impaling Strike",
            "§7Increase the amount of damage you\n§7deal with Impaling Strike\n§7by 10% and increase the leech\nduration by 5 seconds.",
            "§aIncrease the amount of damage you\n§adeal with Impaling Strike\n§aby §c10% §aand increase the leech\nduration by §c5 §aseconds.",
            ImpalingStrike.class,
            abstractAbility -> {
                if (abstractAbility instanceof ImpalingStrike) {
                    ((ImpalingStrike) abstractAbility).setLeechDuration(10);
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.1f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.1f);
                }
            }
    ),
    SOOTHING_PUDDLE("Soothing Elixir",
            "§7Increase the amount of health you\n§7restore with Soothing Elixir\n§7by 25%",
            "§aIncrease the amount of health you\n§arestore with Soothing Elixir\n§aby §c25%",
            SoothingElixir.class,
            abstractAbility -> {
                if (abstractAbility instanceof SoothingElixir) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.25f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.25f);
                }
            }
    ),
    VITALITY_LIQUOR("Vitality Liquor",
            "§7Increase the amount of health you\n§7restore with Vitality Liquor\n§7by 15% and reduce the cooldown\nby 30%",
            "§aIncrease the amount of health you\n§arestore with Vitality Liquor\n§aby §c15% §aand reduce the cooldown\nby §c30%",
            VitalityLiquor.class,
            abstractAbility -> {
                if (abstractAbility instanceof VitalityLiquor) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.15f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.15f);
                    ((VitalityLiquor) abstractAbility).setMinWaveHealing(((VitalityLiquor) abstractAbility).getMinWaveHealing() * 1.15f);
                    ((VitalityLiquor) abstractAbility).setMaxWaveHealing(((VitalityLiquor) abstractAbility).getMaxWaveHealing() * 1.15f);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),
    REMEDIC_CHAINS("Remedic Chains",
            "§7Increase the amount of health\nyou restore with Remedic Chains\nby 10% and increase the link break\nradius by 10 blocks.",
            "§aIncrease the amount of health\nyou restore with Remedic Chains\nby §c10% §aand increase the link break\nradius by §c10 §ablocks.",
            RemedicChains.class,
            abstractAbility -> {
                if (abstractAbility instanceof RemedicChains) {
                    abstractAbility.setMinDamageHeal(abstractAbility.getMinDamageHeal() * 1.1f);
                    abstractAbility.setMaxDamageHeal(abstractAbility.getMaxDamageHeal() * 1.1f);
                    ((RemedicChains) abstractAbility).setLinkBreakRadius(25);
                }
            }
    ),
    DRAINING_MIASMA("Draining Miasma",
            "§7Increase the leech duration of Draining Miasma\nby 5 seconds and reduce the cooldown\nby 30%",
            "§aIncrease the leech duration of Draining Miasma\nby §c5 §aseconds and reduce the cooldown\nby §c30%",
            DrainingMiasma.class,
            abstractAbility -> {
                if (abstractAbility instanceof DrainingMiasma) {
                    ((DrainingMiasma) abstractAbility).setLeechDuration(10);
                    abstractAbility.setCooldown(abstractAbility.getCooldown() * .7f);
                }
            }
    ),

    ;

    public final String name;
    public final String description;
    public final String selectedDescription;
    public final Class<?> ability;
    public final Consumer<AbstractAbility> applyBoost;

    SkillBoosts(String name, String description, String selectedDescription, Class<?> ability, Consumer<AbstractAbility> applyBoost) {
        this.name = name;
        this.description = description;
        this.selectedDescription = selectedDescription;
        this.ability = ability;
        this.applyBoost = applyBoost;
    }
}
package com.ebicep.warlords.player;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.*;

import java.util.function.Consumer;

public enum ClassesSkillBoosts {
    FIREBALL("Fireball",
            "§7Increases the damage you\n§7deal with Fireball by 20%",
            "§aIncreases the damage you\n§adeal with Fireball by §c20%",
            Fireball.class,
            abstractAbility -> {
            }
    ),
    FLAME_BURST("Flame Burst",
            "§7Increases the damage you\n§7deal with Flame Burst by\n§720%",
            "§aIncreases the damage you\n§adeal with Flame Burst by\n§c20%",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    TIME_WARP_PYROMANCER("Time Warp",
            "§7Increase the amount of health you\n§7restore with Time Warp by §75% §7and reduce\n§7the cooldown by 10%.",
            "§aIncrease the amount of health\n§ayou restore with Time Warp by §c5% §aand reduce\n§athe cooldown by §c10%.",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    ARCANE_SHIELD_PYROMANCER("Arcane Shield",
            "§7Increase the amount of health\n§7converted to shield by 20%",
            "§aIncrease the amount of health\n§aconverted to shield by §c20%",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    INFERNO("Inferno",
            "§7Reduce the cooldown of Inferno\n§7by 25%",
            "§aReduce the cooldown of Inferno\n§aby §c25%",
            Inferno.class,
            abstractAbility -> {
            }
    ),
    FROST_BOLT("Frostbolt",
            "§7Increases the damage you\n§7deal with Frostbolt by\n§720%",
            "§aIncreases the damage you\n§adeal with Frostbolt by\n§c20%",
            FrostBolt.class,
            abstractAbility -> {
            }
    ),
    FREEZING_BREATH("Freezing Breath",
            "§7Increase the damage you\n§7deal with Freezing Breath\n§7by 25%",
            "§aIncrease the damage you\n§adeal with Freezing Breath\n§aby §c25%",
            FreezingBreath.class,
            abstractAbility -> {
            }
    ),
    TIME_WARP_CRYOMANCER("Flame Burst",
            "§7Increase the amount of health you\n§7restore with Time Warp by §75% §7and reduce\n§7the cooldown by 10%.",
            "§aIncrease the amount of health\n§ayou restore with Time Warp by §c5% §aand reduce\n§athe cooldown by §c10%.",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    ARCANE_SHIELD_CRYOMANCER("Arcane Shield",
            "§7Increase the amount of health\n§7converted to shield by 20%",
            "§aIncrease the amount of health\n§aconverted to shield by §c20%",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    ICE_BARRIER("Ice Barrier",
            "§7Increase the amount damage you\n§7reduce with Ice Barrier by\n§710% §7and reduce the cooldown by 10%",
            "§aIncrease the amount damage you\n§areduce with Ice Barrier by\n§c10% §aand reduce the cooldown by §c10%",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    WATER_BOLT("Waterbolt",
            "§7Increases the amount of\n§7health you restore with\n§7Water Bolt by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aWater Bolt by §c20%",
            WaterBolt.class,
            abstractAbility -> {
            }
    ),
    WATER_BREATH("Water Breath",
            "§7Increases the amount of\n§7health you restore with\n§7Water Breath by 25%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aWater Breath by §c25%",
            WaterBreath.class,
            abstractAbility -> {
            }
    ),
    TIME_WARP_AQUAMANCER("Time Warp",
            "§7Increase the amount of health you\n§7restore with Time Warp by §75% and reduce\n§7the cooldown by 10%.",
            "§aIncrease the amount of health\n§ayou restore with Time Warp by §c5% §aand reduce\n§athe cooldown by §c10%.",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    ARCANE_SHIELD_AQUAMANCER("Arcane Shield",
            "§7Increase the amount of health\n§7converted to shield by 20%",
            "§aIncrease the amount of health\n§aconverted to shield by §c20%",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    HEALING_RAIN("Healing Rain",
            "§7Increases the range of\n§7Healing Rain by 2 blocks and\n§7reduce the cooldown by 20%",
            "§aIncreases the range of\n§aHealing Rain by §c2 §ablocks and\n§areduce the cooldown by §c20%",
            HealingRain.class,
            abstractAbility -> {
            }
    ),
    WOUNDING_STRIKE_BERSERKER("Wounding Strike",
            "§7Increase the damage you\n§7deal with Wounding Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Wounding Strike\n§aby §c20%",
            WoundingStrikeBerserker.class,
            abstractAbility -> {
            }
    ),
    SEISMIC_WAVE_BERSERKER("Seismic Wave",
            "§7Increase the damage you\n§7deal with Seismic Wave by\n§730%",
            "§aIncrease the damage you\n§adeal with Seismic Wave by\n§c30%",
            SeismicWave.class,
            abstractAbility -> {
            }
    ),
    GROUND_SLAM_BERSERKER("Ground Slam",
            "§7Reduce the energy cost by 30\n§7and reduce the cooldown\nby §710%",
            "§aReduce the energy cost by §c30\n§aand reduce the cooldown\nby §c10%",
            GroundSlam.class,
            abstractAbility -> {
            }
    ),
    BLOOD_LUST("Blood Lust",
            "§7Increase the amount of damage\n§7you convert into healing with\n§7Blood Lust by 5% and reduce the cooldown\nby 10%",
            "§aIncrease the amount of damage\n§ayou convert into healing with\n§aBlood Lust by §c5% and reduce the cooldown\nby §c10%",
            Berserk.class,
            abstractAbility -> {
            }
    ),
    BERSERK("Berserk",
            "§7Reduce the cooldown of Berserk\n§7by 25%",
            "§aReduce the cooldown of Berserk\n§aby §c25%",
            Berserk.class,
            abstractAbility -> {
            }
    ),
    WOUNDING_STRIKE_DEFENDER("Wounding Strike",
            "§7Increase the damage you\n§7deal with Wounding Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Wounding Strike\n§aby §c20%",
            WoundingStrikeDefender.class,
            abstractAbility -> {
            }
    ),
    SEISMIC_WAVE_DEFENDER("Seismic Wave",
            "§7Increase the damage you\n§7deal with Seismic Wave by\n§730%",
            "§aIncrease the damage you\n§adeal with Seismic Wave by\n§c30%",
            SeismicWave.class,
            abstractAbility -> {
            }
    ),
    GROUND_SLAM_DEFENDER("Ground Slam",
            "§7Increase the damage you\n§7deal with Ground Slam by\n§710% §7and reduce the cooldown by §a10%",
            "§7Increase the damage you\n§7deal with Ground Slam by\n§c10% §aand reduce the cooldown by §a10%",
            GroundSlam.class,
            abstractAbility -> {
            }
    ),
    INTERVENE("Intervene",
            "§7Reduce the cooldown of Intervene\n§7by 10%",
            "§aReduce the cooldown of Intervene\n§aby §c10%",
            Berserk.class,
            abstractAbility -> {
            }
    ),
    LAST_STAND("Last Stand",
            "§7Increase the amount damage you\n§7reduce with Last Stand by\n§710% §7and reduce the cooldown by 10%",
            "§aIncrease the amount damage you\n§areduce with Last Stand by\n§c10% §aand reduce the cooldown by §c10%",
            Berserk.class,
            abstractAbility -> {
            }
    ),
    CRIPPLING_STRIKE("Crippling Strike",
            "§7Increase the damage you\n§7deal with Crippling Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Crippling Strike\n§aby §c20%",
            CripplingStrike.class,
            abstractAbility -> {
            }
    ),
    RECKLESS_CHARGE("Reckless Charge",
            "§7Increase the damage you\n§7deal with Reckless Charge by\n§710% and reduce the cooldown\n§7by 10%",
            "§aIncrease the damage you\n§adeal with Reckless Charge by\n§c10% §aand reduce the cooldown\n§aby §c10%",
            RecklessCharge.class,
            abstractAbility -> {
            }
    ),
    GROUND_SLAM_REVENANT("Ground Slam",
            "§7Reduce the cooldown of Intervene\n§7by 40%",
            "§aReduce the cooldown of Intervene\n§aby §c40%",
            GroundSlam.class,
            abstractAbility -> {
            }
    ),
    ORBS_OF_LIFE("Orbs of Life",
            "§7Increases the amount of\n§7health you restore with\n§7Orbs of Life by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aOrbs of Life by §c20%",
            OrbsOfLife.class,
            abstractAbility -> {
            }
    ),
    UNDYING_ARMY("Berserk",
            "§7Increase the amount of allies\naffected by 2 §7and reduce the cooldown\n§7by 10%",
            "§7Increase the amount of allies\n§7affected by 2 §aand reduce the cooldown\n§aby §c10%",
            Berserk.class,
            abstractAbility -> {
            }
    ),
    AVENGER_STRIKE("Avenger's Strike",
            "§7Increase the damage you\n§7deal with Avenger's Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Avenger's Strike\n§aby §c20%",
            AvengersStrike.class,
            abstractAbility -> {
            }
    ),
    CONSECRATE_AVENGER("Consecrate",
            "§7Increases the range of\n§7Consecrate by 2 blocks and\n§7reduce the energy cost\n§7by 20",
            "§aIncreases the range of\n§a7Consecrate by §c2 §ablocks and\n§areduce the energy cost\n§aby §c20",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    LIGHT_INFUSION_AVENGER("Light Infusion",
            "§7Reduce the cooldown of Light Infusion\n§7by 10% and increase the speed\n§7duration by 1 second",
            "§aReduce the cooldown of Light Infusion\n§aby §c10% §aand increase the speed\n§aduration by §c1 §asecond",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    HOLY_RADIANCE_AVENGER("Holy Radiance",
            "§7Reduce the cooldown of Holy Radiance\n§7by 30%",
            "§aReduce the cooldown of Holy Radiance\n§aby §c30%",
            HolyRadiance.class,
            abstractAbility -> {
            }
    ),
    AVENGERS_WRATH("Avenger's Wrath",
            "§7Reduce the cooldown of Avenger's Wrath\n§7by 25%",
            "§aReduce the cooldown of Avenger's Wrath\n§aby §c25%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    CRUSADER_STRIKE("Crusader's Strike",
            "§7Increase the damage you\n§7deal with Crusader's Strike\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Crusader's Strike\n§aby §c20%",
            CrusadersStrike.class,
            abstractAbility -> {
            }
    ),
    CONSECRATE_CRUSADER("Consecrate",
            "§7Increases the range of\n§7Consecrate by 2 blocks and\n§7reduce the energy cost\n§7by 20",
            "§aIncreases the range of\n§a7Consecrate by §c2 §ablocks and\n§areduce the energy cost\n§aby §c20",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    LIGHT_INFUSION_CRUSADER("Light Infusion",
            "§7Reduce the cooldown of Light Infusion\n§7by 10% and increase the speed\n§7duration by 1 second",
            "§aReduce the cooldown of Light Infusion\n§aby §c10% §aand increase the speed\n§aduration by §c1 §asecond",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    HOLY_RADIANCE_CRUSADER("Holy Radiance",
            "§7Reduce the cooldown of Holy Radiance\n§7by 10% and increase the duration\n§7of Holy Mark by 2 seconds",
            "§aReduce the cooldown of Holy Radiance\n§aby §c10% §aand increase the duration\n§aof Holy Mark by §c2 §aseconds",
            HolyRadiance.class,
            abstractAbility -> {
            }
    ),
    INSPIRING_PRESENCE("Inspiring Presence",
            "§7Reduce the cooldown of Inspiring Presence\n§7by 25%",
            "§aReduce the cooldown of Inspiring Presence\n§aby §c25%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    PROTECTOR_STRIKE("Protector's Strike",
            "§7Increase the amount of damage\n§7you convert into healing with\n§7Protector's Strike by 20%",
            "§aIncrease the amount of damage\n§ayou convert into healing with\n§aProtector's Strike by §c20%",
            ProtectorsStrike.class,
            abstractAbility -> {
            }
    ),
    CONSECRATE_PROTECTOR("Consecrate",
            "§7Increases the range of\n§7Consecrate by 2 blocks and\n§7reduce the cooldown\n§7by 20%",
            "§aIncreases the range of\n§a7Consecrate by §c2 §ablocks and\n§areduce the cooldown\n§aby §c20%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    LIGHT_INFUSION_PROTECTOR("Light Infusion",
            "§7Reduce the cooldown of Light Infusion\n§7by 10% and increase the speed\n§7duration by 1 second",
            "§aReduce the cooldown of Light Infusion\n§aby §c10% §aand increase the speed\n§aduration by §c1 §asecond",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    HOLY_RADIANCE_PROTECTOR("Holy Radiance",
            "§7Increases the amount of\n§7health you restore with\n§7Holy Radiance by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aHoly Radiance by §c20%",
            HolyRadiance.class,
            abstractAbility -> {
            }
    ),
    HAMMER_OF_LIGHT("Hammer of Light",
            "§7Increases the amount of\n§7health you restore with\n§7Hammer of Light by 20% and\nreduce the cooldown by 15%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aHammer of Light by §c20% §aand\nreduce the cooldown by §c15%",
            HammerOfLight.class,
            abstractAbility -> {
            }
    ),
    LIGHTNING_BOLT("Lightning Bolt",
            "§7Increase the damage you\n§7deal with Lightning Bolt by\n§720%",
            "§aIncrease the damage you\n§adeal with Lightning Bolt by\n§c20%",
            LightningBolt.class,
            abstractAbility -> {
            }
    ),
    CHAIN_LIGHTNING("Chain Lightning",
            "§7Increase the damage you\n§7deal with Chain Lightning\n§7by 25%",
            "§aIncrease the damage you\n§adeal with Chain Lightning\n§aby §c25%",
            ChainLightning.class,
            abstractAbility -> {
            }
    ),
    WINDFURY_WEAPON("Windfury Weapon",
            "§7Increase the damage you\n§7deal with Windfury Weapon\n§7by 15% and increase the proc\n§7chance by 5%",
            "§aIncrease the damage you\n§adeal with Windfury Weapon\n§aby §c15% and increase the proc\n§achance by §c5%",
            Windfury.class,
            abstractAbility -> {
            }
    ),
    LIGHTNING_ROD("Lightning Rod",
            "§7Reduce the cooldown of Lightning Rod\n§7by 25%",
            "§aReduce the cooldown of Lightning Rod\n§aby §c25%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    CAPACITOR_TOTEM("Capacitor Totem",
            "§7Increase the damage you\n§7deal with Capacitor Totem\n§7by 10% and reduce the cooldown\n§7by 15%",
            "§aIncrease the damage you\n§adeal with Capacitor Totem\n§aby §c10% §aand reduce the cooldown\n§aby §c15%",
            CapacitorTotem.class,
            abstractAbility -> {
            }
    ),
    FALLEN_SOULS("Fallen Souls",
            "§7Increase the damage you\n§7deal with Fallen Souls by\n§720%",
            "§aIncrease the damage you\n§adeal with Fallen Souls by\n§c20%",
            FallenSouls.class,
            abstractAbility -> {
            }
    ),
    SPIRIT_LINK("Spirit Link",
            "§7Increase the damage you\n§7deal with Spirit Link by\n§725%",
            "§aIncrease the damage you\n§adeal with Spirit Link by\n§c25%",
            SpiritLink.class,
            abstractAbility -> {
            }
    ),
    SOULBINDING_WEAPON("Soulbinding Weapon",
            "§7Increase the duration of\n§7binds by 0.5 seconds and\n§7reduce the cooldown by 10%",
            "§aIncrease the duration of\n§abinds by §c0.5 §aseconds and\n§areduce the cooldown by §c10%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    REPENTANCE("Repentance",
            "§7Increase the damage you\n§7convert by 10%",
            "§aIncrease the damage you\n§aconvert by §c10%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    DEATHS_DEBT("Death's Debt",
            "§7Increase the range of Death's Debt\n§7by 2 blocks and reduce the cooldown\n§7by 20%",
            "§aIncrease the range of Death's Debt\n§aby §c2 §ablocks and reduce the cooldown\n§aby §c20%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    EARTHEN_SPIKE("Earthen Spike",
            "§7Increase the damage you\n§7deal with Earthen Spike by\n§720%",
            "§aIncrease the damage you\n§adeal with Earthen Spike by\n§c20%",
            EarthenSpike.class,
            abstractAbility -> {
            }
    ),
    BOULDER("Boulder",
            "§7Increase the damage you\n§7deal with Boulder by 20%",
            "§aIncrease the damage you\n§adeal with Boulder by §c20%",
            Boulder.class,
            abstractAbility -> {
            }
    ),
    EARTHLIVING_WEAPON("Earthliving Weapon",
            "§7Increase the amount of health you\n§7restore with Earthliving Weapon\n§7by 10% and increase the proc\n§7chance by 10%",
            "§aIncrease the amount of health you\n§arestore with Earthliving Weapon\n§aby §c10% §aand increase the proc\n§achance by §c10%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    CHAIN_HEAL("Chain Heal",
            "§7Increases the amount of\n§7health you restore with\n§7Chain Heal by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aChain Heal by §c20%",
            ChainHeal.class,
            abstractAbility -> {
            }
    ),
    HEALING_TOTEM("Healing Totem",
            "§7Increase the amount of health you\n§7restore with Healing Totem\n§7by 10% and reduce the\n§7cooldown by 10%",
            "§aIncrease the amount of health you\n§arestore with Healing Totem\n§aby §c10% §aand reduce the\n§acooldown by §c10%",
            HealingTotem.class,
            abstractAbility -> {
            }
    );

    public final String name;
    public final String description;
    public final String selectedDescription;
    public final Class ability;
    public final Consumer<AbstractAbility> applyBoost;

    ClassesSkillBoosts(String name, String description, String selectedDescription, Class ability, Consumer<AbstractAbility> applyBoost) {
        this.name = name;
        this.description = description;
        this.selectedDescription = selectedDescription;
        this.ability = ability;
        this.applyBoost = applyBoost;
    }
}
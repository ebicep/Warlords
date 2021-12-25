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
    TIME_WARP_PYROMANCER("Flame Burst",
            "§7Increases the damage you\n§7deal with Flame Burst by\n§720%",
            "§aIncreases the damage you\n§adeal with Flame Burst by\n§c20%",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    ARCANE_SHIELD_PYROMANCER("Flame Burst",
            "§7Increases the damage you\n§7deal with Flame Burst by\n§720%",
            "§aIncreases the damage you\n§adeal with Flame Burst by\n§c20%",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    INFERNO("Inferno",
            "§7Reduce the cooldown of Inferno\n§7by 20%",
            "§aReduce the cooldown of Inferno\n§aby §c20%",
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
            "§7Increase the damage you\n§7deal with Freezing Breath\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Freezing Breath\n§aby §c20%",
            FreezingBreath.class,
            abstractAbility -> {
            }
    ),
    TIME_WARP_CRYOMANCER("Flame Burst",
            "§7Increases the damage you\n§7deal with Flame Burst by\n§720%",
            "§aIncreases the damage you\n§adeal with Flame Burst by\n§c20%",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    ARCANE_SHIELD_CRYOMANCER("Flame Burst",
            "§7Increases the damage you\n§7deal with Flame Burst by\n§720%",
            "§aIncreases the damage you\n§adeal with Flame Burst by\n§c20%",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    ICE_BARRIER("Flame Burst",
            "§7Increases the damage you\n§7deal with Flame Burst by\n§720%",
            "§aIncreases the damage you\n§adeal with Flame Burst by\n§c20%",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    WATER_BOLT("Water Bolt",
            "§7Increases the amount of\n§7health you restore with\n§7Water Bolt by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aWater Bolt by §c20%",
            WaterBolt.class,
            abstractAbility -> {
            }
    ),
    WATER_BREATH("Water Breath",
            "§7Increases the amount of\n§7health you restore with\n§7Water Breath by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aWater Breath by §c20%",
            WaterBreath.class,
            abstractAbility -> {
            }
    ),
    TIME_WARP_AQUAMANCER("Flame Burst",
            "§7Increases the damage you\n§7deal with Flame Burst by\n§720%",
            "§aIncreases the damage you\n§adeal with Flame Burst by\n§c20%",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    ARCANE_SHIELD_AQUAMANCER("Flame Burst",
            "§7Increases the damage you\n§7deal with Flame Burst by\n§720%",
            "§aIncreases the damage you\n§adeal with Flame Burst by\n§c20%",
            FlameBurst.class,
            abstractAbility -> {
            }
    ),
    HEALING_RAIN("Healing Rain",
            "§7Increases the amount of\n§7health you restore with\n§7Healing Rain by 30%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aHealing Rain by §c30%",
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
            "§7Increase the damage you\n§7deal with Seismic Wave by\n§720%",
            "§aIncrease the damage you\n§adeal with Seismic Wave by\n§c20%",
            SeismicWave.class,
            abstractAbility -> {
            }
    ),
    GROUND_SLAM_BERSERKER("Ground Slam",
            "§7Increase the damage you\n§7deal with Ground Slam by\n§720%",
            "§aIncrease the damage you\n§adeal with Ground Slam by\n§c20%",
            GroundSlam.class,
            abstractAbility -> {
            }
    ),
    BLOOD_LUST("Berserk",
            "§7Reduce the cooldown of Berserk\n§7by 20%",
            "§aReduce the cooldown of Berserk\n§aby §c20%",
            Berserk.class,
            abstractAbility -> {
            }
    ),
    BERSERK("Berserk",
            "§7Reduce the cooldown of Berserk\n§7by 20%",
            "§aReduce the cooldown of Berserk\n§aby §c20%",
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
            "§7Increase the damage you\n§7deal with Seismic Wave by\n§720%",
            "§aIncrease the damage you\n§adeal with Seismic Wave by\n§c20%",
            SeismicWave.class,
            abstractAbility -> {
            }
    ),
    GROUND_SLAM_DEFENDER("Ground Slam",
            "§7Increase the damage you\n§7deal with Ground Slam by\n§720%",
            "§aIncrease the damage you\n§adeal with Ground Slam by\n§c20%",
            GroundSlam.class,
            abstractAbility -> {
            }
    ),
    INTERVENE("Berserk",
            "§7Reduce the cooldown of Berserk\n§7by 20%",
            "§aReduce the cooldown of Berserk\n§aby §c20%",
            Berserk.class,
            abstractAbility -> {
            }
    ),
    LAST_STAND("Berserk",
            "§7Reduce the cooldown of Berserk\n§7by 20%",
            "§aReduce the cooldown of Berserk\n§aby §c20%",
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
            "§7Increase the damage you\n§7deal with Reckless Charge by\n§720%",
            "§aIncrease the damage you\n§adeal with Reckless Charge by\n§c20%",
            RecklessCharge.class,
            abstractAbility -> {
            }
    ),
    GROUND_SLAM_REVENANT("Ground Slam",
            "§7Increase the damage you\n§7deal with Ground Slam by\n§720%",
            "§aIncrease the damage you\n§adeal with Ground Slam by\n§c20%",
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
            "§7Reduce the cooldown of Berserk\n§7by 20%",
            "§aReduce the cooldown of Berserk\n§aby §c20%",
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
            "§7Increase the damage you\n§7deal with Consecrate by 20%",
            "§aIncrease the damage you\n§adeal with Consecrate by §c20%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    LIGHT_INFUSION_AVENGER("Consecrate",
            "§7Increase the damage you\n§7deal with Consecrate by 20%",
            "§aIncrease the damage you\n§adeal with Consecrate by §c20%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    HOLY_RADIANCE_AVENGER("Holy Radiance",
            "§7Increases the amount of\n§7health you restore with\n§7Holy Radiance by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aHoly Radiance by §c20%",
            HolyRadiance.class,
            abstractAbility -> {
            }
    ),
    AVENGERS_WRATH("Consecrate",
            "§7Increase the damage you\n§7deal with Consecrate by 20%",
            "§aIncrease the damage you\n§adeal with Consecrate by §c20%",
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
            "§7Increase the damage you\n§7deal with Consecrate by 20%",
            "§aIncrease the damage you\n§adeal with Consecrate by §c20%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    LIGHT_INFUSION_CRUSADER("Consecrate",
            "§7Increase the damage you\n§7deal with Consecrate by 20%",
            "§aIncrease the damage you\n§adeal with Consecrate by §c20%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    HOLY_RADIANCE_CRUSADER("Holy Radiance",
            "§7Increases the amount of\n§7health you restore with\n§7Holy Radiance by 20%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aHoly Radiance by §c20%",
            HolyRadiance.class,
            abstractAbility -> {
            }
    ),
    INSPIRING_PRESENCE("Consecrate",
            "§7Increase the damage you\n§7deal with Consecrate by 20%",
            "§aIncrease the damage you\n§adeal with Consecrate by §c20%",
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
            "§7Increase the damage you\n§7deal with Consecrate by 20%",
            "§aIncrease the damage you\n§adeal with Consecrate by §c20%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    LIGHT_INFUSION_PROTECTOR("Consecrate",
            "§7Increase the damage you\n§7deal with Consecrate by 20%",
            "§aIncrease the damage you\n§adeal with Consecrate by §c20%",
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
            "§7Increases the amount of\n§7health you restore with\n§7Hammer of Light by 40%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aHammer of Light by §c40%",
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
            "§7Increase the damage you\n§7deal with Chain Lightning\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Chain Lightning\n§aby §c20%",
            ChainLightning.class,
            abstractAbility -> {
            }
    ),
    WINDFURY_WEAPON("Windfury Weapon",
            "§7Increase the damage you\n§7deal with Windfury Weapon\n§7by 20%",
            "§aIncrease the damage you\n§adeal with Windfury Weapon\n§aby §c20%",
            Windfury.class,
            abstractAbility -> {
            }
    ),
    LIGHTNING_ROD("Consecrate",
            "§7Increase the damage you\n§7deal with Consecrate by 20%",
            "§aIncrease the damage you\n§adeal with Consecrate by §c20%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    CAPACITOR_TOTEM("Capacitor Totem",
            "§7Increase the damage you\n§7deal with Capacitor Totem\n§7by 30%",
            "§aIncrease the damage you\n§adeal with Capacitor Totem\n§aby §c30%",
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
            "§7Increase the damage you\n§7deal with Spirit Link by\n§720%",
            "§aIncrease the damage you\n§adeal with Spirit Link by\n§c20%",
            SpiritLink.class,
            abstractAbility -> {
            }
    ),
    SOULBINDING_WEAPON("Consecrate",
            "§7Increase the damage you\n§7deal with Consecrate by 20%",
            "§aIncrease the damage you\n§adeal with Consecrate by §c20%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    REPETENCE("Consecrate",
            "§7Increase the damage you\n§7deal with Consecrate by 20%",
            "§aIncrease the damage you\n§adeal with Consecrate by §c20%",
            Consecrate.class,
            abstractAbility -> {
            }
    ),
    DEBTS_DEBT("Consecrate",
            "§7Increase the damage you\n§7deal with Consecrate by 20%",
            "§aIncrease the damage you\n§adeal with Consecrate by §c20%",
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
    EARTHLIVING_WEAPON("Consecrate",
            "§7Increase the damage you\n§7deal with Consecrate by 20%",
            "§aIncrease the damage you\n§adeal with Consecrate by §c20%",
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
            "§7Increases the amount of\n§7health you restore with\n§7Healing Totem by 40%",
            "§aIncreases the amount of\n§ahealth you restore with\n§aHealing Totem by §c40%",
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
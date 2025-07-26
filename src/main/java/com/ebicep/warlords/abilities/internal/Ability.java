package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Enum of all spec abilities (no mob ones)
 */
public class Ability<T extends AbstractAbility> {

    public static final Ability<ArcaneShield> ARCANE_SHIELD = new Ability<>(ArcaneShield.class, ArcaneShield::new);
    public static final Ability<AstralPlague> ASTRAL_PLAGUE = new Ability<>(AstralPlague.class, AstralPlague::new);
    public static final Ability<AvengersStrike> AVENGERS_STRIKE = new Ability<>(AvengersStrike.class, AvengersStrike::new);
    public static final Ability<AvengersWrath> AVENGERS_WRATH = new Ability<>(AvengersWrath.class, AvengersWrath::new);
    public static final Ability<BeaconOfLight> BEACON_OF_LIGHT = new Ability<>(BeaconOfLight.class, BeaconOfLight::new);
    public static final Ability<Berserk> BERSERK = new Ability<>(Berserk.class, Berserk::new);
    public static final Ability<Blink> BLINK = new Ability<>(Blink.class, Blink::new);
    public static final Ability<BloodLust> BLOOD_LUST = new Ability<>(BloodLust.class, BloodLust::new);
    public static final Ability<Boulder> BOULDER = new Ability<>(Boulder.class, Boulder::new);
    public static final Ability<BullRush> BULL_RUSH = new Ability<>(BullRush.class, BullRush::new);
    public static final Ability<CapacitorTotem> CAPACITOR_TOTEM = new Ability<>(CapacitorTotem.class, CapacitorTotem::new);
    public static final Ability<ChainHeal> CHAIN_HEAL = new Ability<>(ChainHeal.class, ChainHeal::new);
    public static final Ability<ChainLightning> CHAIN_LIGHTNING = new Ability<>(ChainLightning.class, ChainLightning::new);
    public static final Ability<Clairvoyance> CLAIRVOYANCE = new Ability<>(Clairvoyance.class, Clairvoyance::new);
    public static final Ability<ConsecrateAvenger> CONSECRATE_AVENGER = new Ability<>(ConsecrateAvenger.class, ConsecrateAvenger::new);
    public static final Ability<ConsecrateCrusader> CONSECRATE_CRUSADER = new Ability<>(ConsecrateCrusader.class, ConsecrateCrusader::new);
    public static final Ability<ConsecrateProtector> CONSECRATE_PROTECTOR = new Ability<>(ConsecrateProtector.class, ConsecrateProtector::new);
    public static final Ability<ContagiousFacade> CONTAGIOUS_FACADE = new Ability<>(ContagiousFacade.class, ContagiousFacade::new);
    public static final Ability<CripplingStrike> CRIPPLING_STRIKE = new Ability<>(CripplingStrike.class, CripplingStrike::new);
    public static final Ability<CrusadersStrike> CRUSADERS_STRIKE = new Ability<>(CrusadersStrike.class, CrusadersStrike::new);
    public static final Ability<CrystalOfHealing> CRYSTAL_OF_HEALING = new Ability<>(CrystalOfHealing.class, CrystalOfHealing::new);
    public static final Ability<DeathsDebt> DEATHS_DEBT = new Ability<>(DeathsDebt.class, DeathsDebt::new);
    public static final Ability<DivineBlessing> DIVINE_BLESSING = new Ability<>(DivineBlessing.class, DivineBlessing::new);
    public static final Ability<DrainingMiasma> DRAINING_MIASMA = new Ability<>(DrainingMiasma.class, DrainingMiasma::new);
    public static final Ability<EarthenSpike> EARTHEN_SPIKE = new Ability<>(EarthenSpike.class, EarthenSpike::new);
    public static final Ability<EarthlivingWeapon> EARTHLIVING_WEAPON = new Ability<>(EarthlivingWeapon.class, EarthlivingWeapon::new);
    public static final Ability<EnergySeerConjurer> ENERGY_SEER_CONJURER = new Ability<>(EnergySeerConjurer.class, EnergySeerConjurer::new);
    public static final Ability<EnergySeerLuminary> ENERGY_SEER_LUMINARY = new Ability<>(EnergySeerLuminary.class, EnergySeerLuminary::new);
    public static final Ability<EnergySeerSentinel> ENERGY_SEER_SENTINEL = new Ability<>(EnergySeerSentinel.class, EnergySeerSentinel::new);
    public static final Ability<FallenSouls> FALLEN_SOULS = new Ability<>(FallenSouls.class, FallenSouls::new);
    public static final Ability<Fireball> FIREBALL = new Ability<>(Fireball.class, Fireball::new);
    public static final Ability<FlameBurst> FLAME_BURST = new Ability<>(FlameBurst.class, FlameBurst::new);
    public static final Ability<FlameBreath> FLAME_BREATH = new Ability<>(FlameBreath.class, FlameBreath::new);
    public static final Ability<FortifyingHex> FORTIFYING_HEX = new Ability<>(FortifyingHex.class, FortifyingHex::new);
    public static final Ability<FreezingBreath> FREEZING_BREATH = new Ability<>(FreezingBreath.class, FreezingBreath::new);
    public static final Ability<FrostBolt> FROST_BOLT = new Ability<>(FrostBolt.class, FrostBolt::new);
    public static final Ability<GroundSlamBerserker> GROUND_SLAM_BERSERKER = new Ability<>(GroundSlamBerserker.class, GroundSlamBerserker::new);
    public static final Ability<GroundSlamDefender> GROUND_SLAM_DEFENDER = new Ability<>(GroundSlamDefender.class, GroundSlamDefender::new);
    public static final Ability<GroundSlamRevenant> GROUND_SLAM_REVENANT = new Ability<>(GroundSlamRevenant.class, GroundSlamRevenant::new);
    public static final Ability<GuardianBeam> GUARDIAN_BEAM = new Ability<>(GuardianBeam.class, GuardianBeam::new);
    public static final Ability<HammerOfLight> HAMMER_OF_LIGHT = new Ability<>(HammerOfLight.class, HammerOfLight::new);
    public static final Ability<Haze> HAZE = new Ability<>(Haze.class, Haze::new);
    public static final Ability<HealingLink> HEALING_LINK = new Ability<>(HealingLink.class, HealingLink::new);
    public static final Ability<HealingRain> HEALING_RAIN = new Ability<>(HealingRain.class, HealingRain::new);
    public static final Ability<HealingTotem> HEALING_TOTEM = new Ability<>(HealingTotem.class, HealingTotem::new);
    public static final Ability<HeartToHeart> HEART_TO_HEART = new Ability<>(HeartToHeart.class, HeartToHeart::new);
    public static final Ability<HolyRadianceAvenger> HOLY_RADIANCE_AVENGER = new Ability<>(HolyRadianceAvenger.class, HolyRadianceAvenger::new);
    public static final Ability<HolyRadianceCrusader> HOLY_RADIANCE_CRUSADER = new Ability<>(HolyRadianceCrusader.class, HolyRadianceCrusader::new);
    public static final Ability<HolyRadianceProtector> HOLY_RADIANCE_PROTECTOR = new Ability<>(HolyRadianceProtector.class, HolyRadianceProtector::new);
    public static final Ability<IceBarrier> ICE_BARRIER = new Ability<>(IceBarrier.class, IceBarrier::new);
    public static final Ability<ImpalingStrike> IMPALING_STRIKE = new Ability<>(ImpalingStrike.class, ImpalingStrike::new);
    public static final Ability<IncendiaryCurse> INCENDIARY_CURSE = new Ability<>(IncendiaryCurse.class, IncendiaryCurse::new);
    public static final Ability<Inferno> INFERNO = new Ability<>(Inferno.class, Inferno::new);
    public static final Ability<InspiringPresence> INSPIRING_PRESENCE = new Ability<>(InspiringPresence.class, InspiringPresence::new);
    public static final Ability<Intervene> INTERVENE = new Ability<>(Intervene.class, Intervene::new);
    public static final Ability<JudgementStrike> JUDGEMENT_STRIKE = new Ability<>(JudgementStrike.class, JudgementStrike::new);
    public static final Ability<LastStand> LAST_STAND = new Ability<>(LastStand.class, LastStand::new);
    public static final Ability<LightInfusionAvenger> LIGHT_INFUSION_AVENGER = new Ability<>(LightInfusionAvenger.class, LightInfusionAvenger::new);
    public static final Ability<LightInfusionCrusader> LIGHT_INFUSION_CRUSADER = new Ability<>(LightInfusionCrusader.class, LightInfusionCrusader::new);
    public static final Ability<LightInfusionProtector> LIGHT_INFUSION_PROTECTOR = new Ability<>(LightInfusionProtector.class, LightInfusionProtector::new);
    public static final Ability<LightningBolt> LIGHTNING_BOLT = new Ability<>(LightningBolt.class, LightningBolt::new);
    public static final Ability<LightningRod> LIGHTNING_ROD = new Ability<>(LightningRod.class, LightningRod::new);
    public static final Ability<MercifulHex> MERCIFUL_HEX = new Ability<>(MercifulHex.class, MercifulHex::new);
    public static final Ability<MysticalBarrier> MYSTICAL_BARRIER = new Ability<>(MysticalBarrier.class, MysticalBarrier::new);
    public static final Ability<NotAShield> NOT_A_SHIELD = new Ability<>(NotAShield.class, NotAShield::new);
    public static final Ability<OrbsOfLife> ORBS_OF_LIFE = new Ability<>(OrbsOfLife.class, OrbsOfLife::new);
    public static final Ability<OrderOfEviscerate> ORDER_OF_EVISCERATE = new Ability<>(OrderOfEviscerate.class, OrderOfEviscerate::new);
    public static final Ability<Parry> PARRY = new Ability<>(Parry.class, Parry::new);
    public static final Ability<PoisonousHex> POISONOUS_HEX = new Ability<>(PoisonousHex.class, PoisonousHex::new);
    public static final Ability<Portal> PORTAL = new Ability<>(Portal.class, Portal::new);
    public static final Ability<PrismGuard> PRISM_GUARD = new Ability<>(PrismGuard.class, PrismGuard::new);
    public static final Ability<ProtectorsStrike> PROTECTORS_STRIKE = new Ability<>(ProtectorsStrike.class, ProtectorsStrike::new);
    public static final Ability<RayOfLight> RAY_OF_LIGHT = new Ability<>(RayOfLight.class, RayOfLight::new);
    public static final Ability<RecklessCharge> RECKLESS_CHARGE = new Ability<>(RecklessCharge.class, RecklessCharge::new);
    public static final Ability<RemedicChains> REMEDIC_CHAINS = new Ability<>(RemedicChains.class, RemedicChains::new);
    public static final Ability<Repentance> REPENTANCE = new Ability<>(Repentance.class, Repentance::new);
    public static final Ability<RighteousStrike> RIGHTEOUS_STRIKE = new Ability<>(RighteousStrike.class, RighteousStrike::new);
    public static final Ability<SanctifiedBeacon> SANCTIFIED_BEACON = new Ability<>(SanctifiedBeacon.class, SanctifiedBeacon::new);
    public static final Ability<Sanctuary> SANCTUARY = new Ability<>(Sanctuary.class, Sanctuary::new);
    public static final Ability<SeismicWaveBerserker> SEISMIC_WAVE_BERSERKER = new Ability<>(SeismicWaveBerserker.class, SeismicWaveBerserker::new);
    public static final Ability<SeismicWaveDefender> SEISMIC_WAVE_DEFENDER = new Ability<>(SeismicWaveDefender.class, SeismicWaveDefender::new);
    public static final Ability<ShadowStep> SHADOW_STEP = new Ability<>(ShadowStep.class, ShadowStep::new);
    public static final Ability<Solitary> SOLITARY = new Ability<>(Solitary.class, Solitary::new);
    public static final Ability<SoothingElixir> SOOTHING_ELIXIR = new Ability<>(SoothingElixir.class, SoothingElixir::new);
    public static final Ability<Soulbinding> SOULBINDING = new Ability<>(Soulbinding.class, Soulbinding::new);
    public static final Ability<SoulfireBeam> SOULFIRE_BEAM = new Ability<>(SoulfireBeam.class, SoulfireBeam::new);
    public static final Ability<SoulShackle> SOUL_SHACKLE = new Ability<>(SoulShackle.class, SoulShackle::new);
    public static final Ability<SoulSwitch> SOUL_SWITCH = new Ability<>(SoulSwitch.class, SoulSwitch::new);
    public static final Ability<SpiritLink> SPIRIT_LINK = new Ability<>(SpiritLink.class, SpiritLink::new);
    public static final Ability<SuperBrew> SUPER_BREW = new Ability<>(SuperBrew.class, SuperBrew::new);
    public static final Ability<TimeSurge> TIME_SURGE = new Ability<>(TimeSurge.class, TimeSurge::new);
    public static final Ability<TimeWarpAquamancer> TIME_WARP_AQUAMANCER = new Ability<>(TimeWarpAquamancer.class, TimeWarpAquamancer::new);
    public static final Ability<TimeWarpCryomancer> TIME_WARP_CRYOMANCER = new Ability<>(TimeWarpCryomancer.class, TimeWarpCryomancer::new);
    public static final Ability<TimeWarpPyromancer> TIME_WARP_PYROMANCER = new Ability<>(TimeWarpPyromancer.class, TimeWarpPyromancer::new);
    public static final Ability<Triage> TRIAGE = new Ability<>(Triage.class, Triage::new);
    public static final Ability<UndyingArmy> UNDYING_ARMY = new Ability<>(UndyingArmy.class, UndyingArmy::new);
    public static final Ability<Vindicate> VINDICATE = new Ability<>(Vindicate.class, Vindicate::new);
    public static final Ability<VitalityConcoction> VITALITY_CONCOCTION = new Ability<>(VitalityConcoction.class, VitalityConcoction::new);
    public static final Ability<VitalityLiquor> VITALITY_LIQUOR = new Ability<>(VitalityLiquor.class, VitalityLiquor::new);
    public static final Ability<VolatileBrew> VOLATILE_BREW = new Ability<>(VolatileBrew.class, VolatileBrew::new);
    public static final Ability<WaterBolt> WATER_BOLT = new Ability<>(WaterBolt.class, WaterBolt::new);
    public static final Ability<WaterBreath> WATER_BREATH = new Ability<>(WaterBreath.class, WaterBreath::new);
    public static final Ability<WindfuryWeapon> WINDFURY_WEAPON = new Ability<>(WindfuryWeapon.class, WindfuryWeapon::new);
    public static final Ability<WonderTrap> WONDER_TRAP = new Ability<>(WonderTrap.class, WonderTrap::new);
    public static final Ability<WoundingStrikeBerserker> WOUNDING_STRIKE_BERSERKER = new Ability<>(WoundingStrikeBerserker.class, WoundingStrikeBerserker::new);
    public static final Ability<WoundingStrikeDefender> WOUNDING_STRIKE_DEFENDER = new Ability<>(WoundingStrikeDefender.class, WoundingStrikeDefender::new);

    public static final Ability<?>[] VALUES = new Ability[]{
            ARCANE_SHIELD,
            ASTRAL_PLAGUE,
            AVENGERS_STRIKE,
            AVENGERS_WRATH,
            BEACON_OF_LIGHT,
            BERSERK,
            BLINK,
            BLOOD_LUST,
            BOULDER,
            BULL_RUSH,
            CAPACITOR_TOTEM,
            CHAIN_HEAL,
            CHAIN_LIGHTNING,
            CLAIRVOYANCE,
            CONSECRATE_AVENGER,
            CONSECRATE_CRUSADER,
            CONSECRATE_PROTECTOR,
            CONTAGIOUS_FACADE,
            CRIPPLING_STRIKE,
            CRUSADERS_STRIKE,
            CRYSTAL_OF_HEALING,
            DEATHS_DEBT,
            DIVINE_BLESSING,
            DRAINING_MIASMA,
            EARTHEN_SPIKE,
            EARTHLIVING_WEAPON,
            ENERGY_SEER_CONJURER,
            ENERGY_SEER_LUMINARY,
            ENERGY_SEER_SENTINEL,
            FALLEN_SOULS,
            FIREBALL,
            FLAME_BURST,
            FLAME_BREATH,
            FORTIFYING_HEX,
            FREEZING_BREATH,
            FROST_BOLT,
            GROUND_SLAM_BERSERKER,
            GROUND_SLAM_DEFENDER,
            GROUND_SLAM_REVENANT,
            GUARDIAN_BEAM,
            HAMMER_OF_LIGHT,
            HAZE,
            HEALING_LINK,
            HEALING_RAIN,
            HEALING_TOTEM,
            HEART_TO_HEART,
            HOLY_RADIANCE_AVENGER,
            HOLY_RADIANCE_CRUSADER,
            HOLY_RADIANCE_PROTECTOR,
            ICE_BARRIER,
            IMPALING_STRIKE,
            INCENDIARY_CURSE,
            INFERNO,
            INSPIRING_PRESENCE,
            INTERVENE,
            JUDGEMENT_STRIKE,
            LAST_STAND,
            LIGHT_INFUSION_AVENGER,
            LIGHT_INFUSION_CRUSADER,
            LIGHT_INFUSION_PROTECTOR,
            LIGHTNING_BOLT,
            LIGHTNING_ROD,
            MERCIFUL_HEX,
            MYSTICAL_BARRIER,
            NOT_A_SHIELD,
            ORBS_OF_LIFE,
            ORDER_OF_EVISCERATE,
            PARRY,
            POISONOUS_HEX,
            PORTAL,
            PRISM_GUARD,
            PROTECTORS_STRIKE,
            RAY_OF_LIGHT,
            RECKLESS_CHARGE,
            REMEDIC_CHAINS,
            REPENTANCE,
            RIGHTEOUS_STRIKE,
            SANCTIFIED_BEACON,
            SANCTUARY,
            SEISMIC_WAVE_BERSERKER,
            SEISMIC_WAVE_DEFENDER,
            SHADOW_STEP,
            SOLITARY,
            SOOTHING_ELIXIR,
            SOULBINDING,
            SOULFIRE_BEAM,
            SOUL_SHACKLE,
            SOUL_SWITCH,
            SPIRIT_LINK,
            SUPER_BREW,
            TIME_SURGE,
            TIME_WARP_AQUAMANCER,
            TIME_WARP_CRYOMANCER,
            TIME_WARP_PYROMANCER,
            TRIAGE,
            UNDYING_ARMY,
            VINDICATE,
            VITALITY_CONCOCTION,
            VITALITY_LIQUOR,
            VOLATILE_BREW,
            WATER_BOLT,
            WATER_BREATH,
            WINDFURY_WEAPON,
            WONDER_TRAP,
            WOUNDING_STRIKE_BERSERKER,
            WOUNDING_STRIKE_DEFENDER,
    };
    public static final Map<Class<?>, Ability<?>> ABILITY_MAP = new HashMap<>();
    public static final Map<String, Ability<?>> ABILITY_DATABASE_MAP = new HashMap<>();
    public static final Map<Specializations, Ability<?>[]> SPEC_ABILITIES = new HashMap<>();

    static {
        for (Ability<?> ability : VALUES) {
            ABILITY_MAP.put(ability.clazz, ability);
        }
        for (Ability<?> value : VALUES) {
            ABILITY_DATABASE_MAP.put(value.getDatabaseName(), value);
        }
        for (Specializations spec : Specializations.VALUES) {
            Ability<?>[] abilities = new Ability[5];
            List<AbstractAbility> abstractAbilities = spec.create(ConfigManager.DEFAULT_NAMESPACES).getAbilities();
            for (int i = 0; i < abstractAbilities.size(); i++) {
                AbstractAbility ability = abstractAbilities.get(i);
                Ability<?> abilityRegistry = getAbility(ability.getClass());
                if (abilityRegistry == null) {
                    ChatUtils.MessageType.WARLORDS.sendErrorMessage("Unknown ability for " + spec.name() + ": " + ability.getClass().getSimpleName());
                    Bukkit.getServer().shutdown();
                    continue;
                }
                abilities[i] = abilityRegistry;
            }
            SPEC_ABILITIES.put(spec, abilities);
        }
    }

    @Nullable
    public static <T extends AbstractAbility> Ability<T> getAbility(Class<T> clazz) {
        return (Ability<T>) ABILITY_MAP.get(clazz);
    }

    public final Class<T> clazz;
    public final Supplier<T> create;

    Ability(Class<T> clazz, Supplier<T> create) {
        this.clazz = clazz;
        this.create = create;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clazz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ability<?> ability = (Ability<?>) o;
        return Objects.equals(clazz, ability.clazz);
    }

    public String getDatabaseName() {
        Field[] fields = Ability.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (field.get(null) == this) {
                    return field.getName();
                }
            } catch (IllegalAccessException e) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
            }
        }
        return null;
    }

}

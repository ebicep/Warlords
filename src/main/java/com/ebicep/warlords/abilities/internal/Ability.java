package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Enum of all spec abilities (no mob ones)
 */
public enum Ability {

    ARCANE_SHIELD(ArcaneShield.class, ArcaneShield::new),
    ASTRAL_PLAGUE(AstralPlague.class, AstralPlague::new),
    AVENGERS_STRIKE(AvengersStrike.class, AvengersStrike::new),
    AVENGERS_WRATH(AvengersWrath.class, AvengersWrath::new),
    BEACON_OF_LIGHT(BeaconOfLight.class, BeaconOfLight::new),
    BERSERK(Berserk.class, Berserk::new),
    BLOOD_LUST(BloodLust.class, BloodLust::new),
    BOULDER(Boulder.class, Boulder::new),
    CAPACITOR_TOTEM(CapacitorTotem.class, CapacitorTotem::new),
    CHAIN_HEAL(ChainHeal.class, ChainHeal::new),
    CHAIN_LIGHTNING(ChainLightning.class, ChainLightning::new),
    CONSECRATE_AVENGER(ConsecrateAvenger.class, ConsecrateAvenger::new),
    CONSECRATE_CRUSADER(ConsecrateCrusader.class, ConsecrateCrusader::new),
    CONSECRATE_PROTECTOR(ConsecrateProtector.class, ConsecrateProtector::new),
    CONTAGIOUS_FACADE(ContagiousFacade.class, ContagiousFacade::new),
    CRIPPLING_STRIKE(CripplingStrike.class, CripplingStrike::new),
    CRUSADERS_STRIKE(CrusadersStrike.class, CrusadersStrike::new),
    CRYSTAL_OF_HEALING(CrystalOfHealing.class, CrystalOfHealing::new),
    DEATHS_DEBT(DeathsDebt.class, DeathsDebt::new),
    DIVINE_BLESSING(DivineBlessing.class, DivineBlessing::new),
    DRAINING_MIASMA(DrainingMiasma.class, DrainingMiasma::new),
    EARTHEN_SPIKE(EarthenSpike.class, EarthenSpike::new),
    EARTHLIVING_WEAPON(EarthlivingWeapon.class, EarthlivingWeapon::new),
    ENERGY_SEER(EnergySeer.class, EnergySeer::new),
    ENERGY_SEER_CONJURER(EnergySeerConjurer.class, EnergySeerConjurer::new),
    ENERGY_SEER_LUMINARY(EnergySeerLuminary.class, EnergySeerLuminary::new),
    ENERGY_SEER_SENTINEL(EnergySeerSentinel.class, EnergySeerSentinel::new),
    FALLEN_SOULS(FallenSouls.class, FallenSouls::new),
    FIREBALL(Fireball.class, Fireball::new),
    FLAME_BURST(FlameBurst.class, FlameBurst::new),
    FORTIFYING_HEX(FortifyingHex.class, FortifyingHex::new),
    FREEZING_BREATH(FreezingBreath.class, FreezingBreath::new),
    FROST_BOLT(FrostBolt.class, FrostBolt::new),
    GROUND_SLAM_BERSERKER(GroundSlamBerserker.class, GroundSlamBerserker::new),
    GROUND_SLAM_DEFENDER(GroundSlamDefender.class, GroundSlamDefender::new),
    GROUND_SLAM_REVENANT(GroundSlamRevenant.class, GroundSlamRevenant::new),
    GUARDIAN_BEAM(GuardianBeam.class, GuardianBeam::new),
    HAMMER_OF_LIGHT(HammerOfLight.class, HammerOfLight::new),
    HEALING_RAIN(HealingRain.class, HealingRain::new),
    HEALING_TOTEM(HealingTotem.class, HealingTotem::new),
    HEART_TO_HEART(HeartToHeart.class, HeartToHeart::new),
    HOLY_RADIANCE_AVENGER(HolyRadianceAvenger.class, HolyRadianceAvenger::new),
    HOLY_RADIANCE_CRUSADER(HolyRadianceCrusader.class, HolyRadianceCrusader::new),
    HOLY_RADIANCE_PROTECTOR(HolyRadianceProtector.class, HolyRadianceProtector::new),
    ICE_BARRIER(IceBarrier.class, IceBarrier::new),
    IMPALING_STRIKE(ImpalingStrike.class, ImpalingStrike::new),
    INCENDIARY_CURSE(IncendiaryCurse.class, IncendiaryCurse::new),
    INFERNO(Inferno.class, Inferno::new),
    INSPIRING_PRESENCE(InspiringPresence.class, InspiringPresence::new),
    INTERVENE(Intervene.class, Intervene::new),
    JUDGEMENT_STRIKE(JudgementStrike.class, JudgementStrike::new),
    LAST_STAND(LastStand.class, LastStand::new),
    LIGHT_INFUSION_AVENGER(LightInfusionAvenger.class, LightInfusionAvenger::new),
    LIGHT_INFUSION_CRUSADER(LightInfusionCrusader.class, LightInfusionCrusader::new),
    LIGHT_INFUSION_PROTECTOR(LightInfusionProtector.class, LightInfusionProtector::new),
    LIGHTNING_BOLT(LightningBolt.class, LightningBolt::new),
    LIGHTNING_ROD(LightningRod.class, LightningRod::new),
    MERCIFUL_HEX(MercifulHex.class, MercifulHex::new),
    MYSTICAL_BARRIER(MysticalBarrier.class, MysticalBarrier::new),
    NOT_A_SHIELD(NotAShield.class, NotAShield::new),
    ORBS_OF_LIFE(OrbsOfLife.class, OrbsOfLife::new),
    ORDER_OF_EVISCERATE(OrderOfEviscerate.class, OrderOfEviscerate::new),
    POISONOUS_HEX(PoisonousHex.class, PoisonousHex::new),
    PRISM_GUARD(PrismGuard.class, PrismGuard::new),
    PROTECTORS_STRIKE(ProtectorsStrike.class, ProtectorsStrike::new),
    RAY_OF_LIGHT(RayOfLight.class, RayOfLight::new),
    RECKLESS_CHARGE(RecklessCharge.class, RecklessCharge::new),
    REMEDIC_CHAINS(RemedicChains.class, RemedicChains::new),
    REPENTANCE(Repentance.class, Repentance::new),
    RIGHTEOUS_STRIKE(RighteousStrike.class, RighteousStrike::new),
    SANCTIFIED_BEACON(SanctifiedBeacon.class, SanctifiedBeacon::new),
    SANCTUARY(Sanctuary.class, Sanctuary::new),
    SEISMIC_WAVE_BERSERKER(SeismicWaveBerserker.class, SeismicWaveBerserker::new),
    SEISMIC_WAVE_DEFENDER(SeismicWaveDefender.class, SeismicWaveDefender::new),
    SHADOW_STEP(ShadowStep.class, ShadowStep::new),
    SOOTHING_ELIXIR(SoothingElixir.class, SoothingElixir::new),
    SOULBINDING(Soulbinding.class, Soulbinding::new),
    SOULFIRE_BEAM(SoulfireBeam.class, SoulfireBeam::new),
    SOUL_SHACKLE(SoulShackle.class, SoulShackle::new),
    SOUL_SWITCH(SoulSwitch.class, SoulSwitch::new),
    SPIRIT_LINK(SpiritLink.class, SpiritLink::new),
    TIME_WARP_AQUAMANCER(TimeWarpAquamancer.class, TimeWarpAquamancer::new),
    TIME_WARP_CRYOMANCER(TimeWarpCryomancer.class, TimeWarpCryomancer::new),
    TIME_WARP_PYROMANCER(TimeWarpPyromancer.class, TimeWarpPyromancer::new),
    UNDYING_ARMY(UndyingArmy.class, UndyingArmy::new),
    VINDICATE(Vindicate.class, Vindicate::new),
    VITALITY_LIQUOR(VitalityLiquor.class, VitalityLiquor::new),
    VITALITY_CONCOCTION(VitalityConcoction.class, VitalityConcoction::new),
    WATER_BOLT(WaterBolt.class, WaterBolt::new),
    WATER_BREATH(WaterBreath.class, WaterBreath::new),
    WINDFURY_WEAPON(WindfuryWeapon.class, WindfuryWeapon::new),
    WONDER_TRAP(WonderTrap.class, WonderTrap::new),
    WOUNDING_STRIKE_BERSERKER(WoundingStrikeBerserker.class, WoundingStrikeBerserker::new),
    WOUNDING_STRIKE_DEFENDER(WoundingStrikeDefender.class, WoundingStrikeDefender::new),

    ;

    public static final Ability[] VALUES = values();
    public static final Map<Specializations, Ability[]> SPEC_ABILITIES = new HashMap<>() {{
        for (Specializations spec : Specializations.VALUES) {
            Ability[] abilities = new Ability[5];
            List<AbstractAbility> abstractAbilities = spec.create.get().getAbilities();
            for (int i = 0; i < abstractAbilities.size(); i++) {
                AbstractAbility ability = abstractAbilities.get(i);
                Ability abilityRegistry = getAbility(ability.getClass());
                if (abilityRegistry == null) {
                    ChatUtils.MessageType.WARLORDS.sendErrorMessage("Unknown ability for " + spec.name() + ": " + ability.getClass().getSimpleName());
                    continue;
                }
                abilities[i] = abilityRegistry;
            }
            put(spec, abilities);
        }
    }};

    @Nullable
    public static Ability getAbility(Class<?> clazz) {
        for (Ability ability : VALUES) {
            if (ability.clazz == clazz) {
                return ability;
            }
        }
        return null;
    }

    public final Class<?> clazz;
    public final Supplier<AbstractAbility> create;

    Ability(Class<?> clazz, Supplier<AbstractAbility> create) {
        this.clazz = clazz;
        this.create = create;
    }
}

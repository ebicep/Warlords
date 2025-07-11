package com.ebicep.warlords.player.general.specboosts;

import com.ebicep.warlords.abilities.internal.AbilityDescriptionBuilder;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.specboosts.boosts.*;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.util.*;

public class SpecBoostManager {

    public static final SpecBoost<AbyssalGrasp> ABYSSAL_GRASP = new AbyssalGrasp();
    public static final SpecBoost<AcceleratedSpike> ACCELERATED_SPIKE = new AcceleratedSpike();
    public static final SpecBoost<AirStrike> AIR_STRIKE = new AirStrike();
    public static final SpecBoost<AlchemistsFury> ALCHEMISTS_FURY = new AlchemistsFury();
    public static final SpecBoost<ArcaneRecluse> ARCANE_RECLUSE = new ArcaneRecluse();
    public static final SpecBoost<ArcaneReflection> ARCANE_REFLECTION = new ArcaneReflection();
    public static final SpecBoost<ArcaneShatter> ARCANE_SHATTER = new ArcaneShatter();
    public static final SpecBoost<ArmOfTheAlmighty> ARM_OF_THE_ALMIGHTY = new ArmOfTheAlmighty();
    public static final SpecBoost<AugmentedChains> AUGMENTED_CHAINS = new AugmentedChains();
    public static final SpecBoost<AuraOfRestoration> AURA_OF_RESTORATION = new AuraOfRestoration();
    public static final SpecBoost<BigGuy> BIG_GUY = new BigGuy();
    public static final SpecBoost<Blink> BLINK = new Blink();
    public static final SpecBoost<BlizzardBreath> BLIZZARD_BREATH = new BlizzardBreath();
    public static final SpecBoost<BloodFrenzy> BLOOD_FRENZY = new BloodFrenzy();
    public static final SpecBoost<RighteousRampage> RIGHTEOUS_RAMPAGE = new RighteousRampage();
    public static final SpecBoost<RuinousHex> RUINOUS_HEX = new RuinousHex();
    public static final SpecBoost<BurstChain> BURST_CHAIN = new BurstChain();
    public static final SpecBoost<ChillyAura> CHILLY_AURA = new ChillyAura();
    public static final SpecBoost<Clairvoyance> CLAIRVOYANCE = new Clairvoyance();
    public static final SpecBoost<Conduit> CONDUIT = new Conduit();
    public static final SpecBoost<ConsecratedBeacon> CONSECRATED_BEACON = new ConsecratedBeacon();
    public static final SpecBoost<Contagion> CONTAGION = new Contagion();
    public static final SpecBoost<CrusadersMight> CRUSADERS_MIGHT = new CrusadersMight();
    public static final SpecBoost<DetonationCatalyst> DETONATION_CATALYST = new DetonationCatalyst();
    public static final SpecBoost<DimensionalWarp> DIMENSIONAL_WARP = new DimensionalWarp();
    public static final SpecBoost<DivineEffulgence> DIVINE_EFFULGENCE = new DivineEffulgence();
    public static final SpecBoost<DivinePurification> DIVINE_PURIFICATION = new DivinePurification();
    public static final SpecBoost<DivineShields> DIVINE_SHIELDS = new DivineShields();
    public static final SpecBoost<EarthboundInfusion> EARTHBOUND_INFUSION = new EarthboundInfusion();
    public static final SpecBoost<EcoDrive> ECO_DRIVE = new EcoDrive();
    public static final SpecBoost<EfficientStrikes> EFFICIENT_STRIKES = new EfficientStrikes();
    public static final SpecBoost<ElectromagneticChains> ELECTROMAGNETIC_CHAINS = new ElectromagneticChains();
    public static final SpecBoost<EnergyOversurge> ENERGY_OVERSURGE = new EnergyOversurge();
    public static final SpecBoost<EyeOfTheStorm> EYE_OF_THE_STORM = new EyeOfTheStorm();
    public static final SpecBoost<FarmerBrown> FARMER_BROWN = new FarmerBrown();
    public static final SpecBoost<FerventForce> FERVENT_FORCE = new FerventForce();
    public static final SpecBoost<FlameBreath> FLAME_BREATH = new FlameBreath();
    public static final SpecBoost<FortifiedAegis> FORTIFIED_AEGIS = new FortifiedAegis();
    public static final SpecBoost<FrostMissile> FROST_MISSILE = new FrostMissile();
    public static final SpecBoost<GalvanizedSpark> GALVANIZED_SPARK = new GalvanizedSpark();
    public static final SpecBoost<Goliath> GOLIATH = new Goliath();
    public static final SpecBoost<HammerOfJudgement> HAMMER_OF_JUDGEMENT = new HammerOfJudgement();
    public static final SpecBoost<Haze> HAZE = new Haze();
    public static final SpecBoost<HealingLink> HEALING_LINK = new HealingLink();
    public static final SpecBoost<HeroicIntervention> HEROIC_INTERVENTION = new HeroicIntervention();
    public static final SpecBoost<HolyNova> HOLY_NOVA = new HolyNova();
    public static final SpecBoost<HouseOfLife> HOUSE_OF_LIFE = new HouseOfLife();
    public static final SpecBoost<IceBlock> ICE_BLOCK = new IceBlock();
    public static final SpecBoost<LoneSentinel> LONE_SENTINEL = new LoneSentinel();
    public static final SpecBoost<LustrousCrown> LUSTROUS_CROWN = new LustrousCrown();
    public static final SpecBoost<MarkedForDeath> MARKED_FOR_DEATH = new MarkedForDeath();
    public static final SpecBoost<MegalithicBoulder> MEGALITHIC_BOULDER = new MegalithicBoulder();
    public static final SpecBoost<Meteor> METEOR = new Meteor();
    public static final SpecBoost<MightyFists> MIGHTY_FISTS = new MightyFists();
    public static final SpecBoost<OneManArmy> ONE_MAN_ARMY = new OneManArmy();
    public static final SpecBoost<PactOfProtection> PACT_OF_PROTECTION = new PactOfProtection();
    public static final SpecBoost<Parry> PARRY = new Parry();
    public static final SpecBoost<PenitentResolve> PENITENT_RESOLVE = new PenitentResolve();
    public static final SpecBoost<PermeatingLink> PERMEATING_LINK = new PermeatingLink();
    public static final SpecBoost<RadiantLight> RADIANT_LIGHT = new RadiantLight();
    public static final SpecBoost<RallyingPresence> RALLYING_PRESENCE = new RallyingPresence();
    public static final SpecBoost<RecklessAscent> RECKLESS_ASCENT = new RecklessAscent();
    public static final SpecBoost<RiftAmbush> RIFT_AMBUSH = new RiftAmbush();
    public static final SpecBoost<SanctionBurst> SANCTION_BURST = new SanctionBurst();
    public static final SpecBoost<SanctuaryOfRetribution> SANCTUARY_OF_RETRIBUTION = new SanctuaryOfRetribution();
    public static final SpecBoost<SeismicShift> SEISMIC_SHIFT = new SeismicShift();
    public static final SpecBoost<SharpFangs> SHARP_FANGS = new SharpFangs();
    public static final SpecBoost<Solitary> SOLITARY = new Solitary();
    public static final SpecBoost<SoulRend> SOUL_REND = new SoulRend();
    public static final SpecBoost<SovereignSolitude> SOVEREIGN_SOLITUDE = new SovereignSolitude();
    public static final SpecBoost<StackulatorMax> STACKULATOR_MAX = new StackulatorMax();
    public static final SpecBoost<SteadfastWarp> STEADFAST_WARP = new SteadfastWarp();
    public static final SpecBoost<Striker> STRIKER = new Striker();
    public static final SpecBoost<SuicideInferno> SUICIDE_INFERNO = new SuicideInferno();
    public static final SpecBoost<SuperBrew> SUPER_BREW = new SuperBrew();
    public static final SpecBoost<SustainedOnslaught> SUSTAINED_ONSLAUGHT = new SustainedOnslaught();
    public static final SpecBoost<SwiftJustice> SWIFT_JUSTICE = new SwiftJustice();
    public static final SpecBoost<SymphonicWindfury> SYMPHONIC_WINDFURY = new SymphonicWindfury();
    public static final SpecBoost<TorrentialSoul> TORRENTIAL_SOUL = new TorrentialSoul();
    public static final SpecBoost<TotemicBoon> TOTEMIC_BOON = new TotemicBoon();
    public static final SpecBoost<Transistor> TRANSISTOR = new Transistor();
    public static final SpecBoost<Trickster> TRICKSTER = new Trickster();
    public static final SpecBoost<TyphoonBolt> TYPHOON_BOLT = new TyphoonBolt();
    public static final SpecBoost<UndyingSteed> UNDYING_STEED = new UndyingSteed();
    public static final SpecBoost<UnmercifulHex> UNMERCIFUL_HEX = new UnmercifulHex();
    public static final SpecBoost<UnstoppableSurge> UNSTOPPABLE_SURGE = new UnstoppableSurge();
    public static final SpecBoost<VibrantOrbs> VIBRANT_ORBS = new VibrantOrbs();
    public static final SpecBoost<VigorousInfusion> VIGOROUS_INFUSION = new VigorousInfusion();
    public static final SpecBoost<VitalityBoost> VITALITY_BOOST = new VitalityBoost();
    public static final SpecBoost<VitalPulse> VITAL_PULSE = new VitalPulse();
    public static final SpecBoost<WardingWrath> WARDING_WRATH = new WardingWrath();
    public static final SpecBoost<WitheringPlague> WITHERING_PLAGUE = new WitheringPlague();


    private static final Map<Specializations, List<SpecBoost<?>>> SPEC_BOOSTS = new HashMap<>();

    static {
        SPEC_BOOSTS.put(Specializations.PYROMANCER, List.of(METEOR, FLAME_BREATH, BURST_CHAIN, DIMENSIONAL_WARP, SUICIDE_INFERNO));
        SPEC_BOOSTS.put(Specializations.CRYOMANCER, List.of(FROST_MISSILE, BLIZZARD_BREATH, STEADFAST_WARP, CHILLY_AURA, ICE_BLOCK));
        SPEC_BOOSTS.put(Specializations.AQUAMANCER, List.of(TYPHOON_BOLT, DIVINE_PURIFICATION, CLAIRVOYANCE, FORTIFIED_AEGIS, ARCANE_REFLECTION));
        SPEC_BOOSTS.put(Specializations.BERSERKER, List.of(EFFICIENT_STRIKES, MIGHTY_FISTS, SEISMIC_SHIFT, BLOOD_FRENZY, GOLIATH));
        SPEC_BOOSTS.put(Specializations.DEFENDER, List.of(STRIKER, FERVENT_FORCE, HEROIC_INTERVENTION, VITALITY_BOOST, SOLITARY));
        SPEC_BOOSTS.put(Specializations.REVENANT, List.of(VIBRANT_ORBS, RECKLESS_ASCENT, HEALING_LINK, ONE_MAN_ARMY, UNDYING_STEED));
        SPEC_BOOSTS.put(Specializations.AVENGER, List.of(CONDUIT, ARM_OF_THE_ALMIGHTY, UNSTOPPABLE_SURGE, MARKED_FOR_DEATH, WARDING_WRATH));
        SPEC_BOOSTS.put(Specializations.CRUSADER, List.of(CRUSADERS_MIGHT, PARRY, VIGOROUS_INFUSION, SOVEREIGN_SOLITUDE, RALLYING_PRESENCE));
        SPEC_BOOSTS.put(Specializations.PROTECTOR, List.of(DIVINE_EFFULGENCE, BIG_GUY, ECO_DRIVE, LUSTROUS_CROWN, HAMMER_OF_JUDGEMENT));
        SPEC_BOOSTS.put(Specializations.THUNDERLORD, List.of(TRANSISTOR, ELECTROMAGNETIC_CHAINS, SYMPHONIC_WINDFURY, GALVANIZED_SPARK, EYE_OF_THE_STORM));
        SPEC_BOOSTS.put(Specializations.SPIRITGUARD, List.of(SOUL_REND, PERMEATING_LINK, FARMER_BROWN, PENITENT_RESOLVE));
        SPEC_BOOSTS.put(Specializations.EARTHWARDEN, List.of(ACCELERATED_SPIKE, MEGALITHIC_BOULDER, EARTHBOUND_INFUSION, AUGMENTED_CHAINS, TOTEMIC_BOON));
        SPEC_BOOSTS.put(Specializations.ASSASSIN, List.of(TRICKSTER, BLINK, RIFT_AMBUSH, TORRENTIAL_SOUL, HAZE));
        SPEC_BOOSTS.put(Specializations.VINDICATOR, List.of(VITAL_PULSE, ABYSSAL_GRASP, SANCTION_BURST, RIGHTEOUS_RAMPAGE, SWIFT_JUSTICE));
        SPEC_BOOSTS.put(Specializations.APOTHECARY, List.of(AURA_OF_RESTORATION, ALCHEMISTS_FURY, SUSTAINED_ONSLAUGHT, DETONATION_CATALYST, SUPER_BREW));
        SPEC_BOOSTS.put(Specializations.CONJURER, List.of(STACKULATOR_MAX, HOUSE_OF_LIFE, CONTAGION, WITHERING_PLAGUE, AIR_STRIKE));
        SPEC_BOOSTS.put(Specializations.SENTINEL, List.of(DIVINE_SHIELDS, RUINOUS_HEX, LONE_SENTINEL, PACT_OF_PROTECTION, SANCTUARY_OF_RETRIBUTION));
        SPEC_BOOSTS.put(Specializations.LUMINARY, List.of(RADIANT_LIGHT, UNMERCIFUL_HEX, ENERGY_OVERSURGE, CONSECRATED_BEACON, HOLY_NOVA));
    }

    public static List<SpecBoost<?>> getSpecBoosts(Specializations specializations) {
        return SPEC_BOOSTS.getOrDefault(specializations, new ArrayList<>());
    }

    public static void init() {
        SPEC_BOOSTS.values().stream().flatMap(List::stream).forEach(SpecBoost::init);
    }

    public interface SpecBoost<S extends SpecBoost<S>> {

        List<String> NAMESPACES = List.of("pvp");

        void init();

        default boolean isDisabled() {
            return getValue("disabled", boolean.class, true);
        }

        default <T> T getValue(String fieldName, Class<T> clazz, boolean optionalField) {
            return ConfigManager.getSpecBoostConfigValue(NAMESPACES, getConfigFieldName() + "." + fieldName, clazz, optionalField);
        }

        String getConfigFieldName();

        default TextComponent getName() {
            return Component.text(getStringName(), NamedTextColor.GREEN);
        }

        default String getStringName() {
            return getValue("name", String.class);
        }

        default <T> T getValue(String fieldName, Class<T> clazz) {
            return ConfigManager.getSpecBoostConfigValue(NAMESPACES, getConfigFieldName() + "." + fieldName, clazz);
        }

        default <T> List<T> getListValue(String fieldName, Class<T> clazz) {
            return ConfigManager.getSpecBoostConfigListValue(NAMESPACES, getConfigFieldName() + "." + fieldName, clazz);
        }

        default TextComponent getDifficulty() {
            return Component.text("☆".repeat(getValue("difficulty", int.class, true)), NamedTextColor.YELLOW);
        }

        default List<Component> getDescriptionLore() {
            return WordWrap.wrap(getDescription(), getMaxDescriptionWidth());
        }

        default TextComponent getDescription() {
            return getTextDescription();
        }

        default int getMaxDescriptionWidth() {
            return 150;
        }

        default TextComponent getTextDescription() {
            try {
                Queue<Object> variables = new LinkedList<>(getVariables());
                String descriptionFormat = ConfigManager.getSpecBoostConfigValue(NAMESPACES, getConfigFieldName() + ".description", String.class);
                AbilityDescriptionBuilder abilityDescriptionBuilder = AbilityDescriptionBuilder.create("", NamedTextColor.GRAY);
                for (int i = 0; i < descriptionFormat.length(); i++) {
                    int nextCustomIndex = descriptionFormat.indexOf("{{");
                    if (nextCustomIndex == -1) {
                        abilityDescriptionBuilder.text(descriptionFormat);
                        break;
                    }
                    if (nextCustomIndex != 0) {
                        String text = descriptionFormat.substring(0, nextCustomIndex);
                        abilityDescriptionBuilder.text(text);
                        descriptionFormat = descriptionFormat.substring(nextCustomIndex);
                    } else {
                        int endIndex = descriptionFormat.indexOf("}}");
                        String customValue = descriptionFormat.substring(2, endIndex);
                        int prefixIndex = customValue.indexOf(";");
                        String prefix;
                        if (prefixIndex == -1) {
                            prefix = "";
                        } else {
                            prefix = customValue.substring(prefixIndex + 1);
                            customValue = customValue.substring(0, prefixIndex);
                        }
                        if (customValue.contains(":")) {
                            String type = customValue.substring(0, customValue.indexOf(":"));
                            String value = customValue.substring(customValue.indexOf(":") + 1);
                            // {{type:value;prefix}}
                            abilityDescriptionBuilder.autoFormat(type, prefix, value.isEmpty() ? variables.poll() : value);
                        } else {
                            abilityDescriptionBuilder.autoFormat(customValue, prefix, variables.poll());
                        }
                        descriptionFormat = descriptionFormat.substring(endIndex + 2);
                    }
                    i--;
                }
                return abilityDescriptionBuilder.build();
            } catch (Exception e) {
                ChatUtils.MessageType.CONFIG.sendErrorMessage(e);
                return Component.text("ERROR", NamedTextColor.RED);
            }
        }

        List<Object> getVariables();

        default TextComponent appendAbility(TextComponent component, AbstractAbility ability) {
            ability.init(ability.getBuilder());
            ability.updateDescription(null);
            return component
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text(ability.getName(), NamedTextColor.AQUA))
                    .appendNewline()
                    .append(ability.getItemHeader().stream().collect(Component.toComponent(Component.newline())))
                    .appendNewline()
                    .appendNewline()
                    .append(ability.getDescription());
        }

        Boost create();

        S get();

        default String getDatabaseName() {
            Field[] fields = SpecBoostManager.class.getDeclaredFields();
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

    public interface Boost extends Listener {

        void apply(WarlordsPlayer warlordsPlayer);

        default void unapply(WarlordsPlayer warlordsPlayer) {

        }

    }

}

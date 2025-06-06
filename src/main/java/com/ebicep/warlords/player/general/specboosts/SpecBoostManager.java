package com.ebicep.warlords.player.general.specboosts;

import com.ebicep.warlords.abilities.internal.AbilityDescriptionBuilder;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.specboosts.boosts.*;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.Listener;

import java.util.*;

public class SpecBoostManager {

    public static final SpecBoost<AcceleratedSpike> ACCELERATED_SPIKE = new AcceleratedSpike();
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
    public static final SpecBoost<BurstChain> BURST_CHAIN = new BurstChain();
    public static final SpecBoost<ChillyAura> CHILLY_AURA = new ChillyAura();
    public static final SpecBoost<Clairvoyance> CLAIRVOYANCE = new Clairvoyance();
    public static final SpecBoost<Conduit> CONDUIT = new Conduit();
    public static final SpecBoost<CrusadersMight> CRUSADERS_MIGHT = new CrusadersMight();
    public static final SpecBoost<DetonationCatalyst> DETONATION_CATALYST = new DetonationCatalyst();
    public static final SpecBoost<DimensionalWarp> DIMENSIONAL_WARP = new DimensionalWarp();
    public static final SpecBoost<DivineEffulgence> DIVINE_EFFULGENCE = new DivineEffulgence();
    public static final SpecBoost<DivinePurification> DIVINE_PURIFICATION = new DivinePurification();
    public static final SpecBoost<EarthboundInfusion> EARTHBOUND_INFUSION = new EarthboundInfusion();
    public static final SpecBoost<EfficientStrikes> EFFICIENT_STRIKES = new EfficientStrikes();
    public static final SpecBoost<ElectromagneticChains> ELECTROMAGNETIC_CHAINS = new ElectromagneticChains();
    public static final SpecBoost<EyeOfTheStorm> EYE_OF_THE_STORM = new EyeOfTheStorm();
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
    public static final SpecBoost<LightSpeedInfusion> LIGHT_SPEED_INFUSION = new LightSpeedInfusion();
    public static final SpecBoost<LustrousCrown> LUSTROUS_CROWN = new LustrousCrown();
    public static final SpecBoost<MarkedForDeath> MARKED_FOR_DEATH = new MarkedForDeath();
    public static final SpecBoost<MegalithicBoulder> MEGALITHIC_BOULDER = new MegalithicBoulder();
    public static final SpecBoost<Meteor> METEOR = new Meteor();
    public static final SpecBoost<MightyFists> MIGHTY_FISTS = new MightyFists();
    public static final SpecBoost<OneManArmy> ONE_MAN_ARMY = new OneManArmy();
    public static final SpecBoost<Parry> PARRY = new Parry();
    public static final SpecBoost<PenitentResolve> PENITENT_RESOLVE = new PenitentResolve();
    public static final SpecBoost<PermeatingLink> PERMEATING_LINK = new PermeatingLink();
    public static final SpecBoost<RallyingPresence> RALLYING_PRESENCE = new RallyingPresence();
    public static final SpecBoost<RecklessAscent> RECKLESS_ASCENT = new RecklessAscent();
    public static final SpecBoost<RiftAmbush> RIFT_AMBUSH = new RiftAmbush();
    public static final SpecBoost<SeismicShift> SEISMIC_SHIFT = new SeismicShift();
    public static final SpecBoost<Solitary> SOLITARY = new Solitary();
    public static final SpecBoost<SoulRend> SOUL_REND = new SoulRend();
    public static final SpecBoost<SovereignSolitude> SOVEREIGN_SOLITUDE = new SovereignSolitude();
    public static final SpecBoost<SteadfastWarp> STEADFAST_WARP = new SteadfastWarp();
    public static final SpecBoost<Striker> STRIKER = new Striker();
    public static final SpecBoost<SuperBrew> SUPER_BREW = new SuperBrew();
    public static final SpecBoost<SustainedOnslaught> SUSTAINED_ONSLAUGHT = new SustainedOnslaught();
    public static final SpecBoost<SymphonicWindfury> SYMPHONIC_WINDFURY = new SymphonicWindfury();
    public static final SpecBoost<TorrentialSoul> TORRENTIAL_SOUL = new TorrentialSoul();
    public static final SpecBoost<TotemicBoon> TOTEMIC_BOON = new TotemicBoon();
    public static final SpecBoost<Transistor> TRANSISTOR = new Transistor();
    public static final SpecBoost<Trickster> TRICKSTER = new Trickster();
    public static final SpecBoost<TyphoonBolt> TYPHOON_BOLT = new TyphoonBolt();
    public static final SpecBoost<UndyingSteed> UNDYING_STEED = new UndyingSteed();
    public static final SpecBoost<UnstoppableSurge> UNSTOPPABLE_SURGE = new UnstoppableSurge();
    public static final SpecBoost<VibrantOrbs> VIBRANT_ORBS = new VibrantOrbs();
    public static final SpecBoost<VigorousInfusion> VIGOROUS_INFUSION = new VigorousInfusion();
    public static final SpecBoost<VitalityBoost> VITALITY_BOOST = new VitalityBoost();
    public static final SpecBoost<WardingWrath> WARDING_WRATH = new WardingWrath();


    private static final Map<Specializations, List<SpecBoost<?>>> SPEC_BOOSTS = new HashMap<>();

    static {
        SPEC_BOOSTS.put(Specializations.PYROMANCER, List.of(METEOR, FLAME_BREATH, BURST_CHAIN, DIMENSIONAL_WARP, ARCANE_SHATTER));
        SPEC_BOOSTS.put(Specializations.CRYOMANCER, List.of(FROST_MISSILE, BLIZZARD_BREATH, STEADFAST_WARP, ARCANE_RECLUSE, CHILLY_AURA));
        SPEC_BOOSTS.put(Specializations.AQUAMANCER, List.of(TYPHOON_BOLT, DIVINE_PURIFICATION, CLAIRVOYANCE, FORTIFIED_AEGIS, ARCANE_REFLECTION));
        SPEC_BOOSTS.put(Specializations.BERSERKER, List.of(EFFICIENT_STRIKES, MIGHTY_FISTS, SEISMIC_SHIFT, BLOOD_FRENZY, GOLIATH));
        SPEC_BOOSTS.put(Specializations.DEFENDER, List.of(STRIKER, FERVENT_FORCE, HEROIC_INTERVENTION, VITALITY_BOOST, SOLITARY));
        SPEC_BOOSTS.put(Specializations.REVENANT, List.of(VIBRANT_ORBS, RECKLESS_ASCENT, HEALING_LINK, ONE_MAN_ARMY, UNDYING_STEED));
        SPEC_BOOSTS.put(Specializations.AVENGER, List.of(CONDUIT, ARM_OF_THE_ALMIGHTY, UNSTOPPABLE_SURGE, MARKED_FOR_DEATH, WARDING_WRATH));
        SPEC_BOOSTS.put(Specializations.CRUSADER, List.of(CRUSADERS_MIGHT, PARRY, VIGOROUS_INFUSION, SOVEREIGN_SOLITUDE, RALLYING_PRESENCE));
        SPEC_BOOSTS.put(Specializations.PROTECTOR, List.of(BIG_GUY, LIGHT_SPEED_INFUSION, DIVINE_EFFULGENCE, LUSTROUS_CROWN, HAMMER_OF_JUDGEMENT));
        SPEC_BOOSTS.put(Specializations.THUNDERLORD, List.of(TRANSISTOR, ELECTROMAGNETIC_CHAINS, SYMPHONIC_WINDFURY, GALVANIZED_SPARK, EYE_OF_THE_STORM));
        SPEC_BOOSTS.put(Specializations.SPIRITGUARD, List.of(SOUL_REND, PERMEATING_LINK, PENITENT_RESOLVE));
        SPEC_BOOSTS.put(Specializations.EARTHWARDEN, List.of(ACCELERATED_SPIKE, MEGALITHIC_BOULDER, EARTHBOUND_INFUSION, AUGMENTED_CHAINS, TOTEMIC_BOON));
        SPEC_BOOSTS.put(Specializations.ASSASSIN, List.of(TRICKSTER, BLINK, RIFT_AMBUSH, TORRENTIAL_SOUL, HAZE));
        SPEC_BOOSTS.put(Specializations.APOTHECARY, List.of(AURA_OF_RESTORATION, ALCHEMISTS_FURY, SUSTAINED_ONSLAUGHT, DETONATION_CATALYST, SUPER_BREW));
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

        default TextComponent getName() {
            return Component.text(getStringName(), NamedTextColor.GREEN);
        }

        default String getStringName() {
            return getValue("name", String.class);
        }

        default <T> T getValue(String fieldName, Class<T> clazz) {
            return getValue(NAMESPACES, fieldName, clazz);
        }

        default <T> T getValue(List<String> namespaces, String fieldName, Class<T> clazz) {
            return ConfigManager.getSpecBoostConfigValue(namespaces, getConfigFieldName() + "." + fieldName, clazz);
        }

        String getConfigFieldName();

        default List<Component> getDescriptionLore() {
            return WordWrap.wrap(getDescription(), getMaxDescriptionWidth());
        }

        default TextComponent getDescription() {
            return getTextDescription();
        }

        default TextComponent getTextDescription() {
            Queue<Object> variables = new LinkedList<>(getVariables());
            String descriptionFormat = ConfigManager.getSpecBoostConfigValue(NAMESPACES, getConfigFieldName() + ".description", String.class);
            AbilityDescriptionBuilder abilityDescriptionBuilder = AbilityDescriptionBuilder.create("", NamedTextColor.GRAY);
            for (int i = 0; i < descriptionFormat.length(); i++) {
                int nextCustomIndex = descriptionFormat.indexOf("{{");
                if (nextCustomIndex == -1) {
                    abilityDescriptionBuilder.text(descriptionFormat);
                    break;
                }
                if (nextCustomIndex != 0 || variables.isEmpty()) {
                    String text = descriptionFormat.substring(0, nextCustomIndex - 1);
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
        }

        default TextComponent getDescriptionWithAbility(AbstractAbility ability) {
            ability.init(ability.getBuilder());
            ability.updateDescription(null);
            return getTextDescription()
                    .appendNewline()
                    .appendNewline()
                    .append(ability.getItemHeader().stream().collect(Component.toComponent(Component.newline())))
                    .appendNewline()
                    .appendNewline()
                    .append(ability.getDescription());
        }

        List<Object> getVariables();

        default int getMaxDescriptionWidth() {
            return 150;
        }

        Boost create();

        S get();

    }

    public interface Boost extends Listener {

        void apply(WarlordsPlayer warlordsPlayer);

        default void unapply(WarlordsPlayer warlordsPlayer) {

        }

    }

}

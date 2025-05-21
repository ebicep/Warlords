package com.ebicep.warlords.player.general.specboosts;

import com.ebicep.warlords.abilities.internal.AbilityDescriptionBuilder;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.specboosts.boosts.*;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.Listener;

import java.util.*;

public class SpecBoostManager {

    public static final SpecBoost<Meteor> METEOR = new Meteor();
    public static final SpecBoost<ArcaneShatter> ARCANE_SHATTER = new ArcaneShatter();
    public static final SpecBoost<DimensionalWarp> DIMENSIONAL_WARP = new DimensionalWarp();
    public static final SpecBoost<BurstChain> BURST_CHAIN = new BurstChain();
    public static final SpecBoost<FlameBreath> FLAME_BREATH = new FlameBreath();
    public static final SpecBoost<FrostMissile> FROST_MISSILE = new FrostMissile();
    public static final SpecBoost<ArcaneRecluse> ARCANE_RECLUSE = new ArcaneRecluse();
    public static final SpecBoost<ChillyAura> CHILLY_AURA = new ChillyAura();
    public static final SpecBoost<BlizzardBreath> BLIZZARD_BREATH = new BlizzardBreath();
    public static final SpecBoost<SteadfastWarp> STEADFAST_WARP = new SteadfastWarp();
    public static final SpecBoost<FortifiedAegis> FORTIFIED_AEGIS = new FortifiedAegis();
    public static final SpecBoost<TyphoonBolt> TYPHOON_BOLT = new TyphoonBolt();
    public static final SpecBoost<Clairvoyance> CLAIRVOYANCE = new Clairvoyance();
    public static final SpecBoost<ArcaneReflection> ARCANE_REFLECTION = new ArcaneReflection();
    public static final SpecBoost<BerserkersFury> BERSERKERS_FURY = new BerserkersFury();
    public static final SpecBoost<EfficientStrikes> EFFICIENT_STRIKES = new EfficientStrikes();
    public static final SpecBoost<MightyFists> MIGHTY_FISTS = new MightyFists();
    public static final SpecBoost<BloodFrenzy> BLOOD_FRENZY = new BloodFrenzy();
    public static final SpecBoost<SeismicShift> SEISMIC_SHIFT = new SeismicShift();
    public static final SpecBoost<VigorousInfusion> VIGOROUS_INFUSION = new VigorousInfusion();
    public static final SpecBoost<EyeOfTheStorm> EYE_OF_THE_STORM = new EyeOfTheStorm();
    private static final Map<Specializations, List<SpecBoost<?>>> SPEC_BOOSTS = new HashMap<>();

    static {
        SPEC_BOOSTS.put(Specializations.PYROMANCER, List.of(METEOR, ARCANE_SHATTER, DIMENSIONAL_WARP, BURST_CHAIN, FLAME_BREATH));
        SPEC_BOOSTS.put(Specializations.CRYOMANCER, List.of(FROST_MISSILE, ARCANE_RECLUSE, CHILLY_AURA, BLIZZARD_BREATH, STEADFAST_WARP));
        SPEC_BOOSTS.put(Specializations.AQUAMANCER, List.of(TYPHOON_BOLT, FORTIFIED_AEGIS, CLAIRVOYANCE, ARCANE_REFLECTION));
        SPEC_BOOSTS.put(Specializations.BERSERKER, List.of(BERSERKERS_FURY, EFFICIENT_STRIKES, MIGHTY_FISTS, BLOOD_FRENZY, SEISMIC_SHIFT));
        SPEC_BOOSTS.put(Specializations.CRUSADER, List.of(VIGOROUS_INFUSION));
        SPEC_BOOSTS.put(Specializations.THUNDERLORD, List.of(EYE_OF_THE_STORM));
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

        default TextComponent getDescription() {
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

        List<Object> getVariables();

        Boost create();

        S get();

    }

    public interface Boost extends Listener {

        void apply(WarlordsPlayer warlordsPlayer);

        void unapply(WarlordsPlayer warlordsPlayer);

    }

}

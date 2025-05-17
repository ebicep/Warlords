package com.ebicep.warlords.player.general.specboosts;

import com.ebicep.warlords.abilities.internal.AbilityDescriptionBuilder;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.specboosts.boosts.Meteor;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.Listener;

import java.util.*;

public class SpecBoostManager {

    private static final Map<Specializations, List<SpecBoost>> SPEC_BOOSTS = new HashMap<>();

    static {
        SPEC_BOOSTS.put(Specializations.PYROMANCER, List.of(new Meteor()));
    }

    public static List<SpecBoost> getSpecBoosts(Specializations specializations) {
        return SPEC_BOOSTS.getOrDefault(specializations, new ArrayList<>());
    }

    public static void init() {
        SPEC_BOOSTS.values().stream().flatMap(List::stream).forEach(SpecBoost::init);
    }

    public interface SpecBoost {

        List<String> NAMESPACES = List.of("pvp");

        void init();

        default TextComponent getName() {
            String name = getValue("name", String.class);
            return Component.text(name, NamedTextColor.GREEN);
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
                if (nextCustomIndex != 1 && nextCustomIndex != 0 || variables.isEmpty()) {
                    String text = descriptionFormat.substring(0, nextCustomIndex - 1);
                    abilityDescriptionBuilder.text(text);
                    descriptionFormat = descriptionFormat.substring(nextCustomIndex);
                } else {
                    int endIndex = descriptionFormat.indexOf("}}");
                    String customValue = descriptionFormat.substring(2, endIndex);
                    if (customValue.contains(":")) {
                        String type = customValue.substring(0, customValue.indexOf(":"));
                        String value = customValue.substring(customValue.indexOf(":") + 1);
                        abilityDescriptionBuilder.autoFormat(type, value);
                    } else {
                        abilityDescriptionBuilder.autoFormat(customValue, variables.poll());
                    }
                    descriptionFormat = descriptionFormat.substring(endIndex + 2);
                }
            }
            return abilityDescriptionBuilder.build();
        }

        List<Object> getVariables();

        Boost create();

    }

    public interface Boost extends Listener {

        void apply(WarlordsPlayer warlordsPlayer);

        void unapply(WarlordsPlayer warlordsPlayer);

    }

}

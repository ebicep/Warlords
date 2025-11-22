package com.ebicep.warlords.player.ingame.instances;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class InstanceManager {

    public static Consumer<FloatModifiable.FloatModifier> TARGET_LABEL = floatModifier -> floatModifier.setDebugPrefix(ComponentBuilder.create("(Target) ",
            NamedTextColor.DARK_GREEN
    ));
    public static Consumer<FloatModifiable.FloatModifier> SOURCE_LABEL = floatModifier -> floatModifier.setDebugPrefix(ComponentBuilder.create("(Source) ",
            NamedTextColor.DARK_RED
    ));

    public static Optional<WarlordsDamageHealingFinalEvent> addDamageHealingInstance(WarlordsDamageHealingEvent event) {
        if (event.getWarlordsEntity().isDead()) {
            return Optional.empty();
        }
        InstanceDebugHoverable debugHoverable = new InstanceDebugHoverable();
        List<TextComponent> debugMessages = event.getDebugMessages();
        if (!debugMessages.isEmpty()) {
            debugHoverable.appendTitle("Debug", NamedTextColor.AQUA);
            for (TextComponent debugMessage : debugMessages) {
                debugHoverable.append(InstanceDebugHoverable.LevelBuilder.create(1).value(debugMessage));
            }
        }
        if (event.getWarlordsEntity().getCurrentHealth() <= 0) {
            return Optional.empty();
        }
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return Optional.empty();
        }
        Optional<WarlordsDamageHealingFinalEvent> eventOptional;
        switch (event.getInstanceType()) {
            case HEALING -> eventOptional = HealingInstanceProcessor.addHealingInstance(debugHoverable, event);
            case DAMAGE -> eventOptional = DamageInstanceProcessor.addDamageInstance(debugHoverable, event);
            default -> eventOptional = Optional.empty();
        }
        eventOptional.ifPresent(warlordsDamageHealingFinalEvent -> Bukkit.getPluginManager().callEvent(warlordsDamageHealingFinalEvent));
        return eventOptional;
    }

}

package com.ebicep.warlords.player.ingame.instances;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public class InstanceDebugHoverable {

    private static final TextComponent GRAY_BAR = Component.text(" | ", NamedTextColor.GRAY);
    private static final TextComponent GRAY_DASH = Component.text(" - ", NamedTextColor.GRAY);
    private final TextComponent.Builder debugMessage = Component.text().color(NamedTextColor.GREEN);
    private int titles = 0;

    public void appendTitle(String title, NamedTextColor color) {
        appendTitle(Component.text(title + ": ", color));
    }

    public void appendTitle(Component component) {
        if (titles > 0) {
            debugMessage.append(Component.newline());
        }
        debugMessage.append(component);
        titles++;
    }

    public void appendEvent(WarlordsDamageHealingEvent event) {
        grayDash();
        namedValue("Target", event.getWarlordsEntity().getName());
        grayBar();
        namedValue("Source", event.getSource().getName());
        grayBar();
        namedValue("Ability", event.getCause());
        grayDash();
        namedValue("Min", event.getMin());
        grayBar();
        namedValue("Max", event.getMax());
        grayBar();
        namedValue("Crit Chance", event.getCritChance());
        grayBar();
        namedValue("Crit Multiplier", event.getCritMultiplier());
        grayDash();
        namedValue("Flags", "" + event.getFlags());
    }

    public void grayDash() {
        debugMessage.append(Component.newline()).append(GRAY_DASH);
    }

    public void namedValue(String title, String value) {
        if (value == null) {
            value = "ERROR";
        }
        debugMessage.append(Component.text(title + ": ", NamedTextColor.GREEN))
                    .append(Component.text(value, NamedTextColor.GOLD));
    }

    public void grayBar() {
        debugMessage.append(GRAY_BAR);
    }

    public void namedValue(String title, float value) {
        namedValue(title, NumberFormat.addCommaAndRoundHundredths(value));
    }

    public void cooldown(AbstractCooldown<?> cooldown) {

    }

    public void append(LevelBuilder levelBuilder) {
        debugMessage.append(Component.newline());
        debugMessage.append(levelBuilder.build());
    }

    public void append(Component component) {
        debugMessage.append(Component.newline());
        debugMessage.append(component);
    }

    public TextComponent.Builder getDebugMessage() {
        return debugMessage;
    }

    public static class LevelBuilder {

        public static LevelBuilder create(int level) {
            return new LevelBuilder(level);
        }

        private final int level;
        private final String frontSpacing;
        private TextComponent prefix = Component.empty();
        private TextComponent value = Component.empty();

        public LevelBuilder(int level) {
            this.level = level;
            this.frontSpacing = " ".repeat(Math.max(0, level == 1 ? 1 : level * 2));
        }

        public LevelBuilder prefix(ComponentBuilder componentBuilder) {
            this.prefix = componentBuilder.build();
            return this;
        }

        public LevelBuilder prefix(AbstractCooldown<?> cooldown) {
            ComponentBuilder builder = ComponentBuilder.create(cooldown.getName(), NamedTextColor.GREEN);
            Component cooldownDebugMessage = cooldown.getDebugMessage();
            if (cooldownDebugMessage != null) {
                builder.append(Component.textOfChildren(
                        Component.text(" (", NamedTextColor.DARK_GRAY),
                        cooldownDebugMessage,
                        Component.text(")", NamedTextColor.DARK_GRAY)
                ));
            }
            this.prefix = builder.build();
            return this;
        }

        public LevelBuilder value(ComponentBuilder componentBuilder) {
            this.value = componentBuilder.build();
            return this;
        }

        public LevelBuilder value(TextComponent component) {
            this.value = component;
            return this;
        }

        public LevelBuilder value(FloatModifiable floatModifiable) {
            ComponentBuilder builder = ComponentBuilder.create("", NamedTextColor.GOLD);
            List<Component> debugInfo = floatModifiable.getDebugInfo();
            TextComponent spacer = Component.text(frontSpacing + " ".repeat(Math.max(0, (level + 1) * 2)));
            for (Component component : debugInfo) {
                builder.append(Component.newline()).append(spacer).append(component);
            }
            this.value = builder.build();
            return this;
        }

        public TextComponent build() {
            return Component.textOfChildren(
                    Component.text(frontSpacing + " - ", NamedTextColor.GRAY),
                    prefix,
                    value
            );
        }

    }

}

package com.ebicep.warlords.player.ingame.instances;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.instances.type.DamageInstance;
import com.ebicep.warlords.player.ingame.instances.type.HealingInstance;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
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
        private TextComponent prefix = Component.empty();
        private TextComponent value = Component.empty();

        public LevelBuilder(int level) {
            this.level = level;
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

        public LevelBuilder value(float before, float after, AbstractCooldown<?> cooldown) {
            List<DamageInstance> extraDamageInstances = cooldown.getExtraDamageInstances();
            List<HealingInstance> extraHealingInstances = cooldown.getExtraHealingInstances();
            ComponentBuilder builder = ComponentBuilder
                    .create(NumberFormat.formatOptionalHundredths(after), NamedTextColor.GOLD)
                    .text(" (", NamedTextColor.DARK_GRAY)
                    .text(NumberFormat.formatOptionalHundredths(after / before) + "x", NamedTextColor.RED)
                    .text(")", NamedTextColor.DARK_GRAY)
                    .text(" (", NamedTextColor.DARK_GRAY)
                    .text(cooldown.getName(), NamedTextColor.GRAY)
                    .text(")", NamedTextColor.DARK_GRAY);
            if (extraDamageInstances != null) {
                builder.text(" (", NamedTextColor.DARK_GRAY)
                       .text(extraDamageInstances.size(), NamedTextColor.RED)
                       .text(")", NamedTextColor.DARK_GRAY);
            }
            if (extraHealingInstances != null) {
                builder.text(" (", NamedTextColor.DARK_GRAY)
                       .text(extraHealingInstances.size(), NamedTextColor.RED)
                       .text(")", NamedTextColor.DARK_GRAY);
            }
            this.value = builder.build();
            return this;
        }

        public TextComponent build() {
            return Component.textOfChildren(
                    Component.text(" ".repeat(Math.max(0, level == 1 ? 1 : level * 2)) + " - ", NamedTextColor.GRAY),
                    prefix,
                    value
            );
        }

    }

}

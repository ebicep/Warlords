package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class AbilityDescriptionBuilder {

    final public static TextColor COLOR_BROWN = TextColor.color(100, 66, 33);

    //TODO: auto space
    public static AbilityDescriptionBuilder create(String text) {
        return new AbilityDescriptionBuilder(ComponentBuilder.create(text));
    }

    public static AbilityDescriptionBuilder create(String text, TextColor textColor) {
        return new AbilityDescriptionBuilder(ComponentBuilder.create(text, textColor));
    }

    public static AbilityDescriptionBuilder create(TextComponent component) {
        return new AbilityDescriptionBuilder(ComponentBuilder.create(component));
    }

    private final ComponentBuilder parentBuilder;

    public AbilityDescriptionBuilder(ComponentBuilder parentBuilder) {
        this.parentBuilder = parentBuilder;
    }

    public AbilityDescriptionBuilder append(Component component) {
        parentBuilder.append(component);
        return this;
    }

    public AbilityDescriptionBuilder text(String text) {
        parentBuilder.append(Component.text(text));
        return this;
    }

    public AbilityDescriptionBuilder text(int text, TextColor textColor) {
        parentBuilder.append(Component.text(text, textColor));
        return this;
    }

    public AbilityDescriptionBuilder text(float text, TextColor textColor) {
        return text(NumberFormat.formatOptionalTenths(text), textColor);
    }

    public AbilityDescriptionBuilder text(String text, TextColor textColor) {
        parentBuilder.append(Component.text(text, textColor));
        return this;
    }

    public AbilityDescriptionBuilder text(FloatModifiable text, TextColor textColor) {
        return text(NumberFormat.formatOptionalTenths(text.getCalculatedValue()), textColor);
    }

    public AbilityDescriptionBuilder heal(Value.SetValue setValue, UnaryOperator<Float> modifier) {
        parentBuilder.append(Heals.formatHealingPercent(setValue, modifier));
        return this;
    }

    public AbilityDescriptionBuilder durationSeconds(int seconds) {
        return durationSeconds(seconds, "");
    }

    public AbilityDescriptionBuilder damageReduction(float value) {
        return damageReduction(value, null);
    }

    public AbilityDescriptionBuilder percent(int percent, TextColor color) {
        parentBuilder.text(percent + "%", color);
        return this;
    }

    public AbilityDescriptionBuilder percent(float percent, TextColor color) {
        parentBuilder.text(NumberFormat.formatOptionalTenths(percent) + "%", color);
        return this;
    }

    public AbilityDescriptionBuilder percent(FloatModifiable percent, TextColor color) {
        parentBuilder.text(NumberFormat.formatOptionalTenths(percent.getCalculatedValue()) + "%", color);
        return this;
    }

    public AbilityDescriptionBuilder damageReduction(float value, @Nullable String prefix) {
        parentBuilder.text(NumberFormat.formatOptionalTenths(value) + Objects.requireNonNullElse(prefix, "%"), COLOR_BROWN);
        return this;
    }

    public AbilityDescriptionBuilder damageReduction(int value) {
        return damageReduction(value, null);
    }

    public AbilityDescriptionBuilder damageReduction(int value, @Nullable String prefix) {
        parentBuilder.text(NumberFormat.formatOptionalTenths(value) + Objects.requireNonNullElse(prefix, "%"), COLOR_BROWN);
        return this;
    }

    public void autoFormat(String formatKey, String prefix, @Nonnull Object value) {
        switch (formatKey) {
            case "damage" -> {
                switch (value) {
                    case Value.RangedValue rangedValue -> damage(rangedValue);
                    case Value.SetValue setValue -> damage(setValue);
                    case Float floatValue -> text(NumberFormat.formatOptionalHundredths(floatValue) + prefix, NamedTextColor.RED);
                    case Integer intValue -> text(NumberFormat.formatOptionalHundredths(intValue) + prefix, NamedTextColor.RED);
                    default -> text(value + prefix, NamedTextColor.RED);
                }
            }
            case "heal" -> {
                switch (value) {
                    case Value.RangedValue rangedValue -> heal(rangedValue);
                    case Value.SetValue setValue -> heal(setValue);
                    case Float floatValue -> text(NumberFormat.formatOptionalHundredths(floatValue) + prefix, NamedTextColor.GREEN);
                    case Integer intValue -> text(NumberFormat.formatOptionalHundredths(intValue) + prefix, NamedTextColor.GREEN);
                    default -> text(value + prefix, NamedTextColor.GREEN);
                }
            }
            case "ticks" -> {
                switch (value) {
                    case Integer ticks -> durationTicks(ticks, prefix);
                    case Float seconds -> durationSeconds(seconds, prefix);
                    case String stringValue -> text(stringValue + prefix, NamedTextColor.GOLD);
                    default -> text(value + prefix, NamedTextColor.GOLD);
                }
            }
            case "energy" -> {
                switch (value) {
                    case Integer energy -> energy(energy, prefix);
                    case Float energy -> energy(energy, prefix);
                    default -> text(value + prefix, NamedTextColor.YELLOW);
                }
            }
            case "blocks" -> {
                switch (value) {
                    case Integer blocks -> blocks(blocks, prefix);
                    case Float blocks -> blocks(blocks, prefix);
                    default -> text(value + prefix, NamedTextColor.AQUA);
                }
            }
            case "speed" -> {
                switch (value) {
                    case Integer speed -> speed(speed, prefix);
                    case Float speed -> speed(speed, prefix);
                    default -> text(value + prefix, NamedTextColor.WHITE);
                }
            }
            case "damageReduction" -> {
                switch (value) {
                    case Integer speed -> damageReduction(speed, prefix);
                    case Float speed -> damageReduction(speed, prefix);
                    default -> text(value + prefix, COLOR_BROWN);
                }
            }
            case null -> text("Null formatKey", NamedTextColor.GRAY);
            default -> {
                NamedTextColor color = NamedTextColor.NAMES.value(formatKey);
                if (color != null) {
                    // check if value is int or float then format
                    if (value instanceof Integer intValue) {
                        parentBuilder.text(NumberFormat.formatOptionalTenths(intValue) + prefix, color);
                    } else if (value instanceof Float floatValue) {
                        parentBuilder.text(NumberFormat.formatOptionalTenths(floatValue) + prefix, color);
                    } else {
                        parentBuilder.text(value.toString(), color);
                    }
                } else {
                    // check if type is hex color then covert to text color
                    try {
                        int hex = Integer.parseInt(formatKey, 16);
                        parentBuilder.text(value.toString(), TextColor.fromHexString("#" + formatKey));
                    } catch (NumberFormatException e) {
                        parentBuilder.text(value.toString(), NamedTextColor.DARK_GRAY);
                    }
                }
            }
        }
    }

    public AbilityDescriptionBuilder optimalRange(int range) {
        return range(range, "Has an optimal range of ");
    }

    public AbilityDescriptionBuilder initialRange(float range) {
        return range(range, "Has an initial cast range of ");
    }

    public AbilityDescriptionBuilder emptyLine() {
        parentBuilder.newLine();
        parentBuilder.newLine();
        return this;
    }

    public AbilityDescriptionBuilder maxRange(int range) {
        return range(range, "Has a maximum range of ");
    }

    public AbilityDescriptionBuilder maxRange(double range) {
        return range(range, "Has a maximum range of ");
    }

    private AbilityDescriptionBuilder range(double range, String rangeText) {
        emptyLine();
        parentBuilder.text(rangeText);
        parentBuilder.text(NumberFormat.formatOptionalTenths(range), NamedTextColor.AQUA);
        parentBuilder.text(" blocks.");
        return this;
    }

    public AbilityDescriptionBuilder maxRange(FloatModifiable range) {
        return range(range, "Has a maximum range of ");
    }

    private AbilityDescriptionBuilder range(FloatModifiable range, String rangeText) {
        emptyLine();
        parentBuilder.text(rangeText);
        parentBuilder.text(NumberFormat.formatOptionalTenths(range.getCalculatedValue()), NamedTextColor.AQUA);
        parentBuilder.text(" blocks.");
        return this;
    }

    public AbilityDescriptionBuilder initialRange(int range) {
        return range(range, "Has an initial cast range of ");
    }

    private AbilityDescriptionBuilder range(float range, String rangeText) {
        emptyLine();
        parentBuilder.text(rangeText);
        parentBuilder.text(NumberFormat.formatOptionalTenths(range), NamedTextColor.AQUA);
        parentBuilder.text(" blocks.");
        return this;
    }

    public AbilityDescriptionBuilder initialRange(FloatModifiable range) {
        return range(range, "Has an initial cast range of ");
    }

    public AbilityDescriptionBuilder blocks(FloatModifiable blocks) {
        return blocks(blocks.getCalculatedValue(), "");
    }

    public AbilityDescriptionBuilder blocks(float blocks, String prefix) {
        if (prefix.isEmpty()) {
            parentBuilder.text(NumberFormat.formatOptionalTenths(blocks), NamedTextColor.AQUA);
            parentBuilder.text(blocks == 1 ? " block" : " blocks");
            return this;
        } else {
            parentBuilder.text(NumberFormat.formatOptionalTenths(blocks) + prefix, NamedTextColor.AQUA);
        }
        return this;
    }

    public AbilityDescriptionBuilder damage(Value.RangedValue rangedValue) {
        parentBuilder.append(Damages.formatDamage(rangedValue));
        return this;
    }

    public AbilityDescriptionBuilder damage(Value.SetValue setValue) {
        parentBuilder.append(Damages.formatDamage(setValue));
        return this;
    }

    public AbilityDescriptionBuilder heal(Value.RangedValue rangedValue) {
        parentBuilder.append(Heals.formatHealing(rangedValue));
        return this;
    }

    public AbilityDescriptionBuilder heal(Value.SetValue setValue) {
        parentBuilder.append(Heals.formatHealing(setValue));
        return this;
    }

    public AbilityDescriptionBuilder durationTicks(int ticks, String prefix) {
        return durationSeconds(ticks / 20f, prefix);
    }

    public AbilityDescriptionBuilder durationSeconds(float seconds, String prefix) {
        if (prefix.isEmpty()) {
            parentBuilder.text(NumberFormat.formatOptionalHundredths(seconds), NamedTextColor.GOLD);
            parentBuilder.text(seconds == 1 ? " second" : " seconds");
        } else {
            parentBuilder.text(NumberFormat.formatOptionalHundredths(seconds) + prefix, NamedTextColor.GOLD);
        }
        return this;
    }

    public AbilityDescriptionBuilder energy(int energy, @Nullable String prefix) {
        if (prefix == null) {
            parentBuilder.text(NumberFormat.formatOptionalTenths(energy), NamedTextColor.YELLOW);
            parentBuilder.text(" energy");
        } else {
            parentBuilder.text(NumberFormat.formatOptionalTenths(energy) + prefix, NamedTextColor.YELLOW);
        }
        return this;
    }

    public AbilityDescriptionBuilder energy(float energy, @Nullable String prefix) {
        if (prefix == null) {
            parentBuilder.text(NumberFormat.formatOptionalTenths(energy), NamedTextColor.YELLOW);
            parentBuilder.text(" energy");
        } else {
            parentBuilder.text(NumberFormat.formatOptionalTenths(energy) + prefix, NamedTextColor.YELLOW);
        }
        return this;
    }

    public AbilityDescriptionBuilder blocks(int blocks, String prefix) {
        if (prefix.isEmpty()) {
            parentBuilder.text(NumberFormat.formatOptionalTenths(blocks), NamedTextColor.AQUA);
            parentBuilder.text(blocks == 1 ? " block" : " blocks");
            return this;
        } else {
            parentBuilder.text(NumberFormat.formatOptionalTenths(blocks) + prefix, NamedTextColor.AQUA);
        }
        return this;
    }

    public AbilityDescriptionBuilder speed(float speed, String prefix) {
        parentBuilder.text(NumberFormat.formatOptionalTenths(speed) + prefix, NamedTextColor.WHITE);
        return this;
    }

    public AbilityDescriptionBuilder durationTicks(int ticks) {
        return durationTicks(ticks, "");
    }

    public AbilityDescriptionBuilder durationSeconds(float seconds) {
        return durationSeconds(seconds, "");
    }

    public AbilityDescriptionBuilder energy(int energy) {
        return energy(energy, null);
    }

    public AbilityDescriptionBuilder energy(float energy) {
        return energy(energy, null);
    }

    public AbilityDescriptionBuilder blocks(int blocks) {
        return blocks(blocks, "");
    }

    public AbilityDescriptionBuilder blocks(float blocks) {
        return blocks(blocks, "");
    }

    public AbilityDescriptionBuilder speed(float speed) {
        return speed(speed, "");
    }

    public ComponentBuilder end() {
        return parentBuilder;
    }

    public TextComponent build() {
        return parentBuilder.build();
    }

}

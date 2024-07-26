package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.function.UnaryOperator;

public class AbilityDescriptionBuilder {

    public static AbilityDescriptionBuilder create(String text) {
        return new AbilityDescriptionBuilder(ComponentBuilder.create(text));
    }

    public static AbilityDescriptionBuilder create(String text, NamedTextColor textColor) {
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

    public AbilityDescriptionBuilder text(int text, NamedTextColor textColor) {
        parentBuilder.append(Component.text(text, textColor));
        return this;
    }

    public AbilityDescriptionBuilder text(float text, NamedTextColor textColor) {
        return text(NumberFormat.formatOptionalTenths(text), textColor);
    }

    public AbilityDescriptionBuilder text(String text, NamedTextColor textColor) {
        parentBuilder.append(Component.text(text, textColor));
        return this;
    }

    public AbilityDescriptionBuilder text(FloatModifiable text, NamedTextColor textColor) {
        return text(NumberFormat.formatOptionalTenths(text.getCalculatedValue()), textColor);
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

    public AbilityDescriptionBuilder heal(Value.SetValue setValue, UnaryOperator<Float> modifier) {
        parentBuilder.append(Heals.formatHealingPercent(setValue, modifier));
        return this;
    }

    public AbilityDescriptionBuilder durationTicks(int ticks) {
        return durationSeconds(ticks / 20);
    }

    public AbilityDescriptionBuilder durationSeconds(int seconds) {
        parentBuilder.text(NumberFormat.formatOptionalTenths(seconds), NamedTextColor.GOLD);
        return this;
    }

    public AbilityDescriptionBuilder percent(int percent, NamedTextColor color) {
        parentBuilder.text(percent + "%", color);
        return this;
    }

    public AbilityDescriptionBuilder percent(float percent, NamedTextColor color) {
        parentBuilder.text(NumberFormat.formatOptionalTenths(percent) + "%", color);
        return this;
    }

    public AbilityDescriptionBuilder percent(FloatModifiable percent, NamedTextColor color) {
        parentBuilder.text(NumberFormat.formatOptionalTenths(percent.getCalculatedValue()) + "%", color);
        return this;
    }

    public AbilityDescriptionBuilder optimalRange(int range) {
        return range(range, "Has an optimal range of ");
    }

    private AbilityDescriptionBuilder range(int range, String rangeText) {
        emptyLine();
        parentBuilder.text(rangeText);
        parentBuilder.text(NumberFormat.formatOptionalTenths(range), NamedTextColor.YELLOW);
        parentBuilder.text(" blocks.");
        return this;
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
        parentBuilder.text(NumberFormat.formatOptionalTenths(range), NamedTextColor.YELLOW);
        parentBuilder.text(" blocks.");
        return this;
    }

    public AbilityDescriptionBuilder maxRange(FloatModifiable range) {
        return range(range, "Has a maximum range of ");
    }

    private AbilityDescriptionBuilder range(FloatModifiable range, String rangeText) {
        emptyLine();
        parentBuilder.text(rangeText);
        parentBuilder.text(NumberFormat.formatOptionalTenths(range.getCalculatedValue()), NamedTextColor.YELLOW);
        parentBuilder.text(" blocks.");
        return this;
    }

    public AbilityDescriptionBuilder initialRange(int range) {
        return range(range, "Has an initial cast range of ");
    }

    public AbilityDescriptionBuilder initialRange(FloatModifiable range) {
        return range(range, "Has an initial cast range of ");
    }

    public ComponentBuilder end() {
        return parentBuilder;
    }

    public TextComponent build() {
        return parentBuilder.build();
    }

}

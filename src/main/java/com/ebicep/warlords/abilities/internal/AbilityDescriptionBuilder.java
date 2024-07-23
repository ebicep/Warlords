package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;

public class AbilityDescriptionBuilder {

    private final ComponentBuilder parentBuilder;

    public AbilityDescriptionBuilder(ComponentBuilder parentBuilder) {
        this.parentBuilder = parentBuilder;
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

    public AbilityDescriptionBuilder durationTicks(int ticks) {
        return durationSeconds(ticks / 20);
    }

    public AbilityDescriptionBuilder durationSeconds(int seconds) {
        parentBuilder.text(NumberFormat.formatOptionalTenths(seconds), NamedTextColor.GOLD);
        return this;
    }

    public AbilityDescriptionBuilder percent(int percent, NamedTextColor color) {
        parentBuilder.text(NumberFormat.formatOptionalTenths(percent) + "%", color);
        return this;
    }

    public AbilityDescriptionBuilder percent(FloatModifiable percent, NamedTextColor color) {
        parentBuilder.text(NumberFormat.formatOptionalTenths(percent.getCalculatedValue()) + "%", color);
        return this;
    }

    public AbilityDescriptionBuilder optimalRange(int range) {
        parentBuilder.newLine();
        parentBuilder.newLine();
        parentBuilder.text("Has an optimal range of ");
        parentBuilder.text(range, NamedTextColor.YELLOW);
        parentBuilder.text(" blocks", NamedTextColor.GRAY);
        return this;
    }

    public AbilityDescriptionBuilder maxRange(int range) {
        parentBuilder.newLine();
        parentBuilder.newLine();
        parentBuilder.text("Has a maximum range of ");
        parentBuilder.text(range, NamedTextColor.YELLOW);
        parentBuilder.text(" blocks", NamedTextColor.GRAY);
        return this;
    }

    public ComponentBuilder end() {
        return parentBuilder;
    }

}

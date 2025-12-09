package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Solitary extends AbstractAbility implements OrangeAbilityIcon, Duration, AbilityStats<Solitary, Solitary.SolidaryStats> {

    private final SolidaryStats stats = new SolidaryStats();
    private int tickDuration = 100;
    private float damageReduction = 45;
    private float healthPercentageHealing = 40;

    public Solitary() {
        super(AbstractAbilityBuilder.create("solitary").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.damageReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReduction"), float.class);
        this.healthPercentageHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("healthPercentageHealing"), float.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "warrior.laststand.activation", 2, 1);
        LocationUtils.getCircle(wp.getLocation().add(0, 3, 0), 2, 8).forEach(location -> {
            EffectUtils.displayParticle(Particle.FLAME, location, 1, 0, 0, 0, 0);
        });
        wp.addInstance(InstanceBuilder
                .healing()
                .cause(name)
                .source(wp)
                .value(wp.getMaxHealth() * healthPercentageHealing / 100)
                .flags(InstanceFlags.TRUE_HEALING)
        );
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "LAST",
                Solitary.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                },
                tickDuration
        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, convertToDivisionDecimal(damageReduction));
                }
        ));
        for (Intervene intervene : wp.getAbilitiesMatching(Intervene.class)) {
            intervene.setCurrentCooldown(0);
        }
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Enter a defensive stance, reducing all damage you take by ")
                                               .percent(damageReduction, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" and true heal for ")
                                               .percent(healthPercentageHealing, NamedTextColor.GREEN)
                                               .text(" of your max health.")
                                               .emptyLine()
                                               .text("Resets the cooldown of Intervene.")
                                               .build();
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public SolidaryStats getAbilityStats() {
        return stats;
    }

    public static class SolidaryStats extends AbstractAbilityStats<Solitary, SolidaryStats> {

        @Override
        public Class<SolidaryStats> getClazz() {
            return SolidaryStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public SolidaryStats merge(SolidaryStats other, int multiplier) {
            SolidaryStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public SolidaryStats create() {
            return new SolidaryStats();
        }

    }

}

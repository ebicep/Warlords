package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class VolatileBrew extends AbstractAbility implements OrangeAbilityIcon, HitBox, AbilityStats<VolatileBrew, VolatileBrew.VolatileBrewStats>, Damages<VolatileBrew.DamageValues>, Heals<VolatileBrew.HealingValues> {

    private final VolatileBrewStats stats = new VolatileBrewStats();
    private final DamageValues damageValues = new DamageValues();
    private final HealingValues healingValues = new HealingValues();
    private int ticksUntilExplosion = 240;
    private FloatModifiable radius = new FloatModifiable(7.0f);
    private float energyRestore = 80;
    private int earlyActivationEffectivenessReduction;
    private boolean bothStatesActive = false;

    public VolatileBrew() {
        super(AbstractAbilityBuilder.create("volatileBrew").pvp());
    }

    public VolatileBrew(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public VolatileBrewStats getAbilityStats() {
        return stats;
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.ticksUntilExplosion = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("ticksUntilExplosion"), int.class);
        this.radius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class));
        this.energyRestore = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("energyRestore"), float.class);
        this.earlyActivationEffectivenessReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                builder.getAppendedFieldName("earlyActivationEffectivenessReduction"),
                int.class
        );
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 2, 0.75f);
        Utils.playGlobalSound(wp.getLocation(), "arcanist.mysticalbarrier.activation", 2, 2);

        float radius = this.radius.getCalculatedValue();
        List<WarlordsEntity> targets = PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .requireLineOfSightIntervene(wp, true)
                .lookingAtFirst(wp)
                .limit(1)
                .toList();
        VolatileBrewData data = new VolatileBrewData(targets.isEmpty() ? wp : targets.get(0));
        EffectUtils.playParticleLinkAnimation(wp.getLocation(), data.target.getLocation(), Particle.DRAGON_BREATH);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        LinkedCooldown<VolatileBrewData> brewCooldown = new LinkedCooldown<>(
                name,
                "VOLAT",
                VolatileBrewData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (bothStatesActive || data.damageMode) {
                        float multiplier = data.activatedEarly ? convertToDivisionDecimal(earlyActivationEffectivenessReduction) : 1.0f;
                        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_SPIDER_HURT, 2, .25f);
                        EffectUtils.displayParticle(
                                Particle.EXPLOSION_EMITTER,
                                data.target.getLocation(),
                                1,
                                0,
                                0,
                                0,
                                0
                        );
                        PlayerFilter.entitiesAround(data.target, radius, radius, radius)
                                    .aliveEnemiesOf(wp)
                                    .forEach(warlordsEntity -> {
                                        warlordsEntity.addInstance(InstanceBuilder
                                                .damage()
                                                .ability(this)
                                                .cause("Corrosive Concoction")
                                                .source(wp)
                                                .min(damageValues.brewDamage.getMinValue() * multiplier)
                                                .max(damageValues.brewDamage.getMaxValue() * multiplier)
                                                .flags(InstanceFlags.TRUE_DAMAGE)
                                        );
                                    });
                    }
                    if (bothStatesActive || !data.damageMode) {
                        Utils.playGlobalSound(wp.getLocation(), "paladin.holyradiance.activation", 2, 2f);
                        EffectUtils.displayParticle(
                                Particle.HAPPY_VILLAGER,
                                data.target.getLocation(),
                                100,
                                random.nextDouble(radius * 2 + 1) - radius,
                                random.nextDouble(radius * 2 + 1) - radius,
                                random.nextDouble(radius * 2 + 1) - radius,
                                0
                        );
                        PlayerFilter.entitiesAround(data.target, radius, radius, radius)
                                    .aliveTeammatesOf(wp)
                                    .forEach(warlordsEntity -> {
                                        warlordsEntity.addInstance(InstanceBuilder
                                                .healing()
                                                .ability(this)
                                                .cause("Restorative Elixir")
                                                .source(wp)
                                                .value(healingValues.brewHealing)
                                        );
                                        warlordsEntity.addEnergy(wp, "Restorative Elixir", energyRestore);
                                    });
                    }
                },
                cooldownManager -> {},
                ticksUntilExplosion,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 != 0) {
                        return;
                    }
                    EffectUtils.displayParticle(
                            data.damageMode ? Particle.FLAME : Particle.HAPPY_VILLAGER,
                            data.target.getLocation(),
                            3,
                            random.nextDouble(.5),
                            0,
                            random.nextDouble(.5),
                            .1
                    );
                }),
                data.target
        ) {
            @Override
            protected Listener getListener() {
                LinkedCooldown<VolatileBrewData> cooldown = this;
                return new Listener() {
                    @EventHandler
                    public void onSneak(PlayerToggleSneakEvent event) {
                        if (!event.getPlayer().equals(wp.getEntity()) || !event.isSneaking()) {
                            return;
                        }
                        if (cooldown.getTicksLeft() <= 1) {
                            return;
                        }
                        if (data.target.equals(wp)) {
                            data.activatedEarly = true;
                            cooldown.setTicksLeft(1);
                            wp.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                                    .append(Component.text(" You detonated your ", NamedTextColor.GRAY))
                                    .append(Component.text(name, NamedTextColor.YELLOW))
                                    .append(Component.text(" early!", NamedTextColor.GRAY))
                            );
                            return;
                        }
                        cooldown.getLinkedEntities().remove(data.target);
                        data.target = wp;
                        wp.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                                .append(Component.text(" You recalled your ", NamedTextColor.GRAY))
                                .append(Component.text(name, NamedTextColor.YELLOW))
                                .append(Component.text(" to yourself!", NamedTextColor.GRAY))
                        );
                    }
                };
            }

            @Override
            public TextColor customActionBarColor() {
                return data.damageMode ? NamedTextColor.DARK_RED : NamedTextColor.DARK_GREEN;
            }
        };
        wp.getCooldownManager().addCooldown(brewCooldown);
        if (wp != data.target) {
            data.target.getCooldownManager().addCooldown(brewCooldown);
            data.target.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                    .append(Component.text(" You have been given a ", NamedTextColor.GRAY))
                    .append(Component.text(name, NamedTextColor.YELLOW))
                    .append(Component.text(" by " + wp.getName() + "!", NamedTextColor.GRAY))
            );
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" You gave a ", NamedTextColor.GRAY))
                    .append(Component.text(name, NamedTextColor.YELLOW))
                    .append(Component.text(" to " + data.target.getName() + "!", NamedTextColor.GRAY))
            );
        } else {
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" You gave a ", NamedTextColor.GRAY))
                    .append(Component.text(name, NamedTextColor.YELLOW))
                    .append(Component.text(" to yourself!", NamedTextColor.GRAY)));
        }
        if (!bothStatesActive) {
            addSecondaryAbility(
                    5,
                    () -> {
                        if (wp.isAlive()) {
                            data.damageMode = !data.damageMode;
                            wp.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN.append(Component.textOfChildren(
                                    Component.text(" You changed your ", NamedTextColor.GRAY),
                                    Component.text(name, NamedTextColor.YELLOW),
                                    Component.text(" to ", NamedTextColor.GRAY),
                                    Component.text(data.damageMode ? "Corrosive Concoction" : "Restorative Elixir",
                                            data.damageMode ? NamedTextColor.DARK_RED : NamedTextColor.DARK_GREEN
                                    )
                            )));
                            if (data.target != wp) {
                                data.target.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN.append(Component.textOfChildren(
                                        Component.text(" Your ", NamedTextColor.GRAY),
                                        Component.text(name, NamedTextColor.YELLOW),
                                        Component.text(" changed to ", NamedTextColor.GRAY),
                                        Component.text(data.damageMode ? "Corrosive Concoction" : "Restorative Elixir",
                                                data.damageMode ? NamedTextColor.DARK_RED : NamedTextColor.DARK_GREEN
                                        )
                                )));
                            }
                        }
                    },
                    true,
                    secondaryAbility -> wp.isDead() || !wp.getCooldownManager().hasCooldown(brewCooldown)
            );
        }
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Pass a powerful brew to an ally that detonates after ")
                .durationTicks(ticksUntilExplosion)
                .text(" or when the target dies. The brew has two states, ")
                .emptyLine()
                .text("Corrosive Concoction", NamedTextColor.DARK_RED)
                .text(": Deals ")
                .damage(damageValues.brewDamage)
                .text(" true damage to nearby enemies. Deals ")
                .percent(earlyActivationEffectivenessReduction, NamedTextColor.BLUE)
                .text(" less damage if detonated early.")
                .text("Restorative Elixir", NamedTextColor.DARK_GREEN)
                .text(": Restores ")
                .heal(healingValues.brewHealing)
                .text(" health and ")
                .energy(energyRestore)
                .text(" to nearby allies.")
                .emptyLine()
                .text("Recast to toggle between the two states. Sneak to recall the brew to yourself; the brew detonates if you already have it.")
                .emptyLine()
                .text("If no ally is targeted, receive the brew yourself.")
                .build();
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    public void setBothStatesActive(boolean bothStatesActive) {
        this.bothStatesActive = bothStatesActive;
    }

    public int getEarlyActivationEffectivenessReduction() {
        return earlyActivationEffectivenessReduction;
    }

    public void setEarlyActivationEffectivenessReduction(int earlyActivationEffectivenessReduction) {
        this.earlyActivationEffectivenessReduction = earlyActivationEffectivenessReduction;
    }

    public static class VolatileBrewData {

        private WarlordsEntity target;
        private boolean damageMode = true;
        private boolean activatedEarly = false;

        public VolatileBrewData(WarlordsEntity target) {
            this.target = target;
        }

    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValue brewDamage = new Value.RangedValue(1140, 1680);

        private List<Value> values = List.of(brewDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.brewDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("brewDamage"), Value.RangedValue.class);
            this.values = List.of(brewDamage);
        }

        public Value.RangedValue getBrewDamage() {
            return brewDamage;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.SetValue brewHealing = new Value.SetValue(1000);

        private List<Value> values = List.of(brewHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.brewHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("brewHealing"), Value.SetValue.class);
            this.values = List.of(brewHealing);
        }

        public Value.SetValue getBrewHealing() {
            return brewHealing;
        }

    }

    public static class VolatileBrewStats extends AbstractAbilityStats<VolatileBrew, VolatileBrew.VolatileBrewStats> {


        @Override
        public Class<VolatileBrew.VolatileBrewStats> getClazz() {
            return VolatileBrew.VolatileBrewStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public VolatileBrew.VolatileBrewStats merge(VolatileBrew.VolatileBrewStats other, int multiplier) {
            VolatileBrew.VolatileBrewStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public VolatileBrew.VolatileBrewStats create() {
            return new VolatileBrew.VolatileBrewStats();
        }

    }

}

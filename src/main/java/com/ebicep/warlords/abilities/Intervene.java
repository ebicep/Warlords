package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.player.ingame.instances.type.PlayerNameInstance;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.defender.InterveneBranch;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Intervene extends AbstractAbility implements BlueAbilityIcon, Duration, AbilityStats<Intervene, Intervene.InterveneStats> {

    private final InterveneStats stats = new InterveneStats();
    private int tickDuration = 100;
    private float maxDamagePrevented = 3600;
    private int damageReduction = 50;
    private float radius = 10;
    private float breakRadius = 15;
    private int maxTargets = 1;

    public Intervene() {
        super(AbstractAbilityBuilder.create("intervene").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.maxDamagePrevented = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxDamagePrevented"), float.class);
        this.damageReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReduction"), int.class);
        this.radius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class);
        this.breakRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("breakRadius"), float.class);
        this.maxTargets = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxTargets"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        List<InterveneData> venes = new ArrayList<>();
        for (WarlordsEntity veneTarget : PlayerFilter.entitiesAround(wp, radius, radius, radius)
                                                     .aliveTeammatesOfExcludingSelf(wp)
                                                     .requireLineOfSightIntervene(wp, true)
                                                     .lookingAtFirst(wp)
                                                     .excludingAlliedMobs()
                                                     .limit(maxTargets)) {
            stats.playersIntervened++;
            if (veneTarget.hasFlag()) {
                stats.carriersIntervened++;
            }
            // Green line / Sound
            Utils.playGlobalSound(wp.getLocation(), "warrior.intervene.impact", 1, 1);
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), veneTarget.getLocation(), Particle.HAPPY_VILLAGER);
            // New cooldown, both players have the same instance of intervene.
            InterveneData data = new InterveneData(this, wp, veneTarget, maxDamagePrevented);
            venes.add(data);
            // Removing all other intervenes
            wp.getCooldownManager().removeCooldown(cd -> cd.getCooldownClass() == InterveneData.class && veneTarget.getCooldownManager().hasCooldown(cd.getCooldownObject()));
            veneTarget.getCooldownManager().removeCooldown(cd -> {
                if (cd.getCooldownClass() == InterveneData.class) {
                    cd.getFrom()
                      .sendMessage(WarlordsEntity.RECEIVE_ARROW_RED.append(Component.text(" " + cd.getFrom().getName() + "'s ", NamedTextColor.GRAY))
                                                                   .append(Component.text("Intervene", NamedTextColor.YELLOW))
                                                                   .append(Component.text(" has expired!", NamedTextColor.GRAY)));
                    veneTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED.append(Component.text(" " + cd.getFrom().getName() + "'s ", NamedTextColor.GRAY))
                                                                           .append(Component.text("Intervene", NamedTextColor.YELLOW))
                                                                           .append(Component.text(" has expired!", NamedTextColor.GRAY)));
                    return true;
                } else {
                    return false;
                }
            });
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" You are now protecting " + veneTarget.getName() + " with your ", NamedTextColor.GRAY))
                                                          .append(Component.text("Intervene", NamedTextColor.YELLOW))
                                                          .append(Component.text("!", NamedTextColor.GRAY)));
            veneTarget.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" " + wp.getName() + " is shielding you with their ", NamedTextColor.GRAY))
                                                                  .append(Component.text("Intervene", NamedTextColor.YELLOW))
                                                                  .append(Component.text("!", NamedTextColor.GRAY)));

            if (pveMasterUpgrade2) {
                wp.addSpeedModifier(wp, "Interference - " + veneTarget.getName(), 25, tickDuration);
                veneTarget.addSpeedModifier(wp, "Interference - " + veneTarget.getName(), 25, tickDuration);
            }
            LinkedCooldown<InterveneData> interveneCooldown = new LinkedCooldown<>(name, "VENE", InterveneData.class, data, wp, CooldownTypes.ABILITY, cooldownManager -> {
            }, cooldownManager -> {
                if (!Objects.equals(cooldownManager.getWarlordsEntity(), wp)) {
                    return;
                }
                wp.getSpeed().removeModifier("Interference - " + veneTarget.getName());
                veneTarget.getSpeed().removeModifier("Interference - " + veneTarget.getName());
                wp.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED.append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                                                               .append(Component.text("Intervene", NamedTextColor.YELLOW))
                                                               .append(Component.text(" has expired!", NamedTextColor.GRAY)));
                veneTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED.append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                                                                       .append(Component.text("Intervene", NamedTextColor.YELLOW))
                                                                       .append(Component.text(" has expired!", NamedTextColor.GRAY)));
            }, tickDuration, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                if (wp.isDead() || veneTarget.getLocation().distanceSquared(wp.getLocation()) > breakRadius * breakRadius) {
                    cooldown.setTicksLeft(0);
                    return;
                }
                if (ticksElapsed % 20 == 0) {
                    int timeLeft = Math.round(ticksLeft / 20f);
                    veneTarget.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                                                                          .append(Component.text("Intervene", NamedTextColor.YELLOW))
                                                                          .append(Component.text(" will expire in ", NamedTextColor.GRAY))
                                                                          .append(Component.text(timeLeft, NamedTextColor.GOLD))
                                                                          .append(Component.text(" second" + (timeLeft == 1 ? "!" : "s!"), NamedTextColor.GRAY)));
                }
            }), veneTarget
            ) {
                @Override
                public PlayerNameData addPrefixFromOther() {
                    return PlayerNameData.dynamic(
                            () -> Component.text((int) (data.getMaxDamagePrevented() - data.getDamagePrevented()), NamedTextColor.GOLD),
                            we -> we.isTeammate(wp)
                    );
                }

                @Nonnull
                @Override
                public Component getDebugMessage() {
                    return Component.textOfChildren(Component.text(NumberFormat.formatOptionalTenths(data.getDamagePrevented()), NamedTextColor.YELLOW),
                            Component.text("/", NamedTextColor.GRAY),
                            Component.text(NumberFormat.formatOptionalTenths(data.getMaxDamagePrevented()), NamedTextColor.YELLOW),
                            Component.text(" - ", NamedTextColor.GRAY),
                            Component.text(NumberFormat.formatOptionalTenths(damageReduction), NamedTextColor.GREEN)
                    );
                }
            };
            if (pveMasterUpgrade2) {
                wp.addKnockbackModifier(wp, name, -100, interveneCooldown);
                veneTarget.addKnockbackModifier(wp, name, -100, interveneCooldown);
            }
            wp.getCooldownManager().addCooldown(interveneCooldown);
            veneTarget.getCooldownManager().addCooldown(interveneCooldown);
            Bukkit.getPluginManager().callEvent(new WarlordsAbilityTargetEvent.WarlordsBlueAbilityTargetEvent(wp, name, veneTarget));
        }
        if (inPve) {
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(name + " Damage", null, InterveneData.class, null, wp, CooldownTypes.BUFF, cooldownManager -> {
            }, tickDuration
            ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                        name, (float) (1 + venes.stream().mapToDouble(InterveneData::getDamagePrevented).sum() / 100 * .01)
                );
                    }
            ));
        }
        return !venes.isEmpty();
    }

    @Override
    public void updateDescription(Player player) {
        if (inPve) {
            description = AbilityDescriptionBuilder.create("Protect up to 2 target allies, reducing the damage they take by ")
                                                   .percent(100, AbilityDescriptionBuilder.COLOR_BROWN)
                                                   .text(" and redirecting ")
                                                   .percent(damageReduction, NamedTextColor.RED)
                                                   .text(" of the damage they would have taken back to you. You can protect the target for a maximum of ")
                                                   .text(maxDamagePrevented, AbilityDescriptionBuilder.COLOR_BROWN)
                                                   .text(" damage. You must remain within ")
                                                   .blocks(breakRadius)
                                                   .text(" of each other. For every 100 damage prevented, increase your damage by 1%. Lasts ")
                                                   .durationTicks(tickDuration)
                                                   .text(".")
                                                   .initialRange(radius)
                                                   .build();
        } else {
            description = AbilityDescriptionBuilder.create("Protect the target ally, reducing the damage they take by ")
                                                   .percent(100, AbilityDescriptionBuilder.COLOR_BROWN)
                                                   .text(" and redirecting ")
                                                   .percent(damageReduction, NamedTextColor.RED)
                                                   .text(" of the damage they would have taken back to you. You can protect the target for a maximum of ")
                                                   .text(maxDamagePrevented, AbilityDescriptionBuilder.COLOR_BROWN)
                                                   .text(" damage. You must remain within ")
                                                   .blocks(breakRadius)
                                                   .text(" of each other. Lasts ")
                                                   .durationTicks(tickDuration)
                                                   .text(".")
                                                   .initialRange(radius)
                                                   .build();
        }
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new InterveneBranch(abilityTree, this);
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
    public InterveneStats getAbilityStats() {
        return stats;
    }

    public float getMaxDamagePrevented() {
        return maxDamagePrevented;
    }

    public void setMaxDamagePrevented(float maxDamagePrevented) {
        this.maxDamagePrevented = maxDamagePrevented;
    }

    public float getBreakRadius() {
        return breakRadius;
    }

    public void setBreakRadius(float breakRadius) {
        this.breakRadius = breakRadius;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getDamageReduction() {
        return damageReduction;
    }

    public void setDamageReduction(int damageReduction) {
        this.damageReduction = damageReduction;
    }

    public int getMaxTargets() {
        return maxTargets;
    }

    public void setMaxTargets(int maxTargets) {
        this.maxTargets = maxTargets;
    }

    public static class InterveneData {

        private final Intervene intervene;

        private final WarlordsEntity caster;

        private final WarlordsEntity target;

        private final float maxDamagePrevented;

        private float damagePrevented = 0;

        public InterveneData(Intervene intervene, WarlordsEntity caster, WarlordsEntity target, float maxDamagePrevented) {
            this.intervene = intervene;
            this.caster = caster;
            this.target = target;
            this.maxDamagePrevented = maxDamagePrevented;
        }

        public Intervene getIntervene() {
            return intervene;
        }

        public WarlordsEntity getCaster() {
            return caster;
        }

        public WarlordsEntity getTarget() {
            return target;
        }

        public float getMaxDamagePrevented() {
            return maxDamagePrevented;
        }

        public float getDamagePrevented() {
            return damagePrevented;
        }

        public void addDamagePrevented(float amount) {
            int before = (int) (maxDamagePrevented - damagePrevented);
            this.damagePrevented += amount;
            int after = (int) (maxDamagePrevented - damagePrevented);
            target.getCooldownManager().markNameDisplayDirtyIfChanged(before, after);
            caster.getCooldownManager().markNameDisplayDirtyIfChanged(before, after);
        }

    }

    public static class InterveneStats extends AbstractAbilityStats<Intervene, InterveneStats> {

        @Field("targets_intervened")
        private int playersIntervened = 0;

        @Field("carriers_intervened")
        private int carriersIntervened = 0;

        @Override
        public Class<InterveneStats> getClazz() {
            return InterveneStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Intervened", playersIntervened));
            statsDisplay.add(new AbilityStatDisplay("Carriers Intervened", carriersIntervened));
            return statsDisplay;
        }

        @Override
        public InterveneStats merge(InterveneStats other, int multiplier) {
            InterveneStats stats = super.merge(other, multiplier);
            stats.playersIntervened = this.playersIntervened + other.playersIntervened * multiplier;
            stats.carriersIntervened = this.carriersIntervened + other.carriersIntervened * multiplier;
            return stats;
        }

        @Override
        public InterveneStats create() {
            return new InterveneStats();
        }

    }

}

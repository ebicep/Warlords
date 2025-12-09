package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.sentinel.SanctuaryBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.*;

public class Sanctuary extends AbstractAbility implements OrangeAbilityIcon, Duration, AbilityStats<Sanctuary, Sanctuary.SanctuaryStats> {

    private final SanctuaryStats stats = new SanctuaryStats();
    private int hexTickDurationIncrease = 40;
    private int additionalDamageReduction = 4;
    private int tickDuration = 240;
    private int lethalDamageHealing = 15;

    public Sanctuary() {
        super(AbstractAbilityBuilder.create("sanctuary").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.hexTickDurationIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hexTickDurationIncrease"), int.class);
        this.additionalDamageReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("additionalDamageReduction"), int.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.lethalDamageHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("lethalDamageHealing"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Location loc = wp.getLocation();
        Utils.playGlobalSound(wp.getLocation(), "warrior.laststand.activation", 2, 1.8f);
        Utils.playGlobalSound(loc, "arcanist.sanctuary.activation", 2, 0.55f);
        EffectUtils.playCircularShieldAnimation(loc, Particle.END_ROD, 5, 0.8, 2);
        EffectUtils.playCircularShieldAnimation(loc, Particle.DRIPPING_WATER, 3, 0.6, 1.2);
        List<FloatModifiable.FloatModifier> modifiers;
        if (pveMasterUpgrade2) {
            modifiers = wp.getAbilitiesMatching(GuardianBeam.class)
                          .stream()
                          .map(ability -> ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                  name + " Master", 0.7f
                          ))
                          .toList();
        } else {
            modifiers = Collections.emptyList();
        }
        PlayerFilter.playingGame(wp.getGame()).teammatesOf(wp).forEach(teammate -> {
            new CooldownFilter<>(teammate, RegularCooldown.class).filterCooldownClass(FortifyingHex.class).filterCooldownFrom(wp).forEach(cd -> {
                cd.setTicksLeft(cd.getTicksLeft() + hexTickDurationIncrease);
                stats.hexesProlonged++;
            });
            boolean isSelf = wp == teammate;
            int maxStacks = FortifyingHex.getFromHex(wp).getMaxStacks();
            teammate.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    isSelf ? "SANCTUARY" : null,
                    Sanctuary.class,
                    null,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                        if (isSelf) {
                            modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                        }
                    },
                    false,
                    tickDuration,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (wp.isDead()) {
                            cooldown.setTicksLeft(0);
                        }
                    })
            ) {

                @Override
                protected Listener getListener() {
                    if (!isSelf) {
                        return null;
                    }
                    return new Listener() {

                        private final Set<WarlordsEntity> resurrected = new HashSet<>();

                        @EventHandler(priority = EventPriority.LOWEST)
                        private void onAddCooldown(WarlordsAddCooldownEvent event) {
                            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                            if (!Objects.equals(cooldown.getFrom(), wp) || !(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                                return;
                            }
                            Object cdObject = cooldown.getCooldownObject();
                            if (cdObject instanceof FortifyingHex.FortifyingHexData) {
                                regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + hexTickDurationIncrease);
                                stats.hexesProlonged++;
                            }
                            if (pveMasterUpgrade2 && event.getWarlordsEntity().equals(teammate) && cdObject instanceof GuardianBeam.GuardianBeamShield guardianBeamShield) {
                                float newShieldHealth = guardianBeamShield.getShieldValue() + 600;
                                guardianBeamShield.setMaxShieldHealth(newShieldHealth);
                                guardianBeamShield.setShieldHealth(newShieldHealth);
                            }
                        }

                        @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
                        public void onDeath(WarlordsDeathEvent event) {
                            WarlordsEntity warlordsEntity = event.getWarlordsEntity();
                            if (!warlordsEntity.isTeammateAlive(wp) || resurrected.contains(warlordsEntity)) {
                                return;
                            }
                            int hexStacks = (int) new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                                    .filterCooldownFrom(wp)
                                    .filterCooldownClass(FortifyingHex.FortifyingHexData.class)
                                    .stream()
                                    .count();
                            if (hexStacks < maxStacks) {
                                return;
                            }
                            resurrected.add(warlordsEntity);
                            event.setCancelled(true);
                            warlordsEntity.setCurrentHealth(warlordsEntity.getMaxBaseHealth() * convertToPercent(lethalDamageHealing));
                            Utils.playGlobalSound(warlordsEntity.getLocation(), Sound.ITEM_TOTEM_USE, 2, 0.75f);
                            if (pveMasterUpgrade) {
                                for (WarlordsEntity resTarget : PlayerFilter
                                        .entitiesAround(warlordsEntity, 15, 15, 15)
                                        .aliveEnemiesOf(warlordsEntity)
                                ) {
                                    EffectUtils.strikeLightning(resTarget.getLocation(), true);
                                    EffectUtils.playCrownAnimation(resTarget.getLocation(), Particle.CHERRY_LEAVES);
                                    float damage = warlordsEntity.getEntity() instanceof Player ? warlordsEntity.getMaxHealth() * 2 : warlordsEntity.getMaxHealth() * 0.1f;
                                    resTarget.addInstance(InstanceBuilder.damage()
                                                                         .ability(Sanctuary.this)
                                                                         .source(warlordsEntity)
                                                                         .value(damage)
                                                                         .flag(InstanceFlags.TRUE_DAMAGE, true));
                                }
                            }
                            if (warlordsEntity.equals(wp)) {
                                wp.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                                        .append(Component.text(" Your ", NamedTextColor.GRAY))
                                        .append(Component.text(name, NamedTextColor.YELLOW))
                                        .append(Component.text(" resurrected you!", NamedTextColor.GRAY))
                                );
                            } else {
                                warlordsEntity.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                                        .append(Component.text(" You were resurrected by " + wp.getName() + "'s ", NamedTextColor.GRAY))
                                        .append(Component.text(name, NamedTextColor.YELLOW))
                                        .append(Component.text("!", NamedTextColor.GRAY))
                                );
                                wp.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                                        .append(Component.text(" Your ", NamedTextColor.GRAY))
                                        .append(Component.text(name, NamedTextColor.YELLOW))
                                        .append(Component.text(" resurrected " + warlordsEntity.getName() + "!", NamedTextColor.GRAY))
                                );
                            }
                            stats.playersResurrected++;
                        }
                    };
                }

            }.addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                        int hexStacks = (int) new CooldownFilter<>(event.getWarlordsEntity(), RegularCooldown.class)
                                .filterCooldownFrom(wp)
                                .filterCooldownClass(FortifyingHex.FortifyingHexData.class)
                                .stream()
                                .count();
                if (hexStacks < maxStacks) {
                            return;
                        }
                currentDamageValue.addModifier(
                        FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, name,
                        -additionalDamageReduction * maxStacks / 100f,
                        contribution -> stats.damageReduced += Math.abs(contribution)
                );
                if (pveMasterUpgrade) {
                    currentDamageValue.addModifier(
                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name,
                            .85f,
                            contribution -> stats.damageReduced += Math.abs(contribution)
                    );
                }
                    }
            ));
        });
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Summon your full protective power, increasing ")
                .text("FHEX", NamedTextColor.YELLOW)
                .text(" duration by ")
                .durationTicks(hexTickDurationIncrease)
                .text(" and causing Guardian Beam to not consume ")
                .text("FHEX", NamedTextColor.YELLOW)
                .text(" stacks.")
                .emptyLine()
                .text("All allies with max stacks of ")
                .text("FHEX", NamedTextColor.YELLOW)
                .text(" gain an additional ")
                .percent(additionalDamageReduction, AbilityDescriptionBuilder.COLOR_BROWN)
                .text(" damage reduction per stack and are resurrected to ")
                .percent(lethalDamageHealing, NamedTextColor.GREEN)
                .text(" of their maximum health when taking lethal damage for the first time. Lasts ")
                .durationTicks(tickDuration)
                .text(".")
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SanctuaryBranch(abilityTree, this);
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
    public SanctuaryStats getAbilityStats() {
        return stats;
    }

    public int getHexTickDurationIncrease() {
        return hexTickDurationIncrease;
    }

    public void setHexTickDurationIncrease(int hexTickDurationIncrease) {
        this.hexTickDurationIncrease = hexTickDurationIncrease;
    }

    public int getAdditionalDamageReduction() {
        return additionalDamageReduction;
    }

    public void setAdditionalDamageReduction(int additionalDamageReduction) {
        this.additionalDamageReduction = additionalDamageReduction;
    }

    public static class SanctuaryStats extends AbstractAbilityStats<Sanctuary, SanctuaryStats> {

        @Field("hexes_prolonged")
        private int hexesProlonged = 0;

        @Field("hexes_not_consumed")
        private int hexesNotConsumed = 0;

        @Field("damage_reduced")
        private float damageReduced = 0;

        @Field("total_damage_reflected")
        private float totalDamageReflected = 0;

        @Field("players_resurrected")
        private int playersResurrected = 0;

        @Override
        public Class<SanctuaryStats> getClazz() {
            return SanctuaryStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Hexes Prolonged", hexesProlonged));
            statsDisplay.add(new AbilityStatDisplay("Hexes Not Consumed", hexesNotConsumed));
            statsDisplay.add(new AbilityStatDisplay("Damage Reduced", damageReduced));
            statsDisplay.add(new AbilityStatDisplay("Total Damage Reflected", totalDamageReflected));
            statsDisplay.add(new AbilityStatDisplay("Players Resurrected", playersResurrected));
            return statsDisplay;
        }

        @Override
        public SanctuaryStats merge(SanctuaryStats other, int multiplier) {
            SanctuaryStats stats = super.merge(other, multiplier);
            stats.hexesProlonged = this.hexesProlonged + other.hexesProlonged * multiplier;
            stats.hexesNotConsumed = this.hexesNotConsumed + other.hexesNotConsumed * multiplier;
            stats.damageReduced = this.damageReduced + other.damageReduced * multiplier;
            stats.totalDamageReflected = this.totalDamageReflected + other.totalDamageReflected * multiplier;
            stats.playersResurrected = this.playersResurrected + other.playersResurrected * multiplier;
            return stats;
        }

        @Override
        public SanctuaryStats create() {
            return new SanctuaryStats();
        }

        public int getHexesNotConsumed() {
            return hexesNotConsumed;
        }

        public void setHexesNotConsumed(int hexesNotConsumed) {
            this.hexesNotConsumed = hexesNotConsumed;
        }

        public float getTotalDamageReflected() {
            return totalDamageReflected;
        }

        public void setTotalDamageReflected(float totalDamageReflected) {
            this.totalDamageReflected = totalDamageReflected;
        }

    }

}

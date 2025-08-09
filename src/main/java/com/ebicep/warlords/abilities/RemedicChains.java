package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.apothecary.RemedicChainsBranch;
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
import java.util.*;
import java.util.stream.Collectors;

public class RemedicChains extends AbstractAbility implements BlueAbilityIcon, Duration, Heals<RemedicChains.HealingValues>, Damages<RemedicChains.DamageValues>, AbilityStats<RemedicChains, RemedicChains.RemedicChainsStats> {

    private final RemedicChainsStats stats = new RemedicChainsStats();
    private final HealingValues healingValues = new HealingValues();
    private final DamageValues damageValues = new DamageValues();
    private int tickDuration = 160;
    private int alliesAffected = 3;
    private int linkBreakRadius = 15;
    private int castRange = 10;
    private float healMultiplierOne;
    private float healMultiplierTwo;
    private float healMultiplierThree;

    public RemedicChains() {
        super(AbstractAbilityBuilder.create("remedicChains").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.alliesAffected = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("alliesAffected"), int.class);
        this.linkBreakRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("linkBreakRadius"), int.class);
        this.castRange = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("castRange"), int.class);
        this.healMultiplierOne = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("healMultiplierOne"), float.class);
        this.healMultiplierTwo = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("healMultiplierTwo"), float.class);
        this.healMultiplierThree = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("healMultiplierThree"), float.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Set<WarlordsEntity> teammatesNear = PlayerFilter
                .entitiesAround(wp, castRange, castRange, castRange)
                .aliveTeammatesOfExcludingSelf(wp)
                .excludingDummy()
                .closestFirst(wp)
                .limit(alliesAffected)
                .stream()
                .collect(Collectors.toSet());
        if (teammatesNear.isEmpty()) {
            wp.sendMessage(Component.text("There are no allies nearby to link!", NamedTextColor.RED));
            return false;
        }
        stats.targetsLinked += teammatesNear.size();
        Utils.playGlobalSound(wp.getLocation(), "rogue.remedicchains.activation", 2, 0.2f);
        Map<WarlordsEntity, FloatModifiable.FloatModifier> healthBoosts = new HashMap<>();
        wp.setRegenTickTimer(1);
        teammatesNear.forEach(warlordsEntity -> {
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" Your Remedic Chains is now protecting ", NamedTextColor.GRAY))
                    .append(Component.text(warlordsEntity.getName(), NamedTextColor.YELLOW))
                    .append(Component.text("!", NamedTextColor.GRAY)));
            warlordsEntity.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN.
                    append(Component.text(" " + wp.getName() + "'s", NamedTextColor.GRAY))
                    .append(Component.text(" Remedic Chains", NamedTextColor.YELLOW))
                    .append(Component.text(" is now increasing your ", NamedTextColor.GRAY))
                    .append(Component.text("damage", NamedTextColor.RED))
                    .append(Component.text(" for ", NamedTextColor.GRAY))
                    .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                    .append(Component.text(" seconds!", NamedTextColor.GRAY)));
            warlordsEntity.setRegenTickTimer(1);
            float healthIncrease = warlordsEntity.getMaxHealth() * .25f;
            if (pveMasterUpgrade) {
                healthBoosts.put(warlordsEntity, warlordsEntity.getHealth().addAdditiveModifier("Remedic Chains", healthIncrease));
                warlordsEntity.setCurrentHealth(warlordsEntity.getCurrentHealth() + healthIncrease);
            }
        });
        if (pveMasterUpgrade) {
            float healthIncrease = wp.getMaxHealth() * .25f;
            healthBoosts.put(wp, wp.getHealth().addAdditiveModifier("Remedic Chains", healthIncrease));
            wp.setCurrentHealth(wp.getCurrentHealth() + healthIncrease);
        }
        LinkedCooldown<RemedicChains> remedicChainsCooldown = new LinkedCooldown<>(name,
                "REMEDIC",
                RemedicChains.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                (cooldownManager, linkedCooldown) -> {
                },
                (cooldownManager, linkedCooldown) -> {
                    if (!Objects.equals(cooldownManager.getWarlordsEntity(), wp)) {
                        return;
                    }
                    if (pveMasterUpgrade) {
                        healthBoosts.values().forEach(FloatModifiable.FloatModifier::forceEnd);
                    }
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    Set<WarlordsEntity> linkedEntities = cooldown.getLinkedEntities();
                    if (ticksElapsed % 20 == 0) {
                        Set<WarlordsEntity> linkedEntitiesWithWP = new HashSet<>(linkedEntities);
                        float multiplier = switch (linkedEntitiesWithWP.size()) {
                            case 1 -> healMultiplierOne;
                            case 2 -> healMultiplierTwo;
                            case 3 -> healMultiplierThree;
                            default -> 1f;
                        };
                        linkedEntitiesWithWP.add(wp);
                        linkedEntitiesWithWP.forEach(linked -> {
                            linked.addInstance(InstanceBuilder
                                    .healing()
                                    .ability(this)
                                    .source(wp)
                                    .min(healingValues.chainHealing.getMinValue() * multiplier)
                                    .max(healingValues.chainHealing.getMaxValue() * multiplier)
                            );
                        });
                    }
                    if (ticksElapsed % 8 != 0) {
                        return;
                    }
                    Set<WarlordsEntity> toRemove = new HashSet<>();
                    for (WarlordsEntity linked : linkedEntities) {
                        boolean outOfRange = wp.getLocation().distanceSquared(linked.getLocation()) > linkBreakRadius * linkBreakRadius;
                        if (outOfRange) {
                            linked.getCooldownManager().removeCooldownNoForce(cooldown);
                            Utils.playGlobalSound(linked.getLocation(), "rogue.remedicchains.impact", 0.1f, 1.4f);
                            EffectUtils.displayParticle(Particle.HAPPY_VILLAGER, linked.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 1);
                            stats.numberOfBrokenLinks++;
                        }
                        EffectUtils.playParticleLinkAnimation(wp.getLocation(), linked.getLocation(), 250, 200, 250, 1, 1.25f);
                        if (outOfRange || linked.isDead()) {
                            toRemove.add(linked);
                            if (pveMasterUpgrade) {
                                FloatModifiable.FloatModifier floatModifier = healthBoosts.get(linked);
                                if (floatModifier != null) {
                                    floatModifier.forceEnd();
                                }
                            }
                        }
                    }
                    linkedEntities.removeAll(toRemove);
                    if (linkedEntities.isEmpty()) {
                        cooldown.setTicksLeft(1);
                    }
                }),
                teammatesNear
        ) {

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue + damageValues.getBonusDamage().getValue();
            }

            @Override
            public void onEndFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (!pveMasterUpgrade2) {
                    return;
                }
                if (!event.getCause().contains("Strike")) {
                    return;
                }
                switch (Specializations.getClass(event.getSource().getSpecClass())) {
                    case WARRIOR, PALADIN, ROGUE -> Leech.giveLeechCooldown(Leech.LeechInstance
                            .create(wp, event.getWarlordsEntity())
                            .withImpalingStrike()
                    );
                    default -> {
                    }
                }
            }

            @Override
            public float addEnergyPerHit(WarlordsEntity we, float energyPerHit) {
                if (!pveMasterUpgrade2) {
                    return energyPerHit;
                }
                return switch (Specializations.getClass(we.getSpecClass())) {
                    case MAGE, SHAMAN, ARCANIST -> energyPerHit * 2;
                    default -> energyPerHit;
                };
            }
        };
        wp.getCooldownManager().removeCooldown(RemedicChains.class, false);
        wp.getCooldownManager().addCooldown(remedicChainsCooldown);
        teammatesNear.forEach(entity -> entity.getCooldownManager().removeCooldown(RemedicChains.class, false));
        teammatesNear.forEach(entity -> entity.getCooldownManager().addCooldown(remedicChainsCooldown));
        Bukkit.getPluginManager().callEvent(new WarlordsAbilityTargetEvent.WarlordsBlueAbilityTargetEvent(wp, name, teammatesNear));
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Bind yourself to up to ")
                .text(alliesAffected, NamedTextColor.BLUE)
                .text(" allies near you, increasing the damage they deal by ")
                .damage(damageValues.bonusDamage)
                .text(" and healing all bound for ")
                .heal(healingValues.chainHealing)
                .text(" health per second while the link is active. Lasts ")
                .durationTicks(tickDuration)
                .text(".")
                .emptyLine()
                .text("The link instantly activates natural regeneration and will break if you are more than ")
                .blocks(linkBreakRadius)
                .text(" apart.")
                .initialRange(castRange)
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new RemedicChainsBranch(abilityTree, this);
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
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
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public RemedicChainsStats getAbilityStats() {
        return stats;
    }

    public int getLinkBreakRadius() {
        return linkBreakRadius;
    }

    public void setLinkBreakRadius(int linkBreakRadius) {
        this.linkBreakRadius = linkBreakRadius;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValue chainHealing = new Value.RangedValue(120, 140);

        private List<Value> values = List.of(chainHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.chainHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("chainHealing"), Value.RangedValue.class);
            this.values = List.of(chainHealing);
        }

        public Value.RangedValue getChainHealing() {
            return chainHealing;
        }

    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.SetValue bonusDamage = new Value.SetValue(20);

        private List<Value> values = List.of(bonusDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.bonusDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("bonusDamage"), Value.SetValue.class);
            this.values = List.of(bonusDamage);
        }

        public Value.SetValue getBonusDamage() {
            return bonusDamage;
        }

    }

    public static class RemedicChainsStats extends AbstractAbilityStats<RemedicChains, RemedicChainsStats> {

        @Field("targets_linked")
        private int targetsLinked = 0;

        @Field("number_of_broken_links")
        private int numberOfBrokenLinks = 0;

        @Override
        public Class<RemedicChainsStats> getClazz() {
            return RemedicChainsStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Linked", targetsLinked));
            statsDisplay.add(new AbilityStatDisplay("Times Link Broken", numberOfBrokenLinks));
            return statsDisplay;
        }

        @Override
        public RemedicChainsStats merge(RemedicChainsStats other, int multiplier) {
            RemedicChainsStats stats = super.merge(other, multiplier);
            stats.targetsLinked = this.targetsLinked + other.targetsLinked * multiplier;
            stats.numberOfBrokenLinks = this.numberOfBrokenLinks + other.numberOfBrokenLinks * multiplier;
            return stats;
        }

        @Override
        public RemedicChainsStats create() {
            return new RemedicChainsStats();
        }

    }

}

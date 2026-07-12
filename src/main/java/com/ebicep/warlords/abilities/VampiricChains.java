package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class VampiricChains extends AbstractAbility implements BlueAbilityIcon, Duration, AbilityStats<VampiricChains, VampiricChains.VampiricChainsStats> {

    private final VampiricChainsStats stats = new VampiricChainsStats();
    private int tickDuration = 160;
    private int enemiesAffected = 3;
    private int linkBreakRadius = 18;
    private int castRange = 12;
    private float maxHealthDamage = 2;
    private int healRadius = 6;
    private float healCap = 500;

    public VampiricChains() {
        super(AbstractAbilityBuilder.create("vampiricChains").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.enemiesAffected = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("enemiesAffected"), int.class);
        this.linkBreakRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("linkBreakRadius"), int.class);
        this.castRange = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("castRange"), int.class);
        this.maxHealthDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxHealthDamage"), float.class);
        this.healRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("healRadius"), int.class);
        this.healCap = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("healCap"), float.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Set<WarlordsEntity> enemiesNear = PlayerFilter
                .entitiesAround(wp, castRange, castRange, castRange)
                .aliveEnemiesOf(wp)
                .closestFirst(wp)
                .limit(enemiesAffected)
                .stream()
                .collect(Collectors.toSet());
        if (enemiesNear.isEmpty()) {
            wp.sendMessage(Component.text("There are no enemies nearby to link!", NamedTextColor.RED));
            return false;
        }
        stats.targetsLinked += enemiesNear.size();
        Utils.playGlobalSound(wp.getLocation(), "rogue.remedicchains.activation", 2, 0.1f);
        wp.setRegenTickTimer(1);
        VampiricChainsData data = new VampiricChainsData();
        enemiesNear.forEach(enemy -> {
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_RED
                    .append(Component.text(" Your Vampiric Chains is now draining ", NamedTextColor.GRAY))
                    .append(Component.text(enemy.getName(), NamedTextColor.YELLOW))
                    .append(Component.text("!", NamedTextColor.GRAY)));
            enemy.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED
                    .append(Component.text(" " + wp.getName() + "'s", NamedTextColor.GRAY))
                    .append(Component.text(" Vampiric Chains", NamedTextColor.YELLOW))
                    .append(Component.text(" is now draining your health for ", NamedTextColor.GRAY))
                    .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                    .append(Component.text(" seconds!", NamedTextColor.GRAY)));
        });
        LinkedCooldown<VampiricChainsData> vampiricChainsCooldown = new LinkedCooldown<>(name,
                "VAMP",
                VampiricChainsData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                (cooldownManager, linkedCooldown) -> {
                },
                (cooldownManager, linkedCooldown) -> {
                    if (!Objects.equals(cooldownManager.getWarlordsEntity(), wp)) {
                        return;
                    }
                    if (!data.recasted) {
                        healStoredDamage(wp, data);
                    }
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    Set<WarlordsEntity> linkedEntities = cooldown.getLinkedEntities();
                    if (ticksElapsed % 20 == 0) {
                        for (WarlordsEntity linked : linkedEntities) {
                            float healthDamage = linked.getMaxHealth() * maxHealthDamage / 100f;
                            healthDamage = DamageCheck.clamp(healthDamage);
                            linked.addInstance(InstanceBuilder
                                    .damage()
                                    .ability(this)
                                    .source(wp)
                                    .value(healthDamage))
                                    .ifPresent(finalEvent -> {
                                        data.storedDamage += finalEvent.getValue();
                                        stats.totalDamageStored += finalEvent.getValue();
                                    });
                        }
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
                            EffectUtils.displayParticle(Particle.DAMAGE_INDICATOR, linked.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 1);
                            stats.numberOfBrokenLinks++;
                        }
                        EffectUtils.playParticleLinkAnimation(wp.getLocation(), linked.getLocation(), 200, 50, 50, 1, 1.25f);
                        if (outOfRange || linked.isDead()) {
                            toRemove.add(linked);
                        }
                    }
                    linkedEntities.removeAll(toRemove);
                    if (linkedEntities.isEmpty()) {
                        cooldown.setTicksLeft(1);
                    }
                }),
                enemiesNear
        );
        wp.getCooldownManager().removeCooldown(VampiricChainsData.class, false);
        wp.getCooldownManager().addCooldown(vampiricChainsCooldown);
        enemiesNear.forEach(entity -> entity.getCooldownManager().removeCooldown(VampiricChainsData.class, false));
        enemiesNear.forEach(entity -> entity.getCooldownManager().addCooldown(vampiricChainsCooldown));
        addSecondaryAbility(
                1,
                () -> {
                    data.recasted = true;
                    healStoredDamage(wp, data);
                },
                false,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(vampiricChainsCooldown)
        );
        Bukkit.getPluginManager().callEvent(new WarlordsAbilityTargetEvent.WarlordsBlueAbilityTargetEvent(wp, name, enemiesNear));
        return true;
    }

    private void healStoredDamage(WarlordsEntity wp, VampiricChainsData data) {
        if (data.storedDamage <= 0) {
            return;
        }
        float healAmount = Math.min(data.storedDamage, healCap);
        for (WarlordsEntity ally : PlayerFilter.entitiesAround(wp, healRadius, healRadius, healRadius)
                .aliveTeammatesOf(wp)
                .toList()) {
            stats.damageHealed += healAmount;
            ally.addInstance(InstanceBuilder
                    .healing()
                    .ability(this)
                    .source(wp)
                    .value(healAmount));
            EffectUtils.displayParticle(Particle.HEART, ally.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 1);
        }
        data.storedDamage = 0;
        Utils.playGlobalSound(wp.getLocation(), "rogue.remedicchains.impact", 0.1f, 1.4f);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Bind yourself to up to ")
                .text(enemiesAffected, NamedTextColor.BLUE)
                .text(" enemies near you, dealing ")
                .percent(maxHealthDamage, NamedTextColor.RED)
                .text(" of their max health as damage per second for ")
                .durationTicks(tickDuration)
                .text(" while the link is active.")
                .emptyLine()
                .text("Recast once to heal yourself and all allies within ")
                .blocks(healRadius)
                .text(" for all stored drain damage, up to ")
                .heal(new Value.SetValue(healCap))
                .text(" per ally. If you do not recast, unclaimed damage heals nearby allies when the link ends.")
                .emptyLine()
                .text("The link instantly activates natural regeneration and will break if you are more than ")
                .blocks(linkBreakRadius)
                .text(" apart.")
                .initialRange(castRange)
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
    public VampiricChainsStats getAbilityStats() {
        return stats;
    }

    public int getLinkBreakRadius() {
        return linkBreakRadius;
    }

    public void setLinkBreakRadius(int linkBreakRadius) {
        this.linkBreakRadius = linkBreakRadius;
    }

    public float getMaxHealthDamage() {
        return maxHealthDamage;
    }

    public void setMaxHealthDamage(float maxHealthDamage) {
        this.maxHealthDamage = maxHealthDamage;
    }

    public static class VampiricChainsData {

        private float storedDamage = 0;
        private boolean recasted = false;

        public float getStoredDamage() {
            return storedDamage;
        }

        public boolean hasRecasted() {
            return recasted;
        }

    }

    public static class VampiricChainsStats extends AbstractAbilityStats<VampiricChains, VampiricChainsStats> {

        @Field("targets_linked")
        private int targetsLinked = 0;

        @Field("number_of_broken_links")
        private int numberOfBrokenLinks = 0;

        @Field("total_damage_stored")
        private float totalDamageStored = 0;

        @Field("damage_healed")
        private float damageHealed = 0;

        @Override
        public Class<VampiricChainsStats> getClazz() {
            return VampiricChainsStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Linked", targetsLinked));
            statsDisplay.add(new AbilityStatDisplay("Times Link Broken", numberOfBrokenLinks));
            statsDisplay.add(new AbilityStatDisplay("Total Damage Stored", totalDamageStored));
            statsDisplay.add(new AbilityStatDisplay("Damage Healed", damageHealed));
            return statsDisplay;
        }

        @Override
        public VampiricChainsStats merge(VampiricChainsStats other, int multiplier) {
            VampiricChainsStats stats = super.merge(other, multiplier);
            stats.targetsLinked = this.targetsLinked + other.targetsLinked * multiplier;
            stats.numberOfBrokenLinks = this.numberOfBrokenLinks + other.numberOfBrokenLinks * multiplier;
            stats.totalDamageStored = this.totalDamageStored + other.totalDamageStored * multiplier;
            stats.damageHealed = this.damageHealed + other.damageHealed * multiplier;
            return stats;
        }

        @Override
        public VampiricChainsStats create() {
            return new VampiricChainsStats();
        }

    }

}

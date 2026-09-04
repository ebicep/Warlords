package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.ArcaneShieldBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.*;

public class ArcaneShield extends AbstractAbility implements BlueAbilityIcon, Duration, AbilityStats<ArcaneShield, ArcaneShield.ArcaneShieldStats> {

    private final ArcaneShieldStats stats = new ArcaneShieldStats();
    private int maxShieldHealth;
    private float shieldPercentage = 50;
    private int tickDuration = 120;

    public ArcaneShield() {
        super(AbstractAbilityBuilder.create("arcaneShield").pvp());
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
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.shieldPercentage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("shieldPercentage"), float.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 1);
        Shield shield = new Shield(name, maxShieldHealth);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "ARCA",
                Shield.class,
                shield,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (pveMasterUpgrade2) {
                        List<AbstractAbility> abilities = wp.getAbilities();
                        if (abilities.isEmpty()) {
                            return;
                        }
                        Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 0.5f);
                        EffectUtils.strikeLightning(wp.getLocation(), false);
                        for (WarlordsEntity we : PlayerFilter
                                .entitiesAround(wp, 6, 6, 6)
                                .aliveEnemiesOf(wp)
                                .closestFirst(wp)
                        ) {
                            we.setStunTicks(6 * 20);
                        }
                        AbstractAbility rightClick = abilities.getFirst();
                        FloatModifiable.FloatModifier modifier = rightClick.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER,
                                "Arcane Energy", -.25f
                        );
                        wp.updateItem(rightClick);
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Arcane Energy",
                                "ARC",
                                ArcaneShield.class,
                                new ArcaneShield(),
                                wp,
                                CooldownTypes.ABILITY,
                                cooldownManager2 -> {
                                    modifier.forceEnd();
                                    wp.updateItem(rightClick);
                                },
                                6 * 20,
                                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                    if (ticksElapsed % 3 == 0) {
                                        EffectUtils.displayParticle(Particle.ELECTRIC_SPARK, wp.getLocation().add(0, 1, 0), 10, .4, .4, .4, 0);
                                    }
                                })
                        ));
                    }
                },
                cooldownManager -> {
                    if (shield.isBroken()) {
                        Bukkit.getPluginManager().callEvent(new WarlordsArcaneShieldBrokenEvent(wp));
                        stats.timesBroken++;
                    }
                    stats.totalAbsorbed += shield.getMaxShieldHealth() - Math.max(0, shield.getShieldHealth());
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.5, 0);
                        EffectUtils.displayParticle(Particle.CLOUD, location, 2, 0.15, 0.3, 0.15, 0.01);
                        EffectUtils.displayParticle(Particle.FIREWORK, location, 1, 0.3, 0.3, 0.3, 0.0001);
                        EffectUtils.displayParticle(Particle.WITCH, location, 1, 0.3, 0.3, 0.3, 0);
                    }
                })
        ) {
            @Override
            public PlayerNameData addPrefixFromOther() {
                return PlayerNameData.shieldHealth(shield, we -> we.isTeammate(wp), NamedTextColor.YELLOW);
            }
        });

        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Surround yourself with arcane energy, creating a shield that will absorb up to ")
                                               .percent(shieldPercentage, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" of your maximum health. Lasts ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ArcaneShieldBranch(abilityTree, this);
    }

    @Override
    public void updateCustomStats(WarlordsEntity warlordsEntity) {
        super.updateCustomStats(warlordsEntity);
        if (warlordsEntity != null) {
            setMaxShieldHealth((int) (warlordsEntity.getMaxHealth() * (getShieldPercentage() / 100f)));
            updateDescription(null);
        }
    }

    public void setMaxShieldHealth(int maxShieldHealth) {
        this.maxShieldHealth = maxShieldHealth;
    }

    public float getShieldPercentage() {
        return shieldPercentage;
    }

    public void setShieldPercentage(float shieldPercentage) {
        this.shieldPercentage = shieldPercentage;
    }

    @Override
    public ArcaneShieldStats getAbilityStats() {
        return stats;
    }

    public static class ArcaneShieldStats extends AbstractAbilityStats<ArcaneShield, ArcaneShieldStats> {

        @Field("times_broken")
        private int timesBroken = 0;

        @Field("total_absorbed")
        private float totalAbsorbed = 0;

        @Override
        public Class<ArcaneShieldStats> getClazz() {
            return ArcaneShieldStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Times Broken", timesBroken));
            statsDisplay.add(new AbilityStatDisplay("Total Absorbed", totalAbsorbed));
            return statsDisplay;
        }

        @Override
        public ArcaneShieldStats merge(ArcaneShieldStats other, int multiplier) {
            ArcaneShieldStats stats = super.merge(other, multiplier);
            stats.timesBroken = this.timesBroken + other.timesBroken * multiplier;
            stats.totalAbsorbed = this.totalAbsorbed + other.totalAbsorbed * multiplier;
            return stats;
        }

        @Override
        public ArcaneShieldStats create() {
            return new ArcaneShieldStats();
        }

    }

    public static class WarlordsArcaneShieldBrokenEvent extends AbstractWarlordsEntityEvent {

        private static final HandlerList handlers = new HandlerList();

        public static HandlerList getHandlerList() {
            return handlers;
        }


        public WarlordsArcaneShieldBrokenEvent(@Nonnull WarlordsEntity player) {
            super(player);
        }

        @Nonnull
        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

    }

}

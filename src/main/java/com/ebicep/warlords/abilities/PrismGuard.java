package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.vindicator.PrismGuardBranch;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.*;

import static com.ebicep.warlords.effects.EffectUtils.playSphereAnimation;

public class PrismGuard extends AbstractAbility implements BlueAbilityIcon, Duration, Heals<PrismGuard.HealingValues>, AbilityStats<PrismGuard, PrismGuard.PrismGuardStats> {

    private final PrismGuardStats stats = new PrismGuardStats();
    private final HealingValues healingValues = new HealingValues();
    private int damageReduction = 3;
    private int bubbleRadius = 4;
    private int tickDuration = 100;
    private int projectileDamageReduction = 40;
    private int maxHealing = 1200;
    private int maxDamageReduction = 21;

    public PrismGuard() {
        super(AbstractAbilityBuilder.create("prismGuard").pvp());
    }

    public PrismGuard(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.damageReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReduction"), int.class);
        this.bubbleRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("bubbleRadius"), int.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.projectileDamageReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("projectileDamageReduction"), int.class);
        this.maxHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxHealing"), int.class);
        this.maxDamageReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxDamageReduction"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.teleport", 2, 2);
        Utils.playGlobalSound(wp.getLocation(), "warrior.intervene.impact", 2, 0.1f);
        // First Particle Sphere
        playSphereAnimation(wp.getLocation(), bubbleRadius + 2.5, 68, 176, 236);
        // Second Particle Sphere
        new GameRunnable(wp.getGame()) {

            @Override
            public void run() {
                playSphereAnimation(wp.getLocation(), bubbleRadius + 1, 65, 185, 205);
                Utils.playGlobalSound(wp.getLocation(), "warrior.intervene.impact", 2, 0.2f);
            }
        }.runTaskLater(3);
        Set<WarlordsEntity> isInsideBubble = new HashSet<>();
        Set<WarlordsEntity> playersHit = new HashSet<>();
        PrismGuardData data = new PrismGuardData(this);
        wp.getCooldownManager().removeCooldown(PrismGuardData.class, false);
        RegularCooldown<PrismGuardData> prismGuardCooldown = new RegularCooldown<>(
                "Prism Guard",
                "GUARD",
                PrismGuardData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (data.totalDamageReduced >= 8000) {
                        ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.VENERED_REFRACTION);
                    }
                    if (wp.isDead()) {
                        return;
                    }
                    Utils.playGlobalSound(wp.getLocation(), "paladin.holyradiance.activation", 2, 1.4f);
                    Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 1.5f);
                    new CircleEffect(wp.getGame(),
                            wp.getTeam(),
                            wp.getLocation(),
                            bubbleRadius,
                            new CircumferenceEffect(Particle.EFFECT).particlesPerCircumference(2)
                    ).playEffects();
                    float baseHealing = healingValues.bubbleBaseHealing.getValue();
                    float additionalHealing = data.hitsTaken * healingValues.bubbleMissingHealthHealing.getMultiplicativePercent();
                    for (WarlordsEntity entity : PlayerFilter.entitiesAround(wp, bubbleRadius + 1, bubbleRadius + 1, bubbleRadius + 1).aliveTeammatesOf(wp)) {
                        float missingHealth = entity.getMaxHealth() - entity.getCurrentHealth();
                        entity.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(Math.min(maxHealing, baseHealing + missingHealth * additionalHealing)));
                        if (data.hitsTaken == 0) {
                            continue;
                        }
                        String s = wp == entity ? "Your" : wp.getName() + "'s";
                        int damageReduction = Math.min(maxDamageReduction, data.hitsTaken * PrismGuard.this.damageReduction);
                        entity.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                .append(Component.text(" " + s + " Prism Guard granted you ", NamedTextColor.GRAY))
                                .append(Component.text(damageReduction + "%", NamedTextColor.YELLOW))
                                .append(Component.text(" damage reduction for ", NamedTextColor.GRAY))
                                .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                                .append(Component.text(" seconds!", NamedTextColor.GRAY)));
                        entity.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Prism Guard Resistance",
                                "GUARD RES",
                                PrismGuardData.class,
                                data,
                                wp,
                                CooldownTypes.ABILITY,
                                cm -> {
                                },
                                tickDuration
                        ) {

                            @Override
                            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                float afterReduction = currentDamageValue * convertToDivisionDecimal(damageReduction);
                                data.totalDamageReduced += currentDamageValue - afterReduction;
                                return afterReduction;
                            }
                        });
                    }
                }, tickDuration, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
            if (ticksElapsed < 5) {
                return;
            }
            if (ticksElapsed % 5 == 0) {
                playSphereAnimation(wp.getLocation(), bubbleRadius, 120, 120, 220);
                Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_CREEPER_DEATH, 2, 2);
            }
            if (ticksElapsed % 3 == 0) {
                isInsideBubble.clear();
                for (WarlordsEntity enemyInsideBubble : PlayerFilter.entitiesAround(wp, bubbleRadius, bubbleRadius, bubbleRadius).aliveEnemiesOf(wp)) {
                    isInsideBubble.add(enemyInsideBubble);
                }
                for (WarlordsEntity bubblePlayer : PlayerFilter.entitiesAround(wp, bubbleRadius, bubbleRadius, bubbleRadius).aliveTeammatesOfExcludingSelf(wp)) {
                    if (!playersHit.contains(bubblePlayer)) {
                        Bukkit.getPluginManager().callEvent(new WarlordsAbilityTargetEvent.WarlordsBlueAbilityTargetEvent(wp, name, Set.of(bubblePlayer)));
                    }
                    playersHit.add(bubblePlayer);
                    bubblePlayer.getCooldownManager().removeCooldownByObject(data);
                    bubblePlayer.getCooldownManager().addCooldown(new RegularCooldown<>(
                            "Prism Guard Bubble",
                            "GUARD",
                            PrismGuardData.class,
                            data,
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManager -> {
                            },
                            4
                    ) {

                        @Override
                        public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            float afterReduction;
                            if (Utils.isProjectile(event.getCause())) {
                                if (isInsideBubble.contains(event.getSource())) {
                                    afterReduction = currentDamageValue;
                                } else {
                                    stats.timesProjectilesReduced++;
                                    afterReduction = currentDamageValue * (100 - projectileDamageReduction) / 100f;
                                }
                            } else {
                                afterReduction = currentDamageValue;
                            }
                            data.totalDamageReduced += currentDamageValue - afterReduction;
                            return afterReduction;
                        }
                    });
                }
            }
            if (ticksElapsed % 10 == 0) {
                if (pveMasterUpgrade) {
                    for (WarlordsEntity we : PlayerFilter.entitiesAround(wp, 15, 15, 15).aliveEnemiesOf(wp).closestFirst(wp)) {
                        if (we instanceof WarlordsNPC) {
                            ((WarlordsNPC) we).getMob().setTarget(wp);
                        }
                    }
                }
            }
        })
        ) {

            @Override
            protected Listener getListener() {
                if (!pveMasterUpgrade2) {
                    return super.getListener();
                }
                return new Listener() {

                    @EventHandler
                    public void onDamageHeal(WarlordsDamageHealingEvent event) {
                        WarlordsEntity attacker = event.getSource();
                        if (attacker.isTeammate(wp)) {
                            return;
                        }
                        if (attacker.getLocation().distanceSquared(wp.getLocation()) > bubbleRadius * bubbleRadius) {
                            return;
                        }
                        if (event.getCause().isEmpty()) {
                            event.setMin(event.getMin() * .75f);
                            event.setMax(event.getMax() * .75f);
                        }
                    }
                };
            }

            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                int totalReduction = 0;
                data.hitsTaken++;
                if (Utils.isProjectile(event.getCause())) {
                    if (!isInsideBubble.contains(event.getSource())) {
                        stats.timesProjectilesReduced++;
                        totalReduction += projectileDamageReduction;
                    }
                }
                if (pveMasterUpgrade) {
                    totalReduction += 10;
                }
                float afterReduction = currentDamageValue * (100 - totalReduction) / 100f;
                data.totalDamageReduced += currentDamageValue - afterReduction;
                return afterReduction;
            }
        };
        if (pveMasterUpgrade) {
            wp.addKnockbackModifier(wp, name, -100, prismGuardCooldown);
        }
        wp.getCooldownManager().addCooldown(prismGuardCooldown);
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Create a bubble shield around you. All projectiles that pass through the barrier have their damage reduced by ")
                                               .percent(projectileDamageReduction, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(".")
                                               .emptyLine()
                                               .text("After ")
                                               .durationTicks(tickDuration)
                                               .text(" the bubble will burst, healing you and all allies for ")
                                               .heal(healingValues.bubbleBaseHealing)
                                               .text(" + ")
                                               .percent(healingValues.bubbleMissingHealthHealing.getValue(), NamedTextColor.GREEN)
                                               .text(" missing health and grant ")
                                               .percent(damageReduction, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" damage reduction for ")
                                               .durationTicks(tickDuration)
                                               .text(" based on how many hits you took while Prism Guard was active; up to a maximum of ")
                                               .text(maxHealing, NamedTextColor.GREEN)
                                               .text(" health and ")
                                               .percent(maxDamageReduction, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" damage reduction.")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new PrismGuardBranch(abilityTree, this);
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
    public PrismGuardStats getAbilityStats() {
        return stats;
    }

    public int getProjectileDamageReduction() {
        return projectileDamageReduction;
    }

    public void setProjectileDamageReduction(int projectileDamageReduction) {
        this.projectileDamageReduction = projectileDamageReduction;
    }

    public int getBubbleRadius() {
        return bubbleRadius;
    }

    public void setBubbleRadius(int bubbleRadius) {
        this.bubbleRadius = bubbleRadius;
    }

    public int getMaxHealing() {
        return maxHealing;
    }

    public void setMaxHealing(int maxHealing) {
        this.maxHealing = maxHealing;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.SetValue bubbleBaseHealing = new Value.SetValue(200);

        private Value.SetValue bubbleMissingHealthHealing = new Value.SetValue(1.5f);

        private List<Value> values = List.of(bubbleMissingHealthHealing, bubbleMissingHealthHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.bubbleBaseHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("bubbleBaseHealing"), Value.SetValue.class);
            this.bubbleMissingHealthHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("bubbleMissingHealthHealing"),
                    Value.SetValue.class
            );
            this.values = List.of(bubbleMissingHealthHealing, bubbleMissingHealthHealing);
        }

        public Value.SetValue getBubbleBaseHealing() {
            return bubbleBaseHealing;
        }

        public Value.SetValue getBubbleMissingHealthHealing() {
            return bubbleMissingHealthHealing;
        }

    }

    public static class PrismGuardData {

        private PrismGuard prismGuard;
        private int hitsTaken = 0;
        private float totalDamageReduced = 0;

        public PrismGuardData(PrismGuard prismGuard) {
            this.prismGuard = prismGuard;
        }

        public PrismGuard getPrismGuard() {
            return prismGuard;
        }

    }

    public static class PrismGuardStats extends AbstractAbilityStats<PrismGuard, PrismGuardStats> {

        @Field("times_projectiles_reduced")
        private int timesProjectilesReduced = 0;

        @Override
        public Class<PrismGuardStats> getClazz() {
            return PrismGuardStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Times Projectiles Damage Reduced", timesProjectilesReduced));
            return statsDisplay;
        }

        @Override
        public PrismGuardStats merge(PrismGuardStats other, int multiplier) {
            PrismGuardStats stats = super.merge(other, multiplier);
            stats.timesProjectilesReduced = this.timesProjectilesReduced + other.timesProjectilesReduced * multiplier;
            return stats;
        }

        @Override
        public PrismGuardStats create() {
            return new PrismGuardStats();
        }

    }

}

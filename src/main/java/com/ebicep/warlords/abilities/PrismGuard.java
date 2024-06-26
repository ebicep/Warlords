package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
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
import com.ebicep.warlords.util.java.Pair;
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
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ebicep.warlords.effects.EffectUtils.playSphereAnimation;

public class PrismGuard extends AbstractAbility implements BlueAbilityIcon, Duration, Heals<PrismGuard.HealingValues> {

    public int timesProjectilesReduced = 0;
    public int timesOtherReduced = 0;

    protected float damageReduced = 0;

    private final int damageReduction = 3;
    private final HealingValues healingValues = new HealingValues();
    private int bubbleRadius = 4;
    private int tickDuration = 100;
    private int projectileDamageReduction = 60;

    public PrismGuard() {
        super("Prism Guard", 26, 40);
    }

    public PrismGuard(float cooldown) {
        super("Prism Guard", cooldown, 40);
    }

    public PrismGuard(float cooldown, float startingCooldown) {
        super("Prism Guard", cooldown, 40, startingCooldown);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Create a bubble shield around you that lasts ")
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. All projectiles that pass through the barrier have their damage reduced by "))
                               .append(Component.text(projectileDamageReduction + "%", NamedTextColor.RED))
                               .append(Component.text(".\n\nAfter "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds the bubble will burst, healing you and all allies for "))
                               .append(Heals.formatHealing(healingValues.bubbleBaseHealing))
                               .append(Component.text(" + "))
                               .append(Heals.formatHealingPercent(healingValues.bubbleMissingHealthHealing))
                               .append(Component.text(" missing health and grant "))
                               .append(Component.text(damageReduction + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" damage reduction for "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds based on how many hits you took while Prism Guard was active; up to a maximum of "))
                               .append(Component.text(1200, NamedTextColor.YELLOW))
                               .append(Component.text(" health and "))
                               .append(Component.text("21%", NamedTextColor.YELLOW))
                               .append(Component.text(" damage reduction."))
        ;

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Projectiles Damage Reduced", "" + timesProjectilesReduced));
        info.add(new Pair<>("Times Other Damage Reduced", "" + timesOtherReduced));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

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
        AtomicInteger hits = new AtomicInteger(0);
        PrismGuard tempPrismGuard = new PrismGuard();
        wp.getCooldownManager().removeCooldown(PrismGuard.class, false);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Prism Guard",
                "GUARD",
                PrismGuard.class,
                tempPrismGuard,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (tempPrismGuard.getDamageReduced() >= 8000) {
                        ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.VENERED_REFRACTION);
                    }
                    if (wp.isDead()) {
                        return;
                    }
                    Utils.playGlobalSound(wp.getLocation(), "paladin.holyradiance.activation", 2, 1.4f);
                    Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 1.5f);

                    new CircleEffect(
                            wp.getGame(),
                            wp.getTeam(),
                            wp.getLocation(),
                            bubbleRadius,
                            new CircumferenceEffect(Particle.SPELL).particlesPerCircumference(2)
                    ).playEffects();

                    float baseHealing = healingValues.bubbleBaseHealing.getValue();
                    for (WarlordsEntity entity : PlayerFilter
                            .entitiesAround(wp, bubbleRadius + 1, bubbleRadius + 1, bubbleRadius + 1)
                            .aliveTeammatesOf(wp)
                    ) {
                        float missingHealthHealing = (entity.getMaxHealth() - entity.getCurrentHealth()) * (hits.get() * healingValues.bubbleMissingHealthHealing.getMultiplicativePercent());
                        entity.addInstance(InstanceBuilder
                                .healing()
                                .ability(this)
                                .source(wp)
                                .value(Math.min(1200, baseHealing + missingHealthHealing))
                        );


                        if (hits.get() > 7) {
                            hits.set(7);
                        }

                        if (hits.get() != 0) {
                            String s = wp == entity ? "Your" : wp.getName() + "'s";
                            entity.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" " + s + " Prism Guard granted you ", NamedTextColor.GRAY))
                                                                              .append(Component.text(hits.get() * damageReduction + "%", NamedTextColor.YELLOW))
                                                                              .append(Component.text(" damage reduction for ", NamedTextColor.GRAY))
                                                                              .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                                                                              .append(Component.text(" seconds!", NamedTextColor.GRAY))
                            );
                            entity.getCooldownManager().addCooldown(new RegularCooldown<>(
                                    "Prism Guard",
                                    "GUARD RES",
                                    PrismGuard.class,
                                    tempPrismGuard,
                                    wp,
                                    CooldownTypes.ABILITY,
                                    cm -> {
                                    },
                                    tickDuration
                            ) {
                                @Override
                                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                    float afterReduction;
                                    afterReduction = currentDamageValue * (100 - (hits.get() * 3)) / 100f;
                                    tempPrismGuard.addDamageReduced(currentDamageValue - afterReduction);
                                    return afterReduction;
                                }
                            });
                        }
                    }
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed < 5) {
                        return;
                    }

                    if (ticksElapsed % 5 == 0) {
                        playSphereAnimation(wp.getLocation(), bubbleRadius, 120, 120, 220);
                        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_CREEPER_DEATH, 2, 2);
                    }

                    if (ticksElapsed % 3 == 0) {
                        isInsideBubble.clear();
                        for (WarlordsEntity enemyInsideBubble : PlayerFilter
                                .entitiesAround(wp, bubbleRadius, bubbleRadius, bubbleRadius)
                                .aliveEnemiesOf(wp)
                        ) {
                            isInsideBubble.add(enemyInsideBubble);
                        }

                        for (WarlordsEntity bubblePlayer : PlayerFilter
                                .entitiesAround(wp, bubbleRadius, bubbleRadius, bubbleRadius)
                                .aliveTeammatesOfExcludingSelf(wp)
                        ) {
                            if (!playersHit.contains(bubblePlayer)) {
                                Bukkit.getPluginManager()
                                      .callEvent(new WarlordsAbilityTargetEvent.WarlordsBlueAbilityTargetEvent(wp, name, Set.of(bubblePlayer)));
                            }
                            playersHit.add(bubblePlayer);
                            bubblePlayer.getCooldownManager().removeCooldown(PrismGuard.class, false);
                            bubblePlayer.getCooldownManager().addCooldown(new RegularCooldown<>(
                                    "Prism Guard",
                                    "GUARD",
                                    PrismGuard.class,
                                    tempPrismGuard,
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
                                            timesProjectilesReduced++;
                                            afterReduction = currentDamageValue * (100 - projectileDamageReduction) / 100f;
                                        }
                                    } else {
                                        afterReduction = currentDamageValue;
                                    }
                                    tempPrismGuard.addDamageReduced(currentDamageValue - afterReduction);
                                    return afterReduction;
                                }
                            });
                        }
                    }

                    if (ticksElapsed % 10 == 0) {
                        if (pveMasterUpgrade) {
                            for (WarlordsEntity we : PlayerFilter
                                    .entitiesAround(wp, 15, 15, 15)
                                    .aliveEnemiesOf(wp)
                                    .closestFirst(wp)
                            ) {
                                if (we instanceof WarlordsNPC) {
                                    ((WarlordsNPC) we).getMob().setTarget(wp);
                                }
                            }
                        }
                    }
                })
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                int totalReduction = 0;
                hits.getAndIncrement();
                if (Utils.isProjectile(event.getCause())) {
                    if (!isInsideBubble.contains(event.getSource())) {
                        timesProjectilesReduced++;
                        totalReduction += projectileDamageReduction;
                    }
                }
                if (pveMasterUpgrade) {
                    totalReduction += 10;
                }
                float afterReduction = currentDamageValue * (100 - totalReduction) / 100f;
                tempPrismGuard.addDamageReduced(currentDamageValue - afterReduction);
                return afterReduction;
            }

            @Override
            public void multiplyKB(Vector currentVector) {
                if (pveMasterUpgrade2) {
                    currentVector.multiply(.001);
                }
            }

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
        });

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new PrismGuardBranch(abilityTree, this);
    }

    public float getDamageReduced() {
        return damageReduced;
    }

    public void addDamageReduced(float amount) {
        damageReduced += amount;
    }

    public int getProjectileDamageReduction() {
        return projectileDamageReduction;
    }

    public void setProjectileDamageReduction(int projectileDamageReduction) {
        this.projectileDamageReduction = projectileDamageReduction;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getBubbleRadius() {
        return bubbleRadius;
    }

    public void setBubbleRadius(int bubbleRadius) {
        this.bubbleRadius = bubbleRadius;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.SetValue bubbleBaseHealing = new Value.SetValue(200);
        private final Value.SetValue bubbleMissingHealthHealing = new Value.SetValue(1.5f);
        private final List<Value> values = List.of(bubbleMissingHealthHealing, bubbleMissingHealthHealing);

        public Value.SetValue getBubbleBaseHealing() {
            return bubbleBaseHealing;
        }

        public Value.SetValue getBubbleMissingHealthHealing() {
            return bubbleMissingHealthHealing;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}

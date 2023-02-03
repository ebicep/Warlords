package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsBlueAbilityTargetEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ebicep.warlords.effects.EffectUtils.playSphereAnimation;

public class PrismGuard extends AbstractAbility {

    public int timesProjectilesReduced = 0;
    public int timesOtherReduced = 0;

    protected float damageReduced = 0;

    private final int damageReduction = 3;
    private int bubbleRadius = 4;
    private int duration = 6;
    private int bubbleHealing = 200;
    private float bubbleMissingHealing = 1.5f;
    private int projectileDamageReduction = 75;

    public PrismGuard() {
        super("Prism Guard", 0, 0, 26, 40, 0, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Create a bubble shield around you that lasts §6" + duration +
                " §7seconds. All projectiles that pass through the barrier have their damage reduced by §c" + projectileDamageReduction +
                "%§7.\nAfter §6" + duration + " §7seconds the bubble will burst, healing you and all allies for §a" + bubbleHealing +
                " §7+ §a" + bubbleMissingHealing + "% §7missing health and grant §e" + damageReduction +
                "% §7damage reduction (max 30%) for §6" + duration + " §7seconds based on how many hits you took while Prism Guard was active.";
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
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
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
        wp.getCooldownManager().addCooldown(new RegularCooldown<PrismGuard>(
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
                    Utils.playGlobalSound(wp.getLocation(), Sound.AMBIENCE_THUNDER, 2, 1.5f);

                    new CircleEffect(
                            wp.getGame(),
                            wp.getTeam(),
                            wp.getLocation(),
                            bubbleRadius,
                            new CircumferenceEffect(ParticleEffect.SPELL).particlesPerCircumference(2)
                    ).playEffects();

                    for (WarlordsEntity entity : PlayerFilter
                            .entitiesAround(wp, bubbleRadius + 1, bubbleRadius + 1, bubbleRadius + 1)
                            .aliveTeammatesOf(wp)
                    ) {
                        float healingValue = bubbleHealing + (entity.getMaxHealth() - entity.getHealth()) * (hits.get() * (convertToPercent(bubbleMissingHealing)));
                        entity.addHealingInstance(
                                wp,
                                name,
                                healingValue,
                                healingValue,
                                0,
                                100,
                                false,
                                false
                        );

                        if (hits.get() > 10) {
                            hits.set(10);
                        }

                        if (hits.get() != 0) {
                            String s = wp == entity ? "Your " : wp.getName() + "'s ";
                            entity.sendMessage(
                                    WarlordsEntity.GIVE_ARROW_GREEN + " §7" + s + "§7Prism Guard granted you §e" +
                                            (hits.get() * damageReduction) + "% §7damage reduction for §6" + duration + " §7seconds!"
                            );
                            entity.getCooldownManager().addCooldown(new RegularCooldown<PrismGuard>(
                                    "Prism Guard",
                                    "GUARD RES",
                                    PrismGuard.class,
                                    tempPrismGuard,
                                    wp,
                                    CooldownTypes.ABILITY,
                                    cm -> {
                                    },
                                    duration * 20
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
                duration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed < 5) {
                        return;
                    }

                    if (ticksElapsed % 4 == 0) {
                        playSphereAnimation(wp.getLocation(), bubbleRadius, 120, 120, 220);
                        Utils.playGlobalSound(wp.getLocation(), Sound.CREEPER_DEATH, 2, 2);

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
                                Bukkit.getPluginManager().callEvent(new WarlordsBlueAbilityTargetEvent(wp, Set.of(bubblePlayer)));
                            }
                            playersHit.add(bubblePlayer);
                            bubblePlayer.getCooldownManager().removeCooldown(PrismGuard.class, false);
                            bubblePlayer.getCooldownManager().addCooldown(new RegularCooldown<PrismGuard>(
                                    "Prism Guard",
                                    "GUARD",
                                    PrismGuard.class,
                                    tempPrismGuard,
                                    wp,
                                    CooldownTypes.ABILITY,
                                    cooldownManager -> {
                                    },
                                    10
                            ) {
                                @Override
                                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                    float afterReduction;
                                    if (isProjectile(event.getAbility())) {
                                        if (isInsideBubble.contains(event.getAttacker())) {
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
                        if (pveUpgrade) {
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
                float afterReduction;
                hits.getAndIncrement();
                if (isProjectile(event.getAbility())) {
                    if (isInsideBubble.contains(event.getAttacker())) {
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

        return true;
    }

    public float getDamageReduced() {
        return damageReduced;
    }

    public void addDamageReduced(float amount) {
        damageReduced += amount;
    }

    private boolean isProjectile(String ability) {
        return ability.equals("Fireball") ||
                ability.equals("Frostbolt") ||
                ability.equals("Water Bolt") ||
                ability.equals("Lightning Bolt") ||
                ability.equals("Flame Burst") ||
                ability.equals("Fallen Souls") ||
                ability.equals("Soothing Elixir");
    }

    public int getProjectileDamageReduction() {
        return projectileDamageReduction;
    }

    public void setProjectileDamageReduction(int projectileDamageReduction) {
        this.projectileDamageReduction = projectileDamageReduction;
    }

    public int getBubbleHealing() {
        return bubbleHealing;
    }

    public void setBubbleHealing(int bubbleHealing) {
        this.bubbleHealing = bubbleHealing;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getBubbleRadius() {
        return bubbleRadius;
    }

    public void setBubbleRadius(int bubbleRadius) {
        this.bubbleRadius = bubbleRadius;
    }

    public float getBubbleMissingHealing() {
        return bubbleMissingHealing;
    }

    public void setBubbleMissingHealing(float bubbleMissingHealing) {
        this.bubbleMissingHealing = bubbleMissingHealing;
    }


}

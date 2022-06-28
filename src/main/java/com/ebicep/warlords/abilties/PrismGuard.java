package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsEntity;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;

import static com.ebicep.warlords.effects.EffectUtils.playSphereAnimation;

public class PrismGuard extends AbstractAbility {
    protected int timesProjectilesReduced = 0;
    protected int timesOtherReduced = 0;

    private final int bubbleRadius = 4;
    private final int duration = 4;
    private int bubbleHealing = 400;
    private int projectileDamageReduction = 60;
    private int damageReduction = 25;

    public PrismGuard() {
        super("Prism Guard", 0, 0, 24, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Create a bubble shield around you that\n" +
                "§7lasts §6" + duration + " §7seconds. All projectiles that pass through\n" +
                "§7the barrier have their damage reduced by §c" + projectileDamageReduction + "%§7.\n" +
                "§7Additionally, other damage taken by all allies inside\n" +
                "§7the bubble is reduced by §c" + damageReduction + "%§7." +
                "\n\n" +
                "§7After §6" + duration + " §7seconds the bubble will burst, healing\n" +
                "§7you for §a" + bubbleHealing + " §7+ §a15% §7missing health and\n" +
                "§7allies for half the amount based on how long\n" +
                "§7they've been in the bubble.\n";
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
        wp.subtractEnergy(energyCost);
        Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.teleport", 2, 2);
        Utils.playGlobalSound(player.getLocation(), "warrior.intervene.impact", 2, 0.1f);

        // First Particle Sphere
        playSphereAnimation(wp.getLocation(), bubbleRadius + 2.5, 68, 176, 176);

        // Second Particle Sphere
        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                playSphereAnimation(wp.getLocation(), bubbleRadius + 1, 65, 185, 185);
                Utils.playGlobalSound(wp.getLocation(), "warrior.intervene.impact", 2, 0.2f);
            }
        }.runTaskLater(3);

        Set<WarlordsEntity> isInsideBubble = new HashSet<>();
        HashMap<WarlordsEntity, Integer> timeInBubble = new HashMap<>();

        PrismGuard tempWideGuard = new PrismGuard();
        wp.getCooldownManager().addCooldown(new RegularCooldown<PrismGuard>(
                "Prism Guard",
                "GUARD",
                PrismGuard.class,
                tempWideGuard,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (wp.isDead()) return;
                    Utils.playGlobalSound(wp.getLocation(), "paladin.holyradiance.activation", 2, 1.4f);
                    Utils.playGlobalSound(wp.getLocation(), Sound.AMBIENCE_THUNDER, 2, 1.5f);

                    new CircleEffect(
                            wp.getGame(),
                            wp.getTeam(),
                            wp.getLocation(),
                            bubbleRadius,
                            new CircumferenceEffect(ParticleEffect.SPELL).particlesPerCircumference(2)
                    ).playEffects();

                    float healingValue = bubbleHealing + (wp.getMaxHealth() - wp.getHealth()) * 0.15f;
                    wp.addHealingInstance(
                            wp,
                            name,
                            healingValue,
                            healingValue,
                            -1,
                            100,
                            false,
                            false
                    );

                    for (Map.Entry<WarlordsEntity, Integer> entry : timeInBubble.entrySet()) {
                        // Divide by 8 = half healing for allies, 600 / 4 = 150
                        float teammateHealingValue = (bubbleHealing / 8f) + (entry.getKey().getMaxHealth() - entry.getKey().getHealth()) * 0.0375f;
                        int timeInSeconds = entry.getValue() * 3 / 20;
                        float totalHealing = (timeInSeconds * teammateHealingValue);
                        entry.getKey().addHealingInstance(
                                wp,
                                name,
                                totalHealing,
                                totalHealing,
                                -1,
                                100,
                                false,
                                false
                        );
                    }
                },
                duration * 20,
                (cooldown, ticksLeft, counter) -> {
                    if (counter < 5) return;

                    if (counter % 3 == 0) {
                        playSphereAnimation(wp.getLocation(), bubbleRadius, 190, 190, 190);
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
                            bubblePlayer.getCooldownManager().removeCooldown(PrismGuard.class);
                            bubblePlayer.getCooldownManager().addCooldown(new RegularCooldown<PrismGuard>(
                                    "Prism Guard",
                                    "GUARD",
                                    PrismGuard.class,
                                    tempWideGuard,
                                    wp,
                                    CooldownTypes.ABILITY,
                                    cooldownManager -> {
                                    },
                                    20
                            ) {
                                @Override
                                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                    String ability = event.getAbility();
                                    if (isProjectile(ability)) {
                                        if (isInsideBubble.contains(event.getAttacker())) {
                                            return currentDamageValue;
                                        } else {
                                            timesProjectilesReduced++;
                                            return currentDamageValue * (100 - projectileDamageReduction) / 100f;
                                        }
                                    } else {
                                        timesOtherReduced++;
                                        return currentDamageValue * (100 - damageReduction) / 100f;
                                    }
                                }
                            });
                            timeInBubble.compute(bubblePlayer, (k, v) -> v == null ? 1 : v + 1);
                        }
                    }
                }
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                String ability = event.getAbility();
                if (isProjectile(ability)) {
                    if (isInsideBubble.contains(event.getAttacker())) {
                        return currentDamageValue;
                    } else {
                        timesProjectilesReduced++;
                        return currentDamageValue * (100 - projectileDamageReduction) / 100f;
                    }
                } else {
                    timesOtherReduced++;
                    return currentDamageValue * (100 - damageReduction) / 100f;
                }
            }
        });

        return true;
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

    public int getDamageReduction() {
        return damageReduction;
    }

    public void setDamageReduction(int damageReduction) {
        this.damageReduction = damageReduction;
    }

    public int getBubbleHealing() {
        return bubbleHealing;
    }

    public void setBubbleHealing(int bubbleHealing) {
        this.bubbleHealing = bubbleHealing;
    }
}

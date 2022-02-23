package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static com.ebicep.warlords.util.EffectUtils.playSphereAnimation;

public class PrismGuard extends AbstractAbility {

    public static final int BUBBLE_RADIUS = 4;

    public PrismGuard() {
        super("Prism Guard", 0, 0, 22, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Create a bubble shield around you that\n" +
                "§7lasts §64 §7seconds. All projectiles that pass through\n" +
                "§7the barrier have their damage reduced by §c75%§7.\n" +
                "§7(§c25% §7for all other attacks.)" +
                "\n\n" +
                "§7After §64 §7seconds the bubble will burst, healing\n" +
                "§7all allies for up to §a600 §7+ §a20% §7missing health\n" +
                "§7based on how long they've been in the bubble.\n";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);
        PrismGuard tempWideGuard = new PrismGuard();
        wp.getCooldownManager().addCooldown(new RegularCooldown<PrismGuard>(
                "Prism Guard",
                "GUARD",
                PrismGuard.class,
                tempWideGuard,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                4 * 20
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                String ability = event.getAbility();
                if (
                        ability.equals("Fireball") ||
                        ability.equals("Frostbolt") ||
                        ability.equals("Water Bolt") ||
                        ability.equals("Lightning Bolt") ||
                        ability.equals("Flame Burst")
                ) {
                    return currentDamageValue * .3f;
                }
                return currentDamageValue;
            }
        });

        Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.teleport", 2, 2);
        Utils.playGlobalSound(player.getLocation(), "warrior.intervene.impact", 2, 0.1f);


        // First Particle Sphere
        playSphereAnimation(wp.getLocation(), BUBBLE_RADIUS + 2.5, 68, 176, 176);

        // Second Particle Sphere
        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                playSphereAnimation(wp.getLocation(), BUBBLE_RADIUS + 1, 65, 185, 185);
                Utils.playGlobalSound(wp.getLocation(), "warrior.intervene.impact", 2, 0.2f);
            }
        }.runTaskLater(3);

        HashMap<WarlordsPlayer, Integer> timeInBubble = new HashMap<>();

        // Third Particle Sphere
        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (wp.getCooldownManager().hasCooldown(tempWideGuard)) {

                    playSphereAnimation(wp.getLocation(), BUBBLE_RADIUS, 190, 190, 190);
                    Utils.playGlobalSound(wp.getLocation(), Sound.CREEPER_DEATH, 2, 2);
                    timeInBubble.compute(wp, (k, v) -> v == null ? 1 : v + 1);

                    for (WarlordsPlayer bubblePlayer : PlayerFilter
                            .entitiesAround(wp, BUBBLE_RADIUS, BUBBLE_RADIUS, BUBBLE_RADIUS)
                            .aliveTeammatesOfExcludingSelf(wp)
                    ) {
                        bubblePlayer.getCooldownManager().removeCooldown(PrismGuard.class);
                        bubblePlayer.getCooldownManager().addCooldown(new RegularCooldown<PrismGuard>(
                                "Wide Guard",
                                "GUARD",
                                PrismGuard.class,
                                tempWideGuard,
                                wp,
                                CooldownTypes.ABILITY,
                                cooldownManager -> {
                                },
                                4 * 20
                        ) {
                                    @Override
                                    public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                        String ability = event.getAbility();
                                        if (
                                                ability.equals("Fireball") ||
                                                        ability.equals("Frostbolt") ||
                                                        ability.equals("Water Bolt") ||
                                                        ability.equals("Lightning Bolt") ||
                                                        ability.equals("Flame Burst")
                                        ) {
                                            return currentDamageValue * .3f;
                                        }
                                        return currentDamageValue;
                                    }
                                });
                        timeInBubble.compute(bubblePlayer, (k, v) -> v == null ? 1 : v + 1);
                    }
                } else {
                    this.cancel();

                    Utils.playGlobalSound(wp.getLocation(), "paladin.holyradiance.activation", 2, 1.3f);
                    Utils.playGlobalSound(wp.getLocation(), Sound.AMBIENCE_THUNDER, 2, 1.5f);

                    for (Map.Entry<WarlordsPlayer, Integer> entry : timeInBubble.entrySet()) {
                        // 5% missing health * 4
                        float healingValue = 150 + (entry.getKey().getMaxHealth() - entry.getKey().getHealth()) * 0.05f;
                        int timeInSeconds = entry.getValue() * 4 / 20;
                        float totalHealing = (timeInSeconds * healingValue);
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

                    CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), wp.getLocation(), BUBBLE_RADIUS);
                    circle.addEffect(new CircumferenceEffect(ParticleEffect.SPELL).particlesPerCircumference(2));
                    circle.playEffects();
                }
            }
        }.runTaskTimer(5, 4);

        return true;
    }
}

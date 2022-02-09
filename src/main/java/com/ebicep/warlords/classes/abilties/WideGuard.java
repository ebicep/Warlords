package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static com.ebicep.warlords.util.EffectUtils.playSphereAnimation;

public class WideGuard extends AbstractAbility {

    public static final int BUBBLE_RADIUS = 4;

    public WideGuard() {
        super("Wide Guard", 0, 0, 23, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Create a bubble shield around you that\n" +
                "§7lasts §64 §7seconds. All projectiles that pass through\n" +
                "§7the barrier have their damage reduced by §c70%§7.\n" +
                "§7(scales down with the amount of allies inside\n" +
                "§7the bubble. Minimum §c20%§7.) " +
                "\n\n" +
                "§7After §64 §7seconds, the bubble will burst healing\n" +
                "§7all allies for up to §a600 §7+ §a20% §7missing health\n" +
                "§7based on how long they've been in the bubble.\n";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);
        WideGuard tempWideGuard = new WideGuard();
        wp.getCooldownManager().addRegularCooldown("Wide Guard", "GUARD", WideGuard.class, tempWideGuard, wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, 4 * 20);
        //wp.getCooldownManager().addCooldown("Reflection Shield", this.getClass(), WideGuard.class, "", 4, wp, CooldownTypes.ABILITY);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(wp.getLocation(), "mage.timewarp.teleport", 2, 2);
            player1.playSound(player.getLocation(), "warrior.intervene.impact", 2, 0.1f);
        }

        // First Particle Sphere
        playSphereAnimation(player, BUBBLE_RADIUS + 2.5, 68, 176, 176);

        // Second Particle Sphere
        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                playSphereAnimation(player, BUBBLE_RADIUS + 1, 65, 185, 185);

                for (Player player1 : player.getWorld().getPlayers()) {
                    player1.playSound(player.getLocation(), "warrior.intervene.impact", 2, 0.2f);
                }
            }
        }.runTaskLater(3);

        HashMap<WarlordsPlayer, Integer> timeInBubble = new HashMap<>();

        // Third Particle Sphere
        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (wp.getCooldownManager().hasCooldown(tempWideGuard)) {
                    Location particleLoc = wp.getLocation();
                    particleLoc.add(0, 1, 0);

                    ParticleEffect.ENCHANTMENT_TABLE.display(0.2F, 0F, 0.2F, 0.1F, 1, particleLoc, 500);

                    playSphereAnimation(player, BUBBLE_RADIUS, 190, 190, 190);

                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(player.getLocation(), Sound.CREEPER_DEATH, 2, 2);
                    }

                    timeInBubble.compute(wp, (k, v) -> v == null ? 1 : v + 1);

                    PlayerFilter.entitiesAround(particleLoc, BUBBLE_RADIUS, BUBBLE_RADIUS, BUBBLE_RADIUS)
                            .aliveTeammatesOfExcludingSelf(wp)
                            .forEach(playerInsideBubble -> {
                                playerInsideBubble.getCooldownManager().removeCooldown(WideGuard.class);
                                playerInsideBubble.getCooldownManager().addRegularCooldown(
                                        "Wide Guard",
                                        "GUARD",
                                        WideGuard.class,
                                        tempWideGuard,
                                        wp,
                                        CooldownTypes.ABILITY,
                                        cooldownManager -> {},
                                        20);
                                timeInBubble.compute(playerInsideBubble, (k, v) -> v == null ? 1 : v + 1);
                            });
                } else {
                    this.cancel();

                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(player.getLocation(), "paladin.holyradiance.activation", 2, 1.3f);
                        player1.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 2, 1.5f);
                    }

                    for (Map.Entry<WarlordsPlayer, Integer> entry : timeInBubble.entrySet()) {
                        // 5% missing health * 4
                        float healingValue = 150 + (entry.getKey().getMaxHealth() - entry.getKey().getHealth()) * 0.05f;
                        int timeInSeconds = entry.getValue() * 4 / 20;
                        float totalHealing = (timeInSeconds * healingValue);
                        entry.getKey().addHealingInstance(wp, name, totalHealing, totalHealing, -1, 100, false, false);
                    }

                    CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), player.getLocation(), 4);
                    circle.addEffect(new CircumferenceEffect(ParticleEffect.SPELL).particlesPerCircumference(2));
                    circle.playEffects();
                }
            }
        }.runTaskTimer( 5, 4);

        return true;
    }
}

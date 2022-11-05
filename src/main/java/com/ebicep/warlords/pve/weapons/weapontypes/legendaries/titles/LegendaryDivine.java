package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class LegendaryDivine extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 100;
    public static final int MELEE_DAMAGE_MAX = 120;
    public static final int CRIT_CHANCE = 25;
    public static final int CRIT_MULTIPLIER = 175;
    public static final int HEALTH_BONUS = 500;
    public static final int SPEED_BONUS = 5;
    public static final int ENERGY_PER_SECOND_BONUS = 7;
    public static final int ENERGY_PER_HIT_BONUS = -10;
    public static final int SKILL_CRIT_CHANCE_BONUS = 5;

    public static final int TARGETS_TO_HIT = 40;
    public static final int COOLDOWN = 30;

    public LegendaryDivine() {
    }

    public LegendaryDivine(UUID uuid) {
        super(uuid);
    }

    public LegendaryDivine(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getTitle() {
        return "Divine";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);


        player.getGame().registerEvents(new Listener() {

            final AtomicInteger targetsHit = new AtomicInteger(0);
            final AtomicInteger cooldown = new AtomicInteger(0);

            @EventHandler
            public void onDamageHealing(WarlordsDamageHealingFinalEvent event) {
                if (!event.getAttacker().equals(player)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                if (cooldown.get() != 0) {
                    return;
                }
                if (targetsHit.incrementAndGet() >= TARGETS_TO_HIT) {
                    player.sendMessage(ChatColor.GREEN + "Divine Passive Activated!");

                    player.getCooldownManager().addCooldown(new RegularCooldown<>(
                            "Divine",
                            "DIV",
                            LegendaryDivine.class,
                            null,
                            player,
                            CooldownTypes.BUFF,
                            cooldownManager -> {
                                player.sendMessage(ChatColor.RED + "Divine Passive Deactivated!");
                            },
                            10 * 20
                    ) {
                        @Override
                        public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            return currentDamageValue * 1.1f;
                        }
                    });

                    cooldown.set(COOLDOWN);

                    new GameRunnable(player.getGame()) {

                        @Override
                        public void run() {
                            targetsHit.set(0);
                            cooldown.set(0);
                        }
                    }.runTaskTimer(0, COOLDOWN * 20);

                }
            }

        });
    }

    @Override
    public String getPassiveEffect() {
        return "Gain a 10% damage boost after hitting " + TARGETS_TO_HIT + " targets. Can be triggered every " + COOLDOWN + " seconds.";
    }

    @Override
    public void generateStats() {
        this.meleeDamage = MELEE_DAMAGE_MIN;
        this.critChance = CRIT_CHANCE;
        this.critMultiplier = CRIT_MULTIPLIER;
        this.healthBonus = HEALTH_BONUS;
        this.speedBonus = SPEED_BONUS;
        this.energyPerSecondBonus = ENERGY_PER_SECOND_BONUS;
        this.energyPerHitBonus = ENERGY_PER_HIT_BONUS;
        this.skillCritChanceBonus = SKILL_CRIT_CHANCE_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return MELEE_DAMAGE_MAX - MELEE_DAMAGE_MIN;
    }
}

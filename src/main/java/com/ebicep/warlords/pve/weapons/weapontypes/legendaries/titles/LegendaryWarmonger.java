package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class LegendaryWarmonger extends AbstractLegendaryWeapon {

    public static final int MELEE_DAMAGE_MIN = 170;
    public static final int MELEE_DAMAGE_MAX = 190;
    public static final int CRIT_CHANCE = 15;
    public static final int CRIT_MULTIPLIER = 200;
    public static final int HEALTH_BONUS = 800;
    public static final int SPEED_BONUS = 10;
    public static final int SKILL_CRIT_CHANCE_BONUS = 5;
    public static final int SKILL_CRIT_MULTIPLIER_BONUS = 10;

    public static final int DAMAGE_TO_TAKE = 10000;
    public static final int COOLDOWN = 30;

    public LegendaryWarmonger() {
    }

    public LegendaryWarmonger(UUID uuid) {
        super(uuid);
    }

    public LegendaryWarmonger(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public void generateStats() {
        this.meleeDamage = MELEE_DAMAGE_MIN;
        this.critChance = CRIT_CHANCE;
        this.critMultiplier = CRIT_MULTIPLIER;
        this.healthBonus = HEALTH_BONUS;
        this.speedBonus = SPEED_BONUS;
        this.skillCritChanceBonus = SKILL_CRIT_CHANCE_BONUS;
        this.skillCritMultiplierBonus = SKILL_CRIT_MULTIPLIER_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return MELEE_DAMAGE_MAX - MELEE_DAMAGE_MIN;
    }

    @Override
    public String getTitle() {
        return "Warmonger";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        player.getGame().registerEvents(new Listener() {

            final AtomicDouble damageTaken = new AtomicDouble(0);
            final AtomicInteger cooldown = new AtomicInteger(0);

            @EventHandler
            public void onDamageHealing(WarlordsDamageHealingFinalEvent event) {
                if (!event.getPlayer().equals(player)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                if (cooldown.get() != 0) {
                    return;
                }
                if (damageTaken.addAndGet(event.getValue()) >= DAMAGE_TO_TAKE) {
                    player.sendMessage(ChatColor.GREEN + "Warmonger Passive Activated");

                    player.getCooldownManager().addCooldown(new RegularCooldown<>(
                            "Warmonger",
                            "WAR",
                            LegendaryWarmonger.class,
                            null,
                            player,
                            CooldownTypes.BUFF,
                            cooldownManager -> {
                                player.sendMessage(ChatColor.RED + "Warmonger Passive Deactivated");
                            },
                            10 * 20
                    ) {
                        @Override
                        public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            return currentDamageValue * 1.2f;
                        }
                    });

                    cooldown.set(COOLDOWN);

                    new GameRunnable(player.getGame()) {

                        @Override
                        public void run() {
                            damageTaken.set(0);
                            cooldown.set(0);
                        }
                    }.runTaskLater(COOLDOWN * 20);
                }
            }

        });
    }

    @Override
    public String getPassiveEffect() {
        return "Gain a 20% damage boost for 10 seconds and reset your Purple Rune's cooldown after taking " + DAMAGE_TO_TAKE +
                " damage. Can be triggered every " + COOLDOWN + " seconds.";
    }
}

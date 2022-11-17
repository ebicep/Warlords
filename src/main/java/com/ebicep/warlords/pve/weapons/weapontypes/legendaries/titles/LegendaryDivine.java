package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class LegendaryDivine extends AbstractLegendaryWeapon {

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
    protected float getMeleeDamageMinValue() {
        return 100;
    }

    @Override
    public String getPassiveEffect() {
        return "Gain a 15% damage boost after hitting " + TARGETS_TO_HIT + " targets for 10 seconds. Can be triggered every " + COOLDOWN + " seconds.";
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 120;
    }

    @Override
    protected float getCritChanceValue() {
        return 25;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 175;
    }

    @Override
    protected float getHealthBonusValue() {
        return 500;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 5;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return 7;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return -13;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 5;
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.DIVINE;
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
                            return currentDamageValue * 1.15f;
                        }
                    });

                    cooldown.set(COOLDOWN);

                    new GameRunnable(player.getGame()) {

                        @Override
                        public void run() {
                            targetsHit.set(0);
                            cooldown.set(0);
                        }
                    }.runTaskLater(COOLDOWN * 20);

                }
            }

        });
    }
}

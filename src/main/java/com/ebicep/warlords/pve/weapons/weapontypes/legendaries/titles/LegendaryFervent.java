package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class LegendaryFervent extends AbstractLegendaryWeapon {

    public static final int DAMAGE_TO_TAKE = 10000;
    public static final int COOLDOWN = 30;

    public LegendaryFervent() {
    }

    public LegendaryFervent(UUID uuid) {
        super(uuid);
    }

    public LegendaryFervent(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 170;
    }

    @Override
    public String getPassiveEffect() {
        return "Gain a 20% damage boost for 10 seconds and reset your Purple Rune's cooldown after taking " + DAMAGE_TO_TAKE +
                " damage. Can be triggered every " + COOLDOWN + " seconds.";
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 190;
    }

    @Override
    protected float getCritChanceValue() {
        return 15;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 200;
    }

    @Override
    protected float getHealthBonusValue() {
        return 800;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 10;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 5;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 10;
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.FERVENT;
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
                            LegendaryFervent.class,
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
}

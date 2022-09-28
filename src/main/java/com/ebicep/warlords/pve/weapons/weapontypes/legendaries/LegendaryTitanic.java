package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsPlayerUpgradeUnlockEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class LegendaryTitanic extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 130;
    public static final int MELEE_DAMAGE_MAX = 150;
    public static final int CRIT_CHANCE = 15;
    public static final int CRIT_MULTIPLIER = 160;
    public static final int HEALTH_BONUS = 1500;
    public static final int SPEED_BONUS = 5;

    public LegendaryTitanic() {
    }

    public LegendaryTitanic(UUID uuid) {
        super(uuid);
    }

    @Override
    public String getTitle() {
        return "Titanic";
    }

    @Override
    public String getPassiveEffect() {
        return "Increase maximum health by 0.5% per upgrade purchased.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        player.getGame().registerEvents(new Listener() {
            float baseMaxHealth = -1;
            int upgradeCount = 0;

            @EventHandler
            public void onEvent(WarlordsPlayerUpgradeUnlockEvent event) {
                if (event.getPlayer() == player) {
                    if (baseMaxHealth == -1) {
                        baseMaxHealth = player.getMaxHealth();
                    }
                    player.setMaxHealth(baseMaxHealth * (1 + (++upgradeCount * 0.05f)));
                }
            }
        });
    }

    @Override
    public void generateStats() {
        this.meleeDamage = MELEE_DAMAGE_MIN;
        this.critChance = CRIT_CHANCE;
        this.critMultiplier = CRIT_MULTIPLIER;
        this.healthBonus = HEALTH_BONUS;
        this.speedBonus = SPEED_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return MELEE_DAMAGE_MAX - MELEE_DAMAGE_MIN;
    }
}

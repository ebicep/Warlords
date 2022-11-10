package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class LegendaryGradient extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 140;
    public static final int MELEE_DAMAGE_MAX = 170;
    public static final int CRIT_CHANCE = 20;
    public static final int CRIT_MULTIPLIER = 160;
    public static final int HEALTH_BONUS = 700;
    public static final int ENERGY_PER_SECOND_BONUS = 3;
    public static final int ENERGY_PER_HIT_BONUS = 3;
    public static final int SKILL_CRIT_MULTIPLIER_BONUS = 20;

    public LegendaryGradient() {
    }

    public LegendaryGradient(UUID uuid) {
        super(uuid);
    }

    public LegendaryGradient(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getTitle() {
        return "Gradient";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        new GameRunnable(player.getGame()) {

            @Override
            public void run() {
                if (player.isDead()) {
                    return;
                }
                float healValue = player.getMaxHealth() * .03f;
                player.addHealingInstance(player, "Gradient", healValue, healValue, 0, 100, false, false);
            }
        }.runTaskTimer(0, 5 * 20);
    }

    @Override
    public String getPassiveEffect() {
        return "Perpetually regenerate 3% of your health every 5 seconds.";
    }

    @Override
    public void generateStats() {
        this.meleeDamage = MELEE_DAMAGE_MIN;
        this.critChance = CRIT_CHANCE;
        this.critMultiplier = CRIT_MULTIPLIER;
        this.healthBonus = HEALTH_BONUS;
        this.energyPerSecondBonus = ENERGY_PER_SECOND_BONUS;
        this.energyPerHitBonus = ENERGY_PER_HIT_BONUS;
        this.skillCritMultiplierBonus = SKILL_CRIT_MULTIPLIER_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return MELEE_DAMAGE_MAX - MELEE_DAMAGE_MIN;
    }
}

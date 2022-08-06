package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.events.player.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.AbstractLegendaryWeapon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class LegendaryBenevolent extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 120;
    public static final int MELEE_DAMAGE_MAX = 140;
    public static final int CRIT_CHANCE = 20;
    public static final int CRIT_MULTIPLIER = 180;
    public static final int HEALTH_BONUS = 800;
    public static final int SPEED_BONUS = 10;
    public static final int SKILL_CRIT_CHANCE_BONUS = 2;
    public static final int SKILL_CRIT_MULTIPLIER_BONUS = 4;

    public LegendaryBenevolent() {
    }

    public LegendaryBenevolent(UUID uuid) {
        super(uuid);
    }

    @Override
    public String getTitle() {
        return "Benevolent";
    }

    @Override
    public String getPassiveEffect() {
        return "Increase healing provided by 10%.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        player.getGame().registerEvents(new Listener() {
            @EventHandler
            public void onEvent(WarlordsDamageHealingEvent event) {
                if (event.isHealingInstance() && event.getPlayer() == player) {
                    event.setMin(event.getMin() * 1.1f);
                    event.setMax(event.getMax() * 1.1f);
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
        this.skillCritChanceBonus = SKILL_CRIT_CHANCE_BONUS;
        this.skillCritMultiplierBonus = SKILL_CRIT_MULTIPLIER_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return MELEE_DAMAGE_MAX - MELEE_DAMAGE_MIN;
    }
}

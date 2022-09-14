package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.AbstractLegendaryWeapon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class LegendaryVorpal extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 200;
    public static final int MELEE_DAMAGE_MAX = 220;
    public static final int CRIT_CHANCE = 35;
    public static final int CRIT_MULTIPLIER = 245;
    public static final int HEALTH_BONUS = 300;
    public static final int SPEED_BONUS = 14;

    public LegendaryVorpal() {
    }

    public LegendaryVorpal(UUID uuid) {
        super(uuid);
    }

    @Override
    public String getTitle() {
        return "Vorpal";
    }

    @Override
    public String getPassiveEffect() {
        return "Every 5th melee hit deals 7x damage, bypassing damage reduction.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);
        player.getGame().registerEvents(new Listener() {
            int meleeCounter = 0;

            @EventHandler
            public void onEvent(WarlordsDamageHealingEvent event) {
                if (event.isHealingInstance() || event.getAttacker() != player || !event.getAbility().isEmpty()) return;
                meleeCounter++;
                if (meleeCounter % 5 == 0) {
                    event.setMin(event.getMin() * 7);
                    event.setMax(event.getMax() * 7);
                    event.setIgnoreReduction(true);
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

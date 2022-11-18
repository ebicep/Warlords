package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class LegendaryVorpal extends AbstractLegendaryWeapon {

    public LegendaryVorpal() {
    }

    public LegendaryVorpal(UUID uuid) {
        super(uuid);
    }

    public LegendaryVorpal(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getPassiveEffect() {
        return "Every 5th melee hit deals 7x damage, bypassing damage reduction.";
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 220;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);
        player.getGame().registerEvents(new Listener() {
            int meleeCounter = 0;

            @EventHandler
            public void onEvent(WarlordsDamageHealingEvent event) {
                if (event.getAttacker() != player) {
                    return;
                }
                String ability = event.getAbility();
                if (!ability.isEmpty() && !ability.equals("Windfury Weapon") && !ability.equals("Earthliving Weapon")) {
                    return;
                }
                if (event.isHealingInstance() && !ability.equals("Earthliving Weapon")) {
                    return;
                }
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
    public LegendaryTitles getTitle() {
        return LegendaryTitles.VORPAL;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 200;
    }

    @Override
    protected float getCritChanceValue() {
        return 35;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 200;
    }

    @Override
    protected float getHealthBonusValue() {
        return 300;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 14;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return -3;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return 4;
    }
}

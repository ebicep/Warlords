package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class LegendaryBenevolent extends AbstractLegendaryWeapon {

    private static final int HEALING_INCREASE = 25;

    public LegendaryBenevolent() {
    }

    public LegendaryBenevolent(UUID uuid) {
        super(uuid);
    }

    public LegendaryBenevolent(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getPassiveEffect() {
        return "Increase healing provided by " + HEALING_INCREASE + "%.";
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 140;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        player.getGame().registerEvents(new Listener() {
            @EventHandler
            public void onEvent(WarlordsDamageHealingEvent event) {
                if (event.isHealingInstance() && event.getAttacker().equals(player)) {
                    event.setMin(event.getMin() * (1 + HEALING_INCREASE / 100f));
                    event.setMax(event.getMax() * (1 + HEALING_INCREASE / 100f));
                }
            }
        });
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.BENEVOLENT;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 120;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 180;
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
        return 5;
    }
}

package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.util.java.Pair;

import java.util.List;
import java.util.UUID;

public class LegendaryWeapon extends AbstractLegendaryWeapon {

    public LegendaryWeapon() {
    }

    public LegendaryWeapon(UUID uuid) {
        super(uuid);
    }

    public LegendaryWeapon(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getPassiveEffect() {
        return "";
    }

    @Override
    public List<Pair<String, String>> getPassiveEffectUpgrade() {
        return null;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 180;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 160;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 200;
    }

    @Override
    protected float getHealthBonusValue() {
        return 600;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 10;
    }
}

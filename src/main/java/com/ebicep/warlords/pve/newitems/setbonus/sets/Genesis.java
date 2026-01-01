package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Genesis extends BaseSet {

    private int healthThreshold;
    private int maxHealthDamageMultiplier;
    private int cooldownSeconds;

    @Override
    public void init() {
        super.init();
        this.healthThreshold = getValue("healthThreshold", int.class);
        this.maxHealthDamageMultiplier = getValue("maxHealthDamageMultiplier", int.class);
        this.cooldownSeconds = getValue("cooldownSeconds", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "genesis";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthThreshold, maxHealthDamageMultiplier, cooldownSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {

        }

    }

}

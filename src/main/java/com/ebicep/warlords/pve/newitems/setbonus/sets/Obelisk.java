package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Obelisk extends BaseSet {

    private int bossDamageIncreasePercent;
    private int bossDamageTakenReductionPercent;

    @Override
    public void init() {
        super.init();
        this.bossDamageIncreasePercent = getValue("bossDamageIncreasePercent", int.class);
        this.bossDamageTakenReductionPercent = getValue("bossDamageTakenReductionPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "obelisk";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(bossDamageIncreasePercent, bossDamageTakenReductionPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for increasing outgoing damage to bosses 
            // and decreasing incoming damage from boss-type entities.
        }

    }

}
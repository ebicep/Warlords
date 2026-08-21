package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;

import java.util.Collections;
import java.util.List;

public class RandomRare extends BaseSet {

    @Override
    public Bonus create() {
        return Bonus.NONE;
    }

    @Override
    public List<Object> getVariables() {
        return Collections.emptyList();
    }

    @Override
    public String getConfigFieldName() {
        return "randomRare";
    }

}

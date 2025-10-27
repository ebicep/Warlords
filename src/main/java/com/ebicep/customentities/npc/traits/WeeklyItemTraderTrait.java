package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;

public class WeeklyItemTraderTrait extends WarlordsTrait {

    public WeeklyItemTraderTrait() {
        super("WeeklyItemTraderTrait");
    }

    @Override
    public void onAttach() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.AQUA + "Echelon Trader");
    }
}

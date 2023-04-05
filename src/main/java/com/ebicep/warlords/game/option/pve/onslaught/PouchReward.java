package com.ebicep.warlords.game.option.pve.onslaught;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import org.bukkit.ChatColor;

import java.util.LinkedHashMap;

public class PouchReward extends AbstractReward {

    private PouchType pouchType;

    public PouchReward() {
    }

    public PouchReward(LinkedHashMap<Spendable, Long> rewards, PouchType pouchType) {
        super(rewards, pouchType.name);
        this.pouchType = pouchType;
    }

    @Override
    public ChatColor getNameColor() {
        return ChatColor.AQUA;
    }

    public enum PouchType {
        SYNTHETIC("Synthetic Pouch"),
        ASPIRANT("Aspirant Pouch"),

        ;

        public final String name;

        PouchType(String name) {
            this.name = name;
        }
    }

}

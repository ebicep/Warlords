package com.ebicep.warlords.game.option.towerdefense.mobs.attributes.blocking;

import com.ebicep.warlords.player.ingame.WarlordsNPC;

public interface TDBlockingMode {

    /**
     * Called during the attackers pathfinding, checking to attack defenders
     *
     * @param warlordsNPC the defender
     * @return true if the defender should included in list of agro targets
     */
    default boolean filterDefender(WarlordsNPC warlordsNPC) {
        return true;
    }

    /**
     * Called during the defenders pathfinding, checking to attack attackers
     *
     * @param warlordsNPC the attacker
     * @return true if the attacker should included in list of agro targets
     */
    default boolean filterAttacker(WarlordsNPC warlordsNPC) {
        return true;
    }

}

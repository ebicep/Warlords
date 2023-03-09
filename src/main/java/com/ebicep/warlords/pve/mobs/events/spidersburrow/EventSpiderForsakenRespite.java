package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilties.ImpalingStrike;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.spider.Spider;
import org.bukkit.Location;

public class EventSpiderForsakenRespite extends Spider {

    public EventSpiderForsakenRespite(Location spawnLocation) {
        super(spawnLocation);
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        // Applies leech to enemies for 3s.
        ImpalingStrike.giveLeechCooldown(
                warlordsNPC,
                attacker,
                3 * 20,
                .25f,
                .15f,
                warlordsDamageHealingFinalEvent -> {

                }
        );
    }
}

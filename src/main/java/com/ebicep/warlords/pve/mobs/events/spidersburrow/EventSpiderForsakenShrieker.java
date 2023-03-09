package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.pve.mobs.spider.Spider;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventSpiderForsakenShrieker extends Spider {

    public EventSpiderForsakenShrieker(Location spawnLocation) {
        super(spawnLocation);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        // Applies Darkness to enemies within a 10 block radius for 1s. Can occur every 5s.
        if (ticksElapsed % 100 == 0) {
            PlayerFilterGeneric.entitiesAround(warlordsNPC, 10, 10, 10)
                               .enemiesOf(warlordsNPC)
                               .warlordsPlayers()
                               .forEach(warlordsPlayer -> warlordsPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0, true, false)));
        }
    }

}

package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.spider.Spider;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventSpiderForsakenAberration extends Spider {

    public EventSpiderForsakenAberration(Location spawnLocation) {
        super(spawnLocation);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        // When this spider takes damage, it turns invisible, giving it a 15% increase to movement
        self.getSpeed().addBaseModifier(15);
        self.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 10 * 15, 0, true, false));
    }

}

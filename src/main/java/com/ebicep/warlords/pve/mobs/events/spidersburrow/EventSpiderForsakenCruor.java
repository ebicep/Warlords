package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilties.WoundingStrikeBerserker;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.spider.Spider;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class EventSpiderForsakenCruor extends Spider {

    public EventSpiderForsakenCruor(Location spawnLocation) {
        super(spawnLocation);
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        // Applies wounding to enemies for 3s.
        receiver.getCooldownManager().removePreviousWounding();
        receiver.getCooldownManager().addCooldown(new RegularCooldown<WoundingStrikeBerserker>(
                name,
                "WND",
                WoundingStrikeBerserker.class,
                new WoundingStrikeBerserker(),
                attacker,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                },
                cooldownManager -> {
                    if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterNameActionBar("WND").stream().count() == 1) {
                        receiver.sendMessage(ChatColor.GRAY + "You are no longer " + ChatColor.RED + "wounded" + ChatColor.GRAY + ".");
                    }
                },
                5 * 20
        ) {
            @Override
            public float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                return currentHealValue * .5f;
            }
        });
    }

}

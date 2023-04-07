package com.ebicep.warlords.pve.items.types.specialitems.tome.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsDropRewardEvent;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class FirewaterAlmanac extends SpecialDeltaTome {

    @Override
    public String getName() {
        return "Firewater Almanac";
    }

    @Override
    public String getBonus() {
        return "Increased chance of finding Items when killing mobs with Right-Click";
    }

    @Override
    public String getDescription() {
        return "Now Including Ice Spells!";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        String weaponRightClick = warlordsPlayer.getSpec().getWeapon().getName();
        warlordsPlayer.getGame().registerEvents(new Listener() {

            private final HashMap<UUID, String> mobsLastHitWith = new HashMap<>();

            @EventHandler
            public void onDamageHeall(WarlordsDamageHealingEvent event) {
                if (!Objects.equals(event.getAttacker(), warlordsPlayer)) {
                    return;
                }
                mobsLastHitWith.put(event.getWarlordsEntity().getUuid(), event.getAbility());
            }

            @EventHandler
            public void onItemDrop(WarlordsDropRewardEvent event) {
                AbstractMob<?> deadMob = event.getDeadMob();
                UUID deadMobUUID = deadMob.getWarlordsNPC().getUuid();
                if (event.getRewardType() != WarlordsDropRewardEvent.RewardType.ITEM) {
                    return;
                }
                if (Objects.equals(mobsLastHitWith.getOrDefault(deadMobUUID, null), weaponRightClick)) {
                    AtomicDouble dropRate = event.getDropRate();
                    dropRate.set(dropRate.get() * 1.1);
                }
                mobsLastHitWith.remove(deadMobUUID);
            }
        });

    }
}

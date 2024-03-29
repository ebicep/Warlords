package com.ebicep.warlords.pve.items.types.specialitems.tome.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropItemEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.tome.omega.FlemingAlmanac;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class FirewaterAlmanac extends SpecialDeltaTome implements CraftsInto {

    public FirewaterAlmanac(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public FirewaterAlmanac() {

    }

    @Override
    public String getName() {
        return "Firewater Almanac";
    }

    @Override
    public String getBonus() {
        return "Increased chance of finding Items when killing mobs with Right-Click.";
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
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        String weaponRightClick = warlordsPlayer.getSpec().getWeapon().getName();
        warlordsPlayer.getGame().registerEvents(new Listener() {

            private final HashMap<UUID, String> mobsLastHitWith = new HashMap<>();

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!Objects.equals(event.getAttacker(), warlordsPlayer)) {
                    return;
                }
                mobsLastHitWith.put(event.getWarlordsEntity().getUuid(), event.getAbility());
            }

            @EventHandler
            public void onItemDrop(WarlordsDropItemEvent event) {
                AbstractMob<?> deadMob = event.getDeadMob();
                UUID deadMobUUID = deadMob.getWarlordsNPC().getUuid();
                if (Objects.equals(mobsLastHitWith.getOrDefault(deadMobUUID, null), weaponRightClick)) {
                    event.addModifier(.1);
                }
                mobsLastHitWith.remove(deadMobUUID);
            }
        });

    }


    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new FlemingAlmanac(statPool);
    }
}

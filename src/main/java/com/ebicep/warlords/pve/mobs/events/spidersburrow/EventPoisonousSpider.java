package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.Spider;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class EventPoisonousSpider extends AbstractZombie implements BossMob, Spider {

    public EventPoisonousSpider(Location spawnLocation) {
        super(
                spawnLocation,
                "Poisonous Spider",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.CAVE_SPIDER),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
                        null
                ),
                4000,
                .55f,
                0,
                750,
                850
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        // Poisons enemies every 3s dealing 375-500 true damage.
        if (ticksElapsed % 60 == 0) {
            PlayerFilterGeneric.playingGame(option.getGame())
                               .enemiesOf(warlordsNPC)
                               .forEach(warlordsEntity -> warlordsEntity.addDamageInstance(
                                       warlordsNPC,
                                       "Poison",
                                       375,
                                       500,
                                       0,
                                       0,
                                       true
                               ));
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}

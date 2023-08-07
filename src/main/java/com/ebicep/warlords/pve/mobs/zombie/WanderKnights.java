package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class WanderKnights extends AbstractZombie implements EliteMob {

    public WanderKnights(Location spawnLocation) {
        super(
                spawnLocation,
                "Wander Knights",
                MobTier.ILLUSION,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.FANCY_CUBE),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 105, 147, 158),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 105, 147, 158),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 105, 147, 158),
                        Weapons.LUNAR_RELIC.getItem()
                ),
                5000,
                0.335f,
                10,
                200,
                300
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
    }

}

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

public class OvergrownZombie extends AbstractZombie implements EliteMob {

    public OvergrownZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Lancer",
                MobTier.OVERGROWN,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.GREEN_LANCER),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 130, 20),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 130, 20),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 130, 20),
                        Weapons.NEW_LEAF_AXE.getItem()
                ),
                12000,
                0.42f,
                0,
                700,
                900
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

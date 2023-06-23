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

public class ExtremeZealot extends AbstractZombie implements EliteMob {

    public ExtremeZealot(Location spawnLocation) {
        super(
                spawnLocation,
                "Extreme Zealot",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.FANCY_CUBE_2),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 56, 71, 74),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 56, 71, 74),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 56, 71, 74),
                        Weapons.VENOMSTRIKE.getItem()
                ),
                6000,
                0.45f,
                20,
                500,
                750
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
        receiver.addSpeedModifier(attacker, name, -20, 20, "BASE");
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
    }

}

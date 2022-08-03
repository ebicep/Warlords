package com.ebicep.warlords.game.option.wavedefense.mobs.zombie;

import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BasicMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BasicZombie extends AbstractZombie implements BasicMob {

    public BasicZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Lunar Lancer",
                new Utils.SimpleEntityEquipment(
                        new ItemStack(Material.CARPET, 1, (short) 14),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
                        new ItemStack(Material.WOOD_AXE)
                ),
                2800,
                0.38f,
                0,
                200,
                300
        );
    }


    @Override
    public void onSpawn() {
    }

    @Override
    public void whileAlive() {
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver) {

    }

}

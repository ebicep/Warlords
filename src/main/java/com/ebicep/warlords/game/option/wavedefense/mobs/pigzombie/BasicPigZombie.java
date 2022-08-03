package com.ebicep.warlords.game.option.wavedefense.mobs.pigzombie;

import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BasicMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BasicPigZombie extends AbstractPigZombie implements BasicMob {

    public BasicPigZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Lunar Disciple",
                new Utils.SimpleEntityEquipment(
                        new ItemStack(Material.WOOD, 1, (short) 3),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
                        new ItemStack(Material.COOKIE)
                ),
                2800,
                0.42f,
                0,
                250,
                350
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

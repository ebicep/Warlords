package com.ebicep.warlords.game.option.wavedefense.mobs.skeleton;

import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BasicMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BasicSkeleton extends AbstractSkeleton implements BasicMob {

    public BasicSkeleton(Location spawnLocation) {
        super(
                spawnLocation,
                "Lunar Mage",
                new Utils.SimpleEntityEquipment(
                        Utils.getPlayerSkull("4oot"),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
                        new ItemStack(Material.BOW)
                ),
                1600,
                0.25f,
                0,
                0,
                0
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

    @Override
    public void onDeath(Location deathLocation, WaveDefenseOption waveDefenseOption) {

    }
}

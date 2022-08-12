package com.ebicep.warlords.game.option.wavedefense.mobs.zombie.berserkzombie;

import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BasicBerserkZombie extends AbstractBerserkZombie {

    public BasicBerserkZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Basic Berserker Zombie",
                MobTier.BASE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.PURPLE_KNIGHT),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
                        new ItemStack(Material.WOOD_SWORD)
                ),
                2800,
                0.38f,
                0,
                200,
                300,
                woundingStrikeBerserker -> {
                });
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {

    }
}

package com.ebicep.warlords.game.option.wavedefense.mobs.zombie;

import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

public class Narmer extends AbstractZombie implements BossMob {

    public Narmer(Location spawnLocation) {
        super(spawnLocation,
                "Narmer",
                new Utils.SimpleEntityEquipment(
                        Utils.getMobSkull(SkullType.WITHER),
                        new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                        new ItemStack(Material.CHAINMAIL_LEGGINGS),
                        new ItemStack(Material.CHAINMAIL_BOOTS),
                        new ItemStack(Material.STICK)
                ),
                12000,
                0.5f,
                0,
                400,
                600);
    }

    @Override
    public void onSpawn() {
        Bukkit.broadcastMessage(ChatColor.GREEN + "Narmer onSpawn");
    }

    @Override
    public void whileAlive() {
        Bukkit.broadcastMessage(ChatColor.GRAY + "Narmer whileAlive");
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver) {

    }

    @Override
    public void onDeath(Location deathLocation, WaveDefenseOption waveDefenseOption) {
        Bukkit.broadcastMessage(ChatColor.RED + "Narmer onDeath");
        dropItem();
    }
}

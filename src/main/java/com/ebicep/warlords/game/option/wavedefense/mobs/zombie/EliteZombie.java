package com.ebicep.warlords.game.option.wavedefense.mobs.zombie;

import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

public class EliteZombie extends AbstractZombie implements EliteMob {

    public EliteZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Swordsman",
                new Utils.SimpleEntityEquipment(
                        new ItemStack(Material.CARPET),
                        new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                        new ItemStack(Material.CHAINMAIL_LEGGINGS),
                        new ItemStack(Material.CHAINMAIL_BOOTS),
                        new ItemStack(Material.PRISMARINE_SHARD)
                ),
                4000,
                0.38f,
                10,
                300,
                500
        );
    }

    @Override
    public void onSpawn() {
        getWarlordsNPC().getEntity().getWorld().spigot().strikeLightningEffect(getWarlordsNPC().getLocation(), false);
    }

    @Override
    public void whileAlive() {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver) {
        Location loc = receiver.getLocation();
        Utils.playGlobalSound(loc, Sound.PORTAL_TRAVEL, 1, 1.5f);
        receiver.subtractEnergy(25);
    }

    @Override
    public void onDeath(Location deathLocation, WaveDefenseOption waveDefenseOption) {
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.PURPLE)
                .with(FireworkEffect.Type.BURST)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.ZOMBIE_DEATH, 2, 0.4f);
    }
}

package com.ebicep.warlords.game.option.wavedefense.mobs.skeleton;

import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

public class EliteSkeleton extends AbstractSkeleton implements EliteMob {
    public EliteSkeleton(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Warlock",
                new Utils.SimpleEntityEquipment(
                        new ItemStack(Material.CARPET, 1, (short) 1),
                        new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                        new ItemStack(Material.CHAINMAIL_LEGGINGS),
                        new ItemStack(Material.CHAINMAIL_BOOTS),
                        new ItemStack(Material.BOW)
                ),
                2000,
                0.3f,
                10,
                0,
                0
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

    }

    @Override
    public void onDeath(Location deathLocation, WaveDefenseOption waveDefenseOption) {
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.PURPLE)
                .with(FireworkEffect.Type.BURST)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.SKELETON_DEATH, 2, 0.4f);
    }
}

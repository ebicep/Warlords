package com.ebicep.warlords.game.option.wavedefense.mobs.skeleton;

import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

public class EnvoySkeleton extends AbstractSkeleton implements EliteMob {
    public EnvoySkeleton(Location spawnLocation) {
        super(
                spawnLocation,
                "Envoy Entropy",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        new ItemStack(Material.CARPET, 1, (short) 6),
                        new ItemStack(Material.DIAMOND_HELMET),
                        new ItemStack(Material.DIAMOND_LEGGINGS),
                        new ItemStack(Material.DIAMOND_BOOTS),
                        new ItemStack(Material.DIAMOND_PICKAXE)
                ),
                3000,
                0.35f,
                10,
                0,
                0
        );
    }

    @Override
    public void onSpawn() {
        getWarlordsNPC().getEntity().getWorld().spigot().strikeLightningEffect(getWarlordsNPC().getLocation(), false);
        getWarlordsNPC().getGame().forEachOfflineWarlordsPlayer(we -> {
            we.sendMessage(ChatColor.YELLOW + "An §c" + getWarlordsNPC().getName() + " §ehas spawned.");
        });
    }

    @Override
    public void whileAlive(int ticksElapsed) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, String ability) {
    }

    @Override
    public void onDamageTaken(WarlordsEntity mob, WarlordsEntity attacker) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption waveDefenseOption) {
        super.onDeath(killer, deathLocation, waveDefenseOption);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.PURPLE)
                .with(FireworkEffect.Type.BURST)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.SKELETON_DEATH, 2, 0.4f);
    }
}

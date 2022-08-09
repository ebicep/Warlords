package com.ebicep.warlords.game.option.wavedefense.mobs.pigzombie;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EnvoyPigZombie extends AbstractPigZombie implements EliteMob {

    public EnvoyPigZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Envoy Alleviator",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SAMURAI),
                        new ItemStack(Material.DIAMOND_HELMET),
                        new ItemStack(Material.DIAMOND_LEGGINGS),
                        new ItemStack(Material.DIAMOND_BOOTS),
                        new ItemStack(Material.BAKED_POTATO)
                ),
                6000,
                0.25f,
                20,
                300,
                400
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
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 60 != 0) {
            return;
        }
        Location location = getWarlordsNPC().getLocation();
        Utils.playGlobalSound(location, Sound.ZOMBIE_PIG_ANGRY, 1, 0.5f);
        Utils.playGlobalSound(location, "paladin.holyradiance.activation", 1, 0.5f);
        WarlordsEntity we = Warlords.getPlayer(getWarlordsNPC().getEntity());
        if (we == null) return;
        EffectUtils.playSphereAnimation(location, 8, ParticleEffect.FLAME, 1);
        for (WarlordsEntity ally : PlayerFilter
                .entitiesAround(we, 10, 10, 10)
                .aliveTeammatesOfExcludingSelf(we)
        ) {
            ally.addHealingInstance(
                    we,
                    "Healing",
                    300,
                    300,
                    -1,
                    100,
                    false,
                    false
            );
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, String ability) {
        Vector v = attacker.getLocation().toVector().subtract(receiver.getLocation().toVector()).normalize().multiply(-1.25).setY(0.5);
        receiver.setVelocity(v, false);
    }

    @Override
    public void onDamageTaken(WarlordsEntity mob, WarlordsEntity attacker) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        super.onDeath(killer, deathLocation, option);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.PURPLE)
                .with(FireworkEffect.Type.BURST)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.ZOMBIE_PIG_DEATH, 2, 0.4f);
    }
}

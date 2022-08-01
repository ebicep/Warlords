package com.ebicep.warlords.game.option.wavedefense.mobs.pigzombie;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ElitePigZombie extends AbstractPigZombie implements EliteMob {

    public ElitePigZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Shaman",
                new Utils.SimpleEntityEquipment(
                        new ItemStack(Material.WOOD, 1, (short) 3),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        new ItemStack(Material.DIAMOND_LEGGINGS),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
                        new ItemStack(Material.COOKIE)
                ),
                4000,
                0.35f,
                10,
                300,
                450
        );
    }

    @Override
    public void onSpawn() {
        getWarlordsNPC().getEntity().getWorld().spigot().strikeLightningEffect(getWarlordsNPC().getLocation(), false);
    }

    @Override
    public void whileAlive() {
        Location location = getWarlordsNPC().getLocation();
        Utils.playGlobalSound(location, Sound.ZOMBIE_PIG_ANGRY, 1, 0.5f);
        Utils.playGlobalSound(location, "paladin.holyradiance.activation", 0.8f, 0.6f);
        WarlordsEntity we = Warlords.getPlayer(getWarlordsNPC().getEntity());
        if (we == null) return;
        EffectUtils.playCylinderAnimation(location, 6, ParticleEffect.FIREWORKS_SPARK, 1);
        for (WarlordsEntity ally : PlayerFilter
                .entitiesAround(we, 6, 6, 6)
                .aliveTeammatesOfExcludingSelf(we)
        ) {
            ally.addHealingInstance(
                    we,
                    "Healing",
                    100,
                    100,
                    -1,
                    100,
                    false,
                    false
            );
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver) {
        Vector v = attacker.getLocation().toVector().subtract(receiver.getLocation().toVector()).normalize().multiply(-1.2).setY(0.2);
        receiver.setVelocity(v, false);
    }

    @Override
    public void onDeath(Location deathLocation, WaveDefenseOption waveDefenseOption) {
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.PURPLE)
                .with(FireworkEffect.Type.BURST)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.ZOMBIE_PIG_DEATH, 2, 0.4f);
    }
}

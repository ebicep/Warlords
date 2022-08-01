package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Zenith extends AbstractZombie implements BossMob {

    public Zenith(Location spawnLocation) {
        super(spawnLocation,
                "Zenith",
                new Utils.SimpleEntityEquipment(
                        Utils.getPlayerSkull("4oot"),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 255),
                        new ItemStack(Material.DIAMOND_SPADE)
                ),
                20000,
                0.45f,
                10,
                800,
                1000
        );
    }

    @Override
    public void onSpawn() {
        for (int i = 0; i < 6; i++) {
            getWarlordsNPC().getEntity().getWorld().spigot().strikeLightningEffect(getWarlordsNPC().getLocation(), false);
        }
    }

    @Override
    public void whileAlive() {
        Location loc = getWarlordsNPC().getLocation();
        Utils.playGlobalSound(loc, "rogue.healingremedy.impact", 1.5f, 2);
        EffectUtils.playSphereAnimation(loc, 4, ParticleEffect.SPELL_WITCH, 2);
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver) {
        getWarlordsNPC().getEntity().getWorld().spigot().strikeLightningEffect(receiver.getLocation(), false);
        Vector v = attacker.getLocation().toVector().subtract(receiver.getLocation().toVector()).normalize().multiply(-1.35).setY(0.3);
        receiver.setVelocity(v, false);
    }

    @Override
    public void onDeath(Location deathLocation, WaveDefenseOption waveDefenseOption) {
        dropItem();
    }
}

package com.ebicep.warlords.game.option.wavedefense.mobs.bosses.bossminions;

import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.skeleton.AbstractSkeleton;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.util.Vector;

public class BoltaroShadow extends AbstractSkeleton implements BossMob {

    public BoltaroShadow(Location spawnLocation) {
        super(spawnLocation,
                "Boltaro's Shadow",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.END_MONSTER),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 0),
                        Weapons.DEMONBLADE.getItem()
                ),
                6000,
                0.45f,
                10,
                200,
                400
        );
    }

    @Override
    public void onSpawn() {
        getWarlordsNPC().getEntity().getWorld().spigot().strikeLightningEffect(getWarlordsNPC().getLocation(), false);
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, String ability) {
        Vector v = attacker.getLocation().toVector().subtract(receiver.getLocation().toVector()).normalize().multiply(-1.1).setY(0.3);
        receiver.setVelocity(v, false);
    }

    @Override
    public void onDamageTaken(WarlordsEntity mob, WarlordsEntity attacker) {
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.ORANGE)
                .with(FireworkEffect.Type.BALL)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.ENDERMAN_DEATH, 2, 0.5f);
    }
}

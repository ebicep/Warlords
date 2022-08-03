package com.ebicep.warlords.game.option.wavedefense.mobs.irongolem;

import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

public class IronGolem extends AbstractIronGolem implements EliteMob {

    public IronGolem(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Apprentice",
                MobTier.ELITE,
                null,
                5000,
                0.4f,
                20,
                400,
                600
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
        Utils.playGlobalSound(receiver.getLocation(), Sound.ITEM_BREAK, 1, 0.5f);
        receiver.setVelocity(new Vector(0, 0.5, 0), false);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption waveDefenseOption) {
        super.onDeath(killer, deathLocation, waveDefenseOption);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.PURPLE)
                .with(FireworkEffect.Type.BURST)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.IRONGOLEM_DEATH, 2, 0.4f);
    }
}

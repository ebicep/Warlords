package com.ebicep.warlords.game.option.wavedefense.mobs.bosses.bossminions;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

public class EnvoyLegionnaire extends AbstractZombie implements BossMob {

    public EnvoyLegionnaire(Location spawnLocation) {
        super(spawnLocation,
                "Envoy Legionnaire",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.FACELESS_BANDIT),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 100, 0, 80),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 100, 0, 80),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 100, 0, 80),
                        Weapons.LUNAR_JUSTICE.getItem()
                ),
                6000,
                0.26f,
                10,
                1000,
                1500
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false);
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        WarlordsEntity zenith = PlayerFilter
                .playingGame(warlordsNPC.getGame())
                .filter(we -> we.getName()
                .equals("Zenith"))
                .findFirstOrNull();

        if (ticksElapsed % 100 == 0) {
            if (zenith != null) {
                zenith.addHealingInstance(
                        warlordsNPC,
                        "Remedy",
                        500,
                        500,
                        -1,
                        100,
                        false,
                        false
                );

                Utils.playGlobalSound(zenith.getLocation(), "shaman.earthlivingweapon.impact", 3, 1.5f);
                EffectUtils.playParticleLinkAnimation(zenith.getLocation(), warlordsNPC.getLocation(), ParticleEffect.VILLAGER_HAPPY);
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.addKnockback(attacker.getLocation(), receiver, -1.1, 0.3);
        Utils.playGlobalSound(attacker.getLocation(), Sound.ENDERMAN_DEATH, 2, 0.2f);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.ORANGE)
                .with(FireworkEffect.Type.BALL)
                .withTrail()
                .build());
    }
}
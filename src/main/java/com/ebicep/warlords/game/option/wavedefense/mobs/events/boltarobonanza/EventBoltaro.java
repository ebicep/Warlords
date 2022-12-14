package com.ebicep.warlords.game.option.wavedefense.mobs.events.boltarobonanza;

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
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EventBoltaro extends AbstractZombie implements BossMob {

    public EventBoltaro(Location spawnLocation) {
        super(spawnLocation,
                "Boltaro",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.DEMON),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
                        new ItemStack(Material.CHAINMAIL_LEGGINGS),
                        new ItemStack(Material.CHAINMAIL_BOOTS),
                        Weapons.DRAKEFANG.getItem()
                ),
                12500,
                0.475f,
                20,
                350,
                500
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        ChatColor.RED + getWarlordsNPC().getName(),
                        ChatColor.GOLD + "Right Hand of the Illusion Vanguard",
                        20, 30, 20
                );
            }
        }

//        for (int i = 0; i < option.getGame().warlordsPlayers().count(); i++) {
//            option.spawnNewMob(new BoltaroExiled(spawnLocation));
//        }
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 100 == 0) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENDERDRAGON_GROWL, 2, 1.5f);
        }

        if (warlordsNPC.getHealth() < 6000) {
            EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 6, ParticleEffect.SMOKE_NORMAL, 3, 20);
            for (int i = 0; i < 2; i++) {
                option.spawnNewMob(new EventBoltaroShadow(warlordsNPC.getLocation(), 1));
            }

            warlordsNPC.die(warlordsNPC);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        if (!(event.getAbility().equals("Multi Hit") || event.getAbility().equals("Intervene"))) {
            new GameRunnable(attacker.getGame()) {
                int counter = 0;

                @Override
                public void run() {
                    counter++;
                    Utils.playGlobalSound(receiver.getLocation(), "warrior.mortalstrike.impact", 2, 1.5f);
                    Utils.addKnockback(attacker.getLocation(), receiver, -0.7, 0.2);
                    receiver.addDamageInstance(attacker, "Multi Hit", 120, 180, 0, 100, false);

                    if (counter == 3 || receiver.isDead()) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(10, 3);
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(self.getLocation(), "warrior.intervene.block", 2, 0.3f);
        EffectUtils.playRandomHitEffect(self.getLocation(), 255, 0, 0, 4);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 6, ParticleEffect.SMOKE_NORMAL, 3, 20);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.WHITE)
                                                                       .with(FireworkEffect.Type.STAR)
                                                                       .withTrail()
                                                                       .build());
    }
}

package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.bosses.bossminions.BoltaroExiled;
import com.ebicep.warlords.game.option.wavedefense.mobs.bosses.bossminions.BoltaroShadow;
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

import java.util.UUID;

public class Boltaro extends AbstractZombie implements BossMob {

    public Boltaro(Location spawnLocation) {
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
                0.465f,
                20,
                350,
                500
        );
    }

    @Override
    public void onSpawn() {
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
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 100 == 0) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENDERDRAGON_GROWL, 2, 1.5f);
        }

        if (warlordsNPC.getHealth() < 6000) {
            EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 6, ParticleEffect.SMOKE_NORMAL, 3);
            for (int i = 0; i < 2; i++) {
                BoltaroShadow minionBoltaro = new BoltaroShadow(warlordsNPC.getLocation());
                minionBoltaro.toNPC(warlordsNPC.getGame(), Team.RED, UUID.randomUUID());
                option.getGame().addNPC(minionBoltaro.getWarlordsNPC());
                option.getMobs().add(minionBoltaro);
            }

            for (int i = 0; i < 6; i++) {
                BoltaroExiled exiled = new BoltaroExiled(warlordsNPC.getLocation());
                exiled.toNPC(warlordsNPC.getGame(), Team.RED, UUID.randomUUID());
                option.getGame().addNPC(exiled.getWarlordsNPC());
                option.getMobs().add(exiled);
            }
            warlordsNPC.die(warlordsNPC);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, String ability) {
        if (!ability.equals("Multi Hit")) {
            new GameRunnable(attacker.getGame()) {
                int counter = 0;
                @Override
                public void run() {
                    counter++;
                    Utils.playGlobalSound(receiver.getLocation(), "warrior.mortalstrike.impact", 2, 1.5f);
                    Utils.addKnockback(attacker.getLocation(), receiver, -0.7, 0.2);
                    receiver.addDamageInstance(attacker, "Multi Hit", 120, 180, -1, 100, false);

                    if (counter == 3 || receiver.isDead()) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(10, 3);
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity mob, WarlordsEntity attacker) {
        Utils.playGlobalSound(mob.getLocation(), "warrior.intervene.block", 2, 0.3f);
        EffectUtils.playRandomHitEffect(mob.getLocation(), 255, 0, 0, 4);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 6, ParticleEffect.SMOKE_NORMAL, 3);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.STAR)
                .withTrail()
                .build());
    }
}

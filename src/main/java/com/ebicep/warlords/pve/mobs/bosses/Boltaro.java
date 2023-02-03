package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.BoltaroExiled;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.BoltaroShadow;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Boltaro extends AbstractZombie implements BossMob {

    private boolean split = false;
    private int mobsKilledBeforeSplit = 0;
    private Listener listener;

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

        option.getGame().registerEvents(listener = new Listener() {
            @EventHandler
            public void onMobDeath(WarlordsDeathEvent event) {
                if (!split && event.getPlayer() instanceof WarlordsNPC) {
                    mobsKilledBeforeSplit++;
                }
            }
        });

        for (int i = 0; i < option.getGame().warlordsPlayers().count(); i++) {
            option.spawnNewMob(new BoltaroExiled(spawnLocation));
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 100 == 0) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENDERDRAGON_GROWL, 2, 1.5f);
        }

        if (warlordsNPC.getHealth() < 6000) {
            split = true;
            split(option);
            warlordsNPC.die(warlordsNPC);
        }
    }

    private void split(WaveDefenseOption option) {
        if (mobsKilledBeforeSplit == 0) {
            option.getGame()
                  .warlordsPlayers()
                  .forEach(warlordsPlayer -> ChallengeAchievements.checkForAchievement(warlordsPlayer, ChallengeAchievements.SIRE));
        }
        HandlerList.unregisterAll(listener);

        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 6, ParticleEffect.SMOKE_NORMAL, 3, 20);
        for (int i = 0; i < option.getGame().warlordsPlayers().count(); i++) {
            option.spawnNewMob(new BoltaroShadow(warlordsNPC.getLocation()));
        }

        for (int i = 0; i < 6; i++) {
            option.spawnNewMob(new BoltaroExiled(warlordsNPC.getLocation()));
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
                    Utils.addKnockback(name, attacker.getLocation(), receiver, -0.7, 0.2);
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
        if (!split) {
            split(option);
        }
    }
}

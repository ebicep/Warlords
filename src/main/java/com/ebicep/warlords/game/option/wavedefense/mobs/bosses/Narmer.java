package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.bosses.bossminions.NarmerAcolyte;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.BasicZombie;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class Narmer extends AbstractZombie implements BossMob {

    private final int earthQuakeRadius = 12;
    private final int executeRadius = 40;
    private final List<WarlordsEntity> acolytes = new ArrayList<>();
    private int timesMegaEarthQuakeActivated = 0;
    private Listener listener;
    private int ticksUntilNewAcolyte = 0;
    private int acolyteDeathTickWindow = 0;

    public Narmer(Location spawnLocation) {
        super(spawnLocation,
                "Narmer",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.BURNING_WITHER_SKELETON),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
                        ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
                        Weapons.WALKING_STICK.getItem()
                ),
                16000,
                0.16f,
                20,
                1600,
                2000
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        ChatColor.GOLD + getWarlordsNPC().getName(),
                        ChatColor.YELLOW + "Unifier of Worlds",
                        20, 30, 20
                );
            }
        }

        float multiplier = option.getDifficulty() == DifficultyIndex.HARD ? 2 : 1;
        for (int i = 0; i < (multiplier * option.getGame().warlordsPlayers().count()); i++) {
            NarmerAcolyte acolyte = new NarmerAcolyte(warlordsNPC.getLocation());
            option.spawnNewMob(acolyte);
            acolyte.getWarlordsNPC().teleport(warlordsNPC.getLocation());
            acolytes.add(acolyte.getWarlordsNPC());
        }

        for (int i = 0; i < 8; i++) {
            option.spawnNewMob(new BasicZombie(warlordsNPC.getLocation()));
        }

        listener = new Listener() {

            @EventHandler
            public void onDamageHealEvent(WarlordsDamageHealingEvent event) {
                if (event.getPlayer().equals(getWarlordsNPC())) {
                    float executeHealth = warlordsNPC.getMaxHealth() * 0.4f;
                    if (warlordsNPC.getHealth() < executeHealth && !acolytes.isEmpty()) {
                        warlordsNPC.setHealth(warlordsNPC.getHealth());
                        Location loc = warlordsNPC.getLocation();
                        option.getGame().forEachOnlineWarlordsEntity(we -> {
                            Utils.playGlobalSound(loc, Sound.BLAZE_HIT, 2, 0.2f);
                            Utils.playGlobalSound(loc, "mage.arcaneshield.activation", 0.4f, 0.5f);
                            we.sendMessage(ChatColor.RED + "Narmer cannot take more damage while his acolytes are still alive!");
                        });
                        event.setCancelled(true);
                    }
                }
            }

            @EventHandler
            private void onAllyDeath(WarlordsDeathEvent event) {
                WarlordsEntity eventPlayer = event.getPlayer();
                Location location = warlordsNPC.getLocation();

                if (eventPlayer.isTeammate(warlordsNPC)) {
                    warlordsNPC.setHealth(warlordsNPC.getHealth() * 1.15f);
                }

                if (acolytes.contains(eventPlayer)) {
                    acolytes.remove(eventPlayer);
                    Utils.playGlobalSound(location, Sound.ENDERDRAGON_GROWL, 2, 0.4f);
                    EffectUtils.playHelixAnimation(
                            location.add(0, 0.15, 0),
                            12,
                            ParticleEffect.SPELL,
                            3,
                            60
                    );

                    boolean isHard = option.getDifficulty() == DifficultyIndex.HARD;
                    int hardModifier = isHard ? 260 : 300;
                    if (acolyteDeathTickWindow > 0) {
                        Utils.playGlobalSound(location, Sound.WITHER_DEATH, 500, 0.2f);
                        Utils.playGlobalSound(location, Sound.WITHER_DEATH, 500, 0.2f);
                        EffectUtils.strikeLightning(location, false, 12);
                        List<WarlordsEntity> warlordsEntities = PlayerFilter
                                .entitiesAround(warlordsNPC, executeRadius, executeRadius, executeRadius)
                                .aliveEnemiesOf(warlordsNPC)
                                .toList();
                        float multiplier = isHard ? 16 : 8;
                        for (WarlordsEntity enemy : warlordsEntities) {
                            enemy.addDamageInstance(
                                    warlordsNPC,
                                    "Death Wish",
                                    965 * multiplier,
                                    1138 * multiplier,
                                    0,
                                    100,
                                    false
                            );
                            enemy.sendMessage(ChatColor.RED + "HINT: Killing Acolytes too quickly might result in an unfavourable outcome.");
                        }
                        for (WarlordsEntity warlordsEntity : warlordsEntities) {
                            ChallengeAchievements.checkForAchievement(warlordsEntity, ChallengeAchievements.FISSURED_END);
                            break;
                        }
                        timesMegaEarthQuakeActivated++;
                    } else {
                        for (WarlordsEntity enemy : PlayerFilter
                                .entitiesAround(warlordsNPC, 15, 15, 15)
                                .aliveEnemiesOf(warlordsNPC)
                        ) {
                            enemy.addDamageInstance(
                                    warlordsNPC,
                                    "Acolyte Revenge",
                                    965,
                                    1138,
                                    0,
                                    100,
                                    false
                            );
                        }
                    }

                    if (acolyteDeathTickWindow <= 0) {
                        acolyteDeathTickWindow = option.getDifficulty() == DifficultyIndex.HARD ? 60 : 20;
                    }

                    ticksUntilNewAcolyte = hardModifier;
                }
            }
        };
        option.getGame().registerEvents(listener);
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        boolean isHard = option.getDifficulty() == DifficultyIndex.HARD;
        int hardModifier = isHard ? 260 : 300;
        Location loc = warlordsNPC.getLocation();
        long playerCount = option.getGame().warlordsPlayers().count();
        float multiplier = option.getDifficulty() == DifficultyIndex.HARD ? 2 : 1;

        if (acolytes.size() < multiplier * playerCount && ticksUntilNewAcolyte <= 0) {
            NarmerAcolyte acolyte = new NarmerAcolyte(loc);
            option.spawnNewMob(acolyte);
            acolytes.add(acolyte.getWarlordsNPC());
            ticksUntilNewAcolyte = hardModifier;
        }

        if (ticksUntilNewAcolyte > 0) {
            ticksUntilNewAcolyte--;
        }

        if (acolyteDeathTickWindow > 0) {
            acolyteDeathTickWindow--;

            for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
                if (we.getEntity() instanceof Player) {
                    PacketUtils.sendTitle(
                            (Player) we.getEntity(),
                            ChatColor.RED + "Death Wish",
                            ChatColor.YELLOW.toString() + acolyteDeathTickWindow / 10f,
                            0, acolyteDeathTickWindow, 0
                    );
                }
            }
        }

        if (ticksElapsed % 15 == 0) {
            for (WarlordsEntity acolyte : acolytes) {
                EffectUtils.playParticleLinkAnimation(loc, acolyte.getLocation(), ParticleEffect.DRIP_LAVA);
            }
        }

        if (ticksElapsed % 160 == 0) {
            //Bukkit.broadcastMessage("earthquake");
            Utils.playGlobalSound(loc, Sound.ENDERDRAGON_GROWL, 2, 0.4f);
            EffectUtils.strikeLightning(loc, false);
            EffectUtils.playSphereAnimation(loc, earthQuakeRadius, ParticleEffect.SPELL_WITCH, 2);
            EffectUtils.playHelixAnimation(loc, earthQuakeRadius, ParticleEffect.FIREWORKS_SPARK, 2, 40);
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(warlordsNPC, earthQuakeRadius, earthQuakeRadius, earthQuakeRadius)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                enemy.addDamageInstance(
                        warlordsNPC,
                        "Ground Shred",
                        750,
                        900,
                        0,
                        100,
                        false
                );
            }
        }

        if (ticksElapsed % 300 == 0) {
            //Bukkit.broadcastMessage("projectile");
            warlordsNPC.getRedAbility().onActivate(warlordsNPC, null);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.addKnockback(attacker.getLocation(), receiver, -2.5, 0.25);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        EffectUtils.playRandomHitEffect(self.getLocation(), 255, 255, 255, 7);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 6, ParticleEffect.FIREWORKS_SPARK, 3, 20);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.STAR)
                .withTrail()
                .build());

        if (timesMegaEarthQuakeActivated >= 2) {
            ChallengeAchievements.checkForAchievement(killer, ChallengeAchievements.NEAR_DEATH_EXPERIENCE);
        }

        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }

}

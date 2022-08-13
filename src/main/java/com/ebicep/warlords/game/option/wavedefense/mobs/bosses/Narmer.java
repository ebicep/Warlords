package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.bosses.bossminions.NarmerAcolyte;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Narmer extends AbstractZombie implements BossMob {

    private int acolytesAlive = 0;

    public Narmer(Location spawnLocation) {
        super(spawnLocation,
                "Narmer",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SAMURAI),
                        new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                        new ItemStack(Material.CHAINMAIL_LEGGINGS),
                        new ItemStack(Material.CHAINMAIL_BOOTS),
                        Weapons.WALKING_STICK.getItem()
                ),
                18000,
                0.2f,
                25,
                1200,
                1600
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
                        20, 40, 20
                );
            }
        }

        for (int i = 0; i < 4; i++) {
            NarmerAcolyte acolyte = new NarmerAcolyte(warlordsNPC.getLocation());
            acolyte.toNPC(warlordsNPC.getGame(), Team.RED, UUID.randomUUID());
            warlordsNPC.getGame().addNPC(acolyte.getWarlordsNPC());
            option.getMobs().add(acolyte);
            acolyte.getWarlordsNPC().teleport(warlordsNPC.getLocation());
            acolytesAlive++;
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 300 == 0) {
            Bukkit.broadcastMessage("earthquake");
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENDERDRAGON_GROWL, 2, 0.4f);
            EffectUtils.strikeLightning(warlordsNPC.getLocation(), false);
            EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 12, ParticleEffect.FIREWORKS_SPARK, 2, 40);
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(warlordsNPC, 12, 12, 12)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                enemy.addDamageInstance(
                        warlordsNPC,
                        "Ground Shred",
                        328,
                        497,
                        -1,
                        100,
                        false
                );
            }
        }

        for (WarlordsEntity acolyte : PlayerFilter
                .playingGame(option.getGame())
                .filter(we -> we.getName().equals("Acolyte of Narmer"))
        ) {
            if (ticksElapsed % 20 == 0) {
                EffectUtils.playParticleLinkAnimation(warlordsNPC.getLocation(), acolyte.getLocation(), ParticleEffect.DRIP_LAVA);
            }
            float executeHealth = warlordsNPC.getMaxHealth() * 0.4f;
            if (warlordsNPC.getHealth() < executeHealth && acolyte.isAlive()) {
                warlordsNPC.setHealth(executeHealth);
                option.getGame().forEachOnlineWarlordsEntity(we -> {
                    we.sendMessage(ChatColor.RED + "Narmer is invincible while his acolytes are still among the living!");
                });
            }

            if (acolyte.isDead()) {
                Bukkit.broadcastMessage("acolyte died");
                Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENDERDRAGON_GROWL, 2, 0.4f);
                EffectUtils.playHelixAnimation(warlordsNPC.getLocation().add(0, 0.1, 0), 12, ParticleEffect.SPELL, 3, 60);
                for (WarlordsEntity enemy : PlayerFilter
                        .entitiesAround(warlordsNPC, 12, 12, 12)
                        .aliveEnemiesOf(warlordsNPC)
                ) {
                    enemy.addDamageInstance(
                            warlordsNPC,
                            "Acolyte Revenge",
                            965,
                            1138,
                            -1,
                            100,
                            false
                    );
                }
            }
        }

        for (WarlordsEntity ally : PlayerFilter
                .playingGame(warlordsNPC.getGame())
                .aliveTeammatesOfExcludingSelf(warlordsNPC)
        ) {
            if (ally.isDead()) {
                float currentHealth = warlordsNPC.getHealth() * 0.2f;
                warlordsNPC.setHealth(warlordsNPC.getHealth() + currentHealth);
                Bukkit.broadcastMessage("healed 20% hp");
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, String ability) {
        Utils.addKnockback(attacker.getLocation(), receiver, -2.5, 0.25f);
    }

    @Override
    public void onDamageTaken(WarlordsEntity mob, WarlordsEntity attacker) {
        EffectUtils.playRandomHitEffect(mob.getLocation(), 255, 255, 255, 4);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 6, ParticleEffect.FIREWORKS_SPARK, 3, 20);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.STAR)
                .withTrail()
                .build());
    }
}

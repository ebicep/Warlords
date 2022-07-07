package com.ebicep.warlords.game.option;

import com.ebicep.customentities.nms.pve.CustomEntity;
import com.ebicep.customentities.nms.pve.CustomSkeleton;
import com.ebicep.customentities.nms.pve.CustomZombie;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class SpawnMobOption implements Option {

    private final Location spawnPoint;
    //private List<Class<? extends Monster>> possibleMobs = Arrays.asList(Zombie.class, Spider.class, Skeleton.class, PigZombie.class);
    private List<Class<? extends CustomEntity>> possibleMobs = Arrays.asList(CustomZombie.class, CustomSkeleton.class);
    private int waveCounter = 0;
    private int spawnCount = 1;

    public SpawnMobOption(Location spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    @Override
    public void start(@Nonnull Game game) {

        new GameRunnable(game) {
            @Override
            public void run() {
                waveCounter++;
                spawnCount = (int) (waveCounter * 1.1f);
                for (int i = 0; i < spawnCount; i++) {
//                    WarlordsEntity entity = game.addNPC(new WarlordsNPC(
//                            UUID.randomUUID(),
//                            "Zombie",
//                            Weapons.ABBADON,
//                            Objects.requireNonNull(WarlordsNPC.spawnCustomEntity(possibleMobs.get((int) (Math.random() * possibleMobs.size())),
//                                    null, (mob) -> {
//
//                                    },
//                                    spawnPoint,
//                                    new Utils.SimpleEntityEquipment(
//                                            new ItemStack(Material.CARPET),
//                                            new ItemStack(Material.DIAMOND_CHESTPLATE),
//                                            new ItemStack(Material.DIAMOND_LEGGINGS),
//                                            new ItemStack(Material.DIAMOND_BOOTS),
//                                            new ItemStack(Material.PRISMARINE_SHARD)))),
//                            game,
//                            Team.RED,
//                            Specializations.PYROMANCER
//                    ));
//
//                    int health = (int) Math.pow(1000, waveCounter / 50.0 + 1);
//                    entity.setMaxHealth(health);
//                    entity.setHealth(health);
                }

                Bukkit.broadcastMessage("Wave: " + waveCounter);
                Bukkit.broadcastMessage("Entities spawned: " + spawnCount);
            }
        }.runTaskTimer(200, 200);
    }
}

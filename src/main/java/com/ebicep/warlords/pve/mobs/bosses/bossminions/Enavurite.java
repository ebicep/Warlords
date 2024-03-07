package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.customentities.nms.pve.CustomBat;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.Enavuris;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Enavurite extends AbstractMob implements ChampionMob {

    @Nullable
    private Enavuris enavuris;
    @Nullable
    private CustomBat leashEntity = null;

    public Enavurite(Location spawnLocation) {
        this(
                spawnLocation,
                "Enavurite",
                10525,
                0.24f,
                5,
                550,
                775
        );
    }

    public Enavurite(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                15,
                250
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ENAVURITE;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        createLeashEntity(warlordsNPC.getLocation());

        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                name + " Internal",
                null,
                Enavurite.class,
                null,
                warlordsNPC,
                CooldownTypes.INTERNAL,
                cooldownManager -> {
                },
                true
        ) {
            @Override
            protected Listener getListener() {
                return new Listener() {
                    @EventHandler
                    public void onCooldownAdd(WarlordsAddCooldownEvent event) {
                        if (event.getWarlordsEntity().equals(warlordsNPC) && event.getAbstractCooldown().getName().equals("Crippling Strike")) {
                            event.setCancelled(true);
                        }
                    }
                };
            }
        });
    }

    private void createLeashEntity(Location location) {
        leashEntity = new CustomBat(location.add(0, -.3, 0));
        leashEntity.setResting(false);
        ((CraftWorld) location.getWorld()).getHandle().addFreshEntity(leashEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        Location location = warlordsNPC.getLocation();
        if (leashEntity == null || !leashEntity.valid) {
            createLeashEntity(location);
        }
        if (enavuris == null) {
            option.getMobs()
                  .stream()
                  .filter(mob -> mob instanceof Enavuris)
                  .map(mob -> (Enavuris) mob)
                  .findFirst()
                  .ifPresent(enavuris -> {
                      this.enavuris = enavuris;
                      this.leashEntity.setLeashedTo(Objects.requireNonNull(enavuris.getLeashHolder()), true);
                  });
        } else if (!option.getMobs().contains(enavuris)) {
            enavuris = null;
        }
        leashEntity.teleportTo(location.getX(), location.getY() - 0.3, location.getZ());
    }

    @Override
    public void onFinalAttack(WarlordsDamageHealingFinalEvent event) {
        if (enavuris == null) {
            return;
        }
        float healing = event.getValue() * 0.35f;
        enavuris.getWarlordsNPC().addHealingInstance(
                warlordsNPC,
                "Soul Tether",
                healing,
                healing,
                0,
                100
        );
        Utils.playGlobalSound(warlordsNPC.getLocation(), "paladin.holyradiance.activation", 2, 1.25f);

        LocationBuilder particleLocation = new LocationBuilder(warlordsNPC.getLocation().clone().add(0, .2, 0))
                .faceTowards(enavuris.getWarlordsNPC().getLocation().clone().add(0, .2, 0));
        float randomYaw = ThreadLocalRandom.current().nextFloat() * 60 - 30;
        float randomPitch = ThreadLocalRandom.current().nextFloat() * 18 + 9;
        particleLocation.yaw(particleLocation.getYaw() + randomYaw)
                        .pitch(particleLocation.getPitch() - randomPitch);
        Color color = Color.fromRGB(0, (int) MathUtils.lerp(55, 255, MathUtils.clamp(event.getValue(), 600, 2000) / 2000), 0);
        new GameRunnable(warlordsNPC.getGame()) {

            int counter = 0;

            @Override
            public void run() {
                double maxDistanceSquared = warlordsNPC.getLocation().distanceSquared(enavuris.getWarlordsNPC().getLocation());
                double distanceSquared = particleLocation.distanceSquared(enavuris.getWarlordsNPC().getLocation().clone().add(0, 1.5, 0));
                float size = MathUtils.lerp(.15f, 3.75f, (float) (1 - distanceSquared / maxDistanceSquared));
                if (counter++ < Math.sqrt(maxDistanceSquared)) {
                    if (counter % 5 == 0) {
                        particleLocation.yaw(particleLocation.getYaw() + (randomYaw > 0 ? -3 : 3))
                                        .pitch(particleLocation.getPitch() + 3);
                    }
                    particleLocation.forward(.5);
                    getDisplayParticle(size);
                    return;
                }
                getDisplayParticle(size);
                particleLocation.faceTowards(enavuris.getWarlordsNPC().getLocation().clone().add(0, 1.5, 0))
                                .forward(.5);
                if (distanceSquared < 1.5) {
                    particleLocation.faceTowards(enavuris.getWarlordsNPC().getLocation().clone().add(0, 1.5, 0))
                                    .forward(.5);
                    getDisplayParticle(4);
                    this.cancel();
                }
            }

            private void getDisplayParticle(float size) {
                EffectUtils.displayParticle(
                        Particle.REDSTONE,
                        particleLocation,
                        2,
                        0, 0, 0,
                        0,
                        new Particle.DustOptions(
                                color,
                                size
                        )
                );
            }
        }.runTaskTimer(0, 0);
    }

    @Override
    public void cleanup(PveOption pveOption) {
        super.cleanup(pveOption);
        if (leashEntity != null) {
            leashEntity.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
        }
    }

}

package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.AreaEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ThunderCloudAbility extends AbstractPveAbility {

    private final List<ThunderCloud> thunderClouds = new ArrayList<>();
    private boolean canHitAllies = true;
    private int secondsToLiveMin = 7;
    private int secondsToLiveMax = 12;
    private int sizeMin = 5;
    private int sizeMax = 10;

    public ThunderCloudAbility(float cooldown, boolean canHitAllies, int secondsToLiveMin, int secondsToLiveMax, int sizeMin, int sizeMax) {
        this(cooldown);
        this.canHitAllies = canHitAllies;
        this.secondsToLiveMin = secondsToLiveMin;
        this.secondsToLiveMax = secondsToLiveMax;
        this.sizeMin = sizeMin;
        this.sizeMax = sizeMax;
    }

    public ThunderCloudAbility(float cooldown) {
        super("Thunder Cloud", cooldown, 50);
    }

    @Override
    public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {

        Location randomSpawnLocation = pveOption.getRandomSpawnLocation((WarlordsEntity) null);
        if (randomSpawnLocation == null) {
            return true;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        randomSpawnLocation.add(random.nextDouble(10) - 5, 0, random.nextDouble(10) - 5);
        thunderClouds.add(new ThunderCloud(
                pveOption.getGame(),
                randomSpawnLocation,
                ThreadLocalRandom.current().nextInt(secondsToLiveMin, secondsToLiveMax) * 20,
                ThreadLocalRandom.current().nextInt(sizeMin, sizeMax),
                canHitAllies
        ));
        return true;
    }

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        if (warlordsEntity != null) {
            thunderClouds.removeIf(thunderCloud -> thunderCloud.tick(warlordsEntity));
        }
        super.runEveryTick(warlordsEntity);
    }

    public static class ThunderCloud {

        private static final int[] CLOUD_COLORS = {150, 200, 250};
        private final Location floorLocation;
        private final int ticksToLive;
        private final int size;
        private final List<CircleEffect> effects = new ArrayList<>();
        private int ticksElapsed = ThreadLocalRandom.current().nextInt(15);
        private int startDamageTick = 30;
        private boolean canHitAllies = true;

        public ThunderCloud(Game game, Location floorLocation, int ticksToLive, int size, boolean canHitAllies) {
            this(game, floorLocation, ticksToLive, size);
            this.canHitAllies = canHitAllies;
        }

        public ThunderCloud(Game game, Location floorLocation, int ticksToLive, int size) {
            this.floorLocation = floorLocation.clone();
            this.ticksToLive = ticksToLive;
            this.size = size;
            ThreadLocalRandom random = ThreadLocalRandom.current();
            double yOffset = random.nextInt(4, 7) + .35;
            int cloudColor = CLOUD_COLORS[random.nextInt(3)];
            effects.add(new CircleEffect(
                    game,
                    null,
                    floorLocation.clone(),
                    this.size,
                    new AreaEffect(
                            yOffset,
                            Particle.REDSTONE,
                            new Particle.DustOptions(Color.fromRGB(cloudColor, cloudColor, cloudColor), 5)
                    ).particlesPerSurface(.4)
            ));
        }

        public boolean tick(@Nonnull WarlordsEntity warlordsEntity) {
            ticksElapsed++;
            startDamageTick--;
            if (ticksElapsed % 5 == 0) {
                effects.forEach(CircleEffect::playEffects);
            }
            if (ticksElapsed % 20 == 0 && startDamageTick <= 0) {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                EffectUtils.strikeLightning(
                        floorLocation.clone().add(random.nextDouble(4) - 2, -1, random.nextDouble(4) - 2),
                        true
                );
                PlayerFilter.entitiesAround(floorLocation, size, size, size)
                            .excluding(warlordsEntity)
                            .forEach(entity -> {
                                boolean teammate = entity.isTeammate(warlordsEntity);
                                if (teammate && !canHitAllies) {
                                    return;
                                }
                                float minDamage = teammate ? 100 : 800;
                                float maxDamage = teammate ? 200 : 1000;
                                entity.addDamageInstance(
                                        warlordsEntity,
                                        "Thunder Strike",
                                        minDamage,
                                        maxDamage,
                                        0,
                                        100,
                                        size > 8 ? EnumSet.of(InstanceFlags.PIERCE) : EnumSet.noneOf(InstanceFlags.class)
                                );
                            });
            }
            return ticksElapsed >= ticksToLive;
        }
    }
}

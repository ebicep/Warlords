package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.flags.Untargetable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.GameType;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GoalUtils {

    public static final RandomSource RANDOM = RandomSource.createNewThreadLocalInstance();

    @Nonnull
    public static List<LivingEntity> getNearbyMatchingTeam(Mob mob, WarlordsEntity thisWarlordsEntity, double distance) {
        List<LivingEntity> list = mob.level().getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(distance, 4.0D, distance)); // getEntitiesWithinAABB
        list.removeIf(entity -> {
            WarlordsEntity warlordsEntity = Warlords.getPlayer(entity.getBukkitEntity());
            return warlordsEntity == null ||
                    warlordsEntity.isDead() ||
                    warlordsEntity.isTeammate(thisWarlordsEntity) ||
                    entity.hasEffect(MobEffects.INVISIBILITY) ||
                    (entity instanceof ServerPlayer p && p.gameMode.getGameModeForPlayer() == GameType.CREATIVE);
        });
        return list;
    }

    @Nonnull
    public static List<Entity> getNearby(Entity entity, WarlordsEntity thisWarlordsEntity, double distance) {
        return PlayerFilter.entitiesAround(entity, distance, distance, distance)
                           .filter(warlordsEntity ->
                                   !warlordsEntity.hasPotionEffect(PotionEffectType.INVISIBILITY) &&
                                           !(warlordsEntity.getEntity() instanceof Player) || ((Player) warlordsEntity.getEntity()).getGameMode() != GameMode.CREATIVE
                           )
                           .stream()
                           .map(WarlordsEntity::getEntity)
                           .collect(Collectors.toList());
    }


    @Nonnull
    public static List<WarlordsEntity> getNearbyEnemies(Entity entity, WarlordsEntity thisWarlordsEntity, double distance, List<Predicate<WarlordsEntity>> extraFilters) {
        return PlayerFilter.entitiesAround(entity, distance, distance, distance)
                           .aliveEnemiesOf(thisWarlordsEntity)
                           .filter(warlordsEntity ->
                                   !warlordsEntity.hasPotionEffect(PotionEffectType.INVISIBILITY) &&
                                           !(warlordsEntity.getEntity() instanceof Player) || ((Player) warlordsEntity.getEntity()).getGameMode() != GameMode.CREATIVE &&
                                           !(warlordsEntity instanceof WarlordsNPC npc && npc.getMob() instanceof Untargetable)
                           )
                           .filter(warlordsEntity -> extraFilters.stream().allMatch(filter -> filter.test(warlordsEntity)))
                           .stream()
                           .collect(Collectors.toList());
    }

    @Nonnull
    public static List<Entity> getNearbyMatchingTeam(Entity entity, Team team, double distance) {
        return PlayerFilter.entitiesAround(entity, distance, distance, distance)
                           .aliveMatchingTeam(team)
                           .filter(warlordsEntity ->
                                   !warlordsEntity.hasPotionEffect(PotionEffectType.INVISIBILITY) &&
                                           !(warlordsEntity.getEntity() instanceof Player) || ((Player) warlordsEntity.getEntity()).getGameMode() != GameMode.CREATIVE
                           )
                           .stream()
                           .map(WarlordsEntity::getEntity)
                           .collect(Collectors.toList());
    }

//    @Nullable
//    public static Vec3 getPosAway(Entity entity, int horizontalRange, int verticalRange, Vec3 start) {
//        net.minecraft.world.entity.Entity craftEntity = ((CraftEntity) entity).getHandle();
//        Vec3 vec3 = craftEntity.position().subtract(start);
//        return RandomPos.generateRandomPos(entity, () -> {
//            BlockPos blockPos = RandomPos.generateRandomDirectionWithinRadians(
//                    RANDOM,
//                    horizontalRange,
//                    verticalRange,
//                    0,
//                    vec3.x,
//                    vec3.z,
//                    (float)Math.PI / 2F
//            );
//            return blockPos == null ? null : generateRandomPosTowardDirection(entity, horizontalRange, blockPos);
//        });
//    }
//
//    @Nullable
//    private static BlockPos generateRandomPosTowardDirection(net.minecraft.world.entity.Entity entity, int horizontalRange, BlockPos fuzz) {
//        return RandomPos.generateRandomPosTowardDirection(entity, horizontalRange, RandomSource.createNewThreadLocalInstance(), fuzz);
//    }
}

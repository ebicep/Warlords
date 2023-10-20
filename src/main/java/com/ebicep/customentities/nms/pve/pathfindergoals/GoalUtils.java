package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.GameType;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GoalUtils {
    @Nonnull
    public static List<LivingEntity> getNearbyWarlordEntities(Mob mob, WarlordsEntity thisWarlordsEntity, double distance) {
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
    public static List<Entity> getNearbyWarlordEntities(Entity entity, WarlordsEntity thisWarlordsEntity, double distance) {
        @NotNull Collection<Entity> list = entity.getNearbyEntities(distance, distance, distance);
        list.removeIf(e -> {
            WarlordsEntity warlordsEntity = Warlords.getPlayer(e);
            return warlordsEntity == null ||
                    warlordsEntity.isDead() ||
                    warlordsEntity.isTeammate(thisWarlordsEntity) ||
                    warlordsEntity.hasPotionEffect(PotionEffectType.INVISIBILITY) ||
                    (e instanceof ServerPlayer p && p.gameMode.getGameModeForPlayer() == GameType.CREATIVE);
        });
        return new ArrayList<>(list);
    }
}

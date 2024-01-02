package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.NarmerAcolyte;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;
import java.util.List;

public class TargetNarmerAcolyteGoal extends TargetGoal {
    protected LivingEntity targetEntity;

    public TargetNarmerAcolyteGoal(Mob entitycreature) {
        this(entitycreature, false, true);
    }

    public TargetNarmerAcolyteGoal(Mob entitycreature, boolean checkSight, boolean onlyNearby) {
        super(entitycreature, checkSight, onlyNearby);
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        WarlordsEntity thisWarlordsEntity = Warlords.getPlayer(mob.getBukkitEntity());
        if (thisWarlordsEntity == null) {
            return false;
        }
        double followRange = this.getFollowDistance();
        List<LivingEntity> list = this.mob.level().getEntitiesOfClass(LivingEntity.class, this.getTargetSearchArea(followRange)); // getEntitiesWithinAABB
        list.removeIf(entity -> {
            WarlordsEntity warlordsEntity = Warlords.getPlayer(entity.getBukkitEntity());
            return warlordsEntity == null ||
                    warlordsEntity.isDead() ||
                    !(warlordsEntity instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof NarmerAcolyte) ||
                    (entity instanceof ServerPlayer p && p.gameMode.getGameModeForPlayer() == GameType.CREATIVE);
        });
        if (list.isEmpty()) {
            return false;
        }
        //sort lowest health first
        list.sort((o1, o2) -> {
            WarlordsEntity warlordsEntity1 = Warlords.getPlayer(o1.getBukkitEntity());
            WarlordsEntity warlordsEntity2 = Warlords.getPlayer(o2.getBukkitEntity());
            return Double.compare(warlordsEntity1 != null ? warlordsEntity1.getCurrentHealth() : Double.MAX_VALUE,
                    warlordsEntity2 != null ? warlordsEntity2.getCurrentHealth() : Double.MAX_VALUE
            );
        });
        this.targetEntity = list.get(0);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.getTarget() != null && mob.getTarget().valid;
    }

    protected AABB getTargetSearchArea(double distance) {
        return this.mob.getBoundingBox().inflate(distance, 4.0D, distance);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.targetEntity, EntityTargetEvent.TargetReason.CUSTOM, true);
        super.start();
    }


}
package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.customentities.nms.pve.pathfindergoals.StrafeGoal;
import com.ebicep.customentities.nms.pve.pathfindergoals.TargetAggroWarlordsEntityGoal;
import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;

public class SmartSkeleton extends AbstractMob implements ChampionMob {

    public SmartSkeleton(Location spawnLocation) {
        super(
                spawnLocation,
                "Smart Skeleton",
                8000,
                0.25f,
                10,
                600,
                900,
                new Fireball(5.5f)
        );
    }

    public SmartSkeleton(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new Fireball(5.5f)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SMART_SKELETON;
    }

    @Override
    public void giveGoals() {

    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        //TODO fix
        Entity entity = ((CraftEntity) npc.getEntity()).getHandle();
        if (entity instanceof net.minecraft.world.entity.Mob mob) {
            mob.goalSelector.removeAllGoals(goal -> true);
            mob.targetSelector.removeAllGoals(goal -> true);
            mob.goalSelector.addGoal(1, new StrafeGoal(mob));
            mob.targetSelector.addGoal(1, new TargetAggroWarlordsEntityGoal(mob));
        }
        this.npc.data().set(NPC.Metadata.USE_MINECRAFT_AI, true);
    }

}

package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.customentities.nms.pve.pathfindergoals.PathfinderGoalPredictTargetFutureLocationGoal;
import com.ebicep.customentities.nms.pve.pathfindergoals.PathfinderGoalTargetAgroWarlordsEntity;
import com.ebicep.customentities.nms.pve.pathfindergoals.StrafeGoal;
import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SmartSkeleton extends AbstractSkeleton implements ChampionMob {

    public SmartSkeleton(Location spawnLocation) {
        super(
                spawnLocation,
                "Smart Skeleton",
                new Utils.SimpleEntityEquipment(
                        null,
                        null,
                        null,
                        null,
                        new ItemStack(Material.BOW)
                ),
                8000,
                0.25f,
                10,
                600,
                900,
                new Fireball(5.5f)
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        entity.resetAI();
        entity.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(100);
        entity.addGoalAI(1, new StrafeGoal(mob));
        entity.addGoalAI(2, new PathfinderGoalPredictTargetFutureLocationGoal(mob));
        entity.addTargetAI(1, new PathfinderGoalTargetAgroWarlordsEntity(mob));
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}

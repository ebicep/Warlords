package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.customentities.nms.pve.pathfindergoals.PathfinderGoalTargetNarmerAcolyte;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.List;
import java.util.Objects;

public class UndeadAcolyte extends AbstractZombie implements BossMinionMob {

    public UndeadAcolyte(Location spawnLocation) {
        this(spawnLocation, "Undead Acolyte", 2000, 0.55f, 0, 0, 0);
    }

    public UndeadAcolyte(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.UNDEAD_ACOLYTE;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        mob.targetSelector.addGoal(0, new PathfinderGoalTargetNarmerAcolyte(mob));
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (warlordsNPC.isDead()) {
            return;
        }
        List<WarlordsEntity> warlordsEntities = PlayerFilter.entitiesAround(warlordsNPC, 2, 1, 2).toList();
        if (warlordsEntities.stream().noneMatch(warlordsEntity -> {
            LivingEntity target = mob.getTarget();
            return target != null && Objects.equals(warlordsEntity.getEntity(), target.getBukkitEntity());
        })) {
            return;
        }
        warlordsEntities.forEach(warlordsEntity -> {
            if (warlordsEntity instanceof WarlordsNPC npc && !(npc.getMob() instanceof NarmerAcolyte)) {
                return;
            }
            int damage = warlordsEntity instanceof WarlordsNPC ? 5000 : 1500;
            warlordsEntity.addDamageInstance(warlordsNPC, "Undead Blast", damage, damage, 0, 100);
        });
        if (!warlordsEntities.isEmpty()) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 500, 1);
            EffectUtils.displayParticle(Particle.EXPLOSION_NORMAL, warlordsNPC.getLocation(), 1, 0, 0, 0, 0.5);
            warlordsNPC.die(warlordsNPC);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}

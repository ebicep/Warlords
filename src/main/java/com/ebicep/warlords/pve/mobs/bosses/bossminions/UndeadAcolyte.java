package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.customentities.nms.pve.pathfindergoals.PathfinderGoalTargetNarmerAcolyte;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.RemoveTarget;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.Objects;

public class UndeadAcolyte extends AbstractZombie implements BossMinionMob {

    public UndeadAcolyte(Location spawnLocation) {
        this(spawnLocation, "Undead Acolyte", 2000, 0.4f, 0, 0, 0);
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
                maxMeleeDamage,
                new RemoveTarget(10)
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
        PlayerFilter.entitiesAround(warlordsNPC, 1.5, 1, 1.5)
                    .filter(warlordsEntity -> {
                        LivingEntity target = mob.getTarget();
                        return target != null && Objects.equals(warlordsEntity.getEntity(), target.getBukkitEntity());
                    })
                    .findAny()
                    .ifPresent(warlordsEntity -> {
                        Utils.playGlobalSound(warlordsEntity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 500, 1);
                        EffectUtils.displayParticle(Particle.EXPLOSION_NORMAL, warlordsEntity.getLocation(), 1, 0, 0, 0, 0.5);
                        warlordsEntity.addDamageInstance(warlordsNPC, "Undead Blast", 5000, 5000, 0, 100);
                        warlordsNPC.die(warlordsNPC);
                    });
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}

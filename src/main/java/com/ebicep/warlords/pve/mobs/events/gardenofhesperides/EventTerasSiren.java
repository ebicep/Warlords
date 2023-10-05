package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.customentities.nms.pve.pathfindergoals.FollowWarlordsEntityGoal;
import com.ebicep.customentities.nms.pve.pathfindergoals.TargetAggroWarlordsEntityGoal;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Location;

public class EventTerasSiren extends AbstractZombie implements BossMinionMob {

    private EventCronus cronus;

    public EventTerasSiren(Location spawnLocation) {
        this(spawnLocation, "Teras Siren", 4100, 0.6f, 0, 250, 350);
    }

    public EventTerasSiren(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
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
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_TERAS_SIREN;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        if (cronus != null) {
            entity.resetAI();
            entity.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(100);
            entity.addGoalAI(0, new FollowWarlordsEntityGoal(mob, cronus.getWarlordsNPC(), 1, 10));
            entity.aiMeleeAttack(1);
            entity.addTargetAI(1, new TargetAggroWarlordsEntityGoal(mob));
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        //TODO animation
        if (Utils.isProjectile(event.getAbility())) {
            event.setCancelled(true);
        }
    }

    public void setCronus(EventCronus cronus) {
        this.cronus = cronus;
    }
}

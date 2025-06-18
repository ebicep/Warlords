package com.ebicep.warlords.pve.mobs.events.baneofimpurities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.java.RandomCollection;
import net.citizensnpcs.api.ai.GoalController;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.Location;
import org.bukkit.Particle;

public class EventCalamityCore extends AbstractEventCore {

    public EventCalamityCore(Location spawnLocation) {
        super(spawnLocation,
                "Exiled Core",
                300000,
                60,
                new RandomCollection<Mob>()
                        .add(0.2, Mob.OVERGROWN_ZOMBIE)
                        .add(0.2, Mob.SKELETAL_SORCERER)
                        .add(0.2, Mob.ADVANCED_WARRIOR_BERSERKER)
                        .add(0.2, Mob.ZOMBIE_KNIGHT)
                        .add(0.2, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
        );
    }

    public EventCalamityCore(
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
                60,
                new RandomCollection<Mob>()
                        .add(0.2, Mob.OVERGROWN_ZOMBIE)
                        .add(0.2, Mob.SKELETAL_SORCERER)
                        .add(0.2, Mob.ADVANCED_WARRIOR_BERSERKER)
                        .add(0.2, Mob.ZOMBIE_KNIGHT)
                        .add(0.2, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
        );
    }

    @Override
    public void customDeathAnimation() {
        Location floorLocation = warlordsNPC.getLocation().subtract(0, 3, 0);
        EffectUtils.strikeLightning(floorLocation, false, 3);
        floorLocation.add(0, 1, 0);
        EffectUtils.displayParticle(
                Particle.CRIMSON_SPORE,
                floorLocation,
                1500,
                10,
                0,
                10,
                0
        );
        EffectUtils.displayParticle(
                Particle.POOF,
                floorLocation,
                15,
                10,
                0,
                10,
                0
        );
        floorLocation.add(0, 2, 0);
        EffectUtils.displayParticle(
                Particle.EXPLOSION_EMITTER,
                floorLocation,
                3,
                0,
                0,
                0,
                1
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_CALAMITY_CORE;
    }


    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        GoalController goalController = npc.getDefaultGoalController();
        goalController.clear();
        ArmorStandTrait armorStandTrait = warlordsNPC.getNpc().getOrAddTrait(ArmorStandTrait.class);
        armorStandTrait.setVisible(false);
        armorStandTrait.setGravity(false);
        warlordsNPC.teleport(warlordsNPC.getLocation().add(0, -1, 0));
    }

}

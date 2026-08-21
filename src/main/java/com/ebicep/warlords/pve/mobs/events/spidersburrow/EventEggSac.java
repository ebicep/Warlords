package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import net.citizensnpcs.api.ai.BehaviorController;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.Location;

public class EventEggSac extends AbstractMob implements BossMinionMob {

    public EventEggSac(Location spawnLocation) {
        this(spawnLocation, "Egg Sac", 10000, 0, 0, 0, 0);
    }

    public EventEggSac(
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
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_MITHRA_EGG_SAC;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        BehaviorController goalController = npc.getDefaultBehaviorController();
        goalController.clear();
        ArmorStandTrait armorStandTrait = warlordsNPC.getNpc().getOrAddTrait(ArmorStandTrait.class);
        armorStandTrait.setVisible(false);
        armorStandTrait.setGravity(false);
        warlordsNPC.teleport(warlordsNPC.getLocation().add(0, -1.3, 0));
    }

}

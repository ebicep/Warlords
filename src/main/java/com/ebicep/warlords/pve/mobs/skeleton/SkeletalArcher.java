package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.customentities.nms.pve.pathfindergoals.NPCFleeWarlordsEntityGoal;
import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.Controllable;
import net.citizensnpcs.trait.MountTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.function.Consumer;

public class SkeletalArcher extends AbstractMob implements EliteMob {

    public SkeletalArcher(Location spawnLocation) {
        this(spawnLocation, "Skeletal Archer", 5000, 0f, 10, 0, 0, new Fireball(3));
    }

    public SkeletalArcher(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

    @Override
    public WarlordsNPC toNPC(Game game, Team team, Consumer<WarlordsNPC> modifyStats) {
        //TODO FIX
        NPC mountNPC = NPCManager.NPC_REGISTRY.createNPC(EntityType.HORSE, "Skeletal Archer Mount");
        mountNPC.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        Controllable controllable = mountNPC.getOrAddTrait(Controllable.class);
        controllable.setOwnerRequired(false);
        mountNPC.getDefaultGoalController().addBehavior(new NPCFleeWarlordsEntityGoal(mountNPC, team), 2);
        mountNPC.getNavigator().getDefaultParameters().speedModifier(1.5f);
        mountNPC.spawn(spawnLocation);


        WarlordsNPC toNPC = super.toNPC(game, team, modifyStats);
        npc.getOrAddTrait(MountTrait.class).setMountedOn(mountNPC.getUniqueId());

        new GameRunnable(game) {

            @Override
            public void run() {
                controllable.setEnabled(false);
            }
        }.runTaskLater(5);

        return toNPC;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SKELETAL_ARCHER;
    }


}

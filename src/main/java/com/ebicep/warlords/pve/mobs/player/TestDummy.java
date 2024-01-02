package com.ebicep.warlords.pve.mobs.player;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.PlayerMob;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class TestDummy extends AbstractMob implements PlayerMob {

    public TestDummy(Location spawnLocation) {
        super(
                spawnLocation,
                "TestDummy",
                1_000_000,
                .2f,
                0,
                0,
                0
        );
    }

    public TestDummy(
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
        return Mob.TEST_DUMMY;
    }

    @Override
    public void onSpawn(PveOption option) {
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                TestDummy.class,
                null,
                warlordsNPC,
                CooldownTypes.INTERNAL,
                cooldownManager -> {
                },
                false
        ) {
            @Override
            public void multiplyKB(Vector currentVector) {
                currentVector.zero();
            }
        });
        warlordsNPC.setCurrentHealth(warlordsNPC.getMaxBaseHealth() / 2);
        warlordsNPC.updateHealth();
    }

    @Override
    public void onNPCCreate() {
        super.onNPCCreate();
        npc.data().set(NPC.Metadata.COLLIDABLE, false);
    }

    @Override
    public void giveGoals() {
        //no goals
    }

}

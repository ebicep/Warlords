package com.ebicep.warlords.pve.mobs.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.PlayerMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

import javax.annotation.Nonnull;

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
        return Mob.TEST_DUMMY;
    }

    @Override
    public void giveGoals() {
        //no goals
    }

    @Override
    public void onNPCCreate() {
        super.onNPCCreate();
        npc.data().set(NPC.Metadata.COLLIDABLE, false);
    }

    @Override
    public void onSpawn(PveOption option) {
        warlordsNPC.setNameColor(warlordsNPC.getTeam().getTeamColor());
        warlordsNPC.addKnockbackModifier(warlordsNPC, "KB RES", -100, -1);
        warlordsNPC.setCurrentHealth(warlordsNPC.getMaxBaseHealth() / 2);
        warlordsNPC.updateHealth();
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        Game game = killer.getGame();
        new GameRunnable(game) {
            @Override
            public void run() {
                game.getPlayers().remove(warlordsNPC.getUuid());
                Warlords.removePlayer(warlordsNPC.getUuid());
            }
        }.runTaskLater(1);
    }

}

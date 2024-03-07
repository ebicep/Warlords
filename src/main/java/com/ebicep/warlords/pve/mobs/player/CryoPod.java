package com.ebicep.warlords.pve.mobs.player;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.PlayerMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.Location;

public class CryoPod extends AbstractMob implements PlayerMob {

    public CryoPod(Location spawnLocation) {
        this(spawnLocation, "Cryopod", 20000, 0, 0, 0, 0);
    }

    public CryoPod(Location spawnLocation, String playerName) {
        this(spawnLocation, playerName + "'s Cryopod", 20000, 0, 0, 0, 0);
    }

    public CryoPod(
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
        return Mob.CRYOPOD;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        PlayerFilter.entitiesAround(warlordsNPC.getLocation(), 15, 15, 15)
                    .aliveEnemiesOf(warlordsNPC)
                    .forEach(warlordsEntity -> {
                        if (warlordsEntity instanceof WarlordsNPC) {
                            ((WarlordsNPC) warlordsEntity).getMob().setTarget(warlordsNPC);
                        }
                    });
    }

    @Override
    public void onNPCCreate() {
        super.onNPCCreate();
        ArmorStandTrait armorStandTrait = npc.getOrAddTrait(ArmorStandTrait.class);
        armorStandTrait.setGravity(false);
        armorStandTrait.setVisible(false);
    }

}

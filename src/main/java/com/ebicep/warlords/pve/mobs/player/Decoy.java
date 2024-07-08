package com.ebicep.warlords.pve.mobs.player;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.PlayerMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Decoy extends AbstractMob implements PlayerMob {

    public Decoy(Location spawnLocation) {
        this(spawnLocation, "Decoy", 5000, 0, 0, 0, 0);
    }

    public Decoy(
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


    public Decoy(Location spawnLocation, String playerName, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack weapon) {
        this(spawnLocation, playerName + "'s Decoy", 5000, 0, 0, 0, 0);
        this.equipment = new Utils.SimpleEntityEquipment(
                helmet,
                chestplate,
                leggings,
                boots,
                weapon
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.DECOY;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        warlordsNPC.setStunTicks(100000);
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

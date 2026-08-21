package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import net.citizensnpcs.api.ai.BehaviorController;
import net.citizensnpcs.api.ai.SimpleBehaviorController;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EggSac extends AbstractMob implements BossMinionMob {

    public static final ItemStack EGG_SAC_ITEM = new ItemStack(Material.DRAGON_EGG);

    public EggSac(Location spawnLocation) {
        this(spawnLocation, "Egg Sac", 10000, 0, 0, 0, 0);
    }

    public EggSac(Location spawnLocation, int maxHealth) {
        this(spawnLocation, "Egg Sac", maxHealth, 0, 0, 0, 0);
    }

    public EggSac(
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
        return Mob.MITHRA_EGG_SAC;
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

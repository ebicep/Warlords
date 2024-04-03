package com.ebicep.warlords.game.option.wackamole.moles;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.PlayerMob;
import com.ebicep.warlords.util.pve.PlayerSkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class MoleArmorStand extends AbstractMob implements PlayerMob {

    public static final Set<String> HEADS = new HashSet<>() {{
        for (PlayerSkullID id : PlayerSkullID.VALUES) {
            add(id.getTextureID());
        }
    }};

    public MoleArmorStand(Location spawnLocation) {
        super(spawnLocation, "", 100, 0, 0, 0, 0);
    }

    public MoleArmorStand(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, 0, 0);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.WHACK_A_MOLE_ARMOR_STAND;
    }

    @Override
    public void onNPCCreate() {
        super.onNPCCreate();

        npc.getNavigator().setPaused(true);

        ArmorStandTrait armorStandTrait = npc.getOrAddTrait(ArmorStandTrait.class);
        armorStandTrait.setMarker(false);
        armorStandTrait.setVisible(false);
        armorStandTrait.setGravity(false);

        equipment = new Utils.SimpleEntityEquipment(
                SkullUtils.getSkullFrom(HEADS.stream().skip(ThreadLocalRandom.current().nextInt(HEADS.size())).findFirst().orElse(PlayerSkullID.SUMSMASH.getTextureID())),
                null,
                null,
                null,
                null
        );
        updateEquipment();
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        if (ThreadLocalRandom.current().nextInt(5) == 3) {
            warlordsNPC.setMaxHealthAndHeal(300);
        }
    }
}

package com.ebicep.warlords.pve.mobs.pvp;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.PlayerMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;

public class TricksterDummy extends AbstractMob implements PlayerMob {

    private WarlordsEntity warlordsEntity;

    public TricksterDummy(Location spawnLocation) {
        this(spawnLocation, "Dummy", 1000, 0, 0, 0, 0);
    }

    public TricksterDummy(
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


    public TricksterDummy(Location spawnLocation, int health, WarlordsEntity warlordsEntity) {
        this(spawnLocation, "Dummy", health, 0, 0, 0, 0);
        this.equipment = new Utils.SimpleEntityEquipment(
                warlordsEntity.getHelmet(),
                warlordsEntity.getChestplate(),
                warlordsEntity.getLeggings(),
                warlordsEntity.getBoots(),
                warlordsEntity.getWeaponItem()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.TRICKSTER_DUMMY;
    }

    @Override
    public void giveGoals() {

    }

    @Override
    public void onSpawn(PveOption option) {

    }

}

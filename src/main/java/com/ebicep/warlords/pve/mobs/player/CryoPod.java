package com.ebicep.warlords.pve.mobs.player;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class CryoPod extends AbstractZombie {
    public CryoPod(Location spawnLocation, String playerName) {
        super(
                spawnLocation,
                playerName + "'s Cryopod",
                MobTier.BASE,
                null,
                20000,
                0,
                0,
                0,
                0
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        warlordsNPC.getEntity().remove();
        ArmorStand armorStand = warlordsNPC.getWorld().spawn(warlordsNPC.getLocation(), ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setCustomName(name);
        warlordsNPC.setEntity(armorStand);
        warlordsNPC.updateEntity();

        PlayerFilter.entitiesAround(warlordsNPC.getLocation(), 15, 15, 15)
                    .aliveEnemiesOf(warlordsNPC)
                    .forEach(warlordsEntity -> {
                        if (warlordsEntity instanceof WarlordsNPC) {
                            ((WarlordsNPC) warlordsEntity).getMob().setTarget(warlordsNPC);
                        }
                    });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public double weaponDropRate() {
        return 0;
    }

    @Override
    public int commonWeaponDropChance() {
        return 0;
    }

    @Override
    public int rareWeaponDropChance() {
        return 0;
    }

    @Override
    public int epicWeaponDropChance() {
        return 0;
    }
}

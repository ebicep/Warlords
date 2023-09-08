package com.ebicep.warlords.pve.mobs.player;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Decoy extends AbstractZombie implements BasicMob {
    public Decoy(Location spawnLocation, String playerName, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack weapon) {
        super(
                spawnLocation,
                playerName + "'s Decoy",
                5000,
                0,
                0,
                0,
                0
        );
    }

    @Override
    public Mob getMobRegistry() {
        return null;
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

package com.ebicep.warlords.pve.mobs.witherskeleton;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class CelestialOpus extends AbstractWitherSkeleton implements EliteMob {

    public CelestialOpus(Location spawnLocation) {
        super(
                spawnLocation,
                "Celestial Opus",
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.CELESTIAL_GOLDOR),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 40, 40, 40),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 40, 40, 40),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 40, 40, 40),
                        Weapons.SILVER_PHANTASM_SAWBLADE.getItem()
                ),
                9000,
                0.4f,
                10,
                800,
                1000
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
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
}

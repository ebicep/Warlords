package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.abilities.CripplingStrike;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class VoidAnomaly extends AbstractSkeleton implements EliteMob {

    public VoidAnomaly(Location spawnLocation) {
        super(
                spawnLocation,
                "Void Anomaly",
                MobTier.ILLUSION,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SEEK_DOORS),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 64, 64, 64),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 64, 64, 64),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 64, 64, 64),
                        Weapons.FABLED_HEROICS_SWORD.getItem()
                ),
                10000,
                0.42f,
                0,
                500,
                700
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
        Utils.addKnockback(name, attacker.getLocation(), receiver, 1, 0.15);
        CripplingStrike.cripple(attacker, receiver, name + " Cripple", 80);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

}

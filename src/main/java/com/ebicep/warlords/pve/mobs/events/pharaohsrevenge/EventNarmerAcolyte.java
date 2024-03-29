package com.ebicep.warlords.pve.mobs.events.pharaohsrevenge;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class EventNarmerAcolyte extends AbstractZombie implements BossMob {

    public EventNarmerAcolyte(Location spawnLocation) {
        super(spawnLocation,
                "Acolyte of Narmer",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.RED_EYE),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 15),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 15),
                        Weapons.DEMONBLADE.getItem()
                ),
                5000,
                0.35f,
                0,
                540,
                765
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 400 == 0) {
            warlordsNPC.getMob().removeTarget();
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}

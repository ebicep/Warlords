package com.ebicep.warlords.pve.mobs.zombie;

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
import org.bukkit.Sound;

public class SlimeZombie extends AbstractZombie implements EliteMob {

    public SlimeZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Slime Guard",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SLIME_BLOCK),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 106, 255, 106),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 106, 255, 106),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 106, 255, 106),
                        Weapons.NEW_LEAF_SPEAR.getItem()
                ),
                6000,
                0.39f,
                10,
                500,
                700
        );
    }

    @Override
    public void onSpawn(PveOption option) {

    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        receiver.playSound(receiver.getLocation(), Sound.SLIME_WALK, 500, 0.2f);
        receiver.getSpeed().addSpeedModifier(warlordsNPC, "Slime Slowness", -30, 2 * 20);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}

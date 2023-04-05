package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilties.Earthliving;
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

public class EventForsakenFoliage extends AbstractZombie implements BossMob {


    public EventForsakenFoliage(Location spawnLocation) {
        super(
                spawnLocation,
                "Forsaken Respite",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.JUNGLE_SPIDER),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 14, 87, 9),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 14, 87, 9),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 14, 87, 9),
                        Weapons.NEW_LEAF_SPEAR.getItem()
                ),
                2200,
                0.45f,
                0,
                300,
                450
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        // Attacks are converted into Earth Living with double the proc chance as standard.
        Earthliving earthliving = new Earthliving();
        earthliving.setProcChance(earthliving.getProcChance() * 2);
        earthliving.setTickDuration(18000);
        earthliving.onActivate(warlordsNPC, null);
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

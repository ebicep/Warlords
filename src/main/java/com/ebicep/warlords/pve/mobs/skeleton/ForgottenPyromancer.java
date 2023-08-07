package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class ForgottenPyromancer extends AbstractSkeleton implements EliteMob {

    public ForgottenPyromancer(Location spawnLocation) {
        super(
                spawnLocation,
                "Forgotten Pyromancer",
                MobTier.ILLUSION,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.WITHER_SOUL),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 29, 49, 64),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 29, 49, 64),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 29, 49, 64),
                        null
                ),
                5000,
                0.05f,
                20,
                0,
                0,
                new Fireball(5.5f), new FlameBurst(20)
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

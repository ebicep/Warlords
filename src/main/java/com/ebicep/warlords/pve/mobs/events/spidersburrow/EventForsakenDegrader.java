package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilities.CripplingStrike;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.Spider;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class EventForsakenDegrader extends AbstractZombie implements BossMob, Spider {


    public EventForsakenDegrader(Location spawnLocation) {
        super(
                spawnLocation,
                "Forsaken Respite",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.DECAPITATED_SPIDER),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 5, 5, 5),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 5, 5, 5),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 5, 5, 5),
                        Weapons.DRAKEFANG.getItem()
                ),
                2700,
                0.45f,
                0,
                300,
                450
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        int currentWave = option.getWaveCounter();
        if (currentWave % 5 == 0 && currentWave > 5) {
            float additionalHealthMultiplier = 1 + .15f * (currentWave / 5f - 1);
            warlordsNPC.setMaxBaseHealth(warlordsNPC.getMaxBaseHealth() * additionalHealthMultiplier);
            warlordsNPC.heal();
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        // Applies crippling to enemies for 3s.
        CripplingStrike.cripple(attacker, receiver, name, 3 * 20);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}

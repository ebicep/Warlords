package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventForsakenShrieker extends AbstractZombie implements BossMob {


    public EventForsakenShrieker(Location spawnLocation) {
        super(
                spawnLocation,
                "Forsaken Respite",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.DEEP_DARK_CRAWLER),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 87, 9, 86),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 87, 9, 86),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 87, 9, 86),
                        Weapons.SILVER_PHANTASM_SWORD_3.getItem()
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

    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        // Applies Darkness to enemies within a 10 block radius for 1s. Can occur every 5s.
        if (ticksElapsed % 100 == 0) {
            PlayerFilterGeneric.entitiesAround(warlordsNPC, 10, 10, 10)
                               .enemiesOf(warlordsNPC)
                               .warlordsPlayers()
                               .forEach(warlordsPlayer -> warlordsPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, true, false)));
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

}

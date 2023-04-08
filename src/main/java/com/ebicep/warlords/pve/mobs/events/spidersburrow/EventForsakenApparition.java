package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.Spider;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventForsakenApparition extends AbstractZombie implements BossMob, Spider {

    private boolean damaged = false;

    public EventForsakenApparition(Location spawnLocation) {
        super(
                spawnLocation,
                "Forsaken Apparition",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SPIDER_SPIRIT),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 64, 140, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 64, 140, 255),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 64, 140, 255),
                        Weapons.SILVER_PHANTASM_SWORD_4.getItem()
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

    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (!damaged) {
            damaged = true;
            // When this spider takes damage, it turns invisible, giving it a 15% increase to damage and a 15% resistance boost.
            self.setDamageResistance(15);
            self.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 60 * 15, 0, true, false));
        }
    }

}

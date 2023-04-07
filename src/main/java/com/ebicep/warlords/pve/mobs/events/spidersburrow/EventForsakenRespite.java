package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilties.ImpalingStrike;
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
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class EventForsakenRespite extends AbstractZombie implements BossMob, Spider {


    public EventForsakenRespite(Location spawnLocation) {
        super(
                spawnLocation,
                "Forsaken Respite",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SPIDER),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 120, 120),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 120, 120, 120),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 120, 120, 120),
                        Weapons.NOMEGUSTA.getItem()
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
        // Applies leech to enemies for 3s. Occurs every 7s.
        if (ticksElapsed % 140 == 0) {
            PlayerFilterGeneric.playingGameWarlordsPlayers(option.getGame())
                               .enemiesOf(warlordsNPC)
                               .forEach(warlordsPlayer ->
                                       ImpalingStrike.giveLeechCooldown(
                                               warlordsNPC,
                                               warlordsPlayer,
                                               3 * 20,
                                               .25f,
                                               .15f,
                                               warlordsDamageHealingFinalEvent -> {

                                               }
                                       ));
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}

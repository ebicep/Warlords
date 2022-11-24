package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Void extends AbstractZombie implements BossMob {

    public Void(Location spawnLocation) {
        super(
                spawnLocation,
                "Void",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.IRON_QUEEN),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
                        Weapons.VOID_EDGE.getItem()
                ),
                80000,
                0.2f,
                20,
                7000,
                8500
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        ChatColor.BLACK + getWarlordsNPC().getName(),
                        ChatColor.BLACK + "?????",
                        20, 30, 20
                );
            }
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity mob, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}

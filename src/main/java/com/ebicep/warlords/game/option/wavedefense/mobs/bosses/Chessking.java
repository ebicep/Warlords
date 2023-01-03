package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.slime.AbstractSlime;
import com.ebicep.warlords.game.option.wavedefense.mobs.slime.VoidSlime;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.SlimeZombie;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Chessking extends AbstractSlime implements BossMob {

    public Chessking(Location spawnLocation) {
        super(spawnLocation,
                "Chessking",
                MobTier.BOSS,
                null,
                100000,
                0.3f,
                30,
                0,
                0
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        this.entity.get().setSize(19);
        for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        ChatColor.GREEN + getWarlordsNPC().getName(),
                        ChatColor.GRAY + "Goblin from the local basement",
                        20, 30, 20
                );
            }
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 100 == 0) {
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(warlordsNPC, 8, 8, 8)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                we.addDamageInstance(warlordsNPC, "Belch", 2800, 3600, -1, 100, false);
            }
        }

        if (ticksElapsed % 300 == 0) {
            for (int i = 0; i < option.getGame().warlordsPlayers().count(); i++) {
                option.spawnNewMob(new SlimeZombie(warlordsNPC.getLocation()));
            }
        }

        if (ticksElapsed % 1200 == 0) {
            for (int i = 0; i < option.getGame().warlordsPlayers().count(); i++) {
                option.spawnNewMob(new VoidSlime(warlordsNPC.getLocation()));
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (Utils.isProjectile(event.getAbility())) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ARROW_HIT, 2, 0.1f);
            warlordsNPC.addHealingInstance(warlordsNPC, "Blob Heal", 500, 500, -1, 100, false, false);
        } else {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.SLIME_ATTACK, 2, 0.2f);
        }
    }
}

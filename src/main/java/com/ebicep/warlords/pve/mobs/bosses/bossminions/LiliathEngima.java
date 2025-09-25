package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.commands.miscellaneouscommands.ChatCommand;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.GiantLaserAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Random;

public class LiliathEngima extends AbstractMob implements BossMinionMob {

    private WarlordsEntity target;
    private GiantLaserAbility giantLaserAbility;

    public LiliathEngima(Location spawnLocation, WarlordsEntity target) {
        super(
                spawnLocation,
                ChatColor.BOLD + target.getName(),
                3000,
                0,
                0,
                0,
                0
        );
        this.target = target;
    }

    public LiliathEngima(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        giantLaserAbility = new GiantLaserAbility(
                warlordsNPC,
                warlordsNPC,
                () -> warlordsNPC.getEyeLocation(),
                50,
                15,
                70,
                1.3,
                2,
                1000,
                false,
                2
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.LILIATH_ENIGMA;
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (event.getSource() != target) {
            event.setCancelled(true);
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        Random rand = new Random();
        int t = Math.max(200, rand.nextInt(400));
        if (ticksElapsed % t == 0 && ticksElapsed > 0) {
            giantLaserAbility.start(warlordsNPC.getGame());
        }
    }
}

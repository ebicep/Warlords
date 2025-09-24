package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.commands.miscellaneouscommands.ChatCommand;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class LiliathEngima extends AbstractMob implements BossMinionMob {

    private WarlordsEntity target;

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
    public Mob getMobRegistry() {
        return Mob.LILIATH_ENIGMA;
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (event.getSource() != target) {
            event.setCancelled(true);
        }
    }
}

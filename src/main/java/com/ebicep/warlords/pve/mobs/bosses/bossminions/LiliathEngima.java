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
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LiliathEngima extends AbstractMob implements BossMinionMob {

    private List<WarlordsEntity> targets;
    Random rand = new Random();

    public LiliathEngima(Location spawnLocation, List<WarlordsEntity> targets) {
        super(
                spawnLocation,
                ChatColor.BOLD + (targets.get(new Random().nextInt(targets.size())).getName()),
                3000,
                0,
                0,
                0,
                0
        );
        this.targets = targets;
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

    }

    @Override
    public Mob getMobRegistry() {
        return Mob.LILIATH_ENIGMA;
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        WarlordsEntity target = targets.get(rand.nextInt(targets.size()));
        Bukkit.broadcast(Component.text("target: " + target.getName()));
        if (event.getSource() == target) {
            event.setCancelled(true);
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }
}

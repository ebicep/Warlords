package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.commands.miscellaneouscommands.ChatCommand;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

import javax.annotation.Nonnull;

public class SkyCrystal extends AbstractMob implements BossMinionMob {

    private TextDisplay holo;

    public SkyCrystal(Location spawnLocation, String label) {
        super(
                spawnLocation,
                ChatColor.BOLD + label,
                3000,
                0f,
                20,
                0,
                0
        );
    }

    public SkyCrystal(Location spawnLocation) {
        super(
                spawnLocation,
                "Crystal",
                3000,
                0,
                0,
                0,
                0
        );
    }

    public SkyCrystal(
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
        holo = warlordsNPC.getWorld().spawn(warlordsNPC.getLocation().clone().add(0, 2.1, 0), TextDisplay.class, td -> {
            td.setBillboard(Display.Billboard.CENTER);
            td.setSeeThrough(true);
            td.setBackgroundColor(Color.BLACK);
            td.setText(ChatColor.RED + warlordsNPC.getName());
            td.setLineWidth(20);
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (!holo.isDead() && holo.getWorld() == warlordsNPC.getWorld()) {
            holo.teleport(warlordsNPC.getLocation().clone().add(0, 2.1, 0));
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        holo.remove();
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SKY_CRYSTAL;
    }
}

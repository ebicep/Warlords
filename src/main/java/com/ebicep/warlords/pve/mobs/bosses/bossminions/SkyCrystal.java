package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

public class SkyCrystal extends AbstractMob implements BossMinionMob {

    private TextDisplay holo;

    public SkyCrystal(Location spawnLocation, String label) {
        super(
                spawnLocation,
                ChatColor.BOLD + label,
                4000,
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
                4000,
                0,
                20,
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
        holo = warlordsNPC.getWorld().spawn(warlordsNPC.getLocation().clone().add(0, 4, 0), TextDisplay.class, td -> {
            td.setBillboard(Display.Billboard.CENTER);
            td.setSeeThrough(true);
            td.setBackgroundColor(Color.BLACK);
            td.text(Component.text(ChatColor.stripColor(warlordsNPC.getName()), NamedTextColor.RED, TextDecoration.BOLD));
            td.setLineWidth(20);
            td.setTeleportDuration(0);
            td.setTransformation(new Transformation(
                    new Vector3f(),
                    new Quaternionf(),
                    new Vector3f(5, 5, 5),
                    new Quaternionf()
            ));
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (holo != null && !holo.isDead() && holo.getWorld() == warlordsNPC.getWorld()) {
            holo.teleport(warlordsNPC.getLocation().clone().add(0, 4, 0));
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        if (holo != null) {
            holo.remove();
        }
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SKY_CRYSTAL;
    }
}

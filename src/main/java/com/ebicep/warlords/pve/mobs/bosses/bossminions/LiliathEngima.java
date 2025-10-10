package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.commands.miscellaneouscommands.ChatCommand;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.GiantLaserAbility;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.LaserBarrageAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LiliathEngima extends AbstractMob implements BossMinionMob {

    private LaserBarrageAbility laserBarrageAbility;
    private List<WarlordsEntity> targets;
    private WarlordsEntity target;
    private TextDisplay holo;
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
        this.target = targets.get(rand.nextInt(targets.size()));
    }

    public LiliathEngima(Location spawnLocation) {
        super(
                spawnLocation,
                "Liliath Engima",
                4000,
                0,
                20,
                0,
                0
        );
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
        warlordsNPC.addKnockbackModifier(warlordsNPC, "KB RES", -100, 99999);
        holo = warlordsNPC.getWorld().spawn(warlordsNPC.getLocation().clone().add(0, 3, 0), TextDisplay.class, td -> {
            td.setBillboard(Display.Billboard.CENTER);
            td.setSeeThrough(true);
            td.setBackgroundColor(Color.BLACK);
            td.setText(ChatColor.BOLD + target.getName());
            td.setLineWidth(80);
            td.setTransformation(new Transformation(
                    new Vector3f(),
                    new Quaternionf(),
                    new Vector3f(2f, 2f, 2f),
                    new Quaternionf()
            ));
        });

        laserBarrageAbility = new LaserBarrageAbility(
                warlordsNPC.getGame(),
                warlordsNPC.getLocation(),
                1,
                40,
                20,
                10,
                70,
                2,
                warlordsNPC
        );

        warlordsNPC.setName(ChatColor.BOLD + target.getName());
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
        if (!holo.isDead() && holo.getWorld() == warlordsNPC.getWorld()) {
            holo.teleport(warlordsNPC.getLocation().clone().add(0, 3, 0));
        }

        int randomInterval = rand.nextInt(300, 600);
        if (ticksElapsed % randomInterval == 0 && ticksElapsed > 0) {
            laserBarrageAbility.start(targets);
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        holo.remove();
    }
}

package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.LaserBarrageAbility;
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
import java.util.List;
import java.util.Random;

public class LiliathEngima extends AbstractMob implements BossMinionMob {

    private LaserBarrageAbility laserBarrageAbility;
    private final List<WarlordsEntity> targets;
    private WarlordsEntity target;
    private TextDisplay holo;
    private int nextLaserTick;
    private final Random rand = new Random();

    public LiliathEngima(Location spawnLocation, List<WarlordsEntity> targets) {
        super(
                spawnLocation,
                "Liliath Engima",
                3000,
                0,
                0,
                0,
                0
        );
        this.targets = targets == null ? List.of() : List.copyOf(targets);
        if (!this.targets.isEmpty()) {
            this.target = this.targets.get(rand.nextInt(this.targets.size()));
        }
    }

    public LiliathEngima(Location spawnLocation) {
        super(
                spawnLocation,
                "Liliath Engima",
                3000,
                0,
                0,
                0,
                0
        );
        this.targets = List.of();
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
        this.targets = List.of();
    }

    @Override
    public void onSpawn(PveOption option) {
        warlordsNPC.addKnockbackModifier(warlordsNPC, "KB RES", -100, 99999);
        if (target == null) {
            return;
        }
        holo = warlordsNPC.getWorld().spawn(warlordsNPC.getLocation().clone().add(0, 3, 0), TextDisplay.class, td -> {
            td.setBillboard(Display.Billboard.CENTER);
            td.setSeeThrough(true);
            td.setBackgroundColor(Color.BLACK);
            td.text(Component.text(target.getName(), NamedTextColor.WHITE, TextDecoration.BOLD));
            td.setLineWidth(150);
            td.setTeleportDuration(0);
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

        // scale health with spec
        float newHealth =  warlordsNPC.getMaxHealth();
        switch (target.getSpecClass().specType) {
            case DAMAGE -> newHealth = warlordsNPC.getMaxHealth();
            case TANK -> newHealth = warlordsNPC.getMaxHealth() * 0.5f;
            case HEALER -> newHealth = warlordsNPC.getMaxHealth() * 0.25f;
        }

        warlordsNPC.setMaxHealthAndHeal(newHealth);
        warlordsNPC.setName(ChatColor.BOLD + target.getName());
        nextLaserTick = rand.nextInt(300, 600);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.LILIATH_ENIGMA;
    }


    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (target != null && event.getSource() != target) {
            event.setCancelled(true);
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (holo != null && !holo.isDead() && holo.getWorld() == warlordsNPC.getWorld()) {
            holo.teleport(warlordsNPC.getLocation().clone().add(0, 3, 0));
        }
        if (laserBarrageAbility == null || ticksElapsed < nextLaserTick) {
            return;
        }
        laserBarrageAbility.start(targets);
        nextLaserTick = ticksElapsed + rand.nextInt(300, 600);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        if (holo != null) {
            holo.remove();
        }
    }
}

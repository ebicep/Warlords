package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.BossAbilityPhase;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.NineCrystal;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.cos;

public class Physira extends AbstractMob implements BossMob {

    private Listener listener;
    List<WarlordsEntity> pylons = new ArrayList<>();
    private ItemDisplay blade;
    float angleDeg = 0;

    private BossAbilityPhase phaseOne;
    private BossAbilityPhase phaseTwo;
    private BossAbilityPhase phaseThree;

    public Physira(Location spawnLocation) {
        super(
                spawnLocation,
                "Physira",
                10000,
                0,
                0,
                3000,
                4000
        );
    }

    public Physira(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
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
        return Mob.PHYSIRA;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        phaseOne = new BossAbilityPhase(warlordsNPC, 75, () -> {
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("Destroy Physira's pylons before the time runs out!", NamedTextColor.GRAY),
                    20,
                    60,
                    20
            );

            Location loc = warlordsNPC.getLocation();
            Location crystalLoc = loc.clone();
            for (int j = 0; j < 6; j++) {
                double angle = j / 6D * Math.PI * 2;
                crystalLoc.setX(loc.getX() + Math.sin(angle) * 20);
                crystalLoc.setZ(loc.getZ() + cos(angle) * 20);
                NineCrystal crystal = new NineCrystal(crystalLoc, warlordsNPC, SpecType.VALUES[j % 3]);
                pylons.add(crystal.getWarlordsNPC());
                Bukkit.broadcast(Component.text("pylons: " + pylons.get(j)));
                pveOption.spawnNewMob(crystal, Team.RED);
            }

            listener = new Listener() {
                @EventHandler(ignoreCancelled = true)
                private void onAllyDeath(WarlordsDeathEvent event) {
                    WarlordsEntity we = event.getWarlordsEntity();
                    pylons.remove(we);
                    Bukkit.broadcast(Component.text("pylon removed"));
                }
            };

            warlordsNPC.getGame().registerEvents(listener);

            AtomicInteger countdown = new AtomicInteger(30);
            new GameRunnable(warlordsNPC.getGame()) {
                int counter = 0;
                @Override
                public void run() {
                    if (counter % 20 == 0) {
                        countdown.getAndDecrement();
                        Utils.playGlobalSound(warlordsNPC.getLocation(), "warrior.laststand.activation", 500, 0.6f);
                    }

                    if (pylons.isEmpty() && countdown.get() > 0) {
                        EffectUtils.playFirework(
                                warlordsNPC.getLocation(),
                                FireworkEffect.builder()
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .withColor(Color.WHITE)
                                        .withTrail()
                                        .build()
                        );

                        this.cancel();
                    }

                    if (countdown.get() <= 0) {
                        EffectUtils.strikeLightningTicks(warlordsNPC.getLocation(), true, 60);
                        EffectUtils.playFirework(
                                warlordsNPC.getLocation(),
                                FireworkEffect.builder()
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .withColor(Color.RED)
                                        .withTrail()
                                        .build()
                        );

                        for (WarlordsEntity we : PlayerFilter
                                .playingGame(warlordsNPC.getGame())
                                .aliveEnemiesOf(warlordsNPC)
                        ) {
                            we.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Valerian Death")
                                    .source(warlordsNPC)
                                    .min(700 * 100)
                                    .max(1300 * 100)
                                    .critChance(100)
                                    .critMultiplier(300)
                                    .flags(InstanceFlags.TRUE_DAMAGE)
                            );
                            EffectUtils.strikeLightning(we.getLocation(), false);
                            EffectUtils.playParticleLinkAnimation(
                                    we.getLocation(),
                                    warlordsNPC.getLocation(),
                                    Particle.CHERRY_LEAVES
                            );
                        }

                        this.cancel();
                    }

                    ChatUtils.sendTitleToGamePlayers(
                            warlordsNPC.getGame(),
                            Component.text(countdown.get(), NamedTextColor.GOLD),
                            Component.empty(),
                            0, 4, 0
                    );

                    counter++;
                }
            }.runTaskTimer(60, 0);
        });

        phaseTwo = new BossAbilityPhase(warlordsNPC, 50, () -> {

        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        float health = warlordsNPC.getCurrentHealth();
        phaseOne.initialize(health);
        phaseTwo.initialize(health);
        //phaseThree.initialize(health);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }
}

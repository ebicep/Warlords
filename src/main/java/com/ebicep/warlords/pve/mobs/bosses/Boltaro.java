package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.BoltaroExiled;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.BoltaroShadow;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.EnumSet;

public class Boltaro extends AbstractMob implements BossMob {

    private boolean split = false;
    private int mobsKilledBeforeSplit = 0;
    private Listener listener;

    public Boltaro(Location spawnLocation) {
        super(spawnLocation,
                "Boltaro",
                12500,
                0.475f,
                20,
                350,
                500
        );
    }

    public Boltaro(
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
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        option.getGame().registerEvents(listener = new Listener() {
            @EventHandler
            public void onMobDeath(WarlordsDeathEvent event) {
                if (!split && event.getWarlordsEntity() instanceof WarlordsNPC) {
                    mobsKilledBeforeSplit++;
                }
            }
        });

        new GameRunnable(option.getGame()) {
            @Override
            public void run() {
                for (int i = 0; i < option.getGame().warlordsPlayers().count(); i++) {
                    option.spawnNewMob(new BoltaroExiled(spawnLocation));
                }
            }
        }.runTaskLater(10);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 100 == 0) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 1.5f);
        }

        if (warlordsNPC.getCurrentHealth() < 6000) {
            split = true;
            split(option);
            warlordsNPC.die(warlordsNPC);
        }
    }

    private void split(PveOption option) {
        if (mobsKilledBeforeSplit == 0) {
            option.getGame()
                  .warlordsPlayers()
                  .forEach(warlordsPlayer -> ChallengeAchievements.checkForAchievement(warlordsPlayer, ChallengeAchievements.SIRE));
        }
        HandlerList.unregisterAll(listener);

        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 6, Particle.SMOKE_NORMAL, 3, 20);
        for (int i = 0; i < option.getGame().warlordsPlayers().count(); i++) {
            option.spawnNewMob(new BoltaroShadow(warlordsNPC.getLocation()));
        }

        for (int i = 0; i < 6; i++) {
            option.spawnNewMob(new BoltaroExiled(warlordsNPC.getLocation()));
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        if (!(event.getCause().equals("Multi Hit") || event.getCause().equals("Intervene"))) {
            new GameRunnable(attacker.getGame()) {
                int counter = 0;

                @Override
                public void run() {
                    counter++;
                    Utils.playGlobalSound(receiver.getLocation(), "warrior.mortalstrike.impact", 2, 1.5f);
                    Utils.addKnockback(name, attacker.getLocation(), receiver, -0.55, 0.3);
                    receiver.addDamageInstance(attacker, "Multi Hit", 120, 180, 0, 100, counter == 3 ? EnumSet.of(InstanceFlags.TRUE_DAMAGE) : EnumSet.noneOf(InstanceFlags.class));

                    if (counter == 3 || receiver.isDead()) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(10, 3);
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(self.getLocation(), "warrior.intervene.block", 2, 0.3f);
        EffectUtils.playRandomHitEffect(self.getLocation(), 255, 0, 0, 4);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 6, Particle.SMOKE_NORMAL, 3, 20);
        EffectUtils.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.WHITE)
                                                                       .with(FireworkEffect.Type.STAR)
                                                                       .withTrail()
                                                                       .build());
        if (!split) {
            split(option);
        }
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.RED;
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.BOLTARO;
    }

    @Override
    public Component getDescription() {
        return Component.text("Right Hand of the Illusion Vanguard", NamedTextColor.GOLD);
    }
}

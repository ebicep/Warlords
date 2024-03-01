package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.Unimmobilizable;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CursedPsion extends AbstractMob implements BossMinionMob, Unimmobilizable {

    private static final int STUN_TICKS = 2 * 20;

    public CursedPsion(Location spawnLocation) {
        this(
                spawnLocation,
                "Cursed Psion",
                11500,
                0.38f,
                15,
                525,
                725
        );
    }

    public CursedPsion(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                45,
                245
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.CURSED_PSION;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "KB RES",
                null,
                CursedPsion.class,
                null,
                warlordsNPC,
                CooldownTypes.INTERNAL,
                cooldownManager -> {
                },
                true
        ) {
            @Override
            public void multiplyKB(Vector currentVector) {
                currentVector.multiply(0);
            }
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 5 == 0) {
            List<Location> psionLocations = new ArrayList<>();
            option.getMobs()
                  .stream()
                  .filter(mob -> {
                      boolean isPsion = mob instanceof CursedPsion;
                      if (isPsion) {
                          psionLocations.add(mob.getWarlordsNPC().getLocation());
                      }
                      return isPsion;
                  })
                  .min(Comparator.comparingInt(o -> option.getMobsMap().get(o).getSpawnTick()))
                  .ifPresent(mob -> {
                      PlayerFilter.playingGame(warlordsNPC.getGame())
                                  .aliveEnemiesOf(warlordsNPC)
                                  .forEach(warlordsEntity -> {
                                      boolean withinAnyPsion = psionLocations.stream().anyMatch(location -> location.distanceSquared(warlordsEntity.getLocation()) < 4 * 4);
                                      if (withinAnyPsion) {
                                          if (!warlordsEntity.hasPotionEffect(PotionEffectType.DARKNESS)) {
                                              warlordsEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, -1, 0, false, false, false));
                                          }
                                      } else {
                                          warlordsEntity.removePotionEffect(PotionEffectType.DARKNESS);
                                      }
                                  });
                  });

        }
    }

    @Override
    public void onFinalAttack(WarlordsDamageHealingFinalEvent event) {
        WarlordsEntity receiver = event.getWarlordsEntity();
        boolean crit = event.isCrit();
        if (!crit) {
            warlordsNPC.addSpeedModifier(warlordsNPC, "Melee", 30, 2 * 20, "BASE");
        }
        if (receiver.hasPotionEffect(PotionEffectType.DARKNESS) && crit) {
            if (receiver instanceof WarlordsPlayer warlordsPlayer) {
                warlordsPlayer.stun();
                new GameRunnable(warlordsPlayer.getGame()) {
                    @Override
                    public void run() {
                        warlordsPlayer.unstun();
                    }
                }.runTaskLater(STUN_TICKS);
                warlordsPlayer.getEntity().showTitle(Title.title(
                        Component.empty(),
                        Component.text("STUNNED", NamedTextColor.LIGHT_PURPLE),
                        Title.Times.times(Ticks.duration(0), Ticks.duration(STUN_TICKS), Ticks.duration(0))
                ));
            } else if (receiver instanceof WarlordsNPC wNPC) {
                wNPC.setStunTicks(STUN_TICKS);
            }
        }
    }

    @Override
    public void cleanup(PveOption pveOption) {
        super.cleanup(pveOption);
        if (pveOption.getMobs().stream().anyMatch(mob -> mob.equals(this))) {
            PlayerFilter.playingGame(warlordsNPC.getGame())
                        .enemiesOf(warlordsNPC)
                        .forEach(warlordsEntity -> warlordsEntity.removePotionEffect(PotionEffectType.DARKNESS));
        }
    }

}

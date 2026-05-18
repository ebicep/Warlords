package com.ebicep.warlords.pve.mobs.witherskeleton;

import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;

public class AbyssWatcher extends AbstractMob implements ChampionMob, Listener {

    private static final int WATCH_COOLDOWN_TICKS = 15 * 20; // 15sec
    private static final int WATCH_DURATION_TICKS = 5 * 20;  // 5sec
    private static final int INITIAL_WATCH_DELAY_TICKS = 3 * 20;
    private static final int CASTS_BEFORE_PUNISH = 3;
    private static final int PUNISH_DAMAGE = 5000;
    private static final int ENERGY_DRAIN = 100;
    private static final int WATCH_RANGE = 25;

    private WarlordsEntity watchedTarget;
    private int watchedTicksLeft = 0;
    private int watchedCasts = 0;
    private int watchCooldownTicks = INITIAL_WATCH_DELAY_TICKS;

    public AbyssWatcher(Location spawnLocation) {
        super(
                spawnLocation,
                "Abyss Watcher",
                12000,
                0.01f,
                10,
                350,
                500
        );
    }

    public AbyssWatcher(
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
        return Mob.ABYSS_WATCHER;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        warlordsNPC.getGame().registerEvents(this);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 2, 0.7f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (warlordsNPC == null || warlordsNPC.isDead() || !warlordsNPC.isActive()) {
            return;
        }

        if (watchedTarget != null) {
            tickWatchedTarget();
            return;
        }

        if (watchCooldownTicks > 0) {
            watchCooldownTicks--;
            return;
        }

        applyWatched();
    }

    @EventHandler
    public void onAbilityActivate(WarlordsAbilityActivateEvent.Post event) {
        if (watchedTarget == null) {
            return;
        }
        if (event.getWarlordsEntity() != watchedTarget) {
            return;
        }
        if (!isValidWatchedTarget(watchedTarget)) {
            clearWatched();
            return;
        }

        watchedCasts++;

        watchedTarget.sendMessage(Component.text("The Abyss Watcher tightens its gaze. [" + watchedCasts + "/" + CASTS_BEFORE_PUNISH + "]", NamedTextColor.DARK_PURPLE));
        watchedTarget.playSound(watchedTarget.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, 0.6f);

        if (watchedCasts >= CASTS_BEFORE_PUNISH) {
            punishWatchedTarget();
        }
    }

    private void applyWatched() {
        List<WarlordsEntity> targets = PlayerFilter
                .playingGame(warlordsNPC.getGame())
                .aliveEnemiesOf(warlordsNPC)
                .filter(warlordsEntity -> warlordsEntity instanceof WarlordsPlayer)
                .filter(warlordsEntity -> warlordsEntity.getLocation().distanceSquared(warlordsNPC.getLocation()) <= WATCH_RANGE * WATCH_RANGE)
                .toList();

        if (targets.isEmpty()) {
            watchCooldownTicks = 20;
            return;
        }

        Collections.shuffle(targets);

        watchedTarget = targets.getFirst();
        watchedTicksLeft = WATCH_DURATION_TICKS;
        watchedCasts = 0;

        watchedTarget.sendMessage(Component.text("The Abyss Watcher is watching you. Limit your ability casts.", NamedTextColor.DARK_PURPLE));
        watchedTarget.playSound(watchedTarget.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1, 0.6f);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, 2, 0.6f);
    }

    private void tickWatchedTarget() {
        if (!isValidWatchedTarget(watchedTarget)) {
            clearWatched();
            return;
        }

        watchedTicksLeft--;

        if (watchedTicksLeft % 10 == 0) {
            playWatchedEffects();
        }

        if (watchedTicksLeft <= 0) {
            watchedTarget.sendMessage(Component.text("The Abyss Watcher's gaze fades.", NamedTextColor.GRAY));
            clearWatched();
        }
    }

    private void playWatchedEffects() {
        Location targetLocation = watchedTarget.getLocation().clone().add(0, 1.3, 0);
        watchedTarget.getWorld().spawnParticle(Particle.PORTAL, targetLocation, 18, .3, .45, .3, .025);

        if (warlordsNPC != null && warlordsNPC.isAlive()) {
            Location watcherLocation = warlordsNPC.getLocation().clone().add(0, 1.4, 0);
            warlordsNPC.getWorld().spawnParticle(Particle.SQUID_INK, watcherLocation, 6, .2, .2, .2, .01);
        }
    }

    private void punishWatchedTarget() {
        if (!isValidWatchedTarget(watchedTarget)) {
            clearWatched();
            return;
        }

        watchedTarget.addInstance(InstanceBuilder
                .damage()
                .cause("Abyss Watch")
                .source(warlordsNPC)
                .value(PUNISH_DAMAGE)
        );

        watchedTarget.subtractEnergy("Abyss Watch", ENERGY_DRAIN, true);
        watchedTarget.sendMessage(Component.text("The Abyss Watcher punishes your reckless casting.", NamedTextColor.RED));
        watchedTarget.playSound(watchedTarget.getLocation(), Sound.ENTITY_ENDERMAN_HURT, 1, 0.6f);
        Utils.playGlobalSound(watchedTarget.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 2, 0.7f);

        clearWatched();
    }

    private boolean isValidWatchedTarget(WarlordsEntity target) {
        if (target == null) {
            return false;
        }
        if (!target.isAlive() || !target.isActive()) {
            return false;
        }
        if (target.getWorld() != warlordsNPC.getWorld()) {
            return false;
        }
        return target.getLocation().distanceSquared(warlordsNPC.getLocation()) <= WATCH_RANGE * WATCH_RANGE;
    }

    private void clearWatched() {
        watchedTarget = null;
        watchedTicksLeft = 0;
        watchedCasts = 0;
        watchCooldownTicks = WATCH_COOLDOWN_TICKS;
    }

    @Override
    public void cleanup(PveOption pveOption) {
        HandlerList.unregisterAll(this);
        clearWatched();
        super.cleanup(pveOption);
    }

}

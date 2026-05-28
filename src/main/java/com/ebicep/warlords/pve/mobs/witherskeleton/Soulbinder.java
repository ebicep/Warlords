package com.ebicep.warlords.pve.mobs.witherskeleton;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.skeleton.BoundArcher;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Soulbinder extends AbstractMob implements ChampionMob {

    private static final int MINIONS_TO_SUMMON = 3;
    private static final int EXPOSED_TICKS = 10 * 20;
    private static final int REBIND_CAST_TICKS = 3 * 20;
    private static final double MINION_SPAWN_RADIUS = 4.5;

    private final List<BoundArcher> boundMinions = new ArrayList<>();

    private int exposedTicksLeft = 0;
    private int rebindCastTicksLeft = 0;
    private int protectionFeedbackTicks = 0;
    private boolean exposed = false;

    public Soulbinder(Location spawnLocation) {
        super(
                spawnLocation,
                "Soulbinder",
                12000,
                0.12f,
                15,
                250,
                400
        );
    }

    public Soulbinder(
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
        return Mob.SOULBINDER;
    }

    @Override
    public double getMobScale() {
        return 1.1;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        summonBoundArchers(option);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 2, .6f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

        if (warlordsNPC == null || warlordsNPC.isDead() || !warlordsNPC.isActive()) {
            return;
        }

        if (ticksElapsed % 20 == 0) {
            EffectUtils.playCrownAnimation(warlordsNPC.getLocation(), Particle.SOUL_FIRE_FLAME);
        }
        if (protectionFeedbackTicks > 0) {
            protectionFeedbackTicks--;
        }

        pruneBoundMinions(option);

        if (hasActiveBoundMinions(option)) {
            exposed = false;
            exposedTicksLeft = 0;
            rebindCastTicksLeft = 0;

            if (ticksElapsed % 10 == 0) {
                playSoulLinks(option);
            }

            return;
        }

        if (!exposed) {
            expose(option);
            return;
        }

        if (exposedTicksLeft > 0) {
            exposedTicksLeft--;

            if (exposedTicksLeft % 10 == 0) {
                playExposedEffects();
            }

            return;
        }

        if (rebindCastTicksLeft <= 0) {
            startRebinding(option);
            return;
        }

        rebindCastTicksLeft--;

        if (rebindCastTicksLeft % 10 == 0) {
            playRebindEffects();
        }

        if (rebindCastTicksLeft <= 0) {
            summonBoundArchers(option);
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (self != warlordsNPC) {
            return;
        }
        if (!event.isDamageInstance()) {
            return;
        }
        if (!hasActiveBoundMinions(pveOption)) {
            return;
        }

        event.setCancelled(true);

        if (protectionFeedbackTicks > 0) {
            return;
        }

        protectionFeedbackTicks = 20;

        if (attacker != null) {
            attacker.sendMessage(Component.text("The Soulbinder is protected by its bound archers.", NamedTextColor.DARK_PURPLE));
            attacker.playSound(attacker.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, .5f);
        }

        warlordsNPC.getWorld().spawnParticle(
                Particle.REVERSE_PORTAL,
                warlordsNPC.getLocation().clone().add(0, 1.2, 0),
                24,
                .45,
                .6,
                .45,
                .03
        );
    }

    private void expose(PveOption option) {
        exposed = true;
        exposedTicksLeft = EXPOSED_TICKS;
        rebindCastTicksLeft = 0;

        option.getGame().forEachOnlinePlayer((player, team) -> player.sendMessage(Component.text("The Soulbinder's pact is broken. Strike now.", NamedTextColor.GREEN)));

        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, .6f);
        playExposedEffects();
    }

    private void startRebinding(PveOption option) {
        rebindCastTicksLeft = REBIND_CAST_TICKS;

        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 2, .5f);
        playRebindEffects();
    }

    private void summonBoundArchers(PveOption option) {
        boundMinions.clear();
        exposed = false;
        exposedTicksLeft = 0;
        rebindCastTicksLeft = 0;

        for (int i = 0; i < MINIONS_TO_SUMMON; i++) {
            double angle = Math.PI * 2 * i / MINIONS_TO_SUMMON;
            Location spawnLocation = warlordsNPC.getLocation().clone().add(
                    Math.cos(angle) * MINION_SPAWN_RADIUS,
                    0,
                    Math.sin(angle) * MINION_SPAWN_RADIUS
            );

            BoundArcher boundArcher = new BoundArcher(spawnLocation, this);
            option.spawnNewMob(boundArcher);
            boundMinions.add(boundArcher);

            spawnLocation.getWorld().spawnParticle(
                    Particle.SOUL_FIRE_FLAME,
                    spawnLocation.clone().add(0, 1, 0),
                    24,
                    .35,
                    .45,
                    .35,
                    .02
            );
        }

        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_WITHER_SPAWN, 2, .65f);
    }

    public void onBoundArcherRemoved(BoundArcher boundArcher) {
        boundMinions.remove(boundArcher);
    }

    private boolean hasActiveBoundMinions(PveOption option) {
        if (option == null) {
            return false;
        }

        pruneBoundMinions(option);
        return !boundMinions.isEmpty();
    }

    private void pruneBoundMinions(PveOption option) {
        boundMinions.removeIf(boundArcher -> {
            if (boundArcher == null) {
                return true;
            }
            if (!option.getMobs().contains(boundArcher)) {
                return true;
            }
            if (boundArcher.getWarlordsNPC() == null) {
                return true;
            }
            return !boundArcher.getWarlordsNPC().isAlive() || !boundArcher.getWarlordsNPC().isActive();
        });
    }

    private void playSoulLinks(PveOption option) {
        for (BoundArcher boundArcher : new ArrayList<>(boundMinions)) {
            if (boundArcher.getWarlordsNPC() == null || !option.getMobs().contains(boundArcher)) {
                continue;
            }

            drawParticleLine(
                    warlordsNPC.getLocation().clone().add(0, 1.4, 0),
                    boundArcher.getWarlordsNPC().getLocation().clone().add(0, 1.1, 0),
                    Particle.REVERSE_PORTAL
            );
        }
    }

    private void playExposedEffects() {
        Location location = warlordsNPC.getLocation().clone().add(0, 1.2, 0);
        warlordsNPC.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, location, 36, .75, .7, .75, .04);
    }

    private void playRebindEffects() {
        Location center = warlordsNPC.getLocation().clone();

        for (int i = 0; i < 48; i++) {
            double angle = Math.PI * 2 * i / 48;
            double x = Math.cos(angle) * MINION_SPAWN_RADIUS;
            double z = Math.sin(angle) * MINION_SPAWN_RADIUS;
            center.getWorld().spawnParticle(Particle.REVERSE_PORTAL, center.clone().add(x, .15, z), 1, 0, 0, 0, 0);
        }

        warlordsNPC.getWorld().spawnParticle(
                Particle.SQUID_INK,
                center.clone().add(0, 1.2, 0),
                16,
                .45,
                .45,
                .45,
                .02
        );
    }

    private void drawParticleLine(Location from, Location to, Particle particle) {
        Vector direction = to.toVector().subtract(from.toVector());
        double length = direction.length();

        if (length == 0) {
            return;
        }

        direction.normalize();

        for (double distance = 0; distance < length; distance += .35) {
            from.getWorld().spawnParticle(particle, from.clone().add(direction.clone().multiply(distance)), 1, 0, 0, 0, 0);
        }
    }

    private void despawnBoundMinions(PveOption option) {
        for (BoundArcher boundArcher : new ArrayList<>(boundMinions)) {
            if (boundArcher == null || !option.getMobs().contains(boundArcher)) {
                continue;
            }
            option.despawnMob(boundArcher);
        }

        boundMinions.clear();
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        despawnBoundMinions(option);
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_WITHER_DEATH, 2, .7f);
    }

    @Override
    public void cleanup(PveOption pveOption) {
        boundMinions.clear();
        super.cleanup(pveOption);
    }

}
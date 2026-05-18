package com.ebicep.warlords.pve.mobs.witherskeleton;

import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.MobHologram;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.NoTarget;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Optional;

public class DevouringIdol extends AbstractMob implements ChampionMob, Listener {

    private static final int DEVOUR_RADIUS = 12;
    private static final int PULSE_RADIUS = 12;
    private static final int MAX_ENERGY = 100;
    private static final int ENERGY_GAIN_PER_CAST = 20;
    private static final int PULSE_DAMAGE = 4000;
    private static final int PULSE_COOLDOWN_TICKS = 5 * 20;
    private static final int IDOL_SHIELD_DURATION_TICKS = 6 * 20;
    private static final float IDOL_SHIELD_DAMAGE_REDUCTION = 50;

    private int energy = 0;
    private int pulseCooldownTicks = 0;

    public DevouringIdol(Location spawnLocation) {
        super(
                spawnLocation,
                "Devouring Idol",
                10000,
                0,
                20,
                0,
                0
        );
    }

    public DevouringIdol(
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
        return Mob.DEVOURING_IDOL;
    }

    @Override
    public void giveGoals() {
    }

    @Override
    public double getDefaultAttackRange() {
        return 0;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        option.getGame().registerEvents(this);

        warlordsNPC.getMobHologram().getCustomHologramLines().add(new MobHologram.CustomHologramLine(
                () -> Component.text("Energy: " + energy + "%", energy >= MAX_ENERGY ? NamedTextColor.RED : NamedTextColor.DARK_PURPLE)
        ));

        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 2, .45f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (warlordsNPC == null || warlordsNPC.isDead() || !warlordsNPC.isActive()) {
            return;
        }

        if (pulseCooldownTicks > 0) {
            pulseCooldownTicks--;
        }

        if (ticksElapsed % 10 == 0) {
            playIdleEffects();
        }

        if (energy >= MAX_ENERGY && pulseCooldownTicks <= 0) {
            triggerPulse();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAbilityActivate(WarlordsAbilityActivateEvent.Post event) {
        if (warlordsNPC == null || warlordsNPC.isDead() || !warlordsNPC.isActive()) {
            return;
        }

        WarlordsEntity caster = event.getWarlordsEntity();

        if (!(caster instanceof WarlordsPlayer)) {
            return;
        }
        if (!warlordsNPC.isEnemyAlive(caster)) {
            return;
        }
        if (caster.getWorld() != warlordsNPC.getWorld()) {
            return;
        }
        if (caster.getLocation().distanceSquared(warlordsNPC.getLocation()) > DEVOUR_RADIUS * DEVOUR_RADIUS) {
            return;
        }

        gainEnergy(caster);
    }

    private void gainEnergy(WarlordsEntity caster) {
        if (energy >= MAX_ENERGY) {
            return;
        }

        energy = Math.min(MAX_ENERGY, energy + ENERGY_GAIN_PER_CAST);

        caster.playSound(caster.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, .7f);

        Location location = warlordsNPC.getLocation().clone().add(0, 1.2, 0);
        warlordsNPC.getWorld().spawnParticle(
                Particle.REVERSE_PORTAL,
                location,
                16,
                .45,
                .45,
                .45,
                .03
        );

        if (energy >= MAX_ENERGY) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 2, .5f);
        }
    }

    private void triggerPulse() {
        energy = 0;
        pulseCooldownTicks = PULSE_COOLDOWN_TICKS;

        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 2, .5f);

        PlayerFilter.entitiesAround(warlordsNPC, PULSE_RADIUS, PULSE_RADIUS, PULSE_RADIUS)
                .aliveEnemiesOf(warlordsNPC)
                .forEach(target -> {
                    target.addInstance(InstanceBuilder
                            .damage()
                            .cause("Idol Pulse")
                            .source(warlordsNPC)
                            .value(PULSE_DAMAGE)
                    );

                    target.sendMessage(Component.text("The Devouring Idol releases stored energy.", NamedTextColor.RED));
                    target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_HURT, 1, .6f);
                });

        PlayerFilter.entitiesAround(warlordsNPC, PULSE_RADIUS, PULSE_RADIUS, PULSE_RADIUS)
                .aliveTeammatesOf(warlordsNPC)
                .filter(target -> target instanceof WarlordsNPC)
                .filter(target -> target != warlordsNPC)
                .forEach(this::applyIdolShield);

        playPulseEffects();
    }

    private void applyIdolShield(WarlordsEntity target) {
        Optional<RegularCooldown> existingShield = new CooldownFilter<>(target, RegularCooldown.class)
                .filterCooldownClass(IdolShieldData.class)
                .filterCooldownFrom(warlordsNPC)
                .filterName("Idol Shield")
                .filter(RegularCooldown::hasTicksLeft)
                .findFirst();

        if (existingShield.isPresent()) {
            existingShield.get().setTicksLeft(IDOL_SHIELD_DURATION_TICKS);
            return;
        }

        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Idol Shield",
                "IS",
                IdolShieldData.class,
                new IdolShieldData(),
                warlordsNPC,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                IDOL_SHIELD_DURATION_TICKS
        ).addModifier(Modifier.INCOMING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
            currentDamageValue.addModifier(
                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                    "Idol Shield",
                    1 - IDOL_SHIELD_DAMAGE_REDUCTION / 100f
            );
        }));

        target.getWorld().spawnParticle(
                Particle.SOUL_FIRE_FLAME,
                target.getLocation().clone().add(0, 1, 0),
                18,
                .35,
                .45,
                .35,
                .02
        );
    }

    private void playIdleEffects() {
        Location center = warlordsNPC.getLocation().clone();

        for (int i = 0; i < 24; i++) {
            double angle = Math.PI * 2 * i / 24;
            double x = Math.cos(angle) * DEVOUR_RADIUS;
            double z = Math.sin(angle) * DEVOUR_RADIUS;

            center.getWorld().spawnParticle(
                    Particle.PORTAL,
                    center.clone().add(x, .15, z),
                    1,
                    0,
                    0,
                    0,
                    0
            );
        }

        if (energy <= 0) {
            return;
        }

        warlordsNPC.getWorld().spawnParticle(
                Particle.REVERSE_PORTAL,
                center.clone().add(0, 1.2, 0),
                Math.max(4, energy / 5),
                .35,
                .45,
                .35,
                .02
        );
    }

    private void playPulseEffects() {
        Location center = warlordsNPC.getLocation().clone();

        for (int radius = 2; radius <= PULSE_RADIUS; radius += 2) {
            for (int i = 0; i < 36; i++) {
                double angle = Math.PI * 2 * i / 36;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;

                center.getWorld().spawnParticle(
                        Particle.SQUID_INK,
                        center.clone().add(x, .25, z),
                        1,
                        0,
                        0,
                        0,
                        0
                );
            }
        }

        warlordsNPC.getWorld().spawnParticle(
                Particle.SOUL_FIRE_FLAME,
                center.clone().add(0, 1.2, 0),
                48,
                .8,
                .8,
                .8,
                .04
        );
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 2, .35f);
    }

    @Override
    public void cleanup(PveOption pveOption) {
        HandlerList.unregisterAll(this);
        super.cleanup(pveOption);
    }

    private static class IdolShieldData {
    }

}

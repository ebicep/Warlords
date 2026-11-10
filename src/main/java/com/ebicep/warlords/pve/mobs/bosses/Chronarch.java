package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.abilities.SpawnMobAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

public class Chronarch extends AbstractMob implements BossMob {

    private static final Particle.DustOptions BRASS_DUST = new Particle.DustOptions(Color.fromRGB(212, 175, 55), 1.35f);
    private static final Particle.DustOptions GOLD_DUST = new Particle.DustOptions(Color.fromRGB(255, 220, 90), 1.1f);
    private static final double CLOCK_HAND_RANGE = 12;
    private static final double CLOCK_HAND_WIDTH = 1.65;

    private boolean overclockTriggered = false;
    private boolean twelveChimesTriggered = false;
    private boolean chiming = false;

    public Chronarch(Location spawnLocation) {
        super(
                spawnLocation,
                "Chronarch",
                80000,
                0.30f,
                20,
                900,
                1200,
                createAbilities()
        );
    }

    public Chronarch(
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
                maxMeleeDamage,
                createAbilities()
        );
    }

    private static AbstractAbility[] createAbilities() {
        return new AbstractAbility[]{
                new Pendulum(),
                new Cogburst(),
                new SpawnMobAbility(AbstractAbilityBuilder.create("chronarchSpawnClockboundPhantom")
                        .pve()
                        .name("Clockbound Phantom")
                        .cooldown(22)
                        .energyCost(0), Mob.CLOCKBOUND_PHANTOM) {
                    @Override
                    public int getSpawnAmount() {
                        return Math.max(1, (int) pveOption.getGame().warlordsPlayers().count());
                    }
                }
        };
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.CHRONARCH;
    }

    @Override
    public Component getDescription() {
        return Component.text("Clockwork Sovereign of the Illusion Dynasty", NamedTextColor.GOLD);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.GOLD;
    }

    @Override
    public double getMobScale() {
        return 1.35;
    }

    @Override
    public double getDefaultAttackRange() {
        return 2.2;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        PermanentCooldown<Chronarch> frame = new PermanentCooldown<>(
                "Clockwork Frame",
                null,
                Chronarch.class,
                this,
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                true
        );
        warlordsNPC.addKnockbackModifier(warlordsNPC, "Clockwork Frame", -60, frame);
        warlordsNPC.getCooldownManager().addCooldown(frame);

        ChatUtils.sendTitleToGamePlayers(
                option.getGame(),
                Component.text("CHRONARCH", NamedTextColor.GOLD),
                Component.text("The clockwork sovereign awakens.", NamedTextColor.YELLOW),
                20,
                40,
                20
        );

        new GameRunnable(option.getGame()) {
            @Override
            public void run() {
                int count = Math.max(1, (int) option.getGame().warlordsPlayers().count());
                for (int i = 0; i < count; i++) {
                    option.spawnNewMob(Mob.CLOCKBOUND_PHANTOM.createMob(warlordsNPC.getLocation()));
                }
            }
        }.runTaskLater(10);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        Location loc = warlordsNPC.getLocation();
        if (ticksElapsed % 20 == 0) {
            EffectUtils.playCylinderAnimation(loc, 6, 212, 175, 55, 12, 2);
            Utils.playGlobalSound(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.6f, 0.5f);
        }

        if (!chiming && ticksElapsed % 40 == 0) {
            PlayerFilter.entitiesAround(warlordsNPC, 3.5, 3.5, 3.5)
                    .aliveEnemiesOf(warlordsNPC)
                    .forEach(enemy -> enemy.addInstance(InstanceBuilder
                            .damage()
                            .cause("Mainspring")
                            .source(warlordsNPC)
                            .min(400)
                            .max(600)
                    ));
        }

        float healthPercent = warlordsNPC.getCurrentHealth() / warlordsNPC.getMaxHealth();
        if (healthPercent < 0.65f && !overclockTriggered) {
            overclockTriggered = true;
            overclock(option);
        }
        if (healthPercent < 0.30f && !twelveChimesTriggered) {
            twelveChimesTriggered = true;
            twelveChimes(option);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        EffectUtils.playRandomHitEffect(receiver.getLocation(), 212, 175, 55, 4);
        receiver.addSpeedModifier(attacker, "Clockwork Wound", -15, 2 * 20);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        EffectUtils.playRandomHitEffect(self.getLocation(), 255, 215, 80, 3);
        Utils.playGlobalSound(self.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.4f, 1.6f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        EffectUtils.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.YELLOW)
                .withColor(Color.ORANGE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_IRON_GOLEM_DEATH, 2, 0.5f);
        Utils.playGlobalSound(deathLocation, Sound.BLOCK_BELL_RESONATE, 2, 0.7f);
    }

    private void overclock(PveOption option) {
        ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                Component.text("OVERCLOCK", NamedTextColor.GOLD),
                Component.text("Chronarch's gears spin faster!", NamedTextColor.YELLOW),
                10,
                40,
                10
        );
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 2, 0.4f);
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 8, 255, 200, 40);
        warlordsNPC.addSpeedModifier(warlordsNPC, "Overclock", 40, 30 * 20);

        int count = Math.max(1, (int) option.getGame().warlordsPlayers().count());
        for (int i = 0; i < count; i++) {
            option.spawnNewMob(Mob.CHRONO_WARDEN.createMob(warlordsNPC.getLocation()));
        }
    }

    private void twelveChimes(PveOption option) {
        chiming = true;
        warlordsNPC.addSpeedModifier(warlordsNPC, "Twelve Chimes", -99, 8 * 20);
        ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                Component.text("TWELVE CHIMES", NamedTextColor.GOLD),
                Component.text("Step away from Chronarch before the bells finish!", NamedTextColor.RED),
                10,
                40,
                10
        );

        new GameRunnable(warlordsNPC.getGame()) {
            int chime = 0;

            @Override
            public void run() {
                if (warlordsNPC.isDead()) {
                    chiming = false;
                    cancel();
                    return;
                }

                chime++;
                Location loc = warlordsNPC.getLocation();
                double radius = 7 + (chime * 3);
                float pitch = 0.5f + chime * 0.12f;
                Utils.playGlobalSound(loc, Sound.BLOCK_BELL_USE, 2, pitch);
                EffectUtils.playCylinderAnimation(loc, radius, 0, 0, 0, 18, 3);
                EffectUtils.drawRing(loc, radius, 1, Particle.SOUL_FIRE_FLAME);

                PlayerFilter.entitiesAround(warlordsNPC, radius, radius, radius)
                        .aliveEnemiesOf(warlordsNPC)
                        .forEach(enemy -> {
                            EffectUtils.playParticleLinkAnimation(enemy.getLocation(), loc, 255, 200, 40, 2);
                            enemy.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Twelve Chimes")
                                    .source(warlordsNPC)
                                    .min(800)
                                    .max(1000)
                            );
                        });

                ChatUtils.sendTitleToGamePlayers(
                        warlordsNPC.getGame(),
                        Component.text(chime, NamedTextColor.GOLD),
                        Component.text("Stay out of the ringing circle!", NamedTextColor.YELLOW),
                        0,
                        18,
                        0
                );

                if (chime >= 6) {
                    chiming = false;
                    warlordsNPC.addSpeedModifier(warlordsNPC, "Wound Spring", 15, Integer.MAX_VALUE);
                    Utils.playGlobalSound(warlordsNPC.getLocation(), "raid.church.dingalt", 2, 0.5f);
                    int count = Math.max(1, (int) option.getGame().warlordsPlayers().count());
                    for (int i = 0; i < count; i++) {
                        option.spawnNewMob(Mob.CLOCKBOUND_PHANTOM.createMob(loc));
                    }
                    PlayerFilter.entitiesAround(warlordsNPC, radius, radius, radius)
                            .aliveEnemiesOf(warlordsNPC)
                            .forEach(enemy -> {
                                EffectUtils.playParticleLinkAnimation(enemy.getLocation(), loc, 255, 200, 40, 2);
                                enemy.addInstance(InstanceBuilder
                                        .damage()
                                        .cause("Twelve Chimes")
                                        .source(warlordsNPC)
                                        .min(7000)
                                        .max(9000)
                                );
                            });
                    ChatUtils.sendTitleToGamePlayers(
                            warlordsNPC.getGame(),
                            Component.empty(),
                            Component.text("The mainspring snaps taut!", NamedTextColor.RED),
                            10,
                            30,
                            10
                    );
                    cancel();
                }
            }
        }.runTaskTimer(20, 20);
    }

    public static class Pendulum extends AbstractPveAbility {

        public Pendulum() {
            super(AbstractAbilityBuilder.create("chronarchPendulum")
                    .pve()
                    .name("Pendulum")
                    .cooldown(8)
                    .energyCost(50));
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            Location loc = wp.getLocation();
            Utils.playGlobalSound(loc, Sound.ENTITY_IRON_GOLEM_ATTACK, 2, 0.5f);
            Utils.playGlobalSound(loc, Sound.BLOCK_BELL_USE, 1.5f, 0.6f);
            FallingBlockWaveEffect.create(loc.clone().add(0, 1, 0), 8, 5, Material.GOLD_BLOCK);
            EffectUtils.playHelixAnimation(loc, 8, 212, 175, 55);

            PlayerFilter.entitiesAround(wp, 8, 8, 8)
                    .aliveEnemiesOf(wp)
                    .forEach(enemy -> {
                        Utils.addKnockback(name, loc, enemy, -1.15, 0.32);
                        enemy.addSpeedModifier(wp, "Pendulum", -20, 2 * 20);
                        enemy.addInstance(InstanceBuilder
                                .damage()
                                .ability(this)
                                .source(wp)
                                .min(850)
                                .max(1050)
                        );
                    });
            return true;
        }
    }

    public static class Cogburst extends AbstractPveAbility {

        public Cogburst() {
            super(AbstractAbilityBuilder.create("chronarchCogburst")
                    .pve()
                    .name("Cogburst")
                    .cooldown(12)
                    .energyCost(50));
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            double hourAngle = ThreadLocalRandom.current().nextDouble(Math.PI * 2);
            double minuteAngle = hourAngle + Math.PI / 2;

            Utils.playGlobalSound(wp.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2, 0.4f);

            new GameRunnable(wp.getGame()) {
                int ticks = 0;

                @Override
                public void run() {
                    if (wp.isDead()) {
                        cancel();
                        return;
                    }

                    Location loc = wp.getLocation();
                    drawClockHand(loc, hourAngle, CLOCK_HAND_RANGE, BRASS_DUST);
                    drawClockHand(loc, minuteAngle, CLOCK_HAND_RANGE * 0.75, GOLD_DUST);
                    ticks++;

                    if (ticks < 30) {
                        return;
                    }

                    Utils.playGlobalSound(loc, Sound.ENTITY_IRON_GOLEM_ATTACK, 2, 0.35f);
                    EffectUtils.playHelixAnimation(loc, CLOCK_HAND_RANGE, 255, 200, 40);
                    PlayerFilter.entitiesAround(wp, CLOCK_HAND_RANGE, 6, CLOCK_HAND_RANGE)
                            .aliveEnemiesOf(wp)
                            .forEach(enemy -> {
                                Location enemyLoc = enemy.getLocation();
                                if (!isNearClockHand(loc, enemyLoc, hourAngle, CLOCK_HAND_RANGE, CLOCK_HAND_WIDTH)
                                        && !isNearClockHand(loc, enemyLoc, minuteAngle, CLOCK_HAND_RANGE * 0.75, CLOCK_HAND_WIDTH)) {
                                    return;
                                }
                                Utils.addKnockback(name, loc, enemy, -0.9, 0.25);
                                enemy.addInstance(InstanceBuilder
                                        .damage()
                                        .ability(Cogburst.this)
                                        .source(wp)
                                        .min(850)
                                        .max(1100)
                                );
                            });
                    cancel();
                }
            }.runTaskTimer(0, 1);
            return true;
        }
    }

    private static void drawClockHand(Location center, double angle, double range, Particle.DustOptions dust) {
        Location point = center.clone().add(0, 0.25, 0);
        for (double distance = 1; distance <= range; distance += 0.55) {
            point.setX(center.getX() + Math.cos(angle) * distance);
            point.setZ(center.getZ() + Math.sin(angle) * distance);
            EffectUtils.displayParticle(Particle.DUST, point, 1, dust);
        }
    }

    private static boolean isNearClockHand(Location center, Location target, double angle, double range, double width) {
        double dx = target.getX() - center.getX();
        double dz = target.getZ() - center.getZ();
        double hx = Math.cos(angle);
        double hz = Math.sin(angle);
        double projection = dx * hx + dz * hz;
        if (projection < 0 || projection > range) {
            return false;
        }
        double px = dx - hx * projection;
        double pz = dz - hz * projection;
        return px * px + pz * pz <= width * width;
    }
}

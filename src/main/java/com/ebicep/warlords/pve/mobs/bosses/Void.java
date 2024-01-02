package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.PrismGuard;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.abilities.SpawnSouls;
import com.ebicep.warlords.pve.mobs.abilities.ThunderCloudAbility;
import com.ebicep.warlords.pve.mobs.irongolem.GolemApprentice;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

public class Void extends AbstractMob implements BossMob {

    AtomicInteger damageToDeal = new AtomicInteger(0);
    private final int stormRadius = 10;
    private boolean flamePhaseTrigger = false;
    private boolean flamePhaseTriggerTwo = false;
    private boolean timedDamageTrigger = false;
    private boolean timedDamageTriggerTwo = false;
    private boolean preventArmageddon = false;
    private boolean boltaroPhaseTrigger = false;
    private PrismGuard prismGuard = new PrismGuard() {{
        setTickDuration(200);
    }};

    public Void(Location spawnLocation) {
        this(spawnLocation, "Void", 100000, 0.24f, 20, 3000, 4000);
    }

    public Void(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
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
                new GroundShred(),
                new SpawnSouls(20) {
                    @Override
                    public int getSpawnAmount() {
                        int spawnAmount = 2 * pveOption.playerCount();
                        DifficultyIndex difficulty = pveOption.getDifficulty();
                        if (difficulty == DifficultyIndex.EXTREME) {
                            spawnAmount--;
                        }
                        return spawnAmount;
                    }
                },
                new ThunderCloudAbility(
                        10,
                        false,
                        21, 30,
                        7, 12
                )
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.VOID;
    }

    @Override
    public Component getDescription() {
        return Component.text("?????", NamedTextColor.BLACK);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.DARK_GRAY;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        if (option.getDifficulty() == DifficultyIndex.EXTREME) {
            float newHealth = 55000;
            warlordsNPC.setMaxHealthAndHeal(newHealth);
        }

        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Damage Check",
                null,
                DamageCheck.class,
                DamageCheck.DAMAGE_CHECK,
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                true
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                damageToDeal.set((int) (damageToDeal.get() - currentDamageValue));
                return currentDamageValue;
            }

            @Override
            public void multiplyKB(Vector currentVector) {
                // immune to KB
                currentVector.multiply(0.05);
            }
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        Location loc = warlordsNPC.getLocation();
        long playerCount = option.getGame().warlordsPlayers().count();
        if (warlordsNPC.getCurrentHealth() < (warlordsNPC.getMaxHealth() * 0.8f) && !flamePhaseTrigger) {
            flamePhaseTrigger = true;
            preventArmageddon = true;
            immolation(option, loc);
        }

        long spawnAmount = 2 * playerCount;
        DifficultyIndex difficulty = option.getDifficulty();
        if (difficulty == DifficultyIndex.EXTREME) {
            spawnAmount--;
        }
        if (warlordsNPC.getCurrentHealth() < (warlordsNPC.getMaxHealth() * .5f) && !timedDamageTrigger) {
            timedDamageTrigger = true;
            preventArmageddon = true;
            timedDamage(option, playerCount, difficulty == DifficultyIndex.EXTREME ? 13000 : 15000, 11);
            for (int i = 0; i < spawnAmount; i++) {
                option.spawnNewMob(new GolemApprentice(loc));
            }
        }

        if (warlordsNPC.getCurrentHealth() < (warlordsNPC.getMaxHealth() * 0.35f) && !flamePhaseTriggerTwo) {
            flamePhaseTriggerTwo = true;
            preventArmageddon = true;
            immolation(option, loc);
        }

        if (warlordsNPC.getCurrentHealth() < (warlordsNPC.getMaxHealth() * .25f) && !timedDamageTriggerTwo) {
            timedDamageTriggerTwo = true;
            preventArmageddon = true;
            timedDamage(option, playerCount, difficulty == DifficultyIndex.EXTREME ? 21000 : 25000, 16);
            for (int i = 0; i < spawnAmount; i++) {
                option.spawnNewMob(new GolemApprentice(loc));
            }
        }

        if (warlordsNPC.getCurrentHealth() < (warlordsNPC.getMaxHealth() * 0.1f) && !boltaroPhaseTrigger) {
            boltaroPhaseTrigger = true;
            for (int i = 0; i < playerCount; i++) {
                option.spawnNewMob(new Boltaro(loc));
            }
        }

        if (ticksElapsed % 320 == 0 && !preventArmageddon) {
            Utils.playGlobalSound(loc, "rogue.healingremedy.impact", 500, 0.6f);
            Utils.playGlobalSound(loc, "rogue.healingremedy.impact", 500, 0.6f);
            warlordsNPC.addSpeedModifier(warlordsNPC, "Armageddon Slowness", -99, 90);
            new GameRunnable(warlordsNPC.getGame()) {
                @Override
                public void run() {
                    if (warlordsNPC.isDead() || preventArmageddon) {
                        this.cancel();
                        return;
                    }

                    EffectUtils.strikeLightningInCylinder(loc, stormRadius, false, 10, warlordsNPC.getGame());
                    shockwave(loc, stormRadius, 10, playerCount);
                    EffectUtils.strikeLightningInCylinder(loc, stormRadius + 5, false, 20, warlordsNPC.getGame());
                    shockwave(loc, stormRadius + 5, 20, playerCount);
                    EffectUtils.strikeLightningInCylinder(loc, stormRadius + 10, false, 30, warlordsNPC.getGame());
                    shockwave(loc, stormRadius + 10, 30, playerCount);
                    EffectUtils.strikeLightningInCylinder(loc, stormRadius + 20, false, 40, warlordsNPC.getGame());
                    shockwave(loc, stormRadius + 20, 40, playerCount);
                    EffectUtils.strikeLightningInCylinder(loc, stormRadius + 25, false, 50, warlordsNPC.getGame());
                    shockwave(loc, stormRadius + 25, 50, playerCount);
                }
            }.runTaskLater(60);
        }
    }

    @Override
    public void onAttack(WarlordsEntity mob, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        for (int i = 0; i < 3; i++) {
            EffectUtils.strikeLightningInCylinder(deathLocation, 8, false);
        }
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.BLACK)
                                                                       .with(FireworkEffect.Type.BALL_LARGE)
                                                                       .withTrail()
                                                                       .build());
    }

    private void immolation(PveOption option, Location loc) {
        warlordsNPC.addSpeedModifier(warlordsNPC, "Armageddon Slowness", -99, 250);
        for (int i = 0; i < 3; i++) {
            Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 500, 0.6f);
        }

        ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                Component.text("PREPARE TO DIE", NamedTextColor.RED),
                Component.text("Augmented Immolation Spell", NamedTextColor.LIGHT_PURPLE),
                20,
                60,
                20
        );

        float damage = switch (option.getDifficulty()) {
            case EASY -> 100;
            case NORMAL -> 200;
            default -> 300;
        };
        new GameRunnable(warlordsNPC.getGame()) {
            int counter = 0;

            @Override
            public void run() {
                if (warlordsNPC.isDead()) {
                    this.cancel();
                    return;
                }

                counter++;
                double radius = (4 * counter);
                Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 500, 0.1f);
                Utils.playGlobalSound(loc, "warrior.laststand.activation", 500, 0.2f);
                EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), radius, Particle.SMOKE_LARGE, 1, counter);
                for (WarlordsEntity flameTarget : PlayerFilter
                        .entitiesAround(warlordsNPC, radius, radius, radius)
                        .aliveEnemiesOf(warlordsNPC)
                ) {
                    flameTarget.addDamageInstance(
                            warlordsNPC,
                            "Augmented Immolation",
                            damage,
                            damage,
                            0,
                            100
                    );

                    warlordsNPC.addHealingInstance(
                            warlordsNPC,
                            "Augmented Immolation",
                            damage * 0.5f,
                            damage * 0.5f,
                            0,
                            100
                    );
                }

                if (counter == 60) {
                    this.cancel();
                    warlordsNPC.getSpeed().addBaseModifier(40);
                    preventArmageddon = false;
                }
            }
        }.runTaskTimer(40, 4);
    }

    private void timedDamage(PveOption option, long playerCount, int damageValue, int timeToDealDamage) {
        damageToDeal.set((int) (damageValue * playerCount));

        ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                Component.empty(),
                Component.text("Keep attacking Void to stop the draining!", NamedTextColor.RED),
                10,
                35,
                0
        );
        for (WarlordsEntity we : PlayerFilter
                .playingGame(warlordsNPC.getGame())
                .aliveEnemiesOf(warlordsNPC)
        ) {
            Utils.addKnockback(name, warlordsNPC.getLocation(), we, -4, 0.3);
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_WITHER_SPAWN, 500, 0.3f);
        }

        AtomicInteger countdown = new AtomicInteger(timeToDealDamage);
        new GameRunnable(warlordsNPC.getGame()) {
            int counter = 0;

            @Override
            public void run() {
                if (warlordsNPC.isDead()) {
                    this.cancel();
                    return;
                }

                if (damageToDeal.get() <= 0) {
                    EffectUtils.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                                                                                      .withColor(Color.WHITE)
                                                                                      .with(FireworkEffect.Type.BALL_LARGE)
                                                                                      .build());
                    prismGuard.onActivate(warlordsNPC);
                    preventArmageddon = false;
                    this.cancel();
                    return;
                }

                if (counter++ % 20 == 0) {
                    countdown.getAndDecrement();
                    Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 500, 0.4f);
                    Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 500, 0.4f);
                    for (WarlordsEntity we : PlayerFilter
                            .entitiesAround(warlordsNPC, 100, 100, 100)
                            .aliveEnemiesOf(warlordsNPC)
                    ) {
                        EffectUtils.playParticleLinkAnimation(
                                we.getLocation(),
                                warlordsNPC.getLocation(),
                                255,
                                255,
                                255,
                                2
                        );

                        we.addDamageInstance(
                                warlordsNPC,
                                "Vampiric Leash",
                                700,
                                700,
                                -1,
                                100
                        );
                    }
                }

                if (countdown.get() <= 0 && damageToDeal.get() > 0) {
                    for (int i = 0; i < (2 * option.getGame().warlordsPlayers().count()); i++) {
                        option.spawnNewMob(new GolemApprentice(spawnLocation));
                    }

                    FireWorkEffectPlayer.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                                                                                               .withColor(Color.WHITE)
                                                                                               .with(FireworkEffect.Type.BALL_LARGE)
                                                                                               .build());
                    EffectUtils.strikeLightning(warlordsNPC.getLocation(), false, 10);
                    Utils.playGlobalSound(warlordsNPC.getLocation(), "shaman.earthlivingweapon.impact", 500, 0.5f);

                    for (WarlordsEntity we : PlayerFilter
                            .entitiesAround(warlordsNPC, 100, 100, 100)
                            .aliveEnemiesOf(warlordsNPC)
                    ) {
                        Utils.addKnockback(name, warlordsNPC.getLocation(), we, -2, 0.4);
                        EffectUtils.playParticleLinkAnimation(we.getLocation(), warlordsNPC.getLocation(), Particle.VILLAGER_HAPPY);
                        we.addDamageInstance(
                                warlordsNPC,
                                "Death Ray",
                                we.getMaxHealth() * 0.9f,
                                we.getMaxHealth() * 0.9f,
                                -1,
                                100
                        );

                        warlordsNPC.addHealingInstance(
                                warlordsNPC,
                                "Death Ray Healing",
                                we.getMaxHealth() * 2,
                                we.getMaxHealth() * 2,
                                -1,
                                100
                        );
                    }

                    preventArmageddon = false;
                    this.cancel();
                }

                ChatUtils.sendTitleToGamePlayers(
                        getWarlordsNPC().getGame(),
                        Component.text(countdown.get(), NamedTextColor.YELLOW),
                        Component.text(damageToDeal.get(), NamedTextColor.RED),
                        0, 4, 0
                );
            }
        }.runTaskTimer(40, 0);
    }

    private void shockwave(Location loc, double radius, int tickDelay, long playerCount) {
        new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                if (warlordsNPC.isDead() || preventArmageddon) {
                    this.cancel();
                    return;
                }

                Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 10, 0.4f);
                Utils.playGlobalSound(loc, "warrior.laststand.activation", 10, 0.4f);
                for (WarlordsEntity we : PlayerFilter
                        .entitiesAround(loc, radius, radius, radius)
                        .aliveEnemiesOf(warlordsNPC)
                ) {
                    if (!we.getCooldownManager().hasCooldownFromName("Cloaked")) {
                        we.addDamageInstance(warlordsNPC,
                                "Augmented Armageddon",
                                (550 * playerCount),
                                (700 * playerCount),
                                0,
                                100
                        );
                        Utils.addKnockback(name, warlordsNPC.getLocation(), we, -2, 0.2);
                    }
                }
            }
        }.runTaskLater(tickDelay);
    }

    private static class GroundShred extends AbstractPveAbility {

        private final int earthQuakeRadius = 10;

        public GroundShred() {
            super("Ground Shred", 900, 1200, 8, 50);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {


            Location loc = wp.getLocation();
            Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 0.4f);
            EffectUtils.strikeLightning(loc, false);
            EffectUtils.playSphereAnimation(loc, earthQuakeRadius, Particle.SPELL_WITCH, 2);
            EffectUtils.playHelixAnimation(loc, earthQuakeRadius, Particle.FIREWORKS_SPARK, 2, 40);
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(wp, earthQuakeRadius, earthQuakeRadius, earthQuakeRadius)
                    .aliveEnemiesOf(wp)
            ) {
                Utils.addKnockback(name, loc, enemy, -1.5, 0.25);
                enemy.addDamageInstance(
                        wp,
                        name,
                        minDamageHeal,
                        maxDamageHeal,
                        critChance,
                        critMultiplier
                );
            }
            return true;
        }
    }

}

package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.PrismGuard;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.irongolem.IronGolem;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.skeleton.ExiledSkeleton;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.pve.mobs.zombie.ForgottenZombie;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.*;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

public class Illumina extends AbstractZombie implements BossMob {

    private boolean phaseOneTriggered = false;
    private boolean phaseTwoTriggered = false;
    private boolean phaseThreeTriggered = false;
    private boolean phaseFourTriggered = false;

    private AtomicInteger damageToDeal = new AtomicInteger(0);

    public Illumina(Location spawnLocation) {
        super(spawnLocation,
                "Illumina",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.DEEP_DARK_WORM),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 120, 200),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 120, 120, 200),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 120, 120, 200),
                        Weapons.NEW_LEAF_SCYTHE.getItem()
                ),
                110000,
                0.33f,
                25,
                2000,
                3000
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        for (int i = 0; i < (2 * option.getGame().warlordsPlayers().count()); i++) {
            option.spawnNewMob(new IronGolem(spawnLocation));
        }

        PrismGuard prismGuard = new PrismGuard();
        prismGuard.setTickDuration(200);
        warlordsNPC.getSpec().setBlue(prismGuard);

        warlordsNPC.getCooldownManager().removeCooldown(DamageCheck.class, false);
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
        long playerCount = option.getGame().warlordsPlayers().count();
        // immune to slowness
        warlordsNPC.getSpeed().removeSlownessModifiers();

        Location loc = warlordsNPC.getLocation();
        if (ticksElapsed % 100 == 0) {
            Utils.playGlobalSound(loc, Sound.BLOCK_GRASS_BREAK, 500, 0.4f);
            new FallingBlockWaveEffect(loc.add(0, 1, 0), 7, 1.2, Material.OAK_LEAVES).play();
            for (WarlordsEntity we : PlayerFilterGeneric
                    .entitiesAround(warlordsNPC, 7, 7, 7)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                we.addSpeedModifier(warlordsNPC, "Bramble Slowness", -99, 30);
                we.addDamageInstance(
                        warlordsNPC,
                        "Bramble",
                        1200,
                        1800,
                        -1,
                        100
                );
            }
        }

        if (ticksElapsed % 220 == 0) {
            EffectUtils.strikeLightningInCylinder(loc, 6, false);
            for (WarlordsEntity we : PlayerFilterGeneric
                    .entitiesAround(warlordsNPC, 6, 6, 6)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                we.addSpeedModifier(warlordsNPC, "Bramble Slowness", -99, 30);
                Utils.addKnockback(name, loc, we, -2, 0.3);
            }
        }

        DifficultyIndex difficulty = option.getDifficulty();

        if (ticksElapsed % 600 == 0) {
            for (int i = 0; i < (difficulty == DifficultyIndex.EXTREME ? playerCount / 2 + 1 : playerCount); i++) {
                option.spawnNewMob(new ExiledSkeleton(spawnLocation));
            }
        }

        if (warlordsNPC.getHealth() < (warlordsNPC.getMaxHealth() * .9f) && !phaseOneTriggered) {
            phaseOneTriggered = true;
            timedDamage(option, playerCount, 10000, 11);
        }

        if (warlordsNPC.getHealth() < (warlordsNPC.getMaxHealth() * .6f) && !phaseTwoTriggered) {
            phaseTwoTriggered = true;
            timedDamage(option, playerCount, 12000, 11);
            for (int i = 0; i < (2 * playerCount); i++) {
                option.spawnNewMob(new ExiledSkeleton(loc));
            }
        }

        if (warlordsNPC.getHealth() < (warlordsNPC.getMaxHealth() * .3f) && !phaseThreeTriggered) {
            phaseThreeTriggered = true;
            timedDamage(option, playerCount, 14000, 11);
            for (int i = 0; i < (difficulty == DifficultyIndex.EXTREME ? playerCount / 2 + 1 : playerCount); i++) {
                option.spawnNewMob(new ForgottenZombie(loc));
            }
        }

        if (warlordsNPC.getHealth() < (warlordsNPC.getMaxHealth() * .1f) && !phaseFourTriggered) {
            phaseFourTriggered = true;
            timedDamage(option, playerCount, 5000, 6);
            for (int i = 0; i < ((difficulty == DifficultyIndex.EXTREME ? 1 : 2) * playerCount); i++) {
                option.spawnNewMob(new IronGolem(loc));
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.BLUE)
                                                                       .with(FireworkEffect.Type.BALL_LARGE)
                                                                       .build());
        EffectUtils.strikeLightning(deathLocation, false, 2);
    }

    @Override
    public NamedTextColor getColor() {
        return NamedTextColor.BLUE;
    }

    @Override
    public Component getDescription() {
        return Component.text("General of the Illusion Legion", NamedTextColor.DARK_GRAY);
    }

    private void timedDamage(PveOption option, long playerCount, int damageValue, int timeToDealDamage) {
        damageToDeal.set((int) (damageValue * playerCount));

        for (WarlordsEntity we : PlayerFilter
                .playingGame(getWarlordsNPC().getGame())
                .aliveEnemiesOf(warlordsNPC)
        ) {
            we.getEntity().showTitle(Title.title(
                    Component.empty(),
                    Component.text("Keep attacking Illumina to stop the draining!", NamedTextColor.RED),
                    Title.Times.times(Ticks.duration(10), Ticks.duration(35), Ticks.duration(0))
            ));

            Utils.addKnockback(name, warlordsNPC.getLocation(), we, -4, 0.35);
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
                    FireWorkEffectPlayer.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                                                                                               .withColor(Color.WHITE)
                                                                                               .with(FireworkEffect.Type.BALL_LARGE)
                                                                                               .build());
                    warlordsNPC.getSpec().getBlue().onActivate(warlordsNPC, null);
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
                                600,
                                600,
                                -1,
                                100
                        );
                    }
                }

                if (countdown.get() <= 0 && damageToDeal.get() > 0) {
                    for (int i = 0; i < (2 * option.getGame().warlordsPlayers().count()); i++) {
                        option.spawnNewMob(new IronGolem(spawnLocation));
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
                                100,
                                false,
                                false
                        );
                    }

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
}

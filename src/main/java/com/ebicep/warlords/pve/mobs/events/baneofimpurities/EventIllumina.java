package com.ebicep.warlords.pve.mobs.events.baneofimpurities;

import com.ebicep.warlords.abilities.PrismGuard;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.Illumina;
import com.ebicep.warlords.pve.mobs.irongolem.GolemApprentice;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.*;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

public class EventIllumina extends AbstractMob implements BossMob {

    private final RandomCollection<Mob> summonList = new RandomCollection<Mob>()
            .add(0.1, Mob.EXTREME_ZEALOT)
            .add(0.3, Mob.SKELETAL_SORCERER)
            .add(0.2, Mob.OVERGROWN_ZOMBIE)
            .add(0.1, Mob.RIFT_WALKER)
            .add(0.05, Mob.NIGHTMARE_ZOMBIE)
            .add(0.1, Mob.SLIME_GUARD)
            .add(0.1, Mob.ADVANCED_WARRIOR_BERSERKER)
            .add(0.05, Mob.ZOMBIE_KNIGHT)
            .add(0.1, Mob.FIRE_SPLITTER);
    private boolean phaseOneTriggered = false;
    private boolean phaseTwoTriggered = false;
    private boolean phaseThreeTriggered = false;
    private AtomicInteger damageToDeal = new AtomicInteger(0);
    private PrismGuard prismGuard = new PrismGuard() {{
        setTickDuration(200);
    }};

    public EventIllumina(Location spawnLocation) {
        super(spawnLocation,
                "Illumina",
                525000,
                0.33f,
                0,
                950,
                1350,
                new Illumina.BrambleSlowness()
        );
    }

    public EventIllumina(
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
                maxMeleeDamage,
                new Illumina.BrambleSlowness()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_ILLUMINA;
    }

    @Override
    public Component getDescription() {
        return Component.text("", NamedTextColor.DARK_GRAY);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.BLUE;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        for (int i = 0; i < (2 * option.getGame().warlordsPlayers().count()); i++) {
            option.spawnNewMob(new GolemApprentice(spawnLocation));
        }

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
        // immune to slowness
        warlordsNPC.getSpeed().removeSlownessModifiers();

        long playerCount = option.getGame().warlordsPlayers().count();
        Location loc = warlordsNPC.getLocation();
        DifficultyIndex difficulty = option.getDifficulty();

        if (!phaseOneTriggered && warlordsNPC.getCurrentHealth() < (warlordsNPC.getMaxHealth() * .7f)) {
            phaseOneTriggered = true;
            timedDamage(option, playerCount, 10000, 11);
        } else if (!phaseTwoTriggered && warlordsNPC.getCurrentHealth() < (warlordsNPC.getMaxHealth() * .4f)) {
            phaseTwoTriggered = true;
            timedDamage(option, playerCount, 15000, 11);
        } else if (!phaseThreeTriggered && warlordsNPC.getCurrentHealth() < (warlordsNPC.getMaxHealth() * .1f)) {
            phaseThreeTriggered = true;
            timedDamage(option, playerCount, 20000, 11);
        }

        if (ticksElapsed % 200 == 0) {
            for (int i = 0; i < 5; i++) {
                option.spawnNewMob(summonList.next().createMob(loc));
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
                    prismGuard.onActivate(warlordsNPC);
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

                        we.addInstance(InstanceBuilder
                                .damage()
                                .cause("Vampiric Leash")
                                .source(warlordsNPC)
                                .value(600)
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
                        we.addInstance(InstanceBuilder
                                .damage()
                                .cause("Death Ray")
                                .source(warlordsNPC)
                                .value(7500)
                                .flags(InstanceFlags.TRUE_DAMAGE)
                        );
                        warlordsNPC.addInstance(InstanceBuilder
                                .healing()
                                .cause("Death Ray Healing")
                                .source(warlordsNPC)
                                .value(we.getMaxHealth() * 2)
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

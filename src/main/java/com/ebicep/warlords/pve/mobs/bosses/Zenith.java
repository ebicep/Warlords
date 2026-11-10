package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.abilities.SpawnMobAbility;
import com.ebicep.warlords.pve.mobs.abilities.ThunderCloudAbility;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.BossAbilityPhase;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.LightningChainAbility;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.ShatteringChainsAbility;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.ThunderLineBarrageAbility;
import com.ebicep.warlords.pve.mobs.bosses.raidbosses.RaidBossUtils;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;

public class Zenith extends AbstractMob implements BossMob {

    private ThunderLineBarrageAbility thunderLineBarrageAbility;
    private ShatteringChainsAbility shatteringChainsAbility;
    private BossAbilityPhase phaseOne;
    private BossAbilityPhase phaseTwo;
    private BossAbilityPhase phaseThree;
    private boolean enraged = false;

    public Zenith(Location spawnLocation) {
        this(spawnLocation,
                "Zenith",
                26000,
                0.36f,
                25,
                1800,
                2500
        );
    }

    public Zenith(
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
                new Armageddon(),
                new Cleanse(),
                new SpawnMobAbility(AbstractAbilityBuilder.create("zenithSpawnZenithLegionnaire").pve(), Mob.ZENITH_LEGIONNAIRE) {
                    @Override
                    public int getSpawnAmount() {
                        return (int) pveOption.getGame().warlordsPlayers().count();
                    }
                }
        );
    }

    @Override
    public HashMap<MobDrop, HashMap<DifficultyIndex, Double>> mobDrops() {
        return new HashMap<>() {{
            put(MobDrop.ZENITH_STAR, new HashMap<>() {{
                put(DifficultyIndex.EASY, .015);
                put(DifficultyIndex.NORMAL, .025);
                put(DifficultyIndex.HARD, .06);
                put(DifficultyIndex.EXTREME, .12);
                put(DifficultyIndex.ENDLESS, .06);
            }});
        }};
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ZENITH;
    }

    @Override
    public Component getDescription() {
        return Component.text("Envoy Guard of the Illusion", NamedTextColor.LIGHT_PURPLE);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.DARK_PURPLE;
    }

    @Override
    public double getMobScale() {
        return 1.3;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        if (option.getDifficulty() == DifficultyIndex.ENDLESS) {
            float newHealth = 52000;
            warlordsNPC.setMaxHealthAndHeal(newHealth);
        }

        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false, 6);
        DifficultyIndex difficulty = option.getDifficulty();
        float multiplier = switch (difficulty) {
            case EASY -> 0.25f;
            case HARD -> 1;
            case EXTREME -> 1.25f;
            default -> 0.75f;
        };
        int cooldown = 8;
        int secondsMin = 8;
        int secondsMax = 11;
        int sizeMin = 5;
        int sizeMax = 10;
        if (difficulty == DifficultyIndex.EASY || difficulty == DifficultyIndex.NORMAL) {
            cooldown = 5;
            secondsMin = 6;
            secondsMax = 10;
            sizeMin = 3;
            sizeMax = 6;
        }
        addAbility(new ThunderCloudAbility(
                AbstractAbilityBuilder.create("zenithThunderCloud").pve().cooldown(cooldown),
                true,
                secondsMin, secondsMax,
                sizeMin, sizeMax
        ));

        Location mapCenter = new Location(warlordsNPC.getWorld(), 112.5, 11, 62.5);
        thunderLineBarrageAbility = new ThunderLineBarrageAbility(
                warlordsNPC,
                () -> warlordsNPC.getLocation(),
                40,
                1,
                20,
                80,
                2,
                4000 * multiplier, 6, 2
        );
        shatteringChainsAbility = new ShatteringChainsAbility(
                warlordsNPC,
                () -> mapCenter.clone().add(0, 1.1, 0),
                20,
                40,
                80,
                200,
                10,
                1,
                4,
                2,
                4000
        );

        phaseOne = new BossAbilityPhase(warlordsNPC, 60, () -> {
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("Nine Crowns... The reign was mine to begin with!", NamedTextColor.LIGHT_PURPLE),
                    20, 60, 20
            );
            warlordsNPC.addSpeedModifier(warlordsNPC, "Armageddon Slowness", -99, 150);
            shatteringChainsAbility.start(warlordsNPC.getGame());
        });

        phaseTwo = new BossAbilityPhase(warlordsNPC, 40, () -> {
            warlordsNPC.addSpeedModifier(warlordsNPC, "Armageddon Slowness", -99, 150);
            shatteringChainsAbility.start(warlordsNPC.getGame());
        });

        phaseThree = new BossAbilityPhase(warlordsNPC, 25, () -> {
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("Everyday our power grows, everyday he will get stronger...", NamedTextColor.LIGHT_PURPLE),
                    20, 60, 20
            );
            warlordsNPC.getSpeed().addBaseModifier(40);
            warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Enraged",
                    null,
                    Zenith.class,
                    null,
                    warlordsNPC,
                    CooldownTypes.BUFF,
                    cooldownManager -> {},
                    true
            ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, 0.5f);
                    }
            ));
            enraged = true;
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % (enraged ? 75 : 200) == 0) {
            thunderLineBarrageAbility.start(warlordsNPC.getGame());
        }

        if (option.getDifficulty() == DifficultyIndex.ENDLESS) {
            float health = warlordsNPC.getCurrentHealth();
            phaseOne.initialize(health);
            phaseTwo.initialize(health);
            phaseThree.initialize(health);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), true);

        if (!event.getCause().isEmpty()) {
            return;
        }
        new GameRunnable(attacker.getGame()) {
            int counter = 0;

            @Override
            public void run() {
                if (warlordsNPC.isDead()) {
                    this.cancel();
                }

                counter++;
                EffectUtils.playFirework(
                        receiver.getLocation(),
                        FireworkEffect.builder()
                                      .withColor(Color.WHITE)
                                      .with(FireworkEffect.Type.BURST)
                                      .build()
                );
                Utils.addKnockback(name, attacker.getLocation(), receiver, -1, 0.3);
                receiver.addInstance(InstanceBuilder
                        .damage()
                        .cause("Uppercut")
                        .source(warlordsNPC)
                        .min(250)
                        .max(350)
                );

                if (counter == 3 || receiver.isDead()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(8, 2);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(self.getLocation(), Sound.ENTITY_BLAZE_HURT, 2, 0.2f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        for (int i = 0; i < 3; i++) {
            EffectUtils.playFirework(deathLocation, FireworkEffect.builder()
                                                                  .withColor(Color.WHITE)
                                                                  .with(FireworkEffect.Type.BALL_LARGE)
                                                                  .build());
        }

        EffectUtils.strikeLightning(deathLocation, false, 5);
    }

    private static class Armageddon extends AbstractPveAbility implements Damages<Armageddon.DamageValues> {

        private final int stormRadius = 10;
        private final DamageValues damageValues = new DamageValues();

        @Override
        public DamageValues getDamageValues() {
            return damageValues;
        }

        public Armageddon() {
            super(AbstractAbilityBuilder.create("zenithArmageddon").pve());
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            long playerCount = pveOption.getGame().warlordsPlayers().count();
            Location loc = wp.getLocation();
            DifficultyIndex difficulty = pveOption.getDifficulty();
            float multiplier = switch (difficulty) {
                case EASY -> 0.5f;
                case HARD -> 1;
                case EXTREME -> 1.25f;
                default -> 0.75f;
            };

            Utils.playGlobalSound(loc, "rogue.healingremedy.impact", 500, 0.85f);
            Utils.playGlobalSound(loc, "rogue.healingremedy.impact", 500, 0.85f);
            wp.addSpeedModifier(wp, "Armageddon Slowness", -99, 90);
            Game game = wp.getGame();
            new GameRunnable(game) {
                @Override
                public void run() {
                    if (wp.isDead()) {
                        this.cancel();
                        return;
                    }

                    EffectUtils.strikeLightningInCylinder(loc, stormRadius, false, 12, game);
                    shockwave(loc, stormRadius, 12, playerCount, multiplier, wp);
                    EffectUtils.strikeLightningInCylinder(loc, stormRadius + 5, false, 24, game);
                    shockwave(loc, stormRadius + 5, 24, playerCount, multiplier, wp);
                    EffectUtils.strikeLightningInCylinder(loc, stormRadius + 10, false, 36, game);
                    shockwave(loc, stormRadius + 10, 36, playerCount, multiplier, wp);
                    if (difficulty == DifficultyIndex.HARD || difficulty == DifficultyIndex.EXTREME || difficulty == DifficultyIndex.ENDLESS) {
                        EffectUtils.strikeLightningInCylinder(loc, stormRadius + 15, false, 48, game);
                        shockwave(loc, stormRadius + 15, 48, playerCount, multiplier, wp);
                        EffectUtils.strikeLightningInCylinder(loc, stormRadius + 15, false, 60, game);
                        shockwave(loc, stormRadius + 15, 60, playerCount, multiplier, wp);
                    }
                }
            }.runTaskLater(40);
            return true;
        }

        private void shockwave(Location loc, double radius, int tickDelay, long playerCount, float damageMultiplier, WarlordsEntity wp) {
            new GameRunnable(wp.getGame()) {
                @Override
                public void run() {
                    if (wp.isDead()) {
                        this.cancel();
                    }

                    Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 10, 0.4f);
                    Utils.playGlobalSound(loc, "warrior.laststand.activation", 10, 0.4f);
                    for (WarlordsEntity we : PlayerFilter
                            .entitiesAround(loc, radius, radius, radius)
                            .aliveEnemiesOf(wp)
                    ) {
                        if (we.getCooldownManager().hasCooldownFromName("Cloaked")) {
                            continue;
                        }
                        we.addInstance(InstanceBuilder
                                .damage()
                                .cause("Armageddon")
                                .source(wp)
                                .min((damageValues.armageddonDamage.getMinValue() * playerCount) * damageMultiplier)
                                .max((damageValues.armageddonDamage.getMaxValue() * playerCount) * damageMultiplier)
                        );
                        Utils.addKnockback(name, wp.getLocation(), we, -2, 0.2);
                    }
                }
            }.runTaskLater(tickDelay);
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.RangedValue armageddonDamage = new Value.RangedValue(550, 700);
            private final List<Value> values = List.of(armageddonDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }

    private static class Cleanse extends AbstractPveAbility {

        public Cleanse() {
            super(AbstractAbilityBuilder.create("zenithCleanse").pve());
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            long playerCount = pveOption.getGame().warlordsPlayers().count();
            Location loc = wp.getLocation();
            DifficultyIndex difficulty = pveOption.getDifficulty();
            float multiplier = switch (difficulty) {
                case EASY -> 0.5f;
                case HARD -> 1;
                case EXTREME -> 1.25f;
                default -> 0.75f;
            };

            EffectUtils.playSphereAnimation(loc, 4, Particle.WITCH, 2);
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(loc, 4, 4, 4)
                    .aliveEnemiesOf(wp)
            ) {
                Utils.addKnockback(name, wp.getLocation(), we, -1.5, 0.3);
                we.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(wp)
                        .min((damageValues.cleanseDamage.getMinValue() * playerCount) * multiplier)
                        .max((damageValues.cleanseDamage.getMaxValue() * playerCount) * multiplier)
                );
                EffectUtils.strikeLightning(we.getLocation(), false);
            }
            return true;
        }

        private final DamageValues damageValues = new DamageValues();

        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.RangedValue cleanseDamage = new Value.RangedValue(300, 400);
            private final List<Value> values = List.of(cleanseDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }

    }

}

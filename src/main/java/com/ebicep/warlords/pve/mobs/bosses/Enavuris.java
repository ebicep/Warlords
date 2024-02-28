package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.flags.Unsilencable;
import com.ebicep.warlords.pve.mobs.flags.Unstunnable;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class Enavuris extends AbstractMob implements BossMob, Unsilencable, Unstunnable {

    public static final LocationUtils.LocationXYZ[] CAGE_LOCATIONS = {
            new LocationUtils.LocationXYZ(137, 16, 62),
            new LocationUtils.LocationXYZ(114, 16, 45),
            new LocationUtils.LocationXYZ(87, 16, 45),
            new LocationUtils.LocationXYZ(103, 16, 69),
            new LocationUtils.LocationXYZ(91, 16, 81),
            new LocationUtils.LocationXYZ(128, 16, 85),
    };

    public static final LocationUtils.LocationXYZ[] CURSED_PSION_LOCATIONS = {
            new LocationUtils.LocationXYZ(137, 11, 62),
            new LocationUtils.LocationXYZ(114, 11, 45),
            new LocationUtils.LocationXYZ(87, 12, 45),
            new LocationUtils.LocationXYZ(103, 11, 69),
            new LocationUtils.LocationXYZ(91, 12, 81),
            new LocationUtils.LocationXYZ(128, 12, 85),
    };

    @Nullable
    private static WarlordsEntity getPlayer(WarlordsEntity mob, Comparator<WarlordsEntity> comparator) {
        return PlayerFilter.playingGame(mob.getGame())
                           .enemiesOf(mob)
                           .sorted(comparator)
                           .findFirstOrNull();
    }

    public Enavuris(Location spawnLocation) {
        super(spawnLocation,
                "Enavuris",
                95000,
                0.475f, // TODO
                20,
                745,
                985
        );
    }

    public Enavuris(
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
                maxMeleeDamage
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void setTarget(WarlordsEntity target) {
        super.setTarget(target);
        onTargetSwap(target.getEntity());
    }

    @Override
    public void setTarget(LivingEntity target) {
        super.setTarget(target);
        onTargetSwap(target);
    }

    private void onTargetSwap(Entity target) {
        if (!Objects.equals(getTarget(), target)) {
            Utils.playGlobalSound(target.getLocation(), Sound.ENTITY_ENDERMAN_AMBIENT, 2, .6f);
        }
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.LIGHT_PURPLE;
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ENAVURIS;
    }

    @Override
    public Component getDescription() {
        return Component.text("jowiudnajkksjbciu", NamedTextColor.DARK_PURPLE);
    }

    private static class EnderStones extends AbstractPveAbility {

        public EnderStones() {
            super(
                    "Ender Stones",
                    500,
                    600,
                    10,
                    50,
                    20,
                    180,
                    0
            );
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            // TODO convert to projectile
            return true;
        }
    }

    private static class Imprisonment extends AbstractPveAbility {

        private static final int IMPRISONMENT_TICKS = 10 * 20;

        private WarlordsEntity caster;
        @Nullable
        private WarlordsEntity imprisonedPlayer;
        private int imprisonTicks = 0;

        public Imprisonment() {
            super(
                    "Imprisonment",
                    20,
                    50,
                    true
            );
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            caster = wp;
            imprisonTicks = 0;
            imprisonedPlayer = getPlayer(wp, Comparator.comparing(w -> -w.getMaxEnergy()));
            if (imprisonedPlayer == null) {
                return false;
            }
            LocationUtils.LocationXYZ randomCageLocation = CAGE_LOCATIONS[ThreadLocalRandom.current().nextInt(CAGE_LOCATIONS.length)];
            imprisonedPlayer.teleport(new Location(wp.getWorld(), randomCageLocation.x(), randomCageLocation.y(), randomCageLocation.z()));
            imprisonedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 10 * 20, 1, false, false, false))
            return true;
        }

        @Override
        public void runEveryTick(@org.jetbrains.annotations.Nullable WarlordsEntity warlordsEntity) {
            super.runEveryTick(warlordsEntity);
            if (imprisonedPlayer == null) {
                return;
            }
            if (imprisonTicks++ >= IMPRISONMENT_TICKS) {
                imprisonedPlayer.die(caster);
                imprisonTicks = 0;
            }
        }
    }

    private static class VowsOfTheEnd extends AbstractPveAbility {

        private int maxTargets = 1;

        public VowsOfTheEnd() {
            super(
                    "Vows of the End",
                    10,
                    50
            );
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            PlayerFilter.playingGame(wp.getGame())
                        .enemiesOf(wp)
                        .sorted(Comparator.comparing(w -> ThreadLocalRandom.current().nextInt()))
                        .limit(maxTargets)
                        .forEach(swappedPlayer -> giveVows(wp, swappedPlayer));
            return true;
        }

        private void giveVows(@Nonnull WarlordsEntity wp, WarlordsEntity swappedPlayer) {
            if (swappedPlayer == null) {
                return;
            }
            AtomicReference<Debuff> currentDebuff = new AtomicReference<>(null);
            swappedPlayer.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Vows of the End",
                    "Vows",
                    VowsOfTheEnd.class,
                    null,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {},
                    15 * 20,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 100 == 0) {
                            Debuff randomDebuff = Debuff.RANDOM_DEBUFF.next();
                            currentDebuff.set(randomDebuff);
                            if (randomDebuff == null) {
                                return;
                            }
                            cooldown.setName("Vows of the End - " + randomDebuff.name());
                            switch (randomDebuff) {
                                case SLOW -> swappedPlayer.addSpeedModifier(wp, name, -50, 5 * 20);
                                case DARKNESS -> swappedPlayer.addPotionEffect(new PotionEffect(
                                        PotionEffectType.DARKNESS,
                                        5 * 20,
                                        1, false,
                                        false,
                                        false
                                )); // TODO SILENCE
                            }
                        }
                    })
            ) {
                @Override
                public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                    if (currentDebuff.get() == Debuff.WOUND) {
                        return currentHealValue * .6f;
                    }
                    return currentHealValue;
                }

                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    if (currentDebuff.get() == Debuff.CRIPPLE) {
                        return currentDamageValue * .5f;
                    }
                    return currentDamageValue;
                }

                @Override
                public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    if (currentDebuff.get() != Debuff.LEECH) {
                        return;
                    }
                    float healingMultiplier = .25f
                    float healValue = currentDamageValue * healingMultiplier;
                    event.getAttacker().addHealingInstance(
                            wp,
                            "Leech",
                            healValue,
                            healValue,
                            -1,
                            100
                    );
                }
            });
        }

        public int getMaxTargets() {
            return maxTargets;
        }

        public void setMaxTargets(int maxTargets) {
            this.maxTargets = maxTargets;
        }

        private enum Debuff {
            SLOW,
            WOUND,
            CRIPPLE,
            DARKNESS,
            SILENCE,
            LEECH,

            ;

            public static final RandomCollection<Debuff> RANDOM_DEBUFF = new RandomCollection<Debuff>()
                    .add(25, SLOW)
                    .add(25, WOUND)
                    .add(25, CRIPPLE)
                    .add(15, DARKNESS)
                    .add(15, SILENCE)
                    .add(15, LEECH);
        }

    }


}

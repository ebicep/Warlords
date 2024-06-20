package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.vindicator.SoulShackleBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SoulShackle extends AbstractAbility implements RedAbilityIcon, Damages<SoulShackle.DamageValues> {

    private final int shackleRange = 15;
    private final DamageValues damageValues = new DamageValues();
    private float shacklePool = 0;
    private int maxShackleTargets = 1;
    private int minSilenceDurationInTicks = 40;
    private int maxSilenceDurationInTicks = 70;

    public SoulShackle() {
        super("Soul Shackle", 9, 40);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Shackle up to ")
                               .append(Component.text(maxShackleTargets, NamedTextColor.GOLD))
                               .append(Component.text(" enemy and deal "))
                               .append(Damages.formatDamage(damageValues.shackleDamage))
                               .append(Component.text(" damage. Shackled enemies are silenced for "))
                               .append(formatRange(minSilenceDurationInTicks / 20f, maxSilenceDurationInTicks / 20f, NamedTextColor.GOLD))
                               .append(Component.text(" seconds, making them unable to use their main attack for the duration. The silence duration increases by "))
                               .append(Component.text("0.5", NamedTextColor.GOLD))
                               .append(Component.text(" seconds for every "))
                               .append(Component.text("500", NamedTextColor.RED))
                               .append(Component.text(" damage you took in the last "))
                               .append(Component.text("6", NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Gain a short burst of "))
                               .append(Component.text("40%", NamedTextColor.YELLOW))
                               .append(Component.text(" movement speed for "))
                               .append(Component.text("1.5", NamedTextColor.GOLD))
                               .append(Component.text(" seconds after shackling an enemy."))
                               .append(Component.text("\n\nHas a range of "))
                               .append(Component.text(shackleRange, NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        boolean hasShackled = false;
        if (pveMasterUpgrade || pveMasterUpgrade2) {
            Location playerLoc = new LocationBuilder(wp.getLocation())
                    .pitch(0)
                    .add(0, 1.7, 0);
            Location playerEyeLoc = new LocationBuilder(wp.getLocation())
                    .pitch(0)
                    .backward(1);
            Vector viewDirection = playerLoc.getDirection();
            Utils.playGlobalSound(wp.getLocation(), "warrior.intervene.impact", 1.5f, 0.25f);
            Utils.playGlobalSound(wp.getLocation(), "mage.fireball.activation", 1.5f, 0.2f);
            for (WarlordsEntity shackleTarget : PlayerFilter
                    .entitiesAroundRectangle(wp, shackleRange, shackleRange + 2, shackleRange)
                    .aliveEnemiesOf(wp)
                    .closestFirst(wp)
                    .limit(8)
            ) {
                Vector direction = shackleTarget.getLocation().subtract(playerEyeLoc).toVector().normalize();
                if (viewDirection.dot(direction) > .6) {
                    activateAbility(wp, shackleTarget);
                }

                hasShackled = true;
            }
        } else {
            for (WarlordsEntity shackleTarget : PlayerFilter
                    .entitiesAround(wp, shackleRange, shackleRange, shackleRange)
                    .aliveEnemiesOf(wp)
                    .requireLineOfSight(wp)
                    .closestFirst(wp)
                    .limit(maxShackleTargets)
            ) {
                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                        .append(Component.text(" You shackled ", NamedTextColor.GRAY))
                        .append(Component.text(shackleTarget.getName(), NamedTextColor.YELLOW))
                        .append(Component.text("!", NamedTextColor.GRAY))
                );
                Utils.playGlobalSound(wp.getLocation(), "warrior.intervene.impact", 1.5f, 0.25f);
                Utils.playGlobalSound(wp.getLocation(), "mage.fireball.activation", 1.5f, 0.2f);
                activateAbility(wp, shackleTarget);
                shacklePool = 0;

                hasShackled = true;
            }
        }

        if (hasShackled) {

        }

        return hasShackled;
    }

    private void activateAbility(@Nonnull WarlordsEntity wp, WarlordsEntity shackleTarget) {
        EffectUtils.playChainAnimation(wp, shackleTarget, new ItemStack(Material.PUMPKIN), 15);
        EffectUtils.playFirework(
                shackleTarget.getLocation(),
                FireworkEffect.builder()
                              .withColor(Color.YELLOW)
                              .with(FireworkEffect.Type.BALL)
                              .build(),
                1
        );

        wp.addSpeedModifier(wp, "Shackle Speed", 40, 30, "BASE");

        int silenceDuration = minSilenceDurationInTicks + (int) (shacklePool / 1000) * 20;
        if (silenceDuration > maxSilenceDurationInTicks) {
            silenceDuration = maxSilenceDurationInTicks;
        }
        shackleTarget.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .value(damageValues.shackleDamage)
        );
        shacklePlayer(wp, shackleTarget, silenceDuration);

        if (pveMasterUpgrade2) {
            shackleTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Oppressive Chains",
                    "OPP",
                    SoulShackle.class,
                    new SoulShackle(),
                    wp,
                    CooldownTypes.DEBUFF,
                    cooldownManager -> {
                    },
                    3 * 20
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * 1.25f;
                }
            });
        }
    }

    public static void shacklePlayer(WarlordsEntity wp, WarlordsEntity shackleTarget, int tickDuration) {
        shackleTarget.getCooldownManager().removeCooldown(SoulShackle.class, false);
        if (!shackleTarget.getCooldownManager().hasCooldownFromName("Debuff Immunity")) {
            shackleTarget.getEntity().showTitle(Title.title(
                    Component.empty(),
                    Component.text("SILENCED", NamedTextColor.RED),
                    Title.Times.times(Ticks.duration(0), Ticks.duration(tickDuration), Ticks.duration(0))
            ));
        }
        shackleTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Shackle Silence",
                "SILENCE",
                SoulShackle.class,
                new SoulShackle(),
                wp,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 10 == 0) {
                        Utils.playGlobalSound(shackleTarget.getLocation(), Sound.BLOCK_SAND_BREAK, 2, 2);

                        Location playerLoc = shackleTarget.getLocation();
                        Location particleLoc = playerLoc.clone();
                        for (int i = 0; i < 10; i++) {
                            for (int j = 0; j < 10; j++) {
                                double angle = j / 10D * Math.PI * 2;
                                double width = 1.075;
                                particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                                particleLoc.setY(playerLoc.getY() + i / 5D);
                                particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                                particleLoc.getWorld().spawnParticle(
                                        Particle.REDSTONE,
                                        particleLoc,
                                        1,
                                        0,
                                        0,
                                        0,
                                        0,
                                        new Particle.DustOptions(Color.fromRGB(25, 25, 25), 1),
                                        true
                                );
                            }
                        }
                    }
                })
        ) {
            @Override
            protected Listener getListener() {
                return new Listener() {
                    @EventHandler
                    public void onAbilityActivate(WarlordsAbilityActivateEvent.Pre event) {
                        if (!Objects.equals(event.getWarlordsEntity(), shackleTarget) || event.getSlot() != 0) {
                            return;
                        }
                        event.setCancelled(true);
                        Player player = event.getPlayer();
                        player.sendMessage(Component.text("You have been silenced!", NamedTextColor.RED));
                        player.playSound(player.getLocation(), "notreadyalert", 1, 1);
                    }
                };
            }
        });
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SoulShackleBranch(abilityTree, this);
    }

    @Override
    public void runEverySecond(@Nullable WarlordsEntity warlordsEntity) {
        if (shacklePool > 0) {
            float newPool = shacklePool - 200;
            shacklePool = Math.max(newPool, 0);
        }
    }

    public void addToShacklePool(float amount) {
        this.shacklePool += amount;
    }

    public int getMaxSilenceDurationInTicks() {
        return maxSilenceDurationInTicks;
    }

    public void setMaxSilenceDurationInTicks(int maxSilenceDurationInTicks) {
        this.maxSilenceDurationInTicks = maxSilenceDurationInTicks;
    }

    public int getMinSilenceDurationInTicks() {
        return minSilenceDurationInTicks;
    }

    public void setMinSilenceDurationInTicks(int minSilenceDurationInTicks) {
        this.minSilenceDurationInTicks = minSilenceDurationInTicks;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable shackleDamage = new Value.RangedValueCritable(446, 589, 20, 175);
        private final List<Value> values = List.of(shackleDamage);

        public Value.RangedValueCritable getShackleDamage() {
            return shackleDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}

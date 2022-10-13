package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoulShackle extends AbstractAbility {

    private final int shackleRange = 15;
    private float shacklePool = 0;
    private int maxShackleTargets = 1;
    private int minSilenceDurationInTicks = 40;
    private int maxSilenceDurationInTicks = 70;

    public SoulShackle() {
        super("Soul Shackle", 446, 589, 9, 40, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Shackle up to §e" + maxShackleTargets + " §7enemy and deal" + formatRangeDamage(minDamageHeal, maxDamageHeal) + "damage. Shackled " +
                "enemies are silenced for" + formatRange(minSilenceDurationInTicks / 20f, maxSilenceDurationInTicks / 20f, ChatColor.GOLD) +
                "seconds, making them unable to use their main attack for the duration. The silence duration increases by §60.5 §7second for every §c500 " +
                "§7damage you took in the last §66 §7seconds. Gain a short burst of §e40% §7movement speed for §61.5 §7seconds after shackling an enemy." +
                "\n\nHas an optimal range of §e" + shackleRange + " §7blocks.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        if (pveUpgrade) {
            Location playerLoc = new LocationBuilder(wp.getLocation())
                    .pitch(0)
                    .add(0, 1.7, 0);
            Location playerEyeLoc = new LocationBuilder(wp.getLocation())
                    .pitch(0)
                    .backward(1);
            Vector viewDirection = playerLoc.getDirection();
            Utils.playGlobalSound(player.getLocation(), "warrior.intervene.impact", 1.5f, 0.25f);
            Utils.playGlobalSound(player.getLocation(), "mage.fireball.activation", 1.5f, 0.2f);
            for (WarlordsEntity shackleTarget : PlayerFilter
                    .entitiesAroundRectangle(wp, shackleRange, shackleRange + 2, shackleRange)
                    .aliveEnemiesOf(wp)
                    .closestFirst(wp)
                    .limit(5)
            ) {
                Vector direction = shackleTarget.getLocation().subtract(playerEyeLoc).toVector().normalize();
                if (viewDirection.dot(direction) > .6) {
                    activateAbility(wp, shackleTarget);
                }
            }

            return true;
        } else {
            for (WarlordsEntity shackleTarget : PlayerFilter
                    .entitiesAround(wp, shackleRange, shackleRange, shackleRange)
                    .aliveEnemiesOf(wp)
                    .requireLineOfSight(wp)
                    .closestFirst(wp)
                    .limit(maxShackleTargets)
            ) {
                wp.subtractEnergy(energyCost, false);
                wp.sendMessage(
                        WarlordsEntity.GIVE_ARROW_GREEN +
                                ChatColor.GRAY + " You shackled " +
                                ChatColor.YELLOW + shackleTarget.getName() +
                                ChatColor.GRAY + "!"
                );
                Utils.playGlobalSound(player.getLocation(), "warrior.intervene.impact", 1.5f, 0.25f);
                Utils.playGlobalSound(player.getLocation(), "mage.fireball.activation", 1.5f, 0.2f);
                activateAbility(wp, shackleTarget);
                shacklePool = 0;

                return true;
            }
        }

        return false;
    }

    private void activateAbility(@Nonnull WarlordsEntity wp, WarlordsEntity shackleTarget) {
        EffectUtils.playChainAnimation(wp, shackleTarget, new ItemStack(Material.PUMPKIN), 15);
        FireWorkEffectPlayer.playFirework(shackleTarget.getLocation(), FireworkEffect.builder()
                .withColor(Color.YELLOW)
                .with(FireworkEffect.Type.BALL)
                .build());

        wp.getSpeed().addSpeedModifier("Shackle Speed", 40, 30, "BASE");

        int silenceDuration = minSilenceDurationInTicks + (int) (shacklePool / 1000) * 20;
        if (silenceDuration > maxSilenceDurationInTicks) {
            silenceDuration = maxSilenceDurationInTicks;
        }

        shackleTarget.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        shacklePlayer(wp, shackleTarget, silenceDuration);
    }

    public static void shacklePlayer(WarlordsEntity wp, WarlordsEntity shackleTarget, int tickDuration) {
        shackleTarget.getCooldownManager().removeCooldown(SoulShackle.class);
        if (!shackleTarget.getCooldownManager().hasCooldownFromName("Vindicate Debuff Immunity")) {
            if (shackleTarget.getEntity() instanceof Player) {
                PacketUtils.sendTitle((Player) shackleTarget.getEntity(), "", "§cSILENCED", 0, tickDuration, 0);
            }
        }
        shackleTarget.getCooldownManager().addRegularCooldown(
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
                        Utils.playGlobalSound(shackleTarget.getLocation(), Sound.DIG_SAND, 2, 2);

                        Location playerLoc = shackleTarget.getLocation();
                        Location particleLoc = playerLoc.clone();
                        for (int i = 0; i < 10; i++) {
                            for (int j = 0; j < 10; j++) {
                                double angle = j / 10D * Math.PI * 2;
                                double width = 1.075;
                                particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                                particleLoc.setY(playerLoc.getY() + i / 5D);
                                particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(25, 25, 25), particleLoc, 500);
                            }
                        }
                    }
                })
        );
    }

    @Override
    public void runEverySecond() {
        if (shacklePool > 0) {
            float newPool = shacklePool - 200;
            shacklePool = Math.max(newPool, 0);
        }
    }

    public float getShacklePool() {
        return shacklePool;
    }

    public void addToShacklePool(float amount) {
        this.shacklePool += amount;
    }

    public int getMaxShackleTargets() {
        return maxShackleTargets;
    }

    public void setMaxShackleTargets(int maxShackleTargets) {
        this.maxShackleTargets = maxShackleTargets;
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
}

package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsUndyingArmyPopEvent;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.revenant.UndyingArmyBranch;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class UndyingArmy extends AbstractAbility implements OrangeAbilityIcon, Duration {
    public static final ItemStack BONE = new ItemBuilder(Material.BONE)
            .name(Component.text("Instant Kill", NamedTextColor.RED))
            .lore(
                    Component.text("Right-click this item to die"),
                    Component.text("instantly instead of waiting for"),
                    Component.text("the decay.")
            )
            .get();

    public static boolean checkUndyingArmy(WarlordsEntity warlordsEntity, float newHealth) {
        // Checks whether the player has any remaining active Undying Army instances active.
        if (!warlordsEntity.getCooldownManager().checkUndyingArmy(false) || newHealth > 0) {
            return false;
        }
        for (RegularCooldown<?> undyingArmyCooldown : new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                .filterCooldownClass(UndyingArmy.class)
                .stream()
                .toList()
        ) {
            UndyingArmy undyingArmy = (UndyingArmy) undyingArmyCooldown.getCooldownObject();
            if (undyingArmy.isArmyDead(warlordsEntity)) {
                continue;
            }
            undyingArmy.pop(warlordsEntity);

            // Drops the flag when popped.
            FlagHolder.dropFlagForPlayer(warlordsEntity);

            // Sending the message + check if getFrom is self
            int armyDamage = Math.round(warlordsEntity.getMaxHealth() * (undyingArmy.getMaxHealthDamage() / 100f));
            if (undyingArmyCooldown.getFrom() == warlordsEntity) {
                warlordsEntity.sendMessage(Component.text("» ", NamedTextColor.GREEN)
                                                    .append(Component.text(
                                                            "Your Undying Army revived you with temporary health. Fight until your death! Your health will decay by ",
                                                            NamedTextColor.LIGHT_PURPLE
                                                    ))
                                                    .append(Component.text(armyDamage, NamedTextColor.RED))
                                                    .append(Component.text(" every second.", NamedTextColor.GRAY))
                );
            } else {
                warlordsEntity.sendMessage(Component.text("» ", NamedTextColor.GREEN)
                                                    .append(Component.text(undyingArmyCooldown.getFrom()
                                                                                              .getName() + "'s Undying Army revived you with temporary health. Fight until your death! Your health will decay by ",
                                                            NamedTextColor.LIGHT_PURPLE
                                                    ))
                                                    .append(Component.text(armyDamage, NamedTextColor.RED))
                                                    .append(Component.text(" every second.", NamedTextColor.LIGHT_PURPLE))
                );
            }

            EffectUtils.playFirework(warlordsEntity.getLocation(), FireworkEffect.builder()
                                                                                 .withColor(Color.LIME)
                                                                                 .with(FireworkEffect.Type.BALL)
                                                                                 .build());

            warlordsEntity.heal();

            if (warlordsEntity.getEntity() instanceof Player player) {
                player.getWorld().spigot().strikeLightningEffect(warlordsEntity.getLocation(), false);
                player.getInventory().setItem(5, BONE);
            }

            //gives 50% of max energy if player is less than half
            if (warlordsEntity.getEnergy() < warlordsEntity.getMaxEnergy() / 2) {
                warlordsEntity.setEnergy(warlordsEntity.getMaxEnergy() / 2);
            }

            if (undyingArmy.isPveMasterUpgrade()) {
                warlordsEntity.addSpeedModifier(warlordsEntity, "ARMY", 40, 16 * 20, "BASE");
            }

            undyingArmyCooldown.setNameAbbreviation("POPPED");
            undyingArmyCooldown.setTicksLeft(16 * 20);
            undyingArmyCooldown.setOnRemove(cooldownManager -> {
                if (warlordsEntity.getEntity() instanceof Player) {
                    if (cooldownManager.checkUndyingArmy(true)) {
                        ((Player) warlordsEntity.getEntity()).getInventory().remove(BONE);
                    }
                }
            });
            undyingArmyCooldown.addTriConsumer((cooldown, ticksLeft, ticksElapsed) -> {
                if (ticksElapsed % 20 == 0) {
                    warlordsEntity.addDamageInstance(
                            warlordsEntity,
                            "",
                            warlordsEntity.getMaxHealth() * (undyingArmy.getMaxHealthDamage() / 100f),
                            warlordsEntity.getMaxHealth() * (undyingArmy.getMaxHealthDamage() / 100f),
                            0,
                            100
                    );

                    if (undyingArmy.isPveMasterUpgrade() && ticksElapsed % 40 == 0) {
                        PlayerFilter.entitiesAround(warlordsEntity, 6, 6, 6)
                                    .aliveEnemiesOf(warlordsEntity)
                                    .forEach(enemy -> {
                                        float healthDamage = enemy.getMaxHealth() * .02f;
                                        healthDamage = DamageCheck.clamp(healthDamage);
                                        enemy.addDamageInstance(
                                                warlordsEntity,
                                                "Undying Army",
                                                458 + healthDamage,
                                                612 + healthDamage,
                                                0,
                                                100
                                        );
                                    });

                    }
                }
            });
            Bukkit.getPluginManager().callEvent(new WarlordsUndyingArmyPopEvent(warlordsEntity, undyingArmy));
            return true;
        }
        return false;
    }

    public boolean isArmyDead(WarlordsEntity warlordsPlayer) {
        return playersPopped.get(warlordsPlayer);
    }

    public void pop(WarlordsEntity warlordsPlayer) {
        playersPopped.put(warlordsPlayer, true);
    }

    public int getMaxHealthDamage() {
        return maxHealthDamage;
    }

    public void setMaxHealthDamage(int maxHealthDamage) {
        this.maxHealthDamage = maxHealthDamage;
    }

    public int playersArmied = 0;
    private final HashMap<WarlordsEntity, Boolean> playersPopped = new HashMap<>();
    private int radius = 12;
    private int tickDuration = 200;
    private int maxArmyAllies = 6;
    private int maxHealthDamage = 10;
    private float flatHealing = 50;
    private float missingHealing = 3.5f; // %

    public UndyingArmy(int maxHealthDamage) {
        this();
        this.maxHealthDamage = maxHealthDamage;
    }

    public UndyingArmy() {
        this(62.64f, 0);
    }

    public UndyingArmy(float cooldown, float startCooldown) {
        super("Undying Army", 0, 0, cooldown, 60, startCooldown);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("You may chain up to ")
                               .append(Component.text(maxArmyAllies, NamedTextColor.YELLOW))
                               .append(Component.text(" allies in a "))
                               .append(Component.text(radius, NamedTextColor.YELLOW))
                               .append(Component.text(" block radius to heal them for "))
                               .append(Component.text(format(flatHealing), NamedTextColor.GREEN))
                               .append(Component.text(" + "))
                               .append(Component.text(format(missingHealing), NamedTextColor.GREEN))
                               .append(Component.text(" every second. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."))
                               .append(Component.newline())
                               .append(Component.text("\nChained allies that take fatal damage will be revived with "))
                               .append(Component.text("100%", NamedTextColor.GREEN))
                               .append(Component.text(" of their max health and with at least"))
                               .append(Component.text("50%", NamedTextColor.YELLOW))
                               .append(Component.text(" max energy. Revived allies rapidly take "))
                               .append(Component.text(maxHealthDamage + "%", NamedTextColor.RED))
                               .append(Component.text(" of their max health as damage every second."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Armied", "" + playersArmied));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 2, 0.3f);
        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 0.9f);

        // particles
        Location loc = wp.getEyeLocation();
        loc.setPitch(0);
        loc.setYaw(0);
        Matrix4d matrix = new Matrix4d();
        for (int i = 0; i < 9; i++) {
            loc.setYaw(loc.getYaw() + 360F / 9F);
            matrix.updateFromLocation(loc);
            for (int c = 0; c < 30; c++) {
                double angle = c / 30D * Math.PI * 2;
                double width = 1.5;

                wp.getWorld().spawnParticle(
                        Particle.ENCHANTMENT_TABLE,
                        matrix.translateVector(wp.getWorld(), radius, Math.sin(angle) * width, Math.cos(angle) * width),
                        1,
                        0,
                        0.1,
                        0,
                        0,
                        null,
                        true
                );
            }

            for (int c = 0; c < 15; c++) {
                double angle = c / 15D * Math.PI * 2;
                double width = 0.6;

                wp.getWorld().spawnParticle(
                        Particle.SPELL,
                        matrix.translateVector(wp.getWorld(), radius, Math.sin(angle) * width, Math.cos(angle) * width),
                        1,
                        0,
                        0,
                        0,
                        0,
                        null,
                        true
                );
            }
        }

        new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                wp.getLocation(),
                radius,
                new CircumferenceEffect(Particle.VILLAGER_HAPPY, Particle.REDSTONE).particlesPerCircumference(2)
        ).playEffects();

        UndyingArmy tempUndyingArmy = new UndyingArmy(maxHealthDamage);
        tempUndyingArmy.setInPve(inPve);
        tempUndyingArmy.setPveMasterUpgrade(pveMasterUpgrade);
        int numberOfPlayersWithArmy = 0;
        for (WarlordsEntity teammate : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOf(wp)
                .closestWarlordPlayersFirst(wp.getLocation())
        ) {
            tempUndyingArmy.getPlayersPopped().put(teammate, false);
            if (teammate != wp) {
                playersArmied++;
                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                        .append(Component.text(" Your ", NamedTextColor.GRAY))
                        .append(Component.text("Undying Army", NamedTextColor.YELLOW))
                        .append(Component.text(" is now protecting " + teammate.getName() + ".", NamedTextColor.GRAY))
                );
                teammate.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                        .append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                        .append(Component.text("Undying Army", NamedTextColor.YELLOW))
                        .append(Component.text(" is now protecting you for ", NamedTextColor.GRAY))
                        .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                        .append(Component.text(" seconds.", NamedTextColor.GRAY))
                );
            }
            teammate.getCooldownManager().addRegularCooldown(
                    name,
                    "ARMY",
                    UndyingArmy.class,
                    tempUndyingArmy,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    tickDuration,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 20 != 0) {
                            return;
                        }
                        if (cooldown.getCooldownObject().isArmyDead(teammate)) {
                            return;
                        }
                        float healAmount = flatHealing + (teammate.getMaxHealth() - teammate.getCurrentHealth()) * (missingHealing / 100f);
                        teammate.addHealingInstance(wp, name, healAmount, healAmount, 0, 100);
                        teammate.playSound(teammate.getLocation(), "paladin.holyradiance.activation", 0.1f, 0.7f);
                        // Particles
                        Location playerLoc = teammate.getLocation();
                        playerLoc.add(0, 2.1, 0);
                        Location particleLoc = playerLoc.clone();
                        for (int i = 0; i < 1; i++) {
                            for (int j = 0; j < 10; j++) {
                                double angle = j / 10D * Math.PI * 2;
                                double width = 0.5;
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
                                        new Particle.DustOptions(Color.fromRGB(255, 255, 255), 1),
                                        true
                                );
                            }
                        }
                    })
            );

            numberOfPlayersWithArmy++;

            if (numberOfPlayersWithArmy >= maxArmyAllies) {
                break;
            }
        }

        if (pveMasterUpgrade2) {
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(wp, radius, radius, radius)
                    .aliveEnemiesOf(wp)
            ) {
                enemy.getCooldownManager().addCooldown(new RegularCooldown<>(
                        "Vengeful Army",
                        null,
                        UndyingArmy.class,
                        null,
                        wp,
                        CooldownTypes.ABILITY,
                        cooldownManager -> {
                            if (enemy.isAlive()) {
                                float healthDamage = enemy.getCurrentHealth() * .10f;
                                if (enemy instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof BossLike) {
                                    healthDamage = DamageCheck.clamp(healthDamage);
                                }
                                float damage = 1000 + healthDamage;
                                enemy.addDamageInstance(wp, "Vengeful Army", damage, damage, 0, 100);
                            } else {
                                new CooldownFilter<>(wp, PersistentCooldown.class)
                                        .filterCooldownClass(OrbsOfLife.class)
                                        .forEach(persistentCooldown -> {
                                            OrbsOfLife.spawnOrbs(wp, enemy, "Vengeful Army", persistentCooldown);
                                        });
                            }
                        },
                        10 * 20,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksElapsed % 20 != 0) {
                                return;
                            }
                            // Particles
                            Location playerLoc = enemy.getLocation();
                            playerLoc.add(0, 2.1, 0);
                            Location particleLoc = playerLoc.clone();
                            for (int i = 0; i < 1; i++) {
                                for (int j = 0; j < 10; j++) {
                                    double angle = j / 10D * Math.PI * 2;
                                    double width = 0.5;
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
                                            new Particle.DustOptions(Color.fromRGB(113, 13, 12), 1),
                                            true
                                    );
                                }
                            }

                        })
                ));
            }
        }

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new UndyingArmyBranch(abilityTree, this);
    }

    public HashMap<WarlordsEntity, Boolean> getPlayersPopped() {
        return playersPopped;
    }

    public void setMaxArmyAllies(int maxArmyAllies) {
        this.maxArmyAllies = maxArmyAllies;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public float getFlatHealing() {
        return flatHealing;
    }

    public void setFlatHealing(float flatHealing) {
        this.flatHealing = flatHealing;
    }

    public float getMissingHealing() {
        return missingHealing;
    }

    public void setMissingHealing(float missingHealing) {
        this.missingHealing = missingHealing;
    }
}

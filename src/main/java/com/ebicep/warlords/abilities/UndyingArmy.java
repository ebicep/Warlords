package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
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
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.revenant.UndyingArmyBranch;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UndyingArmy extends AbstractAbility implements OrangeAbilityIcon, Duration, Damages<UndyingArmy.DamageValues>, AbilityStats<UndyingArmy, UndyingArmy.UndyingArmyStats> {

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
                .filterCooldownClass(UndyingArmy.UndyingArmyData.class)
                .stream()
                .toList()
        ) {
            UndyingArmy.UndyingArmyData data = (UndyingArmy.UndyingArmyData) undyingArmyCooldown.getCooldownObject();
            if (data.isArmyDead(warlordsEntity)) {
                continue;
            }
            UndyingArmy undyingArmy = data.getUndyingArmy();
            data.pop(warlordsEntity);

            // Drops the flag when popped.
            FlagHolder.dropFlagForPlayer(warlordsEntity, false);

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
                    warlordsEntity.addInstance(InstanceBuilder
                            .melee()
                            .source(warlordsEntity)
                            .value(warlordsEntity.getMaxHealth() * (undyingArmy.getMaxHealthDamage() / 100f))
                    );

                    if (undyingArmy.isPveMasterUpgrade() && ticksElapsed % 40 == 0) {
                        PlayerFilter.entitiesAround(warlordsEntity, 6, 6, 6)
                                    .aliveEnemiesOf(warlordsEntity)
                                    .forEach(enemy -> {
                                        float healthDamage = enemy.getMaxHealth() * .02f;
                                        healthDamage = DamageCheck.clamp(healthDamage);
                                        enemy.addInstance(InstanceBuilder
                                                .damage()
                                                .ability(undyingArmy)
                                                .source(warlordsEntity)
                                                .min(undyingArmy.damageValues.relentlessArmy.getMinValue() + healthDamage)
                                                .max(undyingArmy.damageValues.relentlessArmy.getMaxValue() + healthDamage)
                                        );
                                    });

                    }
                }
            });
            Bukkit.getPluginManager().callEvent(new WarlordsUndyingArmyPopEvent(warlordsEntity, data));
            return true;
        }
        return false;
    }

    public int getMaxHealthDamage() {
        return maxHealthDamage;
    }

    public void setMaxHealthDamage(int maxHealthDamage) {
        this.maxHealthDamage = maxHealthDamage;
    }

    private final DamageValues damageValues = new DamageValues();
    private final UndyingArmyStats stats = new UndyingArmyStats();
    private int radius = 12;
    private int tickDuration = 200;
    private int maxArmyAllies = 6;
    private int maxHealthDamage = 10;
    private float flatHealing = 50;
    private float missingHealing = 3.5f; // %
    private int healPeriod = 20;

    public UndyingArmy(int maxHealthDamage) {
        this();
        this.maxHealthDamage = maxHealthDamage;
    }

    public UndyingArmy() {
        this(62, 0);
    }

    public UndyingArmy(float cooldown, float startCooldown) {
        super("Undying Army", cooldown, 60, startCooldown);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("You may chain up to ")
                .text(maxArmyAllies, NamedTextColor.BLUE)
                .text(" allies within ")
                .blocks(radius)
                .text(" to heal them for ")
                .text(format(flatHealing), NamedTextColor.GREEN)
                .text(" + ")
                .percent(missingHealing, NamedTextColor.GREEN)
                .text(" missing health every second. Lasts ")
                .durationTicks(tickDuration)
                .text(".")
                .emptyLine()
                .text("Chained allies that take fatal damage will be revived with ")
                .percent(100, NamedTextColor.GREEN)
                .text(" of their max health and ")
                .percent(50, NamedTextColor.YELLOW)
                .text(" of their max energy. Revived allies take ")
                .percent(maxHealthDamage, NamedTextColor.RED)
                .text(" of their max health as damage every second.")
                .build();
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

        UndyingArmyData data = new UndyingArmyData(this);
        List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();
        if (pveMasterUpgrade) {
            wp.doOnStaticAbility(RecklessCharge.class, ability -> ability.getCooldown().addMultiplicativeModifierAdd("Relentless Army", -.5f));
            wp.doOnStaticAbility(GroundSlamRevenant.class, ability -> ability.getCooldown().addMultiplicativeModifierAdd("Relentless Army", -.5f));
        }
        int numberOfPlayersWithArmy = 0;
        for (WarlordsEntity teammate : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOf(wp)
                .closestWarlordPlayersFirst(wp.getLocation())
        ) {
            data.getPlayersPopped().put(teammate, false);
            boolean isCaster = teammate != wp;
            if (isCaster) {
                stats.targetsArmied++;
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
                    UndyingArmyData.class,
                    data,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                        if (isCaster) {
                            modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                        }
                    },
                    tickDuration,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % healPeriod != 0) {
                            return;
                        }
                        if (cooldown.getCooldownObject().isArmyDead(teammate)) {
                            return;
                        }
                        float healAmount = flatHealing + (teammate.getMaxHealth() - teammate.getCurrentHealth()) * (missingHealing / 100f);
                        teammate.addInstance(InstanceBuilder
                                .healing()
                                .ability(this)
                                .source(wp)
                                .value(healAmount)
                        );
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
                                float damage = 2000 + healthDamage;
                                enemy.addInstance(InstanceBuilder
                                        .damage()
                                        .cause("Vengeful Army")
                                        .source(wp)
                                        .value(damage)
                                );
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

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public UndyingArmyStats getAbilityStats() {
        return stats;
    }

    public int getHealPeriod() {
        return healPeriod;
    }

    public void setHealPeriod(int healPeriod) {
        this.healPeriod = healPeriod;
    }

    public static class UndyingArmyData {

        private final UndyingArmy undyingArmy;
        private final HashMap<WarlordsEntity, Boolean> playersPopped = new HashMap<>();

        public UndyingArmyData(UndyingArmy undyingArmy) {
            this.undyingArmy = undyingArmy;
        }

        public UndyingArmy getUndyingArmy() {
            return undyingArmy;
        }

        public HashMap<WarlordsEntity, Boolean> getPlayersPopped() {
            return playersPopped;
        }

        public boolean isArmyDead(WarlordsEntity warlordsPlayer) {
            return playersPopped.get(warlordsPlayer);
        }

        public void pop(WarlordsEntity warlordsPlayer) {
            playersPopped.put(warlordsPlayer, true);
        }

    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValue relentlessArmy = new Value.RangedValue(458, 612);
        private final List<Value> values = List.of(relentlessArmy);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class UndyingArmyStats extends AbstractAbilityStats<UndyingArmy, UndyingArmyStats> {

        @Field("targets_armied")
        private int targetsArmied = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Armied", targetsArmied));
            return statsDisplay;
        }

        @Override
        public UndyingArmyStats merge(UndyingArmyStats other, int multiplier) {
            UndyingArmyStats stats = super.merge(other, multiplier);
            stats.targetsArmied = this.targetsArmied + other.targetsArmied * multiplier;
            return stats;
        }

        @Override
        public Class<UndyingArmyStats> getClazz() {
            return UndyingArmyStats.class;
        }

        @Override
        public UndyingArmyStats create() {
            return new UndyingArmyStats();
        }
    }
}
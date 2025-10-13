package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsUndyingArmyPopEvent;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;

public class UndyingArmy extends AbstractAbility implements OrangeAbilityIcon, Duration, Damages<UndyingArmy.DamageValues>, AbilityStats<UndyingArmy, UndyingArmy.UndyingArmyStats> {

    public static final ItemStack BONE = new ItemBuilder(Material.BONE)
            .name(Component.text("Instant Kill", NamedTextColor.RED))
            .lore(Component.text("Right-click this item to die"),
                    Component.text("instantly instead of waiting for"),
                    Component.text("the decay.")
            )
            .get();
    private final DamageValues damageValues = new DamageValues();
    private final UndyingArmyStats stats = new UndyingArmyStats();
    private int radius = 12;
    private int tickDuration = 200;
    private int maxArmyAllies = 6;
    private int maxHealthDamage = 10;
    private float flatHealing = 50;
    private int healPeriod = 20;

    public UndyingArmy() {
        super(AbstractAbilityBuilder.create("undyingArmy").pvp());
    }

    public UndyingArmy(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.radius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), int.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.maxArmyAllies = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxArmyAllies"), int.class);
        this.maxHealthDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxHealthDamage"), int.class);
        this.flatHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("flatHealing"), float.class);
        this.healPeriod = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("healPeriod"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
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
                EffectUtils.displayParticle(
                        Particle.ENCHANT,
                        matrix.translateVector(wp.getWorld(), radius, Math.sin(angle) * width, Math.cos(angle) * width),
                        1,
                        0,
                        0.1,
                        0,
                        0
                );
            }
            for (int c = 0; c < 15; c++) {
                double angle = c / 15D * Math.PI * 2;
                double width = 0.6;
                EffectUtils.displayParticle(
                        Particle.EFFECT,
                        matrix.translateVector(wp.getWorld(), radius, Math.sin(angle) * width, Math.cos(angle) * width),
                        1,
                        0,
                        0,
                        0,
                        0
                );
            }
        }
        new CircleEffect(wp.getGame(),
                wp.getTeam(),
                wp.getLocation(),
                radius,
                new CircumferenceEffect(Particle.HAPPY_VILLAGER, Particle.DUST).particlesPerCircumference(2)
        ).playEffects();
        UndyingArmyData data = getArmyData(wp);
        List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();
        if (pveMasterUpgrade) {
            wp.doOnStaticAbility(RecklessCharge.class, ability -> modifiers.add(ability.getCooldown().addMultiplicativeModifierAdd("Relentless Army", -.5f)));
            wp.doOnStaticAbility(GroundSlamRevenant.class, ability -> modifiers.add(ability.getCooldown().addMultiplicativeModifierAdd("Relentless Army", -.5f)));
        }
        int numberOfPlayersWithArmy = 0;
        for (WarlordsEntity teammate : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOf(wp)
                .excludingDummy()
                .excludingAlliedMobs()
                .closestWarlordPlayersFirst(wp.getLocation())
        ) {
            data.getPlayersPopped().put(teammate, false);
            boolean isCaster = teammate != wp;
            if (isCaster) {
                stats.targetsArmied++;
                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" Your ", NamedTextColor.GRAY))
                                                              .append(Component.text("Undying Army", NamedTextColor.YELLOW))
                                                              .append(Component.text(" is now protecting " + teammate.getName() + ".", NamedTextColor.GRAY)));
                teammate.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN.append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                                                                       .append(Component.text("Undying Army", NamedTextColor.YELLOW))
                                                                       .append(Component.text(" is now protecting you for ", NamedTextColor.GRAY))
                                                                       .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                                                                       .append(Component.text(" seconds.", NamedTextColor.GRAY)));
            }
            teammate.getCooldownManager().addCooldown(new RegularCooldown<>(
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
                        float healAmount = flatHealing;
                        teammate.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healAmount));
                        teammate.playSound(teammate.getLocation(), "paladin.holyradiance.activation", 0.1f, 0.7f);
                        // Particles
                        Location playerLoc = teammate.getLocation();
                        playerLoc.add(0, 2.1, 0);
                        Location particleLoc = playerLoc.clone();
                        EffectUtils.playCylinderAnimation(particleLoc, 10, 255, 255, 255, 10, 1, 1);
                    })
            ) {

                @Override
                protected Listener getListener() {
                    RegularCooldown<UndyingArmyData> undyingArmyCooldown = this;
                    return new Listener() {
                        @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
                        public void onDeathEvent(WarlordsDeathEvent event) {
                            WarlordsEntity warlordsEntity = event.getWarlordsEntity();
                            if (warlordsEntity != teammate) {
                                return;
                            }
                            if (data.isArmyDead(warlordsEntity)) {
                                return;
                            }
                            event.setCancelled(true);
                            warlordsEntity.heal();
                            data.pop(warlordsEntity);
                            // Sending the message + check if getFrom is self
                            EffectUtils.playFirework(warlordsEntity.getLocation(), FireworkEffect.builder().withColor(Color.LIME).with(FireworkEffect.Type.BALL).build());
                            EffectUtils.strikeLightning(warlordsEntity.getLocation(), false);
                            //gives 50% of max energy if player is less than half
                            if (warlordsEntity.getCurrentEnergy() < warlordsEntity.getMaxEnergy() / 2) {
                                warlordsEntity.setCurrentEnergy(warlordsEntity.getMaxEnergy() / 2);
                            }
                            if (isPveMasterUpgrade()) {
                                warlordsEntity.addSpeedModifier(warlordsEntity, "ARMY", 40, 16 * 20);
                            }
                            data.getOnPop().accept(undyingArmyCooldown, warlordsEntity);
                            Bukkit.getPluginManager().callEvent(new WarlordsUndyingArmyPopEvent(warlordsEntity, data));
                        }
                    };
                }
            });
            numberOfPlayersWithArmy++;
            if (numberOfPlayersWithArmy >= maxArmyAllies) {
                break;
            }
        }
        if (pveMasterUpgrade2) {
            for (WarlordsEntity enemy : PlayerFilter.entitiesAround(wp, radius, radius, radius).aliveEnemiesOf(wp)) {
                enemy.getCooldownManager().addCooldown(new RegularCooldown<>(
                        "Vengeful Army",
                        null,
                        UndyingArmy.class,
                        null,
                        wp,
                        CooldownTypes.ABILITY,
                        cooldownManager -> {
                            if (enemy.isAlive()) {
                                float healthDamage = enemy.getMaxHealth() * .10f;
                                if (enemy instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof BossLike) {
                                    healthDamage = DamageCheck.clamp(healthDamage);
                                }
                                float damage = 2000 + healthDamage;
                                enemy.addInstance(InstanceBuilder.damage().cause("Vengeful Army").source(wp).value(damage));
                            } else {
                                new CooldownFilter<>(wp, PersistentCooldown.class).filterCooldownClass(OrbsOfLife.class).forEach(persistentCooldown -> {
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
                            EffectUtils.playCylinderAnimation(particleLoc, 10, 113, 13, 12, 10, 1, 1);
                        })
                ));
            }
        }
        return true;
    }

    @Nonnull
    private UndyingArmyData getArmyData(WarlordsEntity wp) {
        UndyingArmyData data = new UndyingArmyData();
        data.setOnPop((undyingArmyCooldown, warlordsEntity) -> {
            // Drops the flag when popped.
            FlagHolder.dropFlagForPlayer(warlordsEntity, false);
            if (warlordsEntity.getEntity() instanceof Player player) {
                EffectUtils.strikeLightning(warlordsEntity.getLocation(), false);
                player.getInventory().setItem(5, BONE);
            }
            int armyDamage = Math.round(warlordsEntity.getMaxHealth() * (getMaxHealthDamage() / 100f));
            if (wp == warlordsEntity) {
                warlordsEntity.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                        .append(Component.text(" Your Undying Army revived you with temporary health. Fight until your death! Your health will decay by ",
                                NamedTextColor.LIGHT_PURPLE
                        ))
                        .append(Component.text(armyDamage, NamedTextColor.RED))
                        .append(Component.text(" every second.", NamedTextColor.LIGHT_PURPLE)));
            } else {
                warlordsEntity.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                        .append(Component.text(" " + wp.getName() + "'s Undying Army revived you with temporary health. Fight until your death! Your health will decay by ",
                                NamedTextColor.LIGHT_PURPLE
                        ))
                        .append(Component.text(armyDamage, NamedTextColor.RED))
                        .append(Component.text(" every second.", NamedTextColor.LIGHT_PURPLE)));
            }
            undyingArmyCooldown.setNameAbbreviation("POPPED");
            undyingArmyCooldown.setTicksLeft(16 * 20);
            undyingArmyCooldown.setOnRemove(cooldownManager -> {
                if (warlordsEntity.getEntity() instanceof Player player && !checkUndyingArmy(warlordsEntity, true, data)) {
                    player.getInventory().remove(BONE);
                }
            });
            undyingArmyCooldown.addTriConsumer((cooldown, ticksLeft, ticksElapsed) -> {
                if (ticksElapsed % 20 == 0) {
                    warlordsEntity.addInstance(InstanceBuilder.melee()
                                                              .source(warlordsEntity)
                                                              .value(warlordsEntity.getMaxHealth() * (getMaxHealthDamage() / 100f)));
                    if (isPveMasterUpgrade() && ticksElapsed % 40 == 0) {
                        PlayerFilter.entitiesAround(warlordsEntity, 6, 6, 6).aliveEnemiesOf(warlordsEntity).forEach(enemy -> {
                            float healthDamage = enemy.getMaxHealth() * .02f;
                            healthDamage = DamageCheck.clamp(healthDamage);
                            enemy.addInstance(InstanceBuilder
                                    .damage()
                                    .ability(UndyingArmy.this)
                                    .source(warlordsEntity)
                                    .min(damageValues.relentlessArmy.getMinValue() + healthDamage)
                                    .max(damageValues.relentlessArmy.getMaxValue() + healthDamage)
                                    .flags(InstanceFlags.IGNORE_DAMAGE_BOOST)
                            );
                        });
                    }
                }
            });
        });
        return data;
    }

    public int getMaxHealthDamage() {
        return maxHealthDamage;
    }

    public static boolean checkUndyingArmy(WarlordsEntity warlordsEntity, boolean popped, UndyingArmyData exclude) {
        for (UndyingArmyData data : new CooldownFilter<>(warlordsEntity.getCooldownManager(), RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(UndyingArmyData.class)
                .toList()
        ) {
            if (Objects.equals(data, exclude)) {
                continue;
            }
            if (popped) {
                //returns true if any undying is popped
                if (data.isArmyDead(warlordsEntity)) {
                    return true;
                }
            } else {
                //return true if theres any unpopped armies
                if (!data.isArmyDead(warlordsEntity)) {
                    return true;
                }
            }
        }
        //if popped returns false - all undying armies are not popped (there is no popped armies)
        //if !popped return false - all undying armies are popped (there is no unpopped armies)
        return false;
    }

    public void setMaxHealthDamage(int maxHealthDamage) {
        this.maxHealthDamage = maxHealthDamage;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("You may chain up to ")
                                               .text(maxArmyAllies, NamedTextColor.BLUE)
                                               .text(" allies within ")
                                               .blocks(radius)
                                               .text(" to heal them for ")
                                               .text(format(flatHealing), NamedTextColor.GREEN)
                                               .text(" health every second. Lasts ")
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
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new UndyingArmyBranch(abilityTree, this);
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public UndyingArmyStats getAbilityStats() {
        return stats;
    }

    public void setMaxArmyAllies(int maxArmyAllies) {
        this.maxArmyAllies = maxArmyAllies;
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

    public int getHealPeriod() {
        return healPeriod;
    }

    public void setHealPeriod(int healPeriod) {
        this.healPeriod = healPeriod;
    }

    public static class UndyingArmyData {

        private final HashMap<WarlordsEntity, Boolean> playersPopped = new HashMap<>();
        private BiConsumer<RegularCooldown<UndyingArmyData>, WarlordsEntity> onPop;

        public HashMap<WarlordsEntity, Boolean> getPlayersPopped() {
            return playersPopped;
        }

        public boolean isArmyDead(WarlordsEntity warlordsPlayer) {
            return playersPopped.get(warlordsPlayer);
        }

        public void pop(WarlordsEntity warlordsPlayer) {
            playersPopped.put(warlordsPlayer, true);
        }

        public BiConsumer<RegularCooldown<UndyingArmyData>, WarlordsEntity> getOnPop() {
            return onPop;
        }

        public void setOnPop(BiConsumer<RegularCooldown<UndyingArmyData>, WarlordsEntity> onPop) {
            this.onPop = onPop;
        }

    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValue relentlessArmy = new Value.RangedValue(458, 612);

        private List<Value> values = List.of(relentlessArmy);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.relentlessArmy = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("relentlessArmy"), Value.RangedValue.class);
            this.values = List.of(relentlessArmy);
        }

    }

    public static class UndyingArmyStats extends AbstractAbilityStats<UndyingArmy, UndyingArmyStats> {

        @Field("targets_armied")
        private int targetsArmied = 0;

        @Override
        public Class<UndyingArmyStats> getClazz() {
            return UndyingArmyStats.class;
        }

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
        public UndyingArmyStats create() {
            return new UndyingArmyStats();
        }

    }

}

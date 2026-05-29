package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.spiritguard.DeathsDebtBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class DeathsDebt extends AbstractTotem implements Duration, AbilityStats<DeathsDebt, DeathsDebt.DeathsDebtStats> {

    public static final ItemStack BLUE_TOTEM = new ItemStack(Material.COPPER_BLOCK);
    public static final ItemStack PURPLE_TOTEM = new ItemStack(Material.CHISELED_COPPER);
    private final DeathsDebtStats stats = new DeathsDebtStats();
    private int tickDuration = 120;
    private int respiteRadius = 10;
    private int debtRadius = 8;
    private float damagePercent = 15;
    private float delayedDamageTaken = 90;
    private int debtTickDuration = 120;

    public DeathsDebt() {
        super(AbstractAbilityBuilder.create("deathsDebt").pvp());
    }

    @Override
    protected void playSound(WarlordsEntity warlordsEntity, Location location) {
        Utils.playGlobalSound(location, "shaman.chainlightning.impact", 2, 2);
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return BLUE_TOTEM;
    }

    @Override
    protected void onActivation(WarlordsEntity wp, ArmorStand totemStand) {
        final int duration = tickDuration + (2 * Math.round(wp.getCurrentHealth() / wp.getMaxHealth())) * 20;
        CircleEffect circleEffect = new CircleEffect(wp,
                totemStand.getLocation().clone().add(0, 1.25, 0),
                respiteRadius,
                new CircumferenceEffect(Particle.EFFECT),
                new DoubleLineEffect(Particle.DUST)
        );
        if (wp.isInPve()) {
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(totemStand.getLocation(), respiteRadius, respiteRadius, respiteRadius)
                    .aliveEnemiesOf(wp)
                    .closestFirst(wp)
            ) {
                if (we instanceof WarlordsNPC) {
                    ((WarlordsNPC) we).getMob().setTarget(wp);
                }
            }
        }
        DeathsDebtData data = new DeathsDebtData(this, wp, totemStand);
        if (pveMasterUpgrade) {
            data.spawnRiteTotemVisual();
        }
        RegularCooldown<DeathsDebtData> spiritsRespiteCooldown = new RegularCooldown<>(
                "Spirits' Respite",
                "RESP",
                DeathsDebtData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManagerRespite -> {
                    Optional<RegularCooldown> cd = new CooldownFilter<>(cooldownManagerRespite, RegularCooldown.class).filterCooldownObject(data).findAny();
                    if (wp.isDead() || wp.getWorld() != totemStand.getWorld() || (cd.isPresent() && cd.get().hasTicksLeft())) {
                        return;
                    }
                    data.inDebt = true;
                    if (!data.playerInRadius) {
                        wp.sendMessage(Component.text("You walked outside your ", NamedTextColor.GRAY)
                                                .append(Component.text("Death's Debt ", NamedTextColor.LIGHT_PURPLE))
                                                .append(Component.text("radius.", NamedTextColor.GRAY)));
                    } else {
                        wp.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED.append(Component.text(" Spirit's Respite ", NamedTextColor.DARK_GREEN))
                                                                       .append(Component.text("delayed "))
                                                                       .append(Component.text(Math.round(data.delayedDamage), NamedTextColor.RED))
                                                                       .append(Component.text(" damage. Your debt must now be paid.")));
                    }
                    stats.totalDelayed += data.delayedDamage;
                    //beginning debt
                    wp.getCooldownManager().addCooldown(new RegularCooldown<>(name, "DEBT", DeathsDebtData.class, data, wp, CooldownTypes.ABILITY, cooldownManagerDebt -> {
                        if (wp.isDead()) {
                            return;
                        }

                        EffectUtils.strikeLightning(totemStand.getLocation(), false);
                        // Final enemy damage tick
                        AtomicInteger over5000DamageInstances = new AtomicInteger();

                        List<WarlordsEntity> enemies = PlayerFilter
                                .entitiesAround(totemStand, debtRadius, debtRadius - 1, debtRadius)
                                .aliveEnemiesOf(wp)
                                .toList();
                        for (WarlordsEntity totemTarget : enemies) {
                            stats.targetsDamaged++;
                            totemTarget.addInstance(InstanceBuilder
                                    .damage()
                                    .ability(this)
                                    .source(wp)
                                    .value(data.delayedDamage * damagePercent / 100f)
                            ).ifPresent(finalEvent -> {
                                if (finalEvent.getValue() > 5000) {
                                    over5000DamageInstances.getAndIncrement();
                                }
                                stats.totalDebtDamage += finalEvent.getValue();
                            });
                        }

                        if (pveMasterUpgrade2) {
                            List<Soulbinding> soulbindings = wp.getAbilitiesMatching(Soulbinding.class);
                            if (soulbindings.isEmpty()) {
                                Soulbinding soulbinding = new Soulbinding();
                                soulbinding.init(soulbinding.getBuilder());
                                soulbindings.add(soulbinding);
                            }
                            List<Soulbinding.SoulbindingData> soulbindingData = soulbindings.stream().map(soulbinding -> soulbinding.activeSoulbinding(wp)).toList();
                            float damageReduction = 1;
                            for (int i = 0; i < enemies.size() && i < 10; i++) {
                                WarlordsEntity enemy = enemies.get(i);
                                soulbindingData.forEach(soulbinding -> soulbinding.bindPlayer(wp, enemy));
                                damageReduction -= .025f;
                            }
                            float finalDamageReduction = damageReduction;
                            RegularCooldown<DeathsDebt> deathParadeCooldown = new RegularCooldown<>(
                                    "Death Parade",
                                    "PARADE",
                                    DeathsDebt.class,
                                    null,
                                    wp,
                                    CooldownTypes.BUFF,
                                    cooldownManager -> {
                                    },
                                    5 * 20
                            );
                            deathParadeCooldown.addModifier(
                                    Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE,
                                    (event, currentDamageValue) -> {
                                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, finalDamageReduction);
                                    }
                            );
                            deathParadeCooldown.addModifier(
                                    Modifier.ENERGY_GAIN_PER_HIT,
                                    energyGainPerTick -> energyGainPerTick.addModifier(FloatModifiable.ModifierType.ADDITIVE,
                                            "Death Parade", 30
                                    )
                            );
                            wp.getCooldownManager().addCooldown(deathParadeCooldown);
                        }
                        if (over5000DamageInstances.get() >= 5) {
                            ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.RETRIBUTION_OF_THE_DEAD);
                        }
                    }, cooldownManager -> {
                        data.removeRiteTotemVisuals();
                        totemStand.remove();
                    }, debtTickDuration, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 5 == 0) {
                            circleEffect.playEffects();
                        }
                        //6 self damage ticks
                        if (ticksElapsed % 20 == 0) {
                            data.onDebtTick();
                        }
                    })
                    ));
                    circleEffect.replaceEffects(e -> e instanceof DoubleLineEffect, new DoubleLineEffect(Particle.WITCH));
                    circleEffect.setRadius(debtRadius);
                    //blue to purple totem
                    if (pveMasterUpgrade) {
                        data.transitionRiteTotemVisual();
                    } else {
                        totemStand.getEquipment().setHelmet(PURPLE_TOTEM);
                    }
                },
                cooldownManager -> {
                    Optional<RegularCooldown> cd = new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterCooldownObject(data).findAny();
                    if (wp.isDead() || wp.getWorld() != totemStand.getWorld() || (cd.isPresent() && cd.get().hasTicksLeft())) {
                        data.removeRiteTotemVisuals();
                        totemStand.remove();
                    }
                },
                duration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (pveMasterUpgrade) {
                        data.tickRiteTotemVisual(ticksElapsed);
                    }

                    if (wp.getWorld() != totemStand.getWorld()) {
                        cooldown.setTicksLeft(0);
                        return;
                    }

                    if (ticksElapsed % 5 == 0) {
                        circleEffect.playEffects();
                    }

                    boolean isPlayerInRadius = wp.getLocation().distanceSquared(totemStand.getLocation()) < respiteRadius * respiteRadius;
                    if (!isPlayerInRadius && !data.inDebt) {
                        data.inDebt = true;
                        data.playerInRadius = false;
                        cooldown.setTicksLeft(0);
                        return;
                    }
                    if (ticksElapsed % 20 == 0) {
                        Utils.playGlobalSound(totemStand.getLocation(), "shaman.earthlivingweapon.impact", 2, 1.5F);
                        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" Spirit's Respite", NamedTextColor.DARK_GREEN)
                                                                                       .append(Component.text(" delayed ", NamedTextColor.GRAY))
                                                                                       .append(Component.text(Math.round(data.delayedDamage), NamedTextColor.RED))
                                                                                       .append(Component.text(" damage. ", NamedTextColor.GRAY))
                                                                                       .append(Component.text(Math.round(ticksLeft / 20f), NamedTextColor.GOLD))
                                                                                       .append(Component.text(" seconds left.", NamedTextColor.GRAY))));
                        if (wp.isInPve()) {
                            for (WarlordsEntity we : PlayerFilter.entitiesAround(totemStand.getLocation(), respiteRadius, respiteRadius, respiteRadius)
                                                                 .aliveEnemiesOf(wp)
                                                                 .closestFirst(wp)) {
                                if (we instanceof WarlordsNPC) {
                                    ((WarlordsNPC) we).getMob().setTarget(wp);
                                }
                            }
                        }
                    }
                })
        );
        spiritsRespiteCooldown.addModifier(Modifier.ON_INCOMING_DAMAGE, (event, currentDamageValue, isCrit) -> {
                    data.delayedDamage += currentDamageValue;

                    if (pveMasterUpgrade) {
                        int waves = data.addRiteDamage(currentDamageValue);
                        for (int i = 0; i < waves; i++) {
                            releaseRiteWave(wp, data);
                        }
                    }
                }
        );
        wp.getCooldownManager().addCooldown(spiritsRespiteCooldown);
        if (pveMasterUpgrade) {
            wp.addKnockbackModifier(wp, "Rite of the Unpaid", -50, spiritsRespiteCooldown);
        }
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.respiteRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("respiteRadius"), int.class);
        this.debtRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("debtRadius"), int.class);
        this.damagePercent = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damagePercent"), float.class);
        this.delayedDamageTaken = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("delayedDamageTaken"), float.class);
        this.debtTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("debtTickDuration"), int.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Spirits’ Respite", NamedTextColor.DARK_AQUA)
                                               .text(": Place down a totem that delays ")
                                               .percent(100, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" of incoming damage towards yourself " + (inPve ? " and takes aggro of nearby mobs" + "." : ".") + " Transforms into ")
                                               .text("Death’s Debt ", NamedTextColor.LIGHT_PURPLE)
                                               .text("after ")
                                               .append(formatRange(tickDuration / 20f, (tickDuration / 20f + 2), NamedTextColor.GOLD))
                                               .text(" seconds (increases with higher health), or when you exit its ")
                                               .blocks(respiteRadius)
                                               .text(" radius.")
                                               .emptyLine()
                                               .text("Death’s Debt", NamedTextColor.LIGHT_PURPLE)
                                               .text(": Take ")
                                               .percent(delayedDamageTaken, NamedTextColor.RED)
                                               .text(" of the damage delayed by ")
                                               .text("Spirits’ Respite ", NamedTextColor.DARK_AQUA)
                                               .text("over ")
                                               .durationTicks(debtTickDuration)
                                               .text(". The totem will heal nearby allies for ")
                                               .percent(damagePercent, NamedTextColor.GREEN)
                                               .text(" of all damage that you take. If you survive, deal ")
                                               .percent(damagePercent, NamedTextColor.RED)
                                               .text(" of the damage delayed to nearby enemies in a ")
                                               .blocks(debtRadius)
                                               .text(" radius.")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new DeathsDebtBranch(abilityTree, this);
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
    public DeathsDebtStats getAbilityStats() {
        return stats;
    }

    public float getDelayedDamageTaken() {
        return delayedDamageTaken;
    }

    public void setDelayedDamageTaken(float delayedDamageTaken) {
        this.delayedDamageTaken = delayedDamageTaken;
    }

    public int getDebtTickDuration() {
        return debtTickDuration;
    }

    public int getRespiteRadius() {
        return respiteRadius;
    }

    public void setRespiteRadius(int respiteRadius) {
        this.respiteRadius = respiteRadius;
    }

    public int getDebtRadius() {
        return debtRadius;
    }

    public void setDebtRadius(int debtRadius) {
        this.debtRadius = debtRadius;
    }

    public float getDamagePercent() {
        return damagePercent;
    }

    public void setDamagePercent(float damagePercent) {
        this.damagePercent = damagePercent;
    }

    private void releaseRiteWave(WarlordsEntity wp, DeathsDebtData data) {
        Location totemLocation = data.getArmorStand().getLocation();
        data.pulseRiteTotemVisual();
        Utils.playGlobalSound(totemLocation, "shaman.chainlightning.impact", 2, .65f);
        playRiteWaveEffects(wp, totemLocation);

        for (WarlordsEntity ally : PlayerFilter
                .entitiesAround(totemLocation, respiteRadius, respiteRadius, respiteRadius)
                .aliveTeammatesOf(wp)
        ) {
            reduceAllyCooldowns(ally);
            applyRiteAttackSpeed(wp, ally);
        }
    }

    private void reduceAllyCooldowns(WarlordsEntity ally) {
        ally.getAbilities().forEach(ability -> {
            if (ability.getCurrentCooldown() > 0) {
                ability.subtractCurrentCooldownForce(2);
                AbstractAbility.playCooldownReductionEffect(ally);
            }
        });
    }

    private void applyRiteAttackSpeed(WarlordsEntity wp, WarlordsEntity ally) {
        Optional<RegularCooldown> existingCooldown = new CooldownFilter<>(ally, RegularCooldown.class)
                .filterCooldownClass(RiteAttackSpeedData.class)
                .filterCooldownFrom(wp)
                .filterName("Rite of the Unpaid")
                .filter(RegularCooldown::hasTicksLeft)
                .findFirst();

        if (existingCooldown.isPresent()) {
            existingCooldown.get().setTicksLeft(5 * 20);
            return;
        }

        ally.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Rite of the Unpaid",
                "RITE",
                RiteAttackSpeedData.class,
                new RiteAttackSpeedData(),
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                5 * 20,
                List.of((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ally.getPveHitCooldown() > 0) {
                        ally.setPveHitCooldown(Math.max(0, ally.getPveHitCooldown() - 2));
                    }
                })
        ));
    }

    private void playRiteWaveEffects(WarlordsEntity wp, Location totemLocation) {
        for (int radius = 2; radius <= respiteRadius; radius += 2) {
            for (int i = 0; i < 48; i++) {
                double angle = Math.PI * 2 * i / 48;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;

                EffectUtils.displayParticle(
                        Particle.SOUL_FIRE_FLAME,
                        totemLocation.clone().add(x, .2, z),
                        1,
                        0,
                        0,
                        0,
                        0
                );
            }
        }

        EffectUtils.displayParticle(
                Particle.WITCH,
                totemLocation.clone().add(0, 1.2, 0),
                36,
                .6,
                .6,
                .6,
                .04
        );

        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                .append(Component.text(" Rite of the Unpaid ", NamedTextColor.DARK_PURPLE))
                .append(Component.text("released a ritual wave.", NamedTextColor.GRAY)));
    }

    private static class RiteAttackSpeedData {
    }

    public static class DeathsDebtData extends TotemData<DeathsDebt> {

        private boolean inDebt = false;
        private boolean playerInRadius = true;
        private float delayedDamage = 0;
        private float riteDamageProgress = 0;

        private final List<Entity> riteVisuals = new ArrayList<>();
        private ItemDisplay riteCore;
        private ItemDisplay riteSkull;
        private ItemDisplay riteFocus;

        public DeathsDebtData(DeathsDebt totem, WarlordsEntity owner, ArmorStand armorStand) {
            super(totem, owner, armorStand);
        }

        public void spawnRiteTotemVisual() {
            removeRiteTotemVisuals();

            armorStand.setVisible(false);
            armorStand.getEquipment().setHelmet(null);

            Location center = armorStand.getLocation().clone();

            addRiteItem(
                    center.clone().add(0, .65, 0),
                    new ItemStack(Material.CRYING_OBSIDIAN),
                    new Vector3f(1.8f, 1.8f, 1.8f),
                    0,
                    0
            );

            riteCore = addRiteItem(
                    center.clone().add(0, 1.15, 0),
                    BLUE_TOTEM,
                    new Vector3f(1.45f, 1.45f, 1.45f),
                    45,
                    0
            );

            riteSkull = addRiteItem(
                    center.clone().add(0, 1.85, 0),
                    new ItemStack(Material.WITHER_SKELETON_SKULL),
                    new Vector3f(1.05f, 1.05f, 1.05f),
                    180,
                    0
            );

            riteFocus = addRiteItem(
                    center.clone().add(0, 2.35, 0),
                    new ItemStack(Material.NETHER_STAR),
                    new Vector3f(.75f, .75f, .75f),
                    0,
                    0
            );

            for (int i = 0; i < 4; i++) {
                double angle = Math.PI * 2 * i / 4;
                double x = Math.cos(angle) * 1.35;
                double z = Math.sin(angle) * 1.35;

                addRiteItem(
                        center.clone().add(x, .8, z),
                        new ItemStack(Material.SOUL_LANTERN),
                        new Vector3f(.65f, .65f, .65f),
                        (float) Math.toDegrees(angle),
                        0
                );
            }

            for (int i = 0; i < 8; i++) {
                double angle = Math.PI * 2 * i / 8;
                double x = Math.cos(angle) * 2.15;
                double z = Math.sin(angle) * 2.15;

                addRiteItem(
                        center.clone().add(x, .38, z),
                        new ItemStack(Material.RED_CANDLE),
                        new Vector3f(.45f, .45f, .45f),
                        (float) Math.toDegrees(angle),
                        0
                );
            }
        }

        public void transitionRiteTotemVisual() {
            if (riteCore != null && riteCore.isValid()) {
                riteCore.setItemStack(PURPLE_TOTEM);
            }

            if (riteFocus != null && riteFocus.isValid()) {
                riteFocus.setItemStack(new ItemStack(Material.ECHO_SHARD));
            }

            armorStand.getWorld().spawnParticle(
                    Particle.WITCH,
                    armorStand.getLocation().clone().add(0, 1.3, 0),
                    48,
                    .8,
                    .8,
                    .8,
                    .04
            );
        }

        public void tickRiteTotemVisual(int ticksElapsed) {
            float coreYaw = (ticksElapsed * 3) % 360;
            float skullYaw = 180 - (ticksElapsed * 5) % 360;
            float focusYaw = (ticksElapsed * 8) % 360;

            if (riteCore != null && riteCore.isValid()) {
                riteCore.setRotation(coreYaw, 0);
            }

            if (riteSkull != null && riteSkull.isValid()) {
                Location skullLocation = armorStand.getLocation().clone().add(0, 1.85 + Math.sin(ticksElapsed / 10d) * .08, 0);
                skullLocation.setYaw(skullYaw);
                riteSkull.teleport(skullLocation);
            }

            if (riteFocus != null && riteFocus.isValid()) {
                Location focusLocation = armorStand.getLocation().clone().add(0, 2.35 + Math.sin(ticksElapsed / 8d) * .12, 0);
                focusLocation.setYaw(focusYaw);
                riteFocus.teleport(focusLocation);
            }
        }

        public void pulseRiteTotemVisual() {
            Location center = armorStand.getLocation().clone();

            center.getWorld().spawnParticle(
                    Particle.SOUL_FIRE_FLAME,
                    center.clone().add(0, 1.3, 0),
                    56,
                    .8,
                    .8,
                    .8,
                    .05
            );

            center.getWorld().spawnParticle(
                    Particle.WITCH,
                    center.clone().add(0, 1.7, 0),
                    32,
                    .55,
                    .55,
                    .55,
                    .04
            );
        }

        public void removeRiteTotemVisuals() {
            new ArrayList<>(riteVisuals).forEach(Entity::remove);
            riteVisuals.clear();

            riteCore = null;
            riteSkull = null;
            riteFocus = null;
        }

        public int addRiteDamage(float damage) {
            if (damage <= 0) {
                return 0;
            }

            riteDamageProgress += damage;

            int waves = (int) (riteDamageProgress / 10000);
            if (waves <= 0) {
                return 0;
            }

            riteDamageProgress %= 10000;
            return waves;
        }

        private ItemDisplay addRiteItem(Location location, ItemStack itemStack, Vector3f scale, float yaw, float pitch) {
            location.setYaw(yaw);
            location.setPitch(pitch);

            ItemDisplay itemDisplay = location.getWorld().spawn(location, ItemDisplay.class, display -> {
                display.setItemStack(itemStack);
                display.setBillboard(Display.Billboard.FIXED);
                display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
                display.setViewRange(64);
                display.setPersistent(false);
                display.setBrightness(new Display.Brightness(15, 15));
                display.setTransformation(new Transformation(
                        new Vector3f(),
                        new Quaternionf(),
                        scale,
                        new Quaternionf()
                ));
            });

            riteVisuals.add(itemDisplay);
            return itemDisplay;
        }

        public void onDebtTick() {
            Utils.playGlobalSound(armorStand.getLocation(), "shaman.lightningbolt.impact", 2, 1.5F);
            float selfDamageInPercentPerSecond = convertToPercent(totem.getDelayedDamageTaken() / (totem.getDebtTickDuration() / 20f));
            float damage = delayedDamage * selfDamageInPercentPerSecond;
            float debtTrueDamage = (float) (damage * Math.pow(.8, (int) new CooldownFilter<>(owner, RegularCooldown.class).filterCooldownClass(SpiritLink.class).stream().count()));
            owner.addInstance(InstanceBuilder.melee().source(owner).value(debtTrueDamage));

            for (WarlordsEntity allyTarget : PlayerFilter.entitiesAround(armorStand, totem.debtRadius, totem.debtRadius - 1, totem.debtRadius).aliveTeammatesOf(owner)) {
                totem.stats.targetsHealed++;
                allyTarget.addInstance(InstanceBuilder.healing().ability(totem).source(owner).value(damage * convertToPercent(totem.damagePercent)));
            }

            for (Repentance repentance : owner.getAbilitiesMatching(Repentance.class)) {
                repentance.addToPool(debtTrueDamage);
            }
        }

    }

    public static class DeathsDebtStats extends AbstractAbilityStats<DeathsDebt, DeathsDebtStats> {

        @Field("targets_damaged")
        private int targetsDamaged = 0;

        @Field("targets_healed")
        private int targetsHealed = 0;

        @Field("total_delayed")
        private float totalDelayed = 0;

        @Field("total_debt_damage")
        private float totalDebtDamage = 0;

        @Override
        public Class<DeathsDebtStats> getClazz() {
            return DeathsDebtStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Damaged", targetsDamaged));
            statsDisplay.add(new AbilityStatDisplay("Targets Healed", targetsHealed));
            statsDisplay.add(new AbilityStatDisplay("Total Delayed", totalDelayed));
            statsDisplay.add(new AbilityStatDisplay("Total Debt Damage", totalDebtDamage));
            return statsDisplay;
        }

        @Override
        public DeathsDebtStats merge(DeathsDebtStats other, int multiplier) {
            DeathsDebtStats stats = super.merge(other, multiplier);
            stats.targetsDamaged = this.targetsDamaged + other.targetsDamaged * multiplier;
            stats.targetsHealed = this.targetsHealed + other.targetsHealed * multiplier;
            stats.totalDelayed = this.totalDelayed + other.totalDelayed * multiplier;
            stats.totalDebtDamage = this.totalDebtDamage + other.totalDebtDamage * multiplier;
            return stats;
        }

        @Override
        public DeathsDebtStats create() {
            return new DeathsDebtStats();
        }

    }

}

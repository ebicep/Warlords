package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractTotem;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.spiritguard.DeathsDebtBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DeathsDebt extends AbstractTotem implements Duration {

    public int playersDamaged = 0;
    public int playersHealed = 0;

    private int tickDuration = 120;
    private float delayedDamage = 0;
    private int respiteRadius = 10;
    private int debtRadius = 8;
    private float damagePercent = 15;
    private float delayedDamageTaken = 90;
    private int debtTickDuration = 120;
    private boolean inDebt = false;
    private boolean playerInRadius = true;

    public DeathsDebt() {
        super("Death's Debt", 0, 0, 60f + 10.49f, 20, 0, 100);
    }

    public DeathsDebt(ArmorStand totem, WarlordsEntity owner) {
        super("Death's Debt", 0, 0, 60f + 10.49f, 20, 0, 100, totem, owner);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Spirits’ Respite", NamedTextColor.DARK_GREEN)
                               .append(Component.text(": Place down a totem that delays "))
                               .append(Component.text("100%", NamedTextColor.RED))
                               .append(Component.text(" of incoming damage towards yourself " +
                                       (inPve ? " and takes aggro of nearby mobs" + "." : ".") + "Transforms into "))
                               .append(Component.text("Death’s Debt ", NamedTextColor.LIGHT_PURPLE))
                               .append(Component.text("after "))
                               .append(formatRange(tickDuration / 20f, (tickDuration / 20f + 2), NamedTextColor.GOLD))
                               .append(Component.text(" seconds (increases with higher health), or when you exit its "))
                               .append(Component.text(respiteRadius, NamedTextColor.YELLOW))
                               .append(Component.text(" block radius."))
                               .append(Component.text("\n\nDeath’s Debt", NamedTextColor.LIGHT_PURPLE))
                               .append(Component.text(": Take "))
                               .append(Component.text(format(delayedDamageTaken) + "%", NamedTextColor.RED))
                               .append(Component.text(" of the damage delayed by "))
                               .append(Component.text("Spirits’ Respite ", NamedTextColor.DARK_GREEN))
                               .append(Component.text("over "))
                               .append(Component.text("6", NamedTextColor.GOLD))
                               .append(Component.text(" seconds. The totem will heal nearby allies for "))
                               .append(Component.text("15%", NamedTextColor.GREEN))
                               .append(Component.text(" of all damage that you take. If you survive, deal "))
                               .append(Component.text(format(damagePercent) + "%", NamedTextColor.RED))
                               .append(Component.text(" of the damage delayed to nearby enemies in a "))
                               .append(Component.text(debtRadius, NamedTextColor.YELLOW))
                               .append(Component.text(" block radius."));
//                               .append(Component.text("\n\nSuccessful Soulbind procs on enemies add "))
//                               .append(Component.text("0.5", NamedTextColor.GOLD))
//                               .append(Component.text(" seconds to your totem duration. (Cap of "))
//                               .append(Component.text("6", NamedTextColor.GOLD))
//                               .append(Component.text(" seconds)"));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Damaged", "" + playersDamaged));
        info.add(new Pair<>("Players Healed", "" + playersHealed));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new DeathsDebtBranch(abilityTree, this);
    }

    @Override
    protected void playSound(WarlordsEntity warlordsEntity, Location location) {
        //TODO find the right sound - this aint right chief
        Utils.playGlobalSound(location, "shaman.chainlightning.impact", 2, 2);
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return new ItemStack(Material.JUNGLE_FENCE_GATE);
    }

    @Override
    protected void onActivation(WarlordsEntity wp, ArmorStand totemStand) {
        final int duration = tickDuration + (2 * Math.round(wp.getCurrentHealth() / wp.getMaxHealth())) * 20;

        CircleEffect circleEffect = new CircleEffect(
                wp,
                totemStand.getLocation().clone().add(0, 1.25, 0),
                respiteRadius,
                new CircumferenceEffect(Particle.SPELL),
                new DoubleLineEffect(Particle.REDSTONE)
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

        DeathsDebt tempDeathsDebt = new DeathsDebt(totemStand, wp);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Spirits' Respite",
                "RESP",
                DeathsDebt.class,
                tempDeathsDebt,
                wp,
                CooldownTypes.ABILITY,
                cooldownManagerRespite -> {
                    Optional<RegularCooldown> cd = new CooldownFilter<>(cooldownManagerRespite, RegularCooldown.class)
                            .filterCooldownObject(tempDeathsDebt)
                            .findAny();
                    if (wp.isDead() || wp.getWorld() != totemStand.getWorld() || (cd.isPresent() && cd.get().hasTicksLeft())) {
                        return;
                    }

                    tempDeathsDebt.setInDebt(true);

                    if (!tempDeathsDebt.isPlayerInRadius()) {
                        wp.sendMessage(Component.text("You walked outside your ", NamedTextColor.GRAY)
                                                .append(Component.text("Death's Debt ", NamedTextColor.LIGHT_PURPLE))
                                                .append(Component.text("radius.", NamedTextColor.GRAY)));
                    } else {
                        wp.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED.append(Component.text(" Spirit's Respite ", NamedTextColor.DARK_GREEN))
                                                                       .append(Component.text("delayed "))
                                                                       .append(Component.text(Math.round(tempDeathsDebt.getDelayedDamage()), NamedTextColor.RED))
                                                                       .append(Component.text(" damage. Your debt must now be paid."))
                        );
                    }

                    //beginning debt
                    wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                            name,
                            "DEBT",
                            DeathsDebt.class,
                            tempDeathsDebt,
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManagerDebt -> {
                                if (wp.isDead()) {
                                    return;
                                }

                                wp.getWorld().spigot().strikeLightningEffect(totemStand.getLocation(), false);
                                // Final enemy damage tick
                                AtomicInteger over5000DamageInstances = new AtomicInteger();
                                List<WarlordsEntity> enemies = PlayerFilter
                                        .entitiesAround(totemStand, debtRadius, debtRadius - 1, debtRadius)
                                        .aliveEnemiesOf(wp)
                                        .toList();
                                for (WarlordsEntity totemTarget : enemies) {
                                    playersDamaged++;
                                    totemTarget.addInstance(InstanceBuilder
                                            .damage()
                                            .ability(this)
                                            .source(wp)
                                            .value(tempDeathsDebt.getDelayedDamage() * .15f)
                                    ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                        if (warlordsDamageHealingFinalEvent.getValue() > 5000) {
                                            over5000DamageInstances.getAndIncrement();
                                        }
                                    });
                                }
                                if (pveMasterUpgrade2) {
                                    List<Soulbinding> soulbindings = wp.getAbilitiesMatching(Soulbinding.class);
                                    if (soulbindings.isEmpty()) {
                                        soulbindings.add(new Soulbinding());
                                    }
                                    soulbindings = soulbindings
                                            .stream()
                                            .map(soulbinding -> soulbinding.activeSoulbinding(wp))
                                            .collect(Collectors.toList());
                                    float damageReduction = 1;
                                    for (int i = 0; i < enemies.size() && i < 6; i++) {
                                        WarlordsEntity enemy = enemies.get(i);
                                        soulbindings.forEach(soulbinding -> soulbinding.bindPlayer(wp, enemy));
                                        damageReduction -= .025;
                                    }
                                    float finalDamageReduction = damageReduction;
                                    wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                            "Death Parade",
                                            "PARADE",
                                            DeathsDebt.class,
                                            null,
                                            wp,
                                            CooldownTypes.BUFF,
                                            cooldownManager -> {},
                                            5 * 20
                                    ) {
                                        @Override
                                        public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                            return currentDamageValue * finalDamageReduction;
                                        }
                                    });
                                }
                                if (over5000DamageInstances.get() >= 5) {
                                    ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.RETRIBUTION_OF_THE_DEAD);
                                }
                            },
                            cooldownManager -> {
                                totemStand.remove();
                            },
                            debtTickDuration,
                            Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                if (ticksElapsed % 5 == 0) {
                                    circleEffect.playEffects();
                                }
                                //6 self damage ticks
                                if (ticksElapsed % 20 == 0) {
                                    onDebtTick(wp, totemStand, tempDeathsDebt);
                                }
                            })
                    ));
                    circleEffect.replaceEffects(e -> e instanceof DoubleLineEffect, new DoubleLineEffect(Particle.SPELL_WITCH));
                    circleEffect.setRadius(debtRadius);

                    //blue to purple totem
                    totemStand.getEquipment().setHelmet(new ItemStack(Material.DARK_OAK_FENCE_GATE));
                },
                cooldownManager -> {
                    Optional<RegularCooldown> cd = new CooldownFilter<>(cooldownManager, RegularCooldown.class)
                            .filterCooldownObject(tempDeathsDebt)
                            .findAny();
                    if (wp.isDead() || wp.getWorld() != totemStand.getWorld() || (cd.isPresent() && cd.get().hasTicksLeft())) {
                        totemStand.remove();
                    }
                },
                duration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (wp.getWorld() != totemStand.getWorld()) {
                        cooldown.setTicksLeft(0);
                        return;
                    }

                    if (ticksElapsed % 5 == 0) {
                        circleEffect.playEffects();
                    }

                    boolean isPlayerInRadius = wp.getLocation().distanceSquared(totemStand.getLocation()) < respiteRadius * respiteRadius;
                    if (!isPlayerInRadius && !tempDeathsDebt.isInDebt()) {
                        tempDeathsDebt.setInDebt(true);
                        tempDeathsDebt.setPlayerInRadius(false);
                        cooldown.setTicksLeft(0);
                        return;
                    }

                    if (ticksElapsed % 20 == 0) {
                        Utils.playGlobalSound(totemStand.getLocation(), "shaman.earthlivingweapon.impact", 2, 1.5F);
                        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                .append(Component.text(" Spirit's Respite", NamedTextColor.DARK_GREEN)
                                                 .append(Component.text(" delayed ", NamedTextColor.GRAY))
                                                 .append(Component.text(Math.round(tempDeathsDebt.getDelayedDamage()), NamedTextColor.RED))
                                                 .append(Component.text(" damage. ", NamedTextColor.GRAY))
                                                 .append(Component.text(Math.round(ticksLeft / 20f), NamedTextColor.GOLD))
                                                 .append(Component.text(" seconds left.", NamedTextColor.GRAY))
                                ));

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
                    }
                })
        ) {
            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                tempDeathsDebt.addDelayedDamage(currentDamageValue);
            }

            @Override
            public void multiplyKB(Vector currentVector) {
                if (pveMasterUpgrade) {
                    currentVector.multiply(0.2);
                }
            }
        });
    }

    public boolean isPlayerInRadius() {
        return playerInRadius;
    }

    public float getDelayedDamage() {
        return delayedDamage;
    }

    public void onDebtTick(WarlordsEntity wp, ArmorStand totemStand, DeathsDebt tempDeathsDebt) {
        Utils.playGlobalSound(totemStand.getLocation(), "shaman.lightningbolt.impact", 2, 1.5F);

        // 100% of damage over 6 seconds
        float selfDamageInPercentPerSecond = convertToPercent(tempDeathsDebt.getDelayedDamageTaken() / tempDeathsDebt.getDebtTickDuration() / 20);
        float damage = (tempDeathsDebt.getDelayedDamage() * selfDamageInPercentPerSecond);
        float debtTrueDamage = (float) (damage * Math.pow(.8,
                (int) new CooldownFilter<>(wp, RegularCooldown.class).filterCooldownClass(SpiritLink.class).stream().count()
        ));
        // Player damage
        wp.addInstance(InstanceBuilder
                .melee()
                .source(wp)
                .value(debtTrueDamage)
        );
        // Teammate heal
        for (WarlordsEntity allyTarget : PlayerFilter
                .entitiesAround(totemStand, debtRadius, debtRadius - 1, debtRadius)
                .aliveTeammatesOf(wp)
        ) {
            playersHealed++;
            allyTarget.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(wp)
                    .value(damage * convertToPercent(damagePercent))
            );
        }

        // Adding damage to Repentance Pool
        // @see Repentance.class
        for (Repentance repentance : wp.getAbilitiesMatching(Repentance.class)) {
            repentance.addToPool(debtTrueDamage);
        }
    }

    public boolean isInDebt() {
        return inDebt;
    }

    public void addDelayedDamage(float delayedDamage) {
        this.delayedDamage += delayedDamage;
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

    public void setInDebt(boolean inDebt) {
        this.inDebt = inDebt;
    }

    public void setPlayerInRadius(boolean playerInRadius) {
        this.playerInRadius = playerInRadius;
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

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }
}

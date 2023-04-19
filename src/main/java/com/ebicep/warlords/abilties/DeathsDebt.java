package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractTotemBase;
import com.ebicep.warlords.abilties.internal.Duration;
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
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class DeathsDebt extends AbstractTotemBase implements Duration {

    public int playersDamaged = 0;
    public int playersHealed = 0;

    private int tickDuration = 120;
    private float delayedDamage = 0;
    private int respiteRadius = 10;
    private int debtRadius = 8;
    private float damagePercent = 15;
    private float selfDamageInPercentPerSecond = .1667f;
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
        description = "§2Spirits’ Respite§7: Place down a totem that delays §c100% §7of incoming damage towards yourself. Transforms into §dDeath’s Debt " +
                "§7after §6" + format(tickDuration / 20f) + " §7- §6" + format((tickDuration / 20f + 2)) + " §7seconds (increases with higher health), or when you exit its §e" + respiteRadius + " §7block radius." +
                "\n\n§dDeath’s Debt§7: Take §c" + Math.round((selfDamageInPercentPerSecond * 6) * 100) +
                "% §7of the damage delayed by §2Spirit's Respite §7over §66 §7seconds. The totem will heal nearby allies for §a15% §7of all damage " +
                "that you take. If you survive, deal §c" + format(damagePercent) +
                "% §7of the damage delayed to nearby enemies." +
                "\n\nSuccessful Soulbind procs on enemies add §60.5 §7seconds to your totem duration. (Cap of §66 §7seconds)";
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
    protected void playSound(Player player, Location location) {
        //TODO find the right sound - this aint right chief
        Utils.playGlobalSound(location, "shaman.chainlightning.impact", 2, 2);
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return new ItemStack(Material.JUNGLE_FENCE_GATE);
    }

    @Override
    protected void onActivation(WarlordsEntity wp, Player player, ArmorStand totemStand) {
        final int duration = tickDuration + (2 * Math.round(wp.getHealth() / wp.getMaxHealth())) * 20;

        CircleEffect circle = new CircleEffect(
                wp,
                totemStand.getLocation().clone().add(0, 1.25, 0),
                respiteRadius,
                new CircumferenceEffect(Particle.SPELL),
                new DoubleLineEffect(Particle.REDSTONE)
        );
        BukkitTask effectTask = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), circle::playEffects, 0, 1);

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
                "Spirits Respite",
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
                        wp.sendMessage("§7You walked outside your §dDeath's Debt §7radius");
                    } else {
                        wp.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED + " §2Spirit's Respite §7delayed §c" +
                                Math.round(tempDeathsDebt.getDelayedDamage()) + " §7damage. §dYour debt must now be paid."
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
                                for (WarlordsEntity totemTarget : PlayerFilter
                                        .entitiesAround(totemStand, debtRadius, debtRadius - 1, debtRadius)
                                        .aliveEnemiesOf(wp)
                                ) {
                                    playersDamaged++;
                                    totemTarget.addDamageInstance(
                                            wp,
                                            name,
                                            tempDeathsDebt.getDelayedDamage() * .15f,
                                            tempDeathsDebt.getDelayedDamage() * .15f,
                                            critChance,
                                            critMultiplier, false
                                    ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                        if (warlordsDamageHealingFinalEvent.getValue() > 5000) {
                                            over5000DamageInstances.getAndIncrement();
                                        }
                                    });
                                }
                                if (over5000DamageInstances.get() >= 5) {
                                    ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.RETRIBUTION_OF_THE_DEAD);
                                }
                            },
                            cooldownManager -> {
                                totemStand.remove();
                                effectTask.cancel();
                            },
                            6 * 20,
                            Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                //6 self damage ticks
                                if (ticksElapsed % 20 == 0) {
                                    onDebtTick(wp, totemStand, tempDeathsDebt);
                                }
                            })
                    ));
                    circle.replaceEffects(e -> e instanceof DoubleLineEffect, new DoubleLineEffect(Particle.SPELL_WITCH));
                    circle.setRadius(debtRadius);

                    //blue to purple totem
                    totemStand.getEquipment().setHelmet(new ItemStack(Material.DARK_OAK_FENCE_GATE));
                },
                cooldownManager -> {
                    Optional<RegularCooldown> cd = new CooldownFilter<>(cooldownManager, RegularCooldown.class)
                            .filterCooldownObject(tempDeathsDebt)
                            .findAny();
                    if (wp.isDead() || wp.getWorld() != totemStand.getWorld() || (cd.isPresent() && cd.get().hasTicksLeft())) {
                        totemStand.remove();
                        effectTask.cancel();
                    }
                },
                duration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (wp.getWorld() != totemStand.getWorld()) {
                        cooldown.setTicksLeft(0);
                        return;
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
                        wp.sendMessage(ChatColor.GREEN + WarlordsEntity.GIVE_ARROW_GREEN + " §2Spirit's Respite §7delayed §c" +
                                Math.round(tempDeathsDebt.getDelayedDamage()) + " §7damage. §6" +
                                Math.round(ticksLeft / 20f) + " §7seconds left."
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
                    }
                })
        ) {
            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                tempDeathsDebt.addDelayedDamage(currentDamageValue);
            }

            @Override
            public void multiplyKB(Vector currentVector) {
                if (pveUpgrade) {
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
        float damage = (tempDeathsDebt.getDelayedDamage() * getSelfDamageInPercentPerSecond());
        float debtTrueDamage = (float) (damage * Math.pow(.8,
                (int) new CooldownFilter<>(wp, RegularCooldown.class).filterCooldownClass(SpiritLink.class).stream().count()
        ));
        // Player damage
        wp.addDamageInstance(
                wp,
                "",
                debtTrueDamage,
                debtTrueDamage,
                critChance,
                critMultiplier,
                false
        );
        // Teammate heal
        for (WarlordsEntity allyTarget : PlayerFilter
                .entitiesAround(totemStand, debtRadius, debtRadius - 1, debtRadius)
                .aliveTeammatesOf(wp)
        ) {
            playersHealed++;
            allyTarget.addHealingInstance(
                    wp,
                    name,
                    damage * (damagePercent / 100f),
                    damage * (damagePercent / 100f),
                    critChance,
                    critMultiplier,
                    false,
                    false
            );
        }
        // Adding damage to Repentance Pool
        // @see Repentance.class
        if (wp.getBlueAbility() instanceof Repentance) {
            ((Repentance) wp.getBlueAbility()).addToPool(debtTrueDamage);
        }
    }

    public boolean isInDebt() {
        return inDebt;
    }

    public void addDelayedDamage(float delayedDamage) {
        this.delayedDamage += delayedDamage;
    }

    public float getSelfDamageInPercentPerSecond() {
        return selfDamageInPercentPerSecond;
    }

    public void setSelfDamageInPercentPerSecond(float selfDamageInPercentPerSecond) {
        this.selfDamageInPercentPerSecond = selfDamageInPercentPerSecond;
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

package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractTotemBase;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicReference;

public class DeathsDebt extends AbstractTotemBase {

    private int respiteRadius = 10;
    private int debtRadius = 8;
    private float delayedDamage = 0;
    private float selfDamageInPercentPerSecond = .1667f;

    private boolean inDebt = false;
    private boolean playerInRadius = true;

    public DeathsDebt() {
        super("Death's Debt", 0, 0, 60f + 10.49f, 20, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        int selfDamagePercent = selfDamageInPercentPerSecond == .1667f ? 100 : 75;
        description = "§2Spirits’ Respite§7: Place down a totem that\n" +
                "§7delays §c100% §7of incoming damage towards\n" +
                "§7yourself. Transforms into §dDeath’s Debt §7after\n" +
                "§64 §7- §66 §7seconds (increases with higher health),\n" +
                "§7or when you exit its §e" + respiteRadius + " §7block radius.\n" +
                "\n" +
                "§dDeath’s Debt§7: Take §c" + selfDamagePercent + "% §7of the damage delayed\n" +
                "§7by §2Spirit's Respite §7over §66 §7seconds. The totem\n" +
                "§7will heal nearby allies for §a15% §7of all damage\n" +
                "§7that you take. If you survive, deal §c15% §7of the" +
                "\n\n" +
                "§7Successful Soulbind procs on enemies add §60.5 §7seconds\n" +
                "§7to your totem duration. (cap of §66 §7seconds)";
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return new ItemStack(Material.JUNGLE_FENCE_GATE);
    }

    @Override
    protected void onTotemStand(ArmorStand totemStand, WarlordsPlayer warlordsPlayer) {
    }

    @Override
    protected void playSound(Player player, Location location) {
        //TODO find the right sound - this aint right chief
        Utils.playGlobalSound(location, "shaman.chainlightning.impact", 2, 2);
    }

    @Override
    protected void onActivation(WarlordsPlayer wp, Player player, ArmorStand totemStand) {
        final int ticksLeft = (4 + (2 * (int) Math.round((double) wp.getHealth() / wp.getMaxHealth()))) * 20;

        DeathsDebt tempDeathsDebt = new DeathsDebt();

        CircleEffect circle = new CircleEffect(wp, totemStand.getLocation().clone().add(0, 1.25, 0), respiteRadius);
        circle.addEffect(new CircumferenceEffect(ParticleEffect.SPELL));
        circle.addEffect(new DoubleLineEffect(ParticleEffect.REDSTONE));
        BukkitTask particles = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), circle::playEffects, 0, 1);

        AtomicReference<RegularCooldown<DeathsDebt>> deathsDebtCooldown = new AtomicReference<>();
        RegularCooldown<DeathsDebt> spiritRespiteCooldown = new RegularCooldown<DeathsDebt>(
                "Spirits Respite",
                "RESP",
                DeathsDebt.class,
                tempDeathsDebt,
                wp,
                CooldownTypes.ABILITY,
                cooldownManagerRespite -> {
                    tempDeathsDebt.setInDebt(true);
                    //beginning debt
                    deathsDebtCooldown.set(new RegularCooldown<>(
                            name,
                            "DEBT",
                            DeathsDebt.class,
                            tempDeathsDebt,
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManagerDebt -> {
                                //final damage tick
                                wp.getWorld().spigot().strikeLightningEffect(totemStand.getLocation(), false);
                                // Enemy damage
                                PlayerFilter.entitiesAround(totemStand, debtRadius, debtRadius - 1, debtRadius)
                                        .aliveEnemiesOf(wp)
                                        .forEach((nearPlayer) -> {
                                            nearPlayer.addDamageInstance(wp,
                                                    name,
                                                    tempDeathsDebt.getDelayedDamage() * .15f,
                                                    tempDeathsDebt.getDelayedDamage() * .15f,
                                                    critChance,
                                                    critMultiplier, false);
                                        });
                                // 6 damage waves, stop the function
                                totemStand.remove();
                                particles.cancel();
                            },
                            6 * 20
                    ));
                    wp.getCooldownManager().addCooldown(deathsDebtCooldown.get());

                    if (!tempDeathsDebt.playerInRadius) {
                        wp.sendMessage("§7You walked outside your §dDeath's Debt §7radius");
                    } else {
                        wp.sendMessage("§c\u00AB §2Spirit's Respite §7delayed §c" + Math.round(tempDeathsDebt.getDelayedDamage()) + " §7damage. §dYour debt must now be paid.");
                    }
                    circle.replaceEffects(e -> e instanceof DoubleLineEffect, new DoubleLineEffect(ParticleEffect.SPELL_WITCH));
                    circle.setRadius(debtRadius);

                    //blue to purple totem
                    totemStand.setHelmet(new ItemStack(Material.DARK_OAK_FENCE_GATE));
                },
                ticksLeft
        ) {
            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                tempDeathsDebt.addDelayedDamage(currentDamageValue);
            }
        };
        wp.getCooldownManager().addCooldown(spiritRespiteCooldown);

        new GameRunnable(wp.getGame()) {

            int counter = 0;

            @Override
            public void run() {
                if (wp.isDead()) {
                    totemStand.remove();
                    particles.cancel();
                    this.cancel();
                } else {
                    if (wp.getWorld() != totemStand.getWorld()) {
                        totemStand.remove();
                        particles.cancel();
                        this.cancel();
                        return;
                    }
                    boolean isPlayerInRadius = wp.getLocation().distanceSquared(totemStand.getLocation()) < respiteRadius * respiteRadius;
                    if (!isPlayerInRadius && wp.getCooldownManager().hasCooldown(tempDeathsDebt) && !tempDeathsDebt.isInDebt()) {
                        tempDeathsDebt.setInDebt(true);
                        tempDeathsDebt.setPlayerInRadius(false);
                        spiritRespiteCooldown.setTicksLeft(0);
                    }

                    //every second
                    if (counter % 20 == 0) {
                        if (!tempDeathsDebt.isInDebt()) {
                            //respite
                            Utils.playGlobalSound(totemStand.getLocation(), "shaman.earthlivingweapon.impact", 2, 1.5F);
                            wp.sendMessage(ChatColor.GREEN + "\u00BB §2Spirit's Respite §7delayed §c" + Math.round(tempDeathsDebt.getDelayedDamage()) + " §7damage. §6" + Math.round(spiritRespiteCooldown.getTicksLeft() / 20f) + " §7seconds left.");
                        } else {
                            //during debt
                            onDebtTick(wp, totemStand, tempDeathsDebt);
                        }
                    }
                    counter++;

                    if (deathsDebtCooldown.get() == null) {
                        return;
                    }
                    if (!wp.getCooldownManager().hasCooldown(deathsDebtCooldown.get())) {
                        totemStand.remove();
                        particles.cancel();
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(2, 0);
    }

    public void onDebtTick(WarlordsPlayer wp, ArmorStand totemStand, DeathsDebt tempDeathsDebt) {
        Utils.playGlobalSound(totemStand.getLocation(), "shaman.lightningbolt.impact", 2, 1.5F);

        // 100% of damage over 6 seconds
        float damage = (tempDeathsDebt.getDelayedDamage() * getSelfDamageInPercentPerSecond());
        float debtTrueDamage = (float) (damage * Math.pow(.8, (int) new CooldownFilter<>(wp, RegularCooldown.class).filterCooldownClass(SpiritLink.class).stream().count()));
        // Player damage
        wp.addDamageInstance(wp, "",
                debtTrueDamage,
                debtTrueDamage,
                critChance,
                critMultiplier,
                false);
        // Teammate heal
        PlayerFilter.entitiesAround(totemStand, debtRadius, debtRadius - 1, debtRadius)
                .aliveTeammatesOf(wp)
                .forEach((nearPlayer) -> {
                    nearPlayer.addHealingInstance(wp, name,
                            damage * .15f,
                            damage * .15f,
                            critChance, critMultiplier, false, false);
                });
        //adding to rep pool
        if (wp.getSpec().getBlue() instanceof Repentance) {
            ((Repentance) wp.getSpec().getBlue()).addToPool(debtTrueDamage);
        }
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

    public float getDelayedDamage() {
        return delayedDamage;
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

    public boolean isInDebt() {
        return inDebt;
    }

    public void setInDebt(boolean inDebt) {
        this.inDebt = inDebt;
    }

    public boolean isPlayerInRadius() {
        return playerInRadius;
    }

    public void setPlayerInRadius(boolean playerInRadius) {
        this.playerInRadius = playerInRadius;
    }
}

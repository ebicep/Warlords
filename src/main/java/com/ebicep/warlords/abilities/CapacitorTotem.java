package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractTotem;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.thunderlord.CapacitorTotemBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CapacitorTotem extends AbstractTotem implements Duration {

    public int numberOfProcs = 0;

    protected int numberOfProcsAfterCarrierPassed = 0;
    protected int playersKilledWithFinalHit = 0;

    private Runnable pulseDamage;
    private boolean teamCarrierPassedThrough = false;
    private int tickDuration = 160;
    private double radius = 6;

    public CapacitorTotem() {
        super("Capacitor Totem", 404, 523, 62.64f, 20, 20, 200);
    }

    public CapacitorTotem(ArmorStand totem, WarlordsEntity owner) {
        super("Capacitor Totem", 404, 523, 62.64f, 20, 20, 200, totem, owner);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Place a highly conductive totem on the ground. Casting Chain Lightning or Lightning Rod on the totem will cause it to pulse, dealing ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage to all enemies in a "))
                               .append(Component.text(format(radius), NamedTextColor.YELLOW))
                               .append(Component.text(" block radius. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Proc'd", "" + numberOfProcs));

        return info;
    }

    @Override
    protected void playSound(Player player, Location location) {
        Utils.playGlobalSound(location, "shaman.totem.activation", 2, 1);
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return new ItemStack(Material.RED_TULIP);
    }

    @Override
    protected void onActivation(WarlordsEntity wp, Player player, ArmorStand totemStand) {
        Location totemLocation = wp.getLocation().clone();

        CapacitorTotem tempCapacitorTotem = new CapacitorTotem(totemStand, wp);
        tempCapacitorTotem.setPulseDamage(() -> {
            PlayerFilter.entitiesAround(totemStand.getLocation(),
                            tempCapacitorTotem.getRadius(),
                            tempCapacitorTotem.getRadius(),
                            tempCapacitorTotem.getRadius()
                    )
                    .aliveEnemiesOf(wp)
                    .forEach(warlordsPlayer -> {
                        warlordsPlayer.addDamageInstance(
                                wp,
                                name,
                                minDamageHeal,
                                maxDamageHeal,
                                critChance,
                                critMultiplier
                        ).ifPresent(warlordsDamageHealingFinalEvent -> {
                            if (warlordsDamageHealingFinalEvent.isDead()) {
                                tempCapacitorTotem.addPlayersKilledWithFinalHit();
                                if (tempCapacitorTotem.getPlayersKilledWithFinalHit() >= 15) {
                                    ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.LIGHTNING_EXECUTION);
                                }
                            }
                        });

                        if (pveMasterUpgrade) {
                            int damageResistance = warlordsPlayer.getSpec().getDamageResistance();
                            warlordsPlayer.setDamageResistance(damageResistance - 20);
                        }
                    });

            if (pveMasterUpgrade) {
                tempCapacitorTotem.setRadius(tempCapacitorTotem.getRadius() + 0.5);
            }

            new FallingBlockWaveEffect(totemStand.getLocation().add(0, .75, 0), tempCapacitorTotem.getRadius(), 1.2, Material.OAK_SAPLING).play();
        });
        wp.getCooldownManager().addRegularCooldown(
                name,
                "TOTEM",
                CapacitorTotem.class,
                tempCapacitorTotem,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    totemStand.remove();
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (!tempCapacitorTotem.isTeamCarrierPassedThrough()) {
                        if (PlayerFilter.playingGame(wp.getGame())
                                        .teammatesOfExcludingSelf(wp)
                                        .stream()
                                        .filter(WarlordsEntity::hasFlag)
                                        .map(WarlordsEntity::getLocation)
                                        .anyMatch(location -> location.distanceSquared(totemLocation) <= 1)) {
                            tempCapacitorTotem.setTeamCarrierPassedThrough(true);
                        }
                    }
                })
        );

    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new CapacitorTotemBranch(abilityTree, this);
    }

    public double getRadius() {
        return radius;
    }

    public void addPlayersKilledWithFinalHit() {
        playersKilledWithFinalHit++;
    }

    public boolean isTeamCarrierPassedThrough() {
        return teamCarrierPassedThrough;
    }

    public void setTeamCarrierPassedThrough(boolean teamCarrierPassedThrough) {
        this.teamCarrierPassedThrough = teamCarrierPassedThrough;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void pulseDamage() {
        pulseDamage.run();
    }

    public Runnable getPulseDamage() {
        return pulseDamage;
    }

    public void setPulseDamage(Runnable pulseDamage) {
        this.pulseDamage = pulseDamage;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public void addProc() {
        owner.doOnStaticAbility(CapacitorTotem.class, capacitorTotem -> capacitorTotem.setNumberOfProcs(capacitorTotem.getNumberOfProcs() + 1));
        numberOfProcs++;
        if (teamCarrierPassedThrough) {
            numberOfProcsAfterCarrierPassed++;
        }
    }

    public int getNumberOfProcs() {
        return numberOfProcs;
    }

    public void setNumberOfProcs(int numberOfProcs) {
        this.numberOfProcs = numberOfProcs;
    }

    public int getNumberOfProcsAfterCarrierPassed() {
        return numberOfProcsAfterCarrierPassed;
    }


    public int getPlayersKilledWithFinalHit() {
        return playersKilledWithFinalHit;
    }
}

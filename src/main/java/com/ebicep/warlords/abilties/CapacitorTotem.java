package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractTotemBase;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CapacitorTotem extends AbstractTotemBase {

    public int numberOfProcs = 0;

    protected int numberOfProcsAfterCarrierPassed = 0;
    protected int playersKilledWithFinalHit = 0;

    private Runnable pulseDamage;
    private boolean teamCarrierPassedThrough = false;
    private int duration = 8;
    private double radius = 6;

    public CapacitorTotem() {
        super("Capacitor Totem", 404, 523, 62.64f, 20, 20, 200);
    }

    public CapacitorTotem(ArmorStand totem, WarlordsEntity owner) {
        super("Capacitor Totem", 404, 523, 62.64f, 20, 20, 200, totem, owner);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Place a highly conductive totem on the ground. Casting Chain Lightning or Lightning Rod on the totem will cause it to pulse, dealing" +
                formatRangeDamage(minDamageHeal, maxDamageHeal) + "damage to all enemies nearby. Lasts §6" + duration + " §7seconds.";
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
        return new ItemStack(Material.RED_ROSE, 1, (short) 4);
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
                                critMultiplier,
                                false
                        ).ifPresent(warlordsDamageHealingFinalEvent -> {
                            System.out.println("Killed: " + warlordsDamageHealingFinalEvent.isDead());
                            if (warlordsDamageHealingFinalEvent.isDead()) {
                                tempCapacitorTotem.addPlayersKilledWithFinalHit();
                                System.out.println("Players killed with final hit: " + tempCapacitorTotem.getPlayersKilledWithFinalHit());
                                if (tempCapacitorTotem.getPlayersKilledWithFinalHit() >= 15) {
                                    ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.LIGHTNING_EXECUTION);
                                }
                            }
                        });

                        if (pveUpgrade) {
                            int damageResistance = warlordsPlayer.getSpec().getDamageResistance();
                            warlordsPlayer.getSpec().setDamageResistance(damageResistance - 20);
                        }
                    });

            if (pveUpgrade) {
                tempCapacitorTotem.setRadius(tempCapacitorTotem.getRadius() + 0.5);
            }

            new FallingBlockWaveEffect(totemStand.getLocation().add(0, 1, 0), tempCapacitorTotem.getRadius(), 1.2, Material.SAPLING, (byte) 0).play();
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
                duration * 20,
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

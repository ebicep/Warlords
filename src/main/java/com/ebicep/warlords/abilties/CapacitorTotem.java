package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractTotemBase;
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
import java.util.List;


public class CapacitorTotem extends AbstractTotemBase {
    private boolean pveUpgrade = false;
    private int duration = 8;
    private int radius = 6;
    private Runnable pulseDamage;

    private int numberOfProcs = 0;
    private int numberOfProcsAfterCarrierPassed = 0;
    private boolean teamCarrierPassedThrough = false;

    public CapacitorTotem() {
        super("Capacitor Totem", 404, 523, 62.64f, 20, 20, 200);
    }

    public CapacitorTotem(ArmorStand totem, WarlordsEntity owner, Runnable pulseDamage) {
        super("Capacitor Totem", 404, 523, 62.64f, 20, 20, 200, totem, owner);
        this.pulseDamage = pulseDamage;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Place a highly conductive totem\n" +
                "§7on the ground. Casting Chain Lightning\n" +
                "§7or Lightning Rod on the totem will cause\n" +
                "§7it to pulse, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage\n" +
                "§7to all enemies nearby. Lasts §6" + duration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Proc'd", "" + numberOfProcs));

        return info;
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return new ItemStack(Material.RED_ROSE, 1, (short) 4);
    }

    @Override
    protected void playSound(Player player, Location location) {
        Utils.playGlobalSound(location, "shaman.totem.activation", 2, 1);
    }

    @Override
    protected void onActivation(WarlordsEntity wp, Player player, ArmorStand totemStand) {
        Location totemLocation = wp.getLocation().clone();

        CapacitorTotem tempCapacitorTotem = new CapacitorTotem(totemStand, wp, () -> {
            PlayerFilter.entitiesAround(totemStand.getLocation(), radius, radius, radius)
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
                        );

                        if (pveUpgrade) {
                            int damageResistance = warlordsPlayer.getSpec().getDamageResistance();
                            warlordsPlayer.getSpec().setDamageResistance(damageResistance - 20);
                        }
                    });

            new FallingBlockWaveEffect(totemStand.getLocation().add(0, 1, 0), radius, 1.2, Material.SAPLING, (byte) 0).play();
        });
        wp.getCooldownManager().addRegularCooldown(
                name,
                "TOTEM",
                CapacitorTotem.class,
                tempCapacitorTotem,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    totemStand.remove();
                },
                duration * 20,
                (cooldown, ticksLeft, counter) -> {
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
                }
        );

    }

    public void pulseDamage() {
        pulseDamage.run();
    }

    public Runnable getPulseDamage() {
        return pulseDamage;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
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


    public boolean isTeamCarrierPassedThrough() {
        return teamCarrierPassedThrough;
    }

    public void setTeamCarrierPassedThrough(boolean teamCarrierPassedThrough) {
        this.teamCarrierPassedThrough = teamCarrierPassedThrough;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}

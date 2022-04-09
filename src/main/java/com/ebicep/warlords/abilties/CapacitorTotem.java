package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractTotemBase;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
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
    private int duration = 8;
    private int radius = 6;

    private int numberOfProcs = 0;
    private int numberOfProcsAfterCarrierPassed = 0;
    private boolean teamCarrierPassedThrough = false;

    public CapacitorTotem() {
        super("Capacitor Totem", 404, 523, 62.64f, 20, 20, 200);
    }

    public CapacitorTotem(ArmorStand totem, WarlordsPlayer owner) {
        super("Capacitor Totem", 404, 523, 62.64f, 20, 20, 200, totem, owner);
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
    protected void onActivation(WarlordsPlayer wp, Player player, ArmorStand totemStand) {
        Location totemLocation = wp.getLocation().clone();

        CapacitorTotem tempCapacitorTotem = new CapacitorTotem(totemStand, wp);
        wp.getCooldownManager().addRegularCooldown(
                name,
                "TOTEM",
                CapacitorTotem.class,
                tempCapacitorTotem,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20
        );
        new GameRunnable(wp.getGame()) {
            int counter = 0;
            int timeLeft = duration;

            @Override
            public void run() {
                if (counter % 20 == 0) {
                    if (timeLeft == 0) {
                        totemStand.remove();
                        this.cancel();
                    }
                    timeLeft--;
                }

                if (!tempCapacitorTotem.isTeamCarrierPassedThrough()) {
                    if (PlayerFilter.playingGame(wp.getGame())
                            .teammatesOfExcludingSelf(wp)
                            .stream()
                            .filter(WarlordsPlayer::hasFlag)
                            .map(WarlordsPlayer::getLocation)
                            .anyMatch(location -> location.distanceSquared(totemLocation) <= 1)) {
                        tempCapacitorTotem.setTeamCarrierPassedThrough(true);
                    }
                }

                counter++;
            }

        }.runTaskTimer(0, 0);
    }

    public void pulseDamage() {
        PlayerFilter.entitiesAround(totem.getLocation(), radius, radius, radius)
                .enemiesOf(owner)
                .forEach(warlordsPlayer -> warlordsPlayer.addDamageInstance(owner, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false));
        new FallingBlockWaveEffect(totem.getLocation().add(0, 1, 0), radius, 1.2, Material.SAPLING, (byte) 0).play();
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
}

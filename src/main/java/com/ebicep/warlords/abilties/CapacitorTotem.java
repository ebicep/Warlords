package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractTotemBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;


public class CapacitorTotem extends AbstractTotemBase {

    private int duration = 8;
    private int radius = 6;

    public CapacitorTotem() {
        super("Capacitor Totem", 404, 523, 62.64f, 20, 20, 200);
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
    protected ItemStack getTotemItemStack() {
        return new ItemStack(Material.RED_ROSE, 1, (short) 4);
    }

    @Override
    protected void onTotemStand(ArmorStand totemStand, WarlordsPlayer warlordsPlayer) {
        totemStand.setMetadata("capacitor-totem-" + warlordsPlayer.getName().toLowerCase(), new FixedMetadataValue(Warlords.getInstance(), true));
    }

    @Override
    protected void playSound(Player player, Location location) {
        Utils.playGlobalSound(location, "shaman.totem.activation", 2, 1);
    }

    @Override
    protected void onActivation(WarlordsPlayer wp, Player player, ArmorStand totemStand) {
        wp.getCooldownManager().addRegularCooldown(name, "TOTEM", CapacitorTotem.class, new CapacitorTotem(), wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, duration * 20);
        new GameRunnable(wp.getGame()) {
            int timeLeft = duration;

            @Override
            public void run() {
                if (timeLeft == 0) {
                    totemStand.remove();
                    this.cancel();
                }
                timeLeft--;
            }

        }.runTaskTimer(0, 20);
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
}

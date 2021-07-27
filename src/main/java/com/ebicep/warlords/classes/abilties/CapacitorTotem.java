package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.internal.AbstractTotemBase;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;


public class CapacitorTotem extends AbstractTotemBase {

    public CapacitorTotem() {
        super("Capacitor Totem", -404, -523, 62.64f, 20, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Place a highly conductive totem\n" +
                "§7on the ground. Casting Chain Lightning\n" +
                "§7or Lightning Rod on the totem will cause\n" +
                "§7it to pulse, dealing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                "§7to all enemies nearby. Lasts §68 §7seconds.";
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
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(location, "shaman.totem.activation", 1, 1);
        }
    }

    @Override
    protected void onActivation(WarlordsPlayer wp, Player player, ArmorStand totemStand) {
        wp.getCooldownManager().addCooldown(this.getClass(), new CapacitorTotem(), "TOTEM", 8, wp, CooldownTypes.ABILITY);

        new BukkitRunnable() {
            int timeLeft = 8;

            @Override
            public void run() {
                if (timeLeft == 0) {
                    totemStand.remove();
                    this.cancel();
                }
                timeLeft--;
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }


}

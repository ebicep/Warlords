package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.ActionBarStats;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Totem extends EntityArmorStand {

    private WarlordsPlayer owner;
    private ArmorStand totemArmorStand;
    private int secondsLeft;
    private List<WarlordsPlayer> playersHit;

    public Totem(World world, WarlordsPlayer owner, ArmorStand totemArmorStand, int secondsLeft) {
        super(world);
        this.owner = owner;
        this.totemArmorStand = totemArmorStand;
        this.secondsLeft = secondsLeft;
        playersHit = new ArrayList<>();
    }

    public WarlordsPlayer getOwner() {
        return owner;
    }

    public ArmorStand getTotemArmorStand() {
        return totemArmorStand;
    }

    public void setTotemArmorStand(ArmorStand totemArmorStand) {
        this.totemArmorStand = totemArmorStand;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public void setSecondsLeft(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

    public List<WarlordsPlayer> getPlayersHit() {
        return playersHit;
    }

    public void setPlayersHit(List<WarlordsPlayer> playersHit) {
        this.playersHit = playersHit;
    }

    public static class TotemThunderlord extends AbstractAbility {

        public TotemThunderlord() {
            super("Capacitor Totem", -404, -523, 60 + 2, 20, 20, 200,
                    "§7Place a highly conductive totem\n" +
                    "§7on the ground. Casting Chain Lightning\n" +
                    "§7or Lightning Rod on the totem will cause\n" +
                    "§7it to pulse, dealing §c404 §7- §c523 §7damage\n" +
                    "§7to all enemies nearby. Lasts §68 §7seconds.");
        }

        @Override
        public void onActivate(Player player) {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

            Location standLocation = player.getLocation();
            standLocation.setYaw(0);
            standLocation.setY(standLocation.getWorld().getHighestBlockYAt(standLocation) - 1);
            ArmorStand totemStand = player.getWorld().spawn(standLocation, ArmorStand.class);
            totemStand.setVisible(false);
            totemStand.setGravity(false);
            totemStand.setHelmet(new ItemStack(Material.RED_ROSE, 1, (short) 4));

            Totem capacitorTotem = new Totem(((CraftWorld) player.getWorld()).getHandle(), warlordsPlayer, totemStand, 8);
            Warlords.totems.add(capacitorTotem);

            warlordsPlayer.getActionBarStats().add(new ActionBarStats(warlordsPlayer, "TOTEM", capacitorTotem.getSecondsLeft()));

            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.playSound(player.getLocation(), "shaman.totem.activation", 1, 1);
            }
        }
    }

    public static class TotemSpiritguard extends AbstractAbility {
        private int delayedDamage = 0;
        private int debt = 0;

        public TotemSpiritguard() {
            super("Death's Debt", 0, 0, 60 + 12, 20, -1, 100,
                    "'§2Spirits’ Respite§7: Place down a totem that'\n" +
                            "§7delays §c100% §7of incoming damage towards\n" +
                            "§7yourself. Transforms into §dDeath’s Debt §7after\n" +
                            "§64 §7- §68 §7seconds (increases with higher health),\n" +
                            "§7or when you exit its §e12 §7block radius.\n" +
                            "''\n" +
                            "'§dDeath’s Debt§7: Take §c100% §7of the damage delayed'\n" +
                            "§7by §2Spirit's Respite §7over §66 §7seconds. The totem\n" +
                            "§7will heal nearby allies for §a15% §7of all damage\n" +
                            "§7that you take. If you survive, deal §c15% §7of the\n" +
                            "§7damage delayed to nearby enemies.");
        }

        @Override
        public void onActivate(Player player) {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

            Location standLocation = player.getLocation();
            standLocation.setYaw(0);
            standLocation.setY(standLocation.getWorld().getHighestBlockYAt(standLocation) - 1.25);
            ArmorStand totemStand = player.getWorld().spawn(standLocation, ArmorStand.class);
            totemStand.setVisible(false);
            totemStand.setGravity(false);
            totemStand.setHelmet(new ItemStack(Material.JUNGLE_FENCE_GATE));

            Totem deathsDebtTotem = new Totem(((CraftWorld) player.getWorld()).getHandle(), warlordsPlayer, totemStand, 4 + (4 * (int) Math.round((double) warlordsPlayer.getHealth() / warlordsPlayer.getMaxHealth())));
            Warlords.totems.add(deathsDebtTotem);

            warlordsPlayer.getActionBarStats().add(new ActionBarStats(warlordsPlayer, "RESP", deathsDebtTotem.getSecondsLeft()));

            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.playSound(player.getLocation(), "shaman.earthlivingweapon.impact", 1, 2);
            }
        }

        public int getDelayedDamage() {
            return delayedDamage;
        }

        public void setDelayedDamage(int delayedDamage) {
            this.delayedDamage = delayedDamage;
        }

        public int getDebt() {
            return debt;
        }

        public void setDebt(int debt) {
            this.debt = debt;
        }
    }

    public static class TotemEarthwarden extends AbstractAbility {

        public TotemEarthwarden() {
            super("Healing Totem", 168, 841, 60 + 12, 60, 15, 200,
                    "§7Place a totem on the ground that\n" +
                    "§7pulses constantly, healing nearby\n" +
                    "§7allies for §a168 §7- §a227 §7every\n" +
                    "second. Before disappearing, the totem\n" +
                    "will let out a final pulse that heals for\n" +
                    "§a841 §7- §a1138§7. Lasts §65 §7seconds.");

            //168 - 227
            //841 - 1138
            //1.35x
        }

        @Override
        public void onActivate(Player player) {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

            Location standLocation = player.getLocation();
            standLocation.setYaw(0);
            standLocation.setY(standLocation.getWorld().getHighestBlockYAt(standLocation) - 1);
            ArmorStand totemStand = player.getWorld().spawn(standLocation, ArmorStand.class);
            totemStand.setVisible(false);
            totemStand.setGravity(false);
            totemStand.setHelmet(new ItemStack(Material.RED_ROSE, 1, (short) 7));

            Totem healingTotem = new Totem(((CraftWorld) player.getWorld()).getHandle(), warlordsPlayer, totemStand, 5);
            Warlords.totems.add(healingTotem);

            warlordsPlayer.getActionBarStats().add(new ActionBarStats(warlordsPlayer, "TOTEM", healingTotem.getSecondsLeft()));

            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.playSound(player.getLocation(), "shaman.totem.activation", 1, 1);
            }
        }

    }
}

package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TotemSpiritguard extends AbstractAbility {

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
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

        Location standLocation = player.getLocation();
        standLocation.setYaw(0);
        standLocation.add(0, -1, 0);
        ArmorStand totemStand = e.getPlayer().getWorld().spawn(standLocation, ArmorStand.class);
        totemStand.setVisible(false);
        totemStand.setHelmet(new ItemStack(Material.JUNGLE_FENCE_GATE));

        //TODO find time based on health 6 + (something)
        Totem deathsDebtTotem = new Totem(((CraftWorld) player.getWorld()).getHandle(), warlordsPlayer, totemStand, -1);
        Warlords.totems.add(deathsDebtTotem);

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "shaman.totem.activation", 1, 1);
        }
    }
}

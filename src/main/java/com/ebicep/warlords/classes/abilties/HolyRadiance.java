package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HolyRadiance extends AbstractAbility {

    private final int radius = 4;

    public HolyRadiance(float cooldown, int energyCost, int critChance, int critMultiplier) {
        super("Holy Radiance", 582, 760, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Radiate with holy energy, healing\n" +
                "§7yourself and all nearby allies for\n" +
                "§a" + minDamageHeal + " §7- §a" + maxDamageHeal + " §7health." +
                "\n\n" +
                "§7Has a maximum range of §e" + radius + " §7blocks.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);

        for (WarlordsPlayer p : PlayerFilter
                .entitiesAround(player, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
            p.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
        }

        wp.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.holyradiance.activation", 2, 1);
        }

        Location particleLoc = player.getLocation().add(0, 1.2, 0);
        ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0.1F, 2, particleLoc, 500);
        ParticleEffect.SPELL.display(1, 1, 1, 0.06F, 12, particleLoc, 500);
    }
}

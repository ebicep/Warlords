package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HolyRadiance extends AbstractAbility {

    public HolyRadiance(float cooldown, int energyCost, int critChance, int critMultiplier) {
        super("Holy Radiance", 582, 760, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Radiate with holy energy, healing\n" +
                "§7yourself and all nearby allies for\n" +
                "§a" + minDamageHeal + " §7- §a" + maxDamageHeal + " §7health.";
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        for(WarlordsPlayer p : PlayerFilter
            .entitiesAround(player, 3.6D, 3.6D, 3.6D)
            .aliveEnemiesOf(warlordsPlayer)
        ) {
            p.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
        }

        warlordsPlayer.subtractEnergy(energyCost);
        warlordsPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.holyradiance.activation", 2, 1);
        }

        Location particleLoc = player.getLocation().add(0, 1.2, 0);
        ParticleEffect.VILLAGER_HAPPY.display(1F, 1F, 1F, 0.1F, 2, particleLoc, 500);
        ParticleEffect.SPELL.display(1F, 1F, 1F, 0.06F, 12, particleLoc, 500);
    }
}

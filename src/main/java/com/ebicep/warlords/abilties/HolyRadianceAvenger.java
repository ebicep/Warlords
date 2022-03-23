package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractHolyRadianceBase;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HolyRadianceAvenger extends AbstractHolyRadianceBase {

    private final int markRadius = 15;
    private int markDuration = 8;

    public HolyRadianceAvenger(float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super("Holy Radiance", minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, 6);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Radiate with holy energy, healing\n" +
                "§7yourself and all nearby allies for\n" +
                "§a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health." +
                "\n\n" +
                "§7You may look at an enemy to mark\n" +
                "§7them for §6" + markDuration + " §7seconds. Mark has an\n" +
                "§7optimal range of §e" + markRadius + " §7blocks. Reducing\n" +
                "§7their energy per second by\n" +
                "§e8 §7for the duration.";
    }

    @Override
    public void chain(WarlordsPlayer wp, Player player) {
        for (WarlordsPlayer markTarget : PlayerFilter
                .entitiesAround(player, markRadius, markRadius, markRadius)
                .aliveEnemiesOf(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            if (Utils.isLookingAtMark(player, markTarget.getEntity()) && Utils.hasLineOfSight(player, markTarget.getEntity())) {
                Utils.playGlobalSound(player.getLocation(), "paladin.consecrate.activation", 2, 0.65f);

                // chain particles
                EffectUtils.playParticleLinkAnimation(wp.getLocation(), markTarget.getLocation(), 255, 50, 0, 1);
                EffectUtils.playChainAnimation(wp, markTarget, new ItemStack(Material.LEAVES, 1, (short) 2), 8);

                HolyRadianceAvenger tempMark = new HolyRadianceAvenger(minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
                markTarget.getCooldownManager().addRegularCooldown(
                        name,
                        "AVENGER MARK",
                        HolyRadianceAvenger.class,
                        tempMark,
                        wp,
                        CooldownTypes.DEBUFF,
                        cooldownManager -> {
                        },
                        markDuration * 20
                );

                wp.sendMessage(WarlordsPlayer.GIVE_ARROW_GREEN + ChatColor.GRAY + " You have marked " + ChatColor.GOLD + markTarget.getName() + ChatColor.GRAY + "!");
                markTarget.sendMessage(WarlordsPlayer.RECEIVE_ARROW_RED + ChatColor.GRAY + " You have been cursed with " + ChatColor.GOLD + "Avenger's Mark" + ChatColor.GRAY + " by " + wp.getName() + "!");

                new GameRunnable(wp.getGame()) {
                    @Override
                    public void run() {
                        if (markTarget.getCooldownManager().hasCooldown(tempMark)) {
                            EffectUtils.playCylinderAnimation(markTarget.getLocation(), 1, 250, 25, 25);
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(0, 10);
            } else {
                player.sendMessage("§cYour mark was out of range or you did not target a player!");
            }
        }
    }

}

package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractHolyRadianceBase;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsEntity;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HolyRadianceCrusader extends AbstractHolyRadianceBase {

    private final int markRadius = 15;
    private int markDuration = 8;

    public HolyRadianceCrusader(float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super("Holy Radiance", minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, 6);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Radiate with holy energy, healing\n" +
                "§7yourself and all nearby allies for\n" +
                "§a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health." +
                "\n\n" +
                "§7You may look at an ally to mark\n" +
                "§7them for §6" + markDuration + " §7seconds. Increasing\n" +
                "§7their EPS by §e6 §7and speed by §e25%\n" +
                "§7§7for the duration. Mark has an optimal\n" +
                "§7range of §e" + markRadius + " §7blocks.";

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHealed));
        info.add(new Pair<>("Players Marked", "" + playersMarked));

        return info;
    }

    @Override
    public boolean chain(WarlordsEntity wp, Player player) {
        for (WarlordsEntity markTarget : PlayerFilter
                .entitiesAround(player, markRadius, markRadius, markRadius)
                .aliveTeammatesOfExcludingSelf(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            if (Utils.isLookingAtMark(player, markTarget.getEntity()) && Utils.hasLineOfSight(player, markTarget.getEntity())) {
                Utils.playGlobalSound(player.getLocation(), "paladin.consecrate.activation", 2, 0.65f);
                // chain particles
                EffectUtils.playParticleLinkAnimation(player.getLocation(), markTarget.getLocation(), 255, 170, 0, 1);
                EffectUtils.playChainAnimation(wp, markTarget, new ItemStack(Material.PUMPKIN), 20);

                HolyRadianceCrusader tempMark = new HolyRadianceCrusader(
                        minDamageHeal,
                        maxDamageHeal,
                        cooldown,
                        energyCost,
                        critChance,
                        critMultiplier
                );
                markTarget.getSpeed().addSpeedModifier("Crusader Mark Speed", 25, 20 * markDuration, "BASE");
                markTarget.getCooldownManager().addRegularCooldown(
                        name,
                        "CRUS MARK",
                        HolyRadianceCrusader.class,
                        tempMark,
                        wp,
                        CooldownTypes.BUFF,
                        cooldownManager -> {
                        },
                        markDuration * 20,
                        (cooldown, ticksLeft, counter) -> {
                            if (counter % 10 == 0) {
                                Location playerLoc = markTarget.getLocation();
                                Location particleLoc = playerLoc.clone();
                                for (int i = 0; i < 4; i++) {
                                    for (int j = 0; j < 10; j++) {
                                        double angle = j / 8D * Math.PI * 2;
                                        double width = 1;
                                        particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                                        particleLoc.setY(playerLoc.getY() + i / 6D);
                                        particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                                        ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 170, 0), particleLoc, 500);
                                    }
                                }
                            }
                        }
                );

                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN +
                                ChatColor.GRAY + " You have marked " +
                                ChatColor.YELLOW + markTarget.getName() +
                                ChatColor.GRAY + "!"
                );

                markTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN +
                                ChatColor.GRAY + " You have been granted " +
                                ChatColor.YELLOW + "Crusader's Mark" +
                                ChatColor.GRAY + " by " + wp.getName() + "!"
                );

                return true;
            } else {
                player.sendMessage("§cYour mark was out of range or you did not target a player!");
            }
        }

        return false;
    }

    public void setMarkDuration(int markDuration) {
        this.markDuration = markDuration;
    }
}
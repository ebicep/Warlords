package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractHolyRadianceBase;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HolyRadianceCrusader extends AbstractHolyRadianceBase {

    private final int markRadius = 15;
    private int markDuration = 8;
    private int energyPerSecond = 6;
    private int markSpeed = 25;

    public HolyRadianceCrusader(float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
        super("Holy Radiance", minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, 6);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Radiate with holy energy, healing yourself and all nearby allies for" + formatRangeHealing(minDamageHeal, maxDamageHeal) + "health." +
                "\n\nYou may look at an ally to mark them for §6" + markDuration + " §7seconds. Increasing their EPS by §e" + energyPerSecond +
                " §7and speed by §e" + markSpeed + "% §7for the duration. Mark has an optimal range of §e" + markRadius + " §7blocks.";
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
                markTarget.getSpeed().addSpeedModifier("Crusader Mark Speed", markSpeed, 20 * markDuration, "BASE");
                markTarget.getCooldownManager().addCooldown(new RegularCooldown<HolyRadianceCrusader>(
                        name,
                        "CRUS MARK",
                        HolyRadianceCrusader.class,
                        tempMark,
                        wp,
                        CooldownTypes.BUFF,
                        cooldownManager -> {
                        },
                        markDuration * 20,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksElapsed % 10 == 0) {
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
                        })
                ) {
                    @Override
                    public float addEnergyGainPerTick(float energyGainPerTick) {
                        return energyGainPerTick + energyPerSecond / 20f;
                    }
                });

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

    public int getMarkDuration() {
        return markDuration;
    }

    public void setMarkDuration(int markDuration) {
        this.markDuration = markDuration;
    }

    public int getEnergyPerSecond() {
        return energyPerSecond;
    }

    public void setEnergyPerSecond(int energyPerSecond) {
        this.energyPerSecond = energyPerSecond;
    }

    public int getMarkSpeed() {
        return markSpeed;
    }

    public void setMarkSpeed(int markSpeed) {
        this.markSpeed = markSpeed;
    }
}
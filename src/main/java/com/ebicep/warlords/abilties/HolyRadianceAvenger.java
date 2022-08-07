package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractHolyRadianceBase;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HolyRadianceAvenger extends AbstractHolyRadianceBase {
    private boolean pveUpgrade = false;

    private int markRadius = 15;
    private int markDuration = 8;
    private int energyPerSecond = 8;

    public HolyRadianceAvenger(float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
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
                "§e" + energyPerSecond + " §7for the duration.";
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
        if (pveUpgrade) {
            for (WarlordsEntity circleTarget : PlayerFilter
                    .entitiesAround(wp, 8, 8, 8)
                    .aliveEnemiesOf(wp)
            ) {
                emitMarkRadiance(wp, circleTarget);
            }

            return true;
        }

        for (WarlordsEntity markTarget : PlayerFilter
                .entitiesAround(player, markRadius, markRadius, markRadius)
                .aliveEnemiesOf(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            if (pveUpgrade) return true;
            if (Utils.isLookingAtMark(player, markTarget.getEntity()) && Utils.hasLineOfSight(player, markTarget.getEntity())) {
                Utils.playGlobalSound(player.getLocation(), "paladin.consecrate.activation", 2, 0.65f);

                // chain particles
                EffectUtils.playParticleLinkAnimation(wp.getLocation(), markTarget.getLocation(), 255, 50, 0, 1);
                EffectUtils.playChainAnimation(wp, markTarget, new ItemStack(Material.LEAVES, 1, (short) 2), 8);

                HolyRadianceAvenger tempMark = new HolyRadianceAvenger(
                        minDamageHeal,
                        maxDamageHeal,
                        cooldown,
                        energyCost,
                        critChance,
                        critMultiplier
                );

                markTarget.getCooldownManager().addCooldown(new RegularCooldown<HolyRadianceAvenger>(
                        name,
                        "AVE MARK",
                        HolyRadianceAvenger.class,
                        tempMark,
                        wp,
                        CooldownTypes.DEBUFF,
                        cooldownManager -> {
                        },
                        markDuration * 20,
                        (cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksElapsed % 10 == 0) {
                                EffectUtils.playCylinderAnimation(markTarget.getLocation(), 1, 250, 25, 25);
                            }
                        }
                ) {
                    @Override
                    public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        if (pveUpgrade) {
                            if (event.getAbility().equals("Avenger's Strike")) {
                                return currentDamageValue * 1.4f;
                            }
                            return currentDamageValue;
                        }
                        return currentDamageValue;
                    }

                    @Override
                    public float addEnergyGainPerTick(float energyGainPerTick) {
                        return energyGainPerTick + energyPerSecond / 20f;
                    }
                });

                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN +
                        ChatColor.GRAY + " You have marked " +
                        ChatColor.GOLD + markTarget.getName() +
                        ChatColor.GRAY + "!"
                );

                markTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED +
                        ChatColor.GRAY + " You have been cursed with " +
                        ChatColor.GOLD + "Avenger's Mark" +
                        ChatColor.GRAY + " by " + wp.getName() + "!"
                );

                return true;
            } else {
                player.sendMessage("§cYour mark was out of range or you did not target a player!");
            }
        }
        return false;
    }

    private void emitMarkRadiance(WarlordsEntity giver, WarlordsEntity target) {
        HolyRadianceAvenger tempMark = new HolyRadianceAvenger(
                minDamageHeal,
                maxDamageHeal,
                cooldown,
                energyCost,
                critChance,
                critMultiplier
        );
        target.getCooldownManager().addCooldown(new RegularCooldown<HolyRadianceAvenger>(
                name,
                "AVE MARK",
                HolyRadianceAvenger.class,
                tempMark,
                giver,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                },
                markDuration * 20,
                (cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 10 == 0) {
                        EffectUtils.playCylinderAnimation(target.getLocation(), 1, 250, 25, 25);
                    }
                }
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (pveUpgrade) {
                    if (event.getAbility().equals("Avenger's Strike")) {
                        return currentDamageValue * 1.4f;
                    }
                    return currentDamageValue;
                }
                return currentDamageValue;
            }
        });
    }

    public int getMarkRadius() {
        return markRadius;
    }

    public void setMarkRadius(int markRadius) {
        this.markRadius = markRadius;
    }

    public int getEnergyPerSecond() {
        return energyPerSecond;
    }

    public void setEnergyPerSecond(int energyPerSecond) {
        this.energyPerSecond = energyPerSecond;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}

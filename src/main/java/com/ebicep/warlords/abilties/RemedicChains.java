package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RemedicChains extends AbstractAbility {
    private final int duration = 8;
    private final int alliesAffected = 3;
    // Percent
    private final float healingMultiplier = 0.125f;
    protected int playersLinked = 0;
    protected int numberOfBrokenLinks = 0;
    private int linkBreakRadius = 15;

    public RemedicChains() {
        super("Remedic Chains", 728, 815, 16, 50, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Bind yourself to §e" + alliesAffected + " §7allies near you, increasing\n" +
                "§7the damage they deal by §c12% §7as long as the\n" +
                "§7link is active. Lasts §6" + duration + " §7seconds" +
                "\n\n" +
                "§7When the link expires you and the allies\n" +
                "§7are healed for §a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health. Breaking\n" +
                "§7the link early will only heal the allies\n" +
                "§7for §a12.5% §7of the original amount for\n" +
                "each second they have been linked." +
                "\n\n" +
                "§7The link will break if you are §e" + linkBreakRadius + " §7blocks apart.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Linked", "" + playersLinked));
        info.add(new Pair<>("Times Link Broke", "" + numberOfBrokenLinks));


        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        List<WarlordsPlayer> teammatesNear = PlayerFilter
                .entitiesAround(player, 10, 10, 10)
                .aliveTeammatesOfExcludingSelf(wp)
                .closestFirst(wp)
                .limit(alliesAffected)
                .stream().collect(Collectors.toList());

        if (teammatesNear.size() >= 1) {
            wp.subtractEnergy(energyCost);
            Utils.playGlobalSound(player.getLocation(), "rogue.remedicchains.activation", 2, 0.2f);

            RemedicChains tempRemedicChain = new RemedicChains();
            wp.getCooldownManager().addCooldown(new RegularCooldown<RemedicChains>(
                    name,
                    "REMEDIC",
                    RemedicChains.class,
                    tempRemedicChain,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                        if (wp.isDead()) return;

                        wp.addHealingInstance(
                                wp,
                                name,
                                minDamageHeal,
                                maxDamageHeal,
                                critChance,
                                critMultiplier,
                                false,
                                false
                        );
                    },
                    duration * 20
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * 1.12f;
                }
            });

            for (WarlordsPlayer chainTarget : teammatesNear) {
                playersLinked++;

                AtomicInteger timeLinked = new AtomicInteger();

                chainTarget.getCooldownManager().addCooldown(new RegularCooldown<RemedicChains>(
                        name,
                        "REMEDIC",
                        RemedicChains.class,
                        tempRemedicChain,
                        wp,
                        CooldownTypes.ABILITY,
                        cooldownManager -> {
                            boolean outOfRange = wp.getLocation().distanceSquared(chainTarget.getLocation()) > linkBreakRadius * linkBreakRadius;
                            Utils.playGlobalSound(chainTarget.getLocation(), "rogue.remedicchains.impact", 0.1f, 1.4f);
                            ParticleEffect.VILLAGER_HAPPY.display(
                                    0.5f,
                                    0.5f,
                                    0.5f,
                                    1,
                                    10,
                                    chainTarget.getLocation().add(0, 1, 0),
                                    500
                            );

                            // Ally was in range, full healing
                            if (!outOfRange && chainTarget.isAlive()) {
                                chainTarget.addHealingInstance(
                                        wp,
                                        name,
                                        minDamageHeal,
                                        maxDamageHeal,
                                        critChance,
                                        critMultiplier,
                                        false,
                                        false
                                );
                            }
                        },
                        duration * 20,
                        (cooldown, ticksLeft, counter) -> {
                            boolean outOfRange = wp.getLocation().distanceSquared(chainTarget.getLocation()) > linkBreakRadius * linkBreakRadius;

                            if (counter % 20 == 0 && !outOfRange) {
                                timeLinked.getAndIncrement();
                            }

                            if (counter % 8 == 0) {
                                if (wp.getCooldownManager().hasCooldown(tempRemedicChain)) {
                                    EffectUtils.playParticleLinkAnimation(wp.getLocation(), chainTarget.getLocation(), 250, 200, 250, 1);
                                    // Ally is out of range, break link
                                    if (outOfRange) {
                                        numberOfBrokenLinks++;

                                        float totalHealingMultiplier = (healingMultiplier * timeLinked.get());
                                        chainTarget.addHealingInstance(
                                                wp,
                                                name,
                                                minDamageHeal * totalHealingMultiplier,
                                                maxDamageHeal * totalHealingMultiplier,
                                                -1,
                                                100,
                                                false,
                                                false
                                        );
                                        chainTarget.getCooldownManager().removeCooldown(cooldown);
                                    }
                                } else {
                                    cooldown.setTicksLeft(0);
                                }
                            }
                        }
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * 1.12f;
                    }
                });

                wp.sendMessage(
                        WarlordsPlayer.GIVE_ARROW_GREEN +
                                ChatColor.GRAY + " Your Remedic Chains is now protecting " +
                                ChatColor.YELLOW + chainTarget.getName() +
                                ChatColor.GRAY + "!"
                );

                chainTarget.sendMessage(
                        WarlordsPlayer.RECEIVE_ARROW_GREEN + " " +
                                ChatColor.GRAY + wp.getName() + "'s" +
                                ChatColor.YELLOW + " Remedic Chains" +
                                ChatColor.GRAY + " is now increasing your §cdamage §7for " +
                                ChatColor.GOLD + duration +
                                ChatColor.GRAY + " seconds!"
                );
            }

            return true;
        } else {
            wp.sendMessage(ChatColor.RED + "There are no allies nearby to link!");
            return false;
        }
    }

    public int getLinkBreakRadius() {
        return linkBreakRadius;
    }

    public void setLinkBreakRadius(int linkBreakRadius) {
        this.linkBreakRadius = linkBreakRadius;
    }
}

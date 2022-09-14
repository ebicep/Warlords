package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
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
    private boolean pveUpgrade = false;

    protected int playersLinked = 0;
    protected int numberOfBrokenLinks = 0;
    // Percent
    private float healingMultiplier = 12.5f;
    private float allyDamageIncrease = 12;
    private int duration = 8;
    private int alliesAffected = 3;
    private int linkBreakRadius = 15;
    private int castRange = 10;

    public RemedicChains() {
        super("Remedic Chains", 728, 815, 16, 50, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Bind yourself to §e" + alliesAffected + " §7allies near you, increasing\n" +
                "§7the damage they deal by §c" + format(allyDamageIncrease) + "% §7as long as the\n" +
                "§7link is active. Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7When the link expires you and the allies\n" +
                "§7are healed for §a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health. Breaking\n" +
                "§7the link early will only heal the allies\n" +
                "§7for §a" + healingMultiplier + "% §7of the original amount for\n" +
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
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        List<WarlordsEntity> teammatesNear = PlayerFilter
                .entitiesAround(player, castRange, castRange, castRange)
                .aliveTeammatesOfExcludingSelf(wp)
                .closestFirst(wp)
                .limit(alliesAffected)
                .stream().collect(Collectors.toList());

        if (teammatesNear.size() >= 1) {
            wp.subtractEnergy(energyCost, false);
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

                        if (pveUpgrade) {
                            wp.setMaxHealth(wp.getSpec().getMaxHealth());
                        }
                    },
                    duration * 20
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * (1 + allyDamageIncrease / 100f);
                }
            });

            for (WarlordsEntity chainTarget : teammatesNear) {
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

                            if (pveUpgrade) {
                                chainTarget.setMaxHealth(chainTarget.getSpec().getMaxHealth());
                            }
                        },
                        duration * 20,
                        (cooldown, ticksLeft, ticksElapsed) -> {
                            boolean outOfRange = wp.getLocation().distanceSquared(chainTarget.getLocation()) > linkBreakRadius * linkBreakRadius;

                            if (ticksElapsed % 20 == 0 && !outOfRange) {
                                timeLinked.getAndIncrement();
                            }

                            if (ticksElapsed % 8 == 0) {
                                if (wp.getCooldownManager().hasCooldown(tempRemedicChain)) {
                                    EffectUtils.playParticleLinkAnimation(wp.getLocation(), chainTarget.getLocation(), 250, 200, 250, 1);
                                    // Ally is out of range, break link
                                    if (outOfRange) {
                                        numberOfBrokenLinks++;

                                        float totalHealingMultiplier = ((healingMultiplier / 100f) * timeLinked.get());
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
                        return currentDamageValue * (1 + allyDamageIncrease / 100f);
                    }
                });

                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN +
                        ChatColor.GRAY + " Your Remedic Chains is now protecting " +
                        ChatColor.YELLOW + chainTarget.getName() +
                        ChatColor.GRAY + "!"
                );

                chainTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN + " " +
                        ChatColor.GRAY + wp.getName() + "'s" +
                        ChatColor.YELLOW + " Remedic Chains" +
                        ChatColor.GRAY + " is now increasing your §cdamage §7for " +
                        ChatColor.GOLD + duration +
                        ChatColor.GRAY + " seconds!"
                );

                if (pveUpgrade) {
                    wp.setMaxHealth(wp.getSpec().getMaxHealth() * 1.3f);
                    chainTarget.setMaxHealth(chainTarget.getSpec().getMaxHealth() * 1.3f);
                }
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

    public int getCastRange() {
        return castRange;
    }

    public void setCastRange(int castRange) {
        this.castRange = castRange;
    }

    public int getAlliesAffected() {
        return alliesAffected;
    }

    public void setAlliesAffected(int alliesAffected) {
        this.alliesAffected = alliesAffected;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getHealingMultiplier() {
        return healingMultiplier;
    }

    public void setHealingMultiplier(float healingMultiplier) {
        this.healingMultiplier = healingMultiplier;
    }

    public float getAllyDamageIncrease() {
        return allyDamageIncrease;
    }

    public void setAllyDamageIncrease(float allyDamageIncrease) {
        this.allyDamageIncrease = allyDamageIncrease;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}

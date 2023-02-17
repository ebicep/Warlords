package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsBlueAbilityTargetEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class RemedicChains extends AbstractAbility {

    public int playersLinked = 0;
    public int numberOfBrokenLinks = 0;

    private float healingMultiplier = 12.5f; // %
    private float allyDamageIncrease = 12; // %
    private int duration = 8;
    private int alliesAffected = 3;
    private int linkBreakRadius = 15;
    private int castRange = 10;

    public RemedicChains() {
        super("Remedic Chains", 728, 815, 16, 50, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Bind yourself to §e" + alliesAffected + " §7allies near you, increasing the damage they deal by §c" +
                format(allyDamageIncrease) + "% §7as long as the link is active. Lasts §6" + duration + " §7seconds." +
                "\n\nWhen the link expires you and the allies are healed for" + formatRangeHealing(minDamageHeal, maxDamageHeal) +
                "health. Breaking the link early will only heal the allies for §a" + healingMultiplier +
                "% §7of the original amount for each second they have been linked." +
                "\n\nThe link will break if you are §e" + linkBreakRadius + " §7blocks apart.";
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
        Set<WarlordsEntity> teammatesNear = PlayerFilter
                .entitiesAround(wp, castRange, castRange, castRange)
                .aliveTeammatesOfExcludingSelf(wp)
                .closestFirst(wp)
                .limit(alliesAffected)
                .stream()
                .collect(Collectors.toSet());

        if (teammatesNear.size() < 1) {
            wp.sendMessage(ChatColor.RED + "There are no allies nearby to link!");
            return false;
        }

        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(wp.getLocation(), "rogue.remedicchains.activation", 2, 0.2f);

        HashMap<WarlordsEntity, Float> healthBoosts = new HashMap<>();
        teammatesNear.forEach(warlordsEntity -> {
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN +
                    ChatColor.GRAY + " Your Remedic Chains is now protecting " +
                    ChatColor.YELLOW + warlordsEntity.getName() +
                    ChatColor.GRAY + "!"
            );
            warlordsEntity.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN + " " +
                    ChatColor.GRAY + wp.getName() + "'s" +
                    ChatColor.YELLOW + " Remedic Chains" +
                    ChatColor.GRAY + " is now increasing your §cdamage §7for " +
                    ChatColor.GOLD + duration +
                    ChatColor.GRAY + " seconds!"
            );
            float healthIncrease = warlordsEntity.getMaxHealth() * .25f;
            healthBoosts.put(warlordsEntity, healthIncrease);
            if (pveUpgrade) {
                warlordsEntity.setMaxHealth(warlordsEntity.getMaxHealth() + healthIncrease);
            }

        });

        if (pveUpgrade) {
            float healthIncrease = wp.getMaxHealth() * .25f;
            healthBoosts.put(wp, healthIncrease);
            wp.setMaxHealth(wp.getMaxHealth() + healthIncrease);
        }

        RemedicChains tempRemedicChain = new RemedicChains();
        LinkedCooldown<RemedicChains> remedicChainsCooldown = new LinkedCooldown<>(
                name,
                "REMEDIC",
                RemedicChains.class,
                tempRemedicChain,
                wp,
                CooldownTypes.ABILITY,
                (cooldownManager, linkedCooldown) -> {
                    if (!Objects.equals(cooldownManager.getWarlordsEntity(), wp)) {
                        return;
                    }
                    if (wp.isDead()) {
                        return;
                    }
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
                    for (WarlordsEntity linkedEntity : linkedCooldown.getLinkedEntities()) {
                        linkedEntity.addHealingInstance(
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
                (cooldownManager, linkedCooldown) -> {
                    if (!Objects.equals(cooldownManager.getWarlordsEntity(), wp)) {
                        return;
                    }
                    if (pveUpgrade) {
                        healthBoosts.forEach((entity, aFloat) -> entity.setMaxHealth(entity.getMaxHealth() - aFloat));
                    }
                },
                duration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 8 != 0) {
                        return;
                    }
                    Set<WarlordsEntity> linkedEntities = cooldown.getLinkedEntities();
                    Set<WarlordsEntity> toRemove = new HashSet<>();
                    for (WarlordsEntity linked : linkedEntities) {
                        boolean outOfRange = wp.getLocation().distanceSquared(linked.getLocation()) > linkBreakRadius * linkBreakRadius;
                        if (outOfRange) {
                            linked.getCooldownManager().removeCooldownNoForce(cooldown);
                            Utils.playGlobalSound(linked.getLocation(), "rogue.remedicchains.impact", 0.1f, 1.4f);
                            ParticleEffect.VILLAGER_HAPPY.display(
                                    0.5f,
                                    0.5f,
                                    0.5f,
                                    1,
                                    10,
                                    linked.getLocation().add(0, 1, 0),
                                    500
                            );
                            // Ally is out of range, break link
                            numberOfBrokenLinks++;

                            float totalHealingMultiplier = ((healingMultiplier / 100f) * (ticksElapsed / 20f));
                            linked.addHealingInstance(
                                    wp,
                                    name,
                                    minDamageHeal * totalHealingMultiplier,
                                    maxDamageHeal * totalHealingMultiplier,
                                    0,
                                    100,
                                    false,
                                    false
                            );
                        }
                        EffectUtils.playParticleLinkAnimation(wp.getLocation(), linked.getLocation(), 250, 200, 250, 1);
                        if (outOfRange || linked.isDead()) {
                            toRemove.add(linked);
                            if (pveUpgrade) {
                                linked.setMaxHealth(linked.getMaxHealth() - healthBoosts.get(linked));
                            }
                        }
                    }
                    linkedEntities.removeAll(toRemove);
                }),
                teammatesNear
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 + allyDamageIncrease / 100f);
            }
        };
        wp.getCooldownManager().addCooldown(remedicChainsCooldown);
        teammatesNear.forEach(entity -> entity.getCooldownManager().addCooldown(remedicChainsCooldown));
        Bukkit.getPluginManager().callEvent(new WarlordsBlueAbilityTargetEvent(wp, name, teammatesNear));


        return true;
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


}

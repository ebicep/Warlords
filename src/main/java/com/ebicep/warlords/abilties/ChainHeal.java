package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractChainBase;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChainHeal extends AbstractChainBase {

    private int radius = 15;
    private int bounceRange = 10;

    public ChainHeal() {
        super("Chain Heal", 533, 719, 7.99f, 40, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Discharge a beam of energizing lightning that heals you and a targeted friendly player for" +
                formatRangeHealing(minDamageHeal, maxDamageHeal) + "health and jumps to §e1 §7additional target within §e" + bounceRange + " §7blocks." +
                "\n\nEach ally healed reduces the cooldown of Boulder by §62.5 §7seconds." + "" +
                "\n\nHas an initial cast range of §e" + radius + " §7blocks.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHit));

        return info;
    }

    @Override
    protected Set<WarlordsEntity> getEntitiesHitAndActivate(WarlordsEntity wp, Player p) {
        Set<WarlordsEntity> hitCounter = new HashSet<>();
        for (WarlordsEntity chainTarget : PlayerFilter
                .entitiesAround(p, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .lookingAtFirst(wp)
        ) {
            if (Utils.isLookingAtChain(p, chainTarget.getEntity())) {
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

                chain(p.getLocation(), chainTarget.getLocation());
                hitCounter.add(chainTarget);

                for (WarlordsEntity bounceTarget : PlayerFilter
                        .entitiesAround(chainTarget, bounceRange, bounceRange, bounceRange)
                        .aliveTeammatesOf(wp)
                        .excluding(wp, chainTarget)
                ) {
                    chain(chainTarget.getLocation(), bounceTarget.getLocation());
                    bounceTarget.addHealingInstance(
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
                        for (WarlordsEntity bounceTargetTwo : PlayerFilter
                                .entitiesAround(chainTarget, bounceRange, bounceRange, bounceRange)
                                .aliveTeammatesOf(wp)
                                .excluding(wp, chainTarget, bounceTarget)
                        ) {
                            chain(bounceTarget.getLocation(), bounceTargetTwo.getLocation());
                            bounceTargetTwo.addHealingInstance(
                                    wp,
                                    name,
                                    minDamageHeal,
                                    maxDamageHeal,
                                    critChance,
                                    critMultiplier,
                                    false,
                                    false
                            );

                            hitCounter.add(bounceTargetTwo);
                            break;
                        }
                    }

                    hitCounter.add(bounceTarget);
                    break;
                }

                break;
            }
        }

        return hitCounter;
    }

    @Override
    protected void onHit(WarlordsEntity wp, Player player, int hitCounter) {
        Utils.playGlobalSound(player.getLocation(), "shaman.chainheal.activation", 2, 1);

        float redCurrentCooldown = wp.getRedAbility().getCurrentCooldown();
        if ((hitCounter + 1) * 2.5f > redCurrentCooldown) {
            wp.setRedCurrentCooldown(0);
        } else {
            wp.setRedCurrentCooldown(redCurrentCooldown - (hitCounter + 1) * 2.5f);
        }

        wp.updateRedItem(player);
        wp.updateBlueItem(player);
    }

    @Override
    protected ItemStack getChainItem() {
        return new ItemStack(Material.RED_ROSE, 1, (short) 1);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getBounceRange() {
        return bounceRange;
    }

    public void setBounceRange(int bounceRange) {
        this.bounceRange = bounceRange;
    }


}
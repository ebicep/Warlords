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
import java.util.List;


public class ChainHeal extends AbstractChainBase {

    private final int radius = 15;
    private final int bounceRange = 10;

    public ChainHeal() {
        super("Chain Heal", 533, 719, 7.99f, 40, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Discharge a beam of energizing lightning\n" +
                "§7that heals you and a targeted friendly\n" +
                "§7player for §a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health and\n" +
                "§7jumps to §e1 §7additional target within\n" +
                "§e" + bounceRange + " §7blocks. The last jump heals\n" +
                "§7for §c20% §7less." +
                "\n\n" +
                "§7Each ally healed reduces the cooldown of\n" +
                "§7Boulder by §62.5 §7seconds." +
                "\n\n" +
                "§7Has an initial cast range of §e" + radius + " §7blocks.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHit));

        return info;
    }

    @Override
    protected int getHitCounterAndActivate(WarlordsEntity wp, Player p) {
        int hitCounter = 0;
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
                        critChance, critMultiplier,
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
                hitCounter++;

                for (WarlordsEntity bounceTarget : PlayerFilter
                        .entitiesAround(chainTarget, bounceRange, bounceRange, bounceRange)
                        .aliveTeammatesOf(wp)
                        .excluding(wp, chainTarget)
                ) {
                    chain(chainTarget.getLocation(), bounceTarget.getLocation());
                    bounceTarget.addHealingInstance(
                            wp,
                            name,
                            minDamageHeal * 0.8f,
                            maxDamageHeal * 0.8f,
                            critChance,
                            critMultiplier,
                            false,
                            false
                    );

                    hitCounter++;
                    break;
                }

                break;
            }
        }

        return hitCounter;
    }

    @Override
    protected void onHit(WarlordsEntity warlordsPlayer, Player player, int hitCounter) {
        Utils.playGlobalSound(player.getLocation(), "shaman.chainheal.activation", 2, 1);

        if ((hitCounter + 1) * 2.5f > warlordsPlayer.getSpec().getRed().getCurrentCooldown()) {
            warlordsPlayer.getSpec().getRed().setCurrentCooldown(0);
        } else {
            warlordsPlayer.getSpec().getRed().setCurrentCooldown(warlordsPlayer.getSpec().getRed().getCurrentCooldown() - (hitCounter + 1) * 2.5f);
        }
        warlordsPlayer.updateRedItem(player);
        warlordsPlayer.getSpec().getBlue().setCurrentCooldown((float) (cooldown * warlordsPlayer.getCooldownModifier()));
        warlordsPlayer.updateBlueItem(player);
    }

    @Override
    protected ItemStack getChainItem() {
        return new ItemStack(Material.RED_ROSE, 1, (short) 1);
    }
}
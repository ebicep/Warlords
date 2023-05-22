package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractChainBase;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;


public class ChainHeal extends AbstractChainBase {

    public ChainHeal() {
        super("Chain Heal", 533, 719, 7.99f, 40, 20, 175, 15, 10, 1);
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
                .entitiesAround(wp, radius, radius, radius)
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

                if (pveUpgrade) {
                    critStatsOnHit(wp);
                    critStatsOnHit(chainTarget);
                }

                chain(p.getLocation(), chainTarget.getLocation());
                hitCounter.add(chainTarget);

                additionalBounce(wp, hitCounter, chainTarget, new ArrayList<>(Arrays.asList(wp, chainTarget)), 0);

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

    private void additionalBounce(WarlordsEntity wp, Set<WarlordsEntity> hitCounter, WarlordsEntity chainTarget, List<WarlordsEntity> toExclude, int bounceCount) {
        if (bounceCount >= additionalBounces) {
            return;
        }
        for (WarlordsEntity bounceTarget : PlayerFilter
                .entitiesAround(chainTarget, bounceRange, bounceRange, bounceRange)
                .aliveTeammatesOf(wp)
                .excluding(toExclude)
                .warlordPlayersFirst()
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
                critStatsOnHit(bounceTarget);
            }

            hitCounter.add(bounceTarget);

            toExclude.add(bounceTarget);
            additionalBounce(wp, hitCounter, bounceTarget, toExclude, bounceCount + 1);

            break;
        }
    }

    private void critStatsOnHit(WarlordsEntity we) {
        we.getCooldownManager().removeCooldown(ChainHeal.class, false);
        we.getCooldownManager().addCooldown(new RegularCooldown<ChainHeal>(
                name,
                "CHAIN CRIT",
                ChainHeal.class,
                new ChainHeal(),
                we,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                8 * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksLeft % 6 == 0) {
                        Location loc = we.getLocation().add(0, 1.2, 0);
                        ParticleEffect.VILLAGER_HAPPY.display(
                                0.5F,
                                0.3F,
                                0.5F,
                                0.01F,
                                1,
                                loc,
                                500
                        );
                    }
                })
        ) {
            @Override
            public boolean distinct() {
                return true;
            }

            @Override
            public float addCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
                if (event.getAbility().isEmpty() || event.getAbility().equals("Time Warp")) {
                    return currentCritChance;
                }

                return currentCritChance + 20;
            }

            @Override
            public float addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
                if (event.getAbility().isEmpty() || event.getAbility().equals("Time Warp")) {
                    return currentCritMultiplier;
                }
                return currentCritMultiplier + 40;
            }
        });
    }


}
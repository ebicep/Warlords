package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractChainBase;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class SpiritLink extends AbstractChainBase {

    private final int bounceRange = 10;

    public SpiritLink() {
        super("Spirit Link", 236.25f, 446.25f, 8.61f, 40, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Links your spirit with up to §c3 §7enemy\n" +
                "§7players, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage\n" +
                "§7to the first target hit. Each additional hit\n" +
                "§7deals §c20% §7reduced damage. You gain §e40%\n" +
                "§7speed for §61.5 §7seconds, and take §c15%\n" +
                "§7reduced damage for §64.5 §7seconds.";
    }

    @Override
    protected int getHitCounterAndActivate(WarlordsPlayer wp, Player player) {
        int hitCounter = 0;
        for (WarlordsPlayer nearPlayer : PlayerFilter
                .entitiesAround(player, 20, 18, 20)
                .aliveEnemiesOf(wp)
                .lookingAtFirst(wp)
                .soulBindedFirst(wp)
        ) {
            if (Utils.isLookingAtChain(player, nearPlayer.getEntity()) && Utils.hasLineOfSight(player, nearPlayer.getEntity())) {
                chain(player.getLocation(), nearPlayer.getLocation());
                nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                hitCounter++;

                int numberOfHeals = wp.getCooldownManager().getNumberOfBoundPlayersLink(nearPlayer);
                for (int i = 0; i < numberOfHeals; i++) {
                    healNearPlayers(wp);
                }

                for (WarlordsPlayer chainPlayerOne : PlayerFilter
                        .entitiesAround(nearPlayer, bounceRange, bounceRange, bounceRange)
                        .aliveEnemiesOf(wp)
                        .excluding(nearPlayer)
                        .soulBindedFirst(wp)
                ) {
                    chain(nearPlayer.getLocation(), chainPlayerOne.getLocation());
                    chainPlayerOne.addDamageInstance(wp, name, minDamageHeal * .8f, maxDamageHeal * .8f, critChance, critMultiplier, false);
                    hitCounter++;

                    numberOfHeals = wp.getCooldownManager().getNumberOfBoundPlayersLink(chainPlayerOne);
                    for (int i = 0; i < numberOfHeals; i++) {
                        healNearPlayers(wp);
                    }

                    for (WarlordsPlayer chainPlayerTwo : PlayerFilter
                            .entitiesAround(chainPlayerOne, bounceRange, bounceRange, bounceRange)
                            .aliveEnemiesOf(wp)
                            .excluding(nearPlayer, chainPlayerOne)
                            .soulBindedFirst(wp)
                    ) {
                        chain(chainPlayerOne.getLocation(), chainPlayerTwo.getLocation());
                        chainPlayerTwo.addDamageInstance(wp, name, minDamageHeal * .6f, maxDamageHeal * .6f, critChance, critMultiplier, false);
                        hitCounter++;

                        numberOfHeals = wp.getCooldownManager().getNumberOfBoundPlayersLink(chainPlayerTwo);
                        for (int i = 0; i < numberOfHeals; i++) {
                            healNearPlayers(wp);
                        }

                        break;
                    }
                    break;
                }
                break;
            }
        }
        return hitCounter;
    }

    @Override
    protected void onHit(WarlordsPlayer warlordsPlayer, Player player, int hitCounter) {
        // speed buff
        warlordsPlayer.getSpeed().addSpeedModifier("Spirit Link", 40, 30); // 30 is ticks
        warlordsPlayer.getCooldownManager().addCooldown(new RegularCooldown<SpiritLink>(
                name,
                "LINK",
                SpiritLink.class,
                new SpiritLink(),
                warlordsPlayer,
                CooldownTypes.BUFF,
                cooldownManager -> { },
                (int) (4.5 * 20)
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float newDamageValue = currentDamageValue * .85f;
                event.getPlayer().addAbsorbed(Math.abs(currentDamageValue - newDamageValue));
                return newDamageValue;
            }
        });

        warlordsPlayer.getSpec().getRed().setCurrentCooldown((float) (cooldown * warlordsPlayer.getCooldownModifier()));

        player.playSound(player.getLocation(), "mage.firebreath.activation", 1, 1);
    }

    @Override
    protected ItemStack getChainItem() {
        return new ItemStack(Material.SPRUCE_FENCE_GATE);
    }

    private void healNearPlayers(WarlordsPlayer warlordsPlayer) {
        //adding .25 to totem, cap 6 sec
        new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                .filterName("Spirits Respite")
                .findFirst()
                .ifPresent(regularCooldown -> {
                    regularCooldown.setTicksLeft(Math.min(regularCooldown.getTicksLeft() + 10, 6 * 20));
                });
        warlordsPlayer.addHealingInstance(warlordsPlayer, "Soulbinding Weapon", 400, 400, -1, 100, false, false);
        for (WarlordsPlayer nearPlayer : PlayerFilter
                .entitiesAround(warlordsPlayer, 8, 8, 8)
                .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                .limit(2)
        ) {
            nearPlayer.addHealingInstance(warlordsPlayer, "Soulbinding Weapon", 200, 200, -1, 100, false, false);
        }
    }
}

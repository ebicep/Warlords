package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractChainBase;
import com.ebicep.warlords.player.Cooldown;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class SpiritLink extends AbstractChainBase {

    private final int bounceRange = 10;

    public SpiritLink() {
        super("Spirit Link", -236.25f, -446.25f, 8.61f, 40, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Links your spirit with up to §c3 §7enemy\n" +
                "§7players, dealing §c" + format(-minDamageHeal) + " §7- §c" + format(-maxDamageHeal) + " §7damage\n" +
                "§7to the first target hit. Each additional hit\n" +
                "§7deals §c10% §7reduced damage. You gain §e40%\n" +
                "§7speed for §61.5 §7seconds, and take §c20%\n" +
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
                nearPlayer.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
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
                    chainPlayerOne.addHealth(wp, name, minDamageHeal * .8f, maxDamageHeal * .8f, critChance, critMultiplier, false);
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
                        chainPlayerTwo.addHealth(wp, name, minDamageHeal * .6f, maxDamageHeal * .6f, critChance, critMultiplier, false);
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
        warlordsPlayer.getCooldownManager().addCooldown(name, this.getClass(), new SpiritLink(), "LINK", 4.5f, warlordsPlayer, CooldownTypes.BUFF);

        warlordsPlayer.getSpec().getRed().setCurrentCooldown((float) (cooldown * warlordsPlayer.getCooldownModifier()));

        player.playSound(player.getLocation(), "mage.firebreath.activation", 1, 1);
    }

    @Override
    protected ItemStack getChainItem() {
        return new ItemStack(Material.SPRUCE_FENCE_GATE);
    }

    private void healNearPlayers(WarlordsPlayer warlordsPlayer) {
        //adding .25 to totem, cap 6 sec
        if(warlordsPlayer.getCooldownManager().hasCooldownFromName("Spirits Respite")) {
            Cooldown cooldown = warlordsPlayer.getCooldownManager().getCooldownFromName("Spirits Respite").get(0);
            DeathsDebt deathsDebt = ((DeathsDebt) cooldown.getCooldownObject());
            deathsDebt.setTimeLeftRespite(deathsDebt.getTimeLeftRespite() + .5);
            cooldown.setTimeLeft((float) deathsDebt.getTimeLeftRespite());
        }
        warlordsPlayer.addHealth(warlordsPlayer, "Soulbinding Weapon", 420, 420, -1, 100, false);
        for (WarlordsPlayer nearPlayer : PlayerFilter
                .entitiesAround(warlordsPlayer, 8, 8, 8)
                .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                .limit(2)
        ) {
            nearPlayer.addHealth(warlordsPlayer, "Soulbinding Weapon", 420, 420, -1, 100, false);
        }
    }
}

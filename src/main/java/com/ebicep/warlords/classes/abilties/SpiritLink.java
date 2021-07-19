package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractChainBase;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class SpiritLink extends AbstractChainBase {

    public SpiritLink() {
        super("Spirit Link", -236.25f, -446.25f, 8.61f, 40, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Links your spirit with up to §c3 §7enemy\n" +
                "§7players, dealing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                "§7to the first target hit. Each additional hit\n" +
                "§7deals §c10% §7reduced damage. You gain §e40%\n" +
                "§7speed for §61.5 §7seconds, and take §c20%\n" +
                "§7reduced damage for §64.5 §7seconds.";
    }

    @Override
    protected int getHitCounterAndActivate(WarlordsPlayer warlordsPlayer, Player player) {
        int hitCounter = 0;
        for (WarlordsPlayer nearPlayer : PlayerFilter
                .entitiesAround(player, 15.0D, 13.0D, 15.0D)
                .aliveEnemiesOf(warlordsPlayer)
        ) {
            if (Utils.isLookingAtChain(player, nearPlayer.getEntity()) && Utils.hasLineOfSight(player, nearPlayer.getEntity())) {
                chain(player.getLocation(), nearPlayer.getLocation());
                nearPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                hitCounter++;

                if (warlordsPlayer.getCooldownManager().hasBoundPlayerLink(nearPlayer)) {
                    healNearPlayers(warlordsPlayer);
                }

                for (WarlordsPlayer nearNearPlayer : PlayerFilter
                        .entitiesAround(nearPlayer, 10.0D, 9.0D, 10.0D)
                        .aliveEnemiesOf(warlordsPlayer)
                        .excluding(nearPlayer)
                        .soulBindedFirst(warlordsPlayer)
                ) {
                    chain(nearPlayer.getLocation(), nearNearPlayer.getLocation());
                    nearNearPlayer.addHealth(warlordsPlayer, name, minDamageHeal * .8f, maxDamageHeal * .8f, critChance, critMultiplier);
                    hitCounter++;

                    if (warlordsPlayer.getCooldownManager().hasBoundPlayerLink(nearNearPlayer)) {
                        healNearPlayers(warlordsPlayer);
                    }

                    for (WarlordsPlayer nearNearNearPlayer : PlayerFilter
                            .entitiesAround(nearNearPlayer, 10.0D, 9.0D, 10.0D)
                            .aliveEnemiesOf(warlordsPlayer)
                            .excluding(nearPlayer, nearNearPlayer)
                            .soulBindedFirst(warlordsPlayer)
                    ) {
                        chain(nearNearPlayer.getLocation(), nearNearNearPlayer.getLocation());
                        nearNearPlayer.addHealth(warlordsPlayer, name, minDamageHeal * .6f, maxDamageHeal * .6f, critChance, critMultiplier);
                        hitCounter++;

                        if (warlordsPlayer.getCooldownManager().hasBoundPlayerLink(nearNearNearPlayer)) {
                            healNearPlayers(warlordsPlayer);
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
        warlordsPlayer.getCooldownManager().addCooldown(this.getClass(), new SpiritLink(), "LINK", 4.5f, warlordsPlayer, CooldownTypes.BUFF);

        warlordsPlayer.getSpec().getRed().setCurrentCooldown((float) (cooldown * warlordsPlayer.getCooldownModifier()));

        player.playSound(player.getLocation(), "mage.firebreath.activation", 1.5F, 1);
    }

    @Override
    protected ItemStack getChainItem() {
        return new ItemStack(Material.SPRUCE_FENCE_GATE);
    }

    private void healNearPlayers(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.addHealth(warlordsPlayer, "Soulbinding Weapon", 420, 420, -1, 100);
        for (WarlordsPlayer nearPlayer : PlayerFilter
                .entitiesAround(warlordsPlayer, 2.5, 2.5, 2.5)
                .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                .limit(2)
        ) {
            nearPlayer.addHealth(warlordsPlayer, "Soulbinding Weapon", 420, 420, -1, 100);
        }
    }
}

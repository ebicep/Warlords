package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;

import java.util.UUID;

public class LegendaryGale extends AbstractLegendaryWeapon {

    public static final int BLOCKS_TO_MOVE = 100;
    public static final int COOLDOWN = 30;

    public LegendaryGale() {
    }

    public LegendaryGale(UUID uuid) {
        super(uuid);
    }

    public LegendaryGale(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getPassiveEffect() {
        return "Increase movement speed by 40% and decrease energy consumption of all abilities by 10 after moving " + BLOCKS_TO_MOVE + " blocks. " +
                "Can be triggered every " + COOLDOWN + " seconds.";
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 170;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        new GameRunnable(player.getGame()) {

            private int currentCooldown = 0;
            private int lastBlocksMoved = 0;

            @Override
            public void run() {
                if (currentCooldown > 0) {
                    if (currentCooldown == COOLDOWN - 10) {
                        player.sendMessage(ChatColor.RED + "Gale Passive Deactivated!");
                        passive(-1);
                        player.updateItems();
                    }
                    currentCooldown--;
                    if (currentCooldown == 0) {
                        lastBlocksMoved = player.getBlocksTravelledCM() / 100;
                    }
                    return;
                }
                if (player.getBlocksTravelledCM() / 100 - lastBlocksMoved >= BLOCKS_TO_MOVE) {
                    player.sendMessage(ChatColor.GREEN + "Gale Passive Activated!");
                    passive(1);

                    currentCooldown = COOLDOWN;
                }
            }

            public void passive(int multiplier) {
                player.getSpeed().addBaseModifier(40 * multiplier);
                for (AbstractAbility ability : player.getSpec().getAbilities()) {
                    if (ability.getEnergyCost() > 0) {
                        ability.setEnergyCost(ability.getEnergyCost() - 10 * multiplier);
                    }
                }
                player.updateItems();
            }
        }.runTaskTimer(0, 20);
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.GALE;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 150;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 185;
    }

    @Override
    protected float getHealthBonusValue() {
        return 500;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 20;
    }
}

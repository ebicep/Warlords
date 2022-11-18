package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.springframework.data.annotation.Transient;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class LegendaryGale extends AbstractLegendaryWeapon {

    public static final int COOLDOWN = 30;

    @Transient
    private LegendaryGaleAbility ability;

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
        return "Increase movement speed by 40% and decrease energy consumption of all abilities by 10. " +
                "Can be triggered every " + COOLDOWN + " seconds.";
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 170;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);
    }

    @Override
    public LegendaryGaleAbility getAbility() {
        return ability;
    }

    @Override
    public void resetAbility() {
        ability = new LegendaryGaleAbility();
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

    static class LegendaryGaleAbility extends AbstractAbility {

        public LegendaryGaleAbility() {
            super("Gale", 0, 0, 30, 0);
        }

        @Override
        public void updateDescription(Player player) {
            description = "Increase movement speed by " + ChatColor.YELLOW + "40% " + ChatColor.GRAY +
                    "and decrease energy consumption of all abilities by" + ChatColor.YELLOW + " 10.";
        }

        @Override
        public List<Pair<String, String>> getAbilityInfo() {
            return null;
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
            passive(wp, 1);
            new GameRunnable(wp.getGame()) {
                @Override
                public void run() {
                    passive(wp, -1);
                }
            }.runTaskLater(10 * 20);

            return true;
        }

        public void passive(WarlordsEntity player, int multiplier) {
            player.getSpeed().addBaseModifier(40 * multiplier);
            for (AbstractAbility ability : player.getSpec().getAbilities()) {
                if (ability.getEnergyCost() > 0) {
                    ability.setEnergyCost(ability.getEnergyCost() - 10 * multiplier);
                }
            }
            player.updateItems();
        }

    }
}

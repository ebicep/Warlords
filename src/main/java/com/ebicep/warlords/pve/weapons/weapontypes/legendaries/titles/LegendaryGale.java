package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddVelocityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.springframework.data.annotation.Transient;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LegendaryGale extends AbstractLegendaryWeapon {

    private static final int ABILITY_ENERGY_DECREASE = 10;
    private static final float ABILITY_ENERGY_DECREASE_PER_UPGRADE = 2.5f;
    private static final int ABILITY_ANTI_KB = 20;
    private static final int ABILITY_ANTI_KB_PER_UPGRADE = 5;

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
    public TextComponent getPassiveEffect() {
        return Component.text("Increase movement speed by 50%, decrease energy consumption of all abilities by ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(ABILITY_ENERGY_DECREASE + ABILITY_ENERGY_DECREASE_PER_UPGRADE * getTitleLevel()))
                        .append(Component.text(", and gain "))
                        .append(formatTitleUpgrade(ABILITY_ANTI_KB + ABILITY_ANTI_KB_PER_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text(" knockback resistance. Can be triggered every 30 seconds."));
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(new Pair<>(
                        formatTitleUpgrade(ABILITY_ENERGY_DECREASE + ABILITY_ENERGY_DECREASE_PER_UPGRADE * getTitleLevel()),
                        formatTitleUpgrade(ABILITY_ENERGY_DECREASE + ABILITY_ENERGY_DECREASE_PER_UPGRADE * getTitleLevelUpgraded())
                ),
                new Pair<>(
                        formatTitleUpgrade(ABILITY_ANTI_KB + ABILITY_ANTI_KB_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(ABILITY_ANTI_KB + ABILITY_ANTI_KB_PER_UPGRADE * getTitleLevelUpgraded(), "%"
                        )
                )
        );
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 170;
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.GALE;
    }

    @Override
    public LegendaryGaleAbility getAbility() {
        return ability;
    }

    @Override
    public void resetAbility() {
        ability = new LegendaryGaleAbility(ABILITY_ENERGY_DECREASE + ABILITY_ENERGY_DECREASE_PER_UPGRADE * getTitleLevel(),
                ABILITY_ANTI_KB + ABILITY_ANTI_KB_PER_UPGRADE * getTitleLevel()
        );
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

        private final float abilityEnergyDecrease;
        private final float knockbackResistance;

        public LegendaryGaleAbility(float abilityEnergyDecrease, float knockbackResistance) {
            super("Gale", 0, 0, 30, 0);
            this.abilityEnergyDecrease = abilityEnergyDecrease;
            this.knockbackResistance = knockbackResistance;
        }

        @Override
        public void updateDescription(Player player) {
            description = Component.text("Increase movement speed by ")
                                   .append(Component.text("40%", NamedTextColor.YELLOW))
                                   .append(Component.text(", decrease energy consumption of all abilities by "))
                                   .append(Component.text(DECIMAL_FORMAT_TITLE.format(abilityEnergyDecrease), NamedTextColor.YELLOW))
                                   .append(Component.text(" and gain "))
                                   .append(Component.text(DECIMAL_FORMAT_TITLE.format(knockbackResistance) + "%", NamedTextColor.YELLOW))
                                   .append(Component.text(" knockback resistance.", NamedTextColor.GRAY));
        }

        @Override
        public List<Pair<String, String>> getAbilityInfo() {
            return null;
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
            passive(wp, 1);
            Listener listener = new Listener() {
                @EventHandler
                public void onVelocity(WarlordsAddVelocityEvent event) {
                    if (event.getWarlordsEntity().equals(wp)) {
                        Vector vector = event.getVector();
                        vector.multiply(1 - knockbackResistance / 100);
                    }
                }
            };
            wp.getGame().registerEvents(listener);
            new GameRunnable(wp.getGame()) {
                @Override
                public void run() {
                    passive(wp, -1);
                    HandlerList.unregisterAll(listener);
                }
            }.runTaskLater(10 * 20);

            return true;
        }

        public void passive(WarlordsEntity player, int multiplier) {
            player.getSpeed().addBaseModifier(50 * multiplier);
            for (AbstractAbility ability : player.getSpec().getAbilities()) {
                if (ability.getEnergyCost() > 0) {
                    ability.setEnergyCost(ability.getEnergyCost() - abilityEnergyDecrease * multiplier);
                }
            }
            player.updateItems();
        }

    }
}

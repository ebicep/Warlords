package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsUpgradeUnlockEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LegendaryTitanic extends AbstractLegendaryWeapon {

    private static final float HEALTH_INCREASE = 0.008f;
    private static final float HEALTH_INCREASE_PER_UPGRADE = 0.0035f;

    public LegendaryTitanic() {
    }

    public LegendaryTitanic(UUID uuid) {
        super(uuid);
    }

    public LegendaryTitanic(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Increase maximum health by ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade((HEALTH_INCREASE + HEALTH_INCREASE_PER_UPGRADE * getTitleLevel()) * 100f, "%"))
                        .append(Component.text(" per upgrade purchased."));
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Collections.singletonList(new Pair<>(
                formatTitleUpgrade((HEALTH_INCREASE + HEALTH_INCREASE_PER_UPGRADE * getTitleLevel()) * 100f, "%"),
                formatTitleUpgrade((HEALTH_INCREASE + HEALTH_INCREASE_PER_UPGRADE * getTitleLevelUpgraded()) * 100f, "%")
        ));
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 150;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        player.getGame().registerEvents(new Listener() {
            final FloatModifiable.FloatModifier modifier = player.getHealth().addMultiplicativeModifierAdd(getTitleName() + " (Base)", 0);
            @EventHandler
            public void onEvent(WarlordsUpgradeUnlockEvent event) {
                if (event.getWarlordsEntity() == player) {
                    modifier.setModifier(modifier.getModifier() + (HEALTH_INCREASE + HEALTH_INCREASE_PER_UPGRADE * getTitleLevel()));
                }
            }
        });
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.TITANIC;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 130;
    }

    @Override
    protected float getCritChanceValue() {
        return 15;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 160;
    }

    @Override
    protected float getHealthBonusValue() {
        return 1500;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 5;
    }
}

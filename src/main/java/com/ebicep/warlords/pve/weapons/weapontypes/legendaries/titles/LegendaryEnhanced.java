package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAddSpeedModifierEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.CalculateSpeed;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class LegendaryEnhanced extends AbstractLegendaryWeapon {

    private static final int TICKS_TO_ADD = 40;
    private static final int TICKS_TO_ADD_PER_UPGRADE = 10;
    private static final List<String> EFFECTED_ABILITIES = Arrays.asList("BRN", "WND", "BLEED", "CRIP", "SILENCE", "LCH", "AVE MARK");

    public LegendaryEnhanced() {
    }

    public LegendaryEnhanced(UUID uuid) {
        super(uuid);
    }

    public LegendaryEnhanced(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 180;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        player.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onCooldownAdd(WarlordsAddCooldownEvent event) {
                WarlordsEntity eventPlayer = event.getWarlordsEntity();
                if (!(eventPlayer instanceof WarlordsNPC)) {
                    return;
                }
                if (eventPlayer.isTeammate(player)) {
                    return;
                }
                AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                if (!Objects.equals(cooldown.getFrom(), player)) {
                    return;
                }
                if (!(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                    return;
                }
                if (regularCooldown.isEnhanced()) {
                    return;
                }
                if (EFFECTED_ABILITIES.contains(cooldown.getActionBarName())) {
                    regularCooldown.setEnhanced(true);
                    regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + TICKS_TO_ADD + TICKS_TO_ADD_PER_UPGRADE * getTitleLevel());
                }
            }

            @EventHandler
            public void onSpeedModify(WarlordsAddSpeedModifierEvent event) {
                WarlordsEntity eventPlayer = event.getWarlordsEntity();
                CalculateSpeed.Modifier modifier = event.getModifier();
                if (!(eventPlayer instanceof WarlordsNPC)) {
                    return;
                }
                if (!modifier.getFrom().equals(player)) {
                    return;
                }
                if (eventPlayer.isTeammate(player)) {
                    return;
                }
                if (modifier.getModifier() > 0) {
                    return;
                }
                if (event.isEnhanced()) {
                    return;
                }
                event.setEnhanced(true);
                modifier.setDuration(modifier.getDuration() + TICKS_TO_ADD + TICKS_TO_ADD_PER_UPGRADE * getTitleLevel());
            }

            @EventHandler
            public void onBlueAbilityTarget(WarlordsAbilityTargetEvent.WarlordsBlueAbilityTargetEvent event) {
                if (!event.getWarlordsEntity().equals(player)) {
                    return;
                }
                HashSet<WarlordsEntity> warlordsEntities = new HashSet<>(event.getTargets());
                warlordsEntities.add(player);
                warlordsEntities.stream()
                                .filter(warlordsEntity -> warlordsEntity.isTeammate(player))
                                .forEach(warlordsEntity -> {
                                    List<AbstractCooldown<?>> abstractCooldowns = warlordsEntity.getCooldownManager().getCooldowns();
                                    abstractCooldowns.stream()
                                                     .filter(abstractCooldown -> abstractCooldown.getCooldownType() == CooldownTypes.ABILITY)
                                                     .filter(RegularCooldown.class::isInstance)
                                                     .map(RegularCooldown.class::cast)
                                                     .filter(regularCooldown -> !regularCooldown.isEnhanced())
                                                     .forEachOrdered(regularCooldown -> {
                                                         if (regularCooldown.getTicksLeft() <= 0) {
                                                             return;
                                                         }
                                                         regularCooldown.setEnhanced(true);
                                                         regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + TICKS_TO_ADD + TICKS_TO_ADD_PER_UPGRADE * getTitleLevel());
                                                     });
                                });
            }

        });
    }

    @Override
    public TextComponent getPassiveEffect() {
        Component effectDuration = formatTitleUpgrade((TICKS_TO_ADD + TICKS_TO_ADD_PER_UPGRADE * getTitleLevel()) / 20f, "s");
        return Component.text("Increase the duration of negative effects to enemies by ", NamedTextColor.GRAY)
                        .append(effectDuration)
                        .append(Component.text(" and active abilities of allies by "))
                        .append(effectDuration)
                        .append(Component.text(" whenever you target an ally with a blue rune (Slot 4)."));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.ENHANCED;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 155;
    }

    @Override
    protected float getHealthBonusValue() {
        return 400;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 8;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return 3;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 180;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Collections.singletonList(new Pair<>(
                formatTitleUpgrade((TICKS_TO_ADD + TICKS_TO_ADD_PER_UPGRADE * getTitleLevel()) / 20f, "s"),
                formatTitleUpgrade((TICKS_TO_ADD + TICKS_TO_ADD_PER_UPGRADE * getTitleLevelUpgraded()) / 20f, "s")
        ));
    }
}

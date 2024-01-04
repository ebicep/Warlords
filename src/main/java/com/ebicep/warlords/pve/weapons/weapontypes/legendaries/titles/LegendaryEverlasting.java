package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.data.annotation.Transient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LegendaryEverlasting extends AbstractLegendaryWeapon implements Listener, LibraryArchivesTitle {

    private static final int DAMAGE_REDUCTION = 5;
    private static final float DAMAGE_REDUCTION_PER_UPGRADE = 1;
    private static final int DURATION = 5;
    private static final int DURATION_PER_UPGRADE = 1;

    @Transient
    private RegularCooldown<LegendaryEverlasting> cooldown = null;
    @Transient
    private int stacks = 0;

    public LegendaryEverlasting() {
    }

    public LegendaryEverlasting(UUID uuid) {
        super(uuid);
    }

    public LegendaryEverlasting(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        cooldown = null;
        stacks = 0;
    }

    @Override
    public TextComponent getPassiveEffect() {
        return ComponentBuilder.create("Upon landing a melee crit, heal for 5% of your max HP and gain ")
                               .append(formatTitleUpgrade(DAMAGE_REDUCTION + DAMAGE_REDUCTION_PER_UPGRADE * getTitleLevel(), "%"))
                               .text(" damage reduction for ")
                               .append(formatTitleUpgrade(DURATION + DURATION_PER_UPGRADE * getTitleLevel(), "s"))
                               .text(". Maximum 5 stacks.")
                               .build();
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.EVERLASTING;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 155;
    }

    @Override
    protected float getHealthBonusValue() {
        return 800;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 7;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return 3;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 10;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 175;
    }

    @Override
    protected float getCritChanceValue() {
        return 55;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 125;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(new Pair<>(
                        formatTitleUpgrade(DAMAGE_REDUCTION + DAMAGE_REDUCTION_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(DAMAGE_REDUCTION + DAMAGE_REDUCTION_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(DURATION + DURATION_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(DURATION + DURATION_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                )
        );
    }

    @EventHandler
    public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
        WarlordsEntity attacker = event.getAttacker();
        String ability = event.getAbility();
        if (event.isHealingInstance()) {
            return;
        }
        if (!ability.isEmpty()) {
            return;
        }
        if (!event.isCrit()) {
            return;
        }
        if (!Objects.equals(attacker, warlordsPlayer)) {
            return;
        }
        if (event.getInstanceFlags().contains(InstanceFlags.RECURSIVE)) {
            return;
        }
        float healing = warlordsPlayer.getMaxBaseHealth() * .05f;
        warlordsPlayer.addHealingInstance(
                warlordsPlayer,
                getTitleName(),
                healing,
                healing,
                0,
                100
        );
        if (stacks < 5) {
            stacks++;
        }
        if (cooldown == null) {
            warlordsPlayer.getCooldownManager().addCooldown(cooldown = new RegularCooldown<>(
                    getTitleName() + " 1",
                    "EVER 1",
                    LegendaryEverlasting.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.WEAPON,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                        cooldown = null;
                        stacks = 0;
                    },
                    (DURATION + DURATION_PER_UPGRADE * getTitleLevel()) * 20
            ) {
                final float reduction = (DAMAGE_REDUCTION + DAMAGE_REDUCTION_PER_UPGRADE * getTitleLevel()) / 100;

                @Override
                public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * (1 - stacks * reduction);
                }
            });
        } else {
            cooldown.setTicksLeft(5 * 20);
            cooldown.setName(getTitleName() + " " + stacks);
            cooldown.setNameAbbreviation("EVER " + stacks);
        }
    }

}

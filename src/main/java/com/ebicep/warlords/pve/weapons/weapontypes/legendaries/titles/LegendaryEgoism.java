package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.data.annotation.Transient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class LegendaryEgoism extends AbstractLegendaryWeapon implements PassiveCounter, EventTitle {

    public static final int DEBUFF_IMMUNITY_DURATION = 5;
    public static final int DEBUFF_IMMUNITY_DURATION_INCREASE_PER_UPGRADE = 1;
    public static final int HEALTH_RESTORE = 10;
    public static final int HEALTH_RESTORE_INCREASE_PER_UPGRADE = 3;

    @Transient
    private final AtomicReference<Instant> lastActivated = new AtomicReference<>(Instant.now().minus(35, ChronoUnit.SECONDS));

    public LegendaryEgoism() {
    }

    public LegendaryEgoism(UUID uuid) {
        super(uuid);
    }

    public LegendaryEgoism(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_BANE_OF_IMPURITIES, 1L);
        return baseCost;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        int debuffImmunityTickDuration = (DEBUFF_IMMUNITY_DURATION + DEBUFF_IMMUNITY_DURATION_INCREASE_PER_UPGRADE * getTitleLevel()) * 20;
        pveOption.getGame().registerEvents(new Listener() {
            @EventHandler
            public void onCooldownAdd(WarlordsAddCooldownEvent event) {
                if (!event.getWarlordsEntity().equals(player)) {
                    return;
                }
                if (event.getAbstractCooldown().getCooldownType() != CooldownTypes.DEBUFF) {
                    return;
                }
                if (Instant.now().isBefore(lastActivated.get())) {
                    return;
                }
                lastActivated.set(Instant.now().plus(35, ChronoUnit.SECONDS));
                event.setCancelled(true);
                player.getCooldownManager().addCooldown(new RegularCooldown<>(
                        getTitleName(),
                        null,
                        LegendaryEgoism.class,
                        null,
                        player,
                        CooldownTypes.WEAPON,
                        cooldownManager -> {
                        },
                        debuffImmunityTickDuration,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        })
                ) {
                    @Override
                    protected Listener getListener() {
                        return CooldownManager.getDefaultDebuffImmunityListener();
                    }
                });
                float healthRestore = player.getMaxHealth() * (HEALTH_RESTORE + HEALTH_RESTORE_INCREASE_PER_UPGRADE * getTitleLevel()) / 100;
                player.addInstance(InstanceBuilder
                        .healing()
                        .cause(getTitleName())
                        .source(player)
                        .value(healthRestore)
                );
            }
        });
    }


    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("When you are debuffed, clear the debuffs on you, and become immune to all debuffs for ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(DEBUFF_IMMUNITY_DURATION + DEBUFF_IMMUNITY_DURATION_INCREASE_PER_UPGRADE * getTitleLevel(), "s"))
                        .append(Component.text(" , and restore"))
                        .append(formatTitleUpgrade(HEALTH_RESTORE + HEALTH_RESTORE_INCREASE_PER_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text(" of your maximum health. Can be triggered every 35 seconds."));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.EGOISM;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 120;
    }

    @Override
    protected float getHealthBonusValue() {
        return 700;
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
    protected float getSkillCritMultiplierBonusValue() {
        return 5;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 150;
    }

    @Override
    protected float getCritChanceValue() {
        return 15;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 200;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(new Pair<>(
                        formatTitleUpgrade(DEBUFF_IMMUNITY_DURATION + DEBUFF_IMMUNITY_DURATION_INCREASE_PER_UPGRADE * getTitleLevel(), "s"),
                        formatTitleUpgrade(DEBUFF_IMMUNITY_DURATION + DEBUFF_IMMUNITY_DURATION_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "s")
                ),
                new Pair<>(
                        formatTitleUpgrade(HEALTH_RESTORE + HEALTH_RESTORE_INCREASE_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(HEALTH_RESTORE + HEALTH_RESTORE_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                )
        );
    }

    @Override
    public int getCounter() {
        if (Instant.now().isBefore(lastActivated.get())) {
            return (int) ChronoUnit.SECONDS.between(Instant.now(), lastActivated.get());
        }
        return 0;
    }


}

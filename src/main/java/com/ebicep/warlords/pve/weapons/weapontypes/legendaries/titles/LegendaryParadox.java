package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.PrismGuard;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
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

import java.util.*;

public class LegendaryParadox extends AbstractLegendaryWeapon implements GardenOfHesperidesTitle, PassiveCounter {

    public static final int HP_INTERVAL = 100;
    public static final int HP_INTERVAL_PER_UPGRADE = -10;
    public static final int DAMAGE_BOOST_MAX = 25;
    public static final float DAMAGE_BOOST_MAX_PER_UPGRADE = 2.5f;

    @Transient
    private int secondCounter = 0;

    public LegendaryParadox() {
    }

    public LegendaryParadox(UUID uuid) {
        super(uuid);
    }

    public LegendaryParadox(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_GARDEN_OF_HESPERIDES, 1L);
        return baseCost;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        this.secondCounter = 0;
        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getTitleName(),
                null,
                LegendaryFulcrum.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cooldownManager -> {
                },
                false,
                (cooldown, ticksElapsed) -> {
                    if (ticksElapsed % 20 == 0 && secondCounter > 0) {
                        secondCounter--;
                    }
                }
        ) {

            @Override
            protected Listener getListener() {
                return new Listener() {
                    @EventHandler
                    public void onAbilityUse(WarlordsAbilityActivateEvent.Pre event) {
                        if (!event.getWarlordsEntity().equals(player)) {
                            return;
                        }
                        AbstractAbility ability = event.getAbility();
                        if (ability instanceof OrangeAbilityIcon || ability instanceof PrismGuard) {
                            giveParadoxCooldown(player);
                        }
                    }

                    @EventHandler
                    public void onCooldownAdd(WarlordsAddCooldownEvent event) {
                        if (!event.getWarlordsEntity().equals(player)) {
                            return;
                        }
                        AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                        if (cooldown.getCooldownObject() instanceof Shield) {
                            giveParadoxCooldown(player);
                        }
                    }
                };
            }

            @Override
            public void onHealFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (!event.getWarlordsEntity().equals(player)) {
                    giveParadoxCooldown(player);
                }
            }
        });
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("For 10s after using a orange rune, creating a shield, using Prism Guard, or healing an ally, gain the PARADOX effect.", NamedTextColor.GRAY)
                        .append(Component.newline())
                        .append(Component.newline())
                        .append(Component.text("PARADOX: Gain 25 energy every 2s and increase damage by 0.25% for every "))
                        .append(formatTitleUpgrade(HP_INTERVAL + HP_INTERVAL_PER_UPGRADE * getTitleLevel()))
                        .append(Component.text(" health you possess at the time the effect began. Max of "))
                        .append(formatTitleUpgrade(DAMAGE_BOOST_MAX + DAMAGE_BOOST_MAX_PER_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text(" damage bonus. Effect can occur once every 30s."));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.PARADOX;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 170;
    }

    @Override
    protected float getHealthBonusValue() {
        return 700;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 5;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return 3;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 15;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 190;
    }

    @Override
    protected float getCritChanceValue() {
        return 25;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 180;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(new Pair<>(
                        formatTitleUpgrade(HP_INTERVAL + HP_INTERVAL_PER_UPGRADE * getTitleLevel()),
                        formatTitleUpgrade(HP_INTERVAL + HP_INTERVAL_PER_UPGRADE * getTitleLevelUpgraded())
                ),
                new Pair<>(
                        formatTitleUpgrade(DAMAGE_BOOST_MAX + DAMAGE_BOOST_MAX_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(DAMAGE_BOOST_MAX + DAMAGE_BOOST_MAX_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                )
        );
    }

    private void giveParadoxCooldown(WarlordsEntity player) {
        if (secondCounter != 0) {
            return;
        }
        secondCounter = 30;
        float damageBoost = 1 + Math.min(
                (DAMAGE_BOOST_MAX + DAMAGE_BOOST_MAX_PER_UPGRADE * getTitleLevel()) / 100f,
                player.getHealth() / (HP_INTERVAL + HP_INTERVAL_PER_UPGRADE * getTitleLevel()) * .0025f
        );
        player.getCooldownManager().addCooldown(new RegularCooldown<>(
                getTitleName(),
                "PARA",
                LegendaryParadox.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cooldownManager -> {
                },
                200,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 40 == 0) {
                        player.addEnergy(player, getTitleName(), 25);
                    }
                })
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * damageBoost;
            }
        });
    }

    @Override
    public int getCounter() {
        return secondCounter;
    }
}

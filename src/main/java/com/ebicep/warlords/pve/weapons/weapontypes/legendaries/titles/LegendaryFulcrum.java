package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.annotation.Transient;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class LegendaryFulcrum extends AbstractLegendaryWeapon implements GardenOfHesperidesTitle, PassiveCounter {

    public static final int SHIELD_PERCENT = 25;
    public static final int SHIELD_PERCENT_PER_UPGRADE = 5;
    public static final int COOLDOWN = 20;
    public static final float COOLDOWN_PER_UPGRADE = -1.5f;
    public static final int EPS_BOOST = 10;
//    public static final int EPS_BOOST_PER_UPGRADE = 2;

    @Transient
    private int tickCounter = 0;

    public LegendaryFulcrum() {
    }

    public LegendaryFulcrum(UUID uuid) {
        super(uuid);
    }

    public LegendaryFulcrum(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return ComponentBuilder.create("Taking damage create a shield that absorbs damage based on ", NamedTextColor.GRAY)
                               .append(formatTitleUpgrade(SHIELD_PERCENT + SHIELD_PERCENT_PER_UPGRADE * getTitleLevel(), "%"))
                               .text(" of your max health. This shield lasts 10s or until broken and can only be triggered every ")
                               .append(formatTitleUpgrade(COOLDOWN + COOLDOWN_PER_UPGRADE * getTitleLevel(), "s"))
                               .text(". While the shield is active, EPS is increased by " + EPS_BOOST + ".")
                               .build();
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.FULCRUM;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 160;
    }

    @Override
    protected float getHealthBonusValue() {
        return 800;
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
    protected float getSkillCritChanceBonusValue() {
        return 5;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 5;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        this.tickCounter = 0;

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
                    if (tickCounter > 0) {
                        tickCounter--;
                    }
                }
        ).addModifier(Modifier.ON_INCOMING_DAMAGE, (event, currentDamageValue, isCrit) -> {
                    if (tickCounter != 0) {
                        return;
                    }
                    tickCounter = (int) (COOLDOWN + COOLDOWN_PER_UPGRADE * getTitleLevel()) * 20;
                    float shieldHealth = player.getMaxBaseHealth() * (SHIELD_PERCENT + SHIELD_PERCENT_PER_UPGRADE * getTitleLevel()) / 100;
                    Shield shield = new Shield(getTitleName(), shieldHealth);
                    RegularCooldown<Shield> fulcrumCooldown = new RegularCooldown<>(
                            getTitleName(),
                            null,
                            Shield.class,
                            shield,
                            player,
                            CooldownTypes.WEAPON,
                            cooldownManager -> {
                            },
                            200
                    ) {
                        @Override
                        public PlayerNameData addPrefixFromOther() {
                            return PlayerNameData.shieldHealth(shield, we -> we.isTeammate(player), NamedTextColor.YELLOW);
                        }
                    };
            fulcrumCooldown.addModifier(Modifier.ENERGY_GAIN_PER_TICK, energyGainPerTick -> energyGainPerTick.addModifier(FloatModifiable.ModifierType.ADDITIVE,
                            getTitleName(), EPS_BOOST / 20f
                    )
            );
                    player.getCooldownManager().addCooldown(fulcrumCooldown);
                }
        ));
    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_GARDEN_OF_HESPERIDES, 1L);
        return baseCost;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 185;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 190;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(SHIELD_PERCENT + SHIELD_PERCENT_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(SHIELD_PERCENT + SHIELD_PERCENT_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(COOLDOWN + COOLDOWN_PER_UPGRADE * getTitleLevel(), "s"),
                        formatTitleUpgrade(COOLDOWN + COOLDOWN_PER_UPGRADE * getTitleLevelUpgraded(), "s")
                )
        );
    }

    @Override
    public int getCounter() {
        return tickCounter;
    }

}

package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class LegendaryJuggernaut extends AbstractLegendaryWeapon implements FightersGloryTitle {

    public static final int DAMAGE_BOOST = 10;
    public static final float DAMAGE_BOOST_INCREASE_PER_UPGRADE = 1;
    public static final int HEALTH_BOOST = 5;
    public static final float HEALTH_BOOST_INCREASE_PER_UPGRADE = 0.5f;
    public static final List<Integer> KILL_MILESTONES = Arrays.asList(100, 200, 400, 800, 1600);

    public LegendaryJuggernaut() {
    }

    public LegendaryJuggernaut(UUID uuid) {
        super(uuid);
    }

    public LegendaryJuggernaut(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Gain a ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(getDamageBoost(), "%"))
                        .append(Component.text(" Damage and "))
                        .append(formatTitleUpgrade(getHealthBoost(), "%"))
                        .append(Component.text(" Health boost when you hit the following kill milestones:"))
                        .append(Component.newline())
                        .append(Component.text("100 Kills"))
                        .append(Component.newline())
                        .append(Component.text("200 Kills"))
                        .append(Component.newline())
                        .append(Component.text("400 Kills"))
                        .append(Component.newline())
                        .append(Component.text("800 Kills"))
                        .append(Component.newline())
                        .append(Component.text("1600 Kills"));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.JUGGERNAUT;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 180;
    }

    @Override
    protected float getHealthBonusValue() {
        return 500;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 5;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        player.getGame().registerEvents(new Listener() {
            final FloatModifiable.FloatModifier modifier = player.getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER,
                    getTitleName() + " (Base)", 0
            );

            @EventHandler(ignoreCancelled = true)
            public void onDeath(WarlordsDeathEvent event) {
                WarlordsEntity killer = event.getKiller();

                if (killer == player) {
                    if (KILL_MILESTONES.contains(player.getMinuteStats().total().getKills())) {
                        modifier.setModifier(modifier.getModifier() + getCalculatedHealthBoost());
                    }
                }
            }
        });
        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Juggernaut",
                null,
                LegendaryJuggernaut.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cooldownManager -> {
                },
                false
        ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                    int playerKills = player.getMinuteStats().total().getKills();
                    for (int i = KILL_MILESTONES.size() - 1; i >= 0; i--) {
                        int killMilestone = KILL_MILESTONES.get(i);
                        if (playerKills >= killMilestone) {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getTitleName(), 1 + (getDamageBoost() * (i + 1)) / 100f);
                            return;
                        }
                    }
                }
        ));
    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_JUGGERNAUT, 1L);
        return baseCost;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 200;
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
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(DAMAGE_BOOST + DAMAGE_BOOST_INCREASE_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(DAMAGE_BOOST + DAMAGE_BOOST_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(HEALTH_BOOST + HEALTH_BOOST_INCREASE_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(HEALTH_BOOST + HEALTH_BOOST_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                )
        );
    }

    private float getCalculatedHealthBoost() {
        return getHealthBoost() / 100f;
    }

    private float getDamageBoost() {
        return DAMAGE_BOOST + DAMAGE_BOOST_INCREASE_PER_UPGRADE * getTitleLevel();
    }

    private float getHealthBoost() {
        return HEALTH_BOOST + HEALTH_BOOST_INCREASE_PER_UPGRADE * getTitleLevel();
    }

}

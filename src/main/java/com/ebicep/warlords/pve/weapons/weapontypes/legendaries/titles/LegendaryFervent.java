package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.data.annotation.Transient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LegendaryFervent extends AbstractLegendaryWeapon implements PassiveCounter {

    private static final class FerventSession {
        double damageTaken;
        int damageBoost;
        RegularCooldown<LegendaryFervent> stackCooldown;
    }

    public static final int DAMAGE_BOOST = 5;
    public static final int DAMAGE_TO_TAKE = 5000;
    public static final int DURATION = 45;

    public static final int ABILITY_STRIKE_DAMAGE_BOOST = 100;
    public static final int ABILITY_STRIKE_DAMAGE_BOOST_PER_UPGRADE = 20;
    public static final int ABILITY_DURATION = 12;
    public static final int ABILITY_DURATION_PER_UPGRADE = 1;

    public static final int MAX_STACKS = 3;

    @Transient
    private int passiveCooldown = 0;

    public LegendaryFervent() {
    }

    public LegendaryFervent(UUID uuid) {
        super(uuid);
    }

    public LegendaryFervent(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Gain a " + DAMAGE_BOOST + "% damage boost for " + DURATION + " seconds when you lose " + NumberFormat.addCommas(DAMAGE_TO_TAKE) +
                                        " health (Pre damage reduction). Maximum 3 stacks.",
                                NamedTextColor.GRAY
                        )
                        .append(Component.newline())
                        .append(Component.newline())
                        .append(Component.text("When at max stacks, shift for 1 second to consume all 3 stacks and your strikes deal "))
                        .append(formatTitleUpgrade(ABILITY_STRIKE_DAMAGE_BOOST + ABILITY_STRIKE_DAMAGE_BOOST_PER_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text(" more damage for "))
                        .append(formatTitleUpgrade(ABILITY_DURATION + ABILITY_DURATION_PER_UPGRADE * getTitleLevel()))
                        .append(Component.text(" seconds. Can be triggered every 40 seconds."));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.FERVENT;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 170;
    }

    @Override
    protected float getHealthBonusValue() {
        return 800;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 10;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 5;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 10;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        this.passiveCooldown = 0;

        final FerventSession session = new FerventSession();

        player.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHealingFinal(WarlordsDamageHealingFinalEvent event) {
                if (!event.getWarlordsEntity().equals(player)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                if (player.getCooldownManager().hasCooldownFromName("Fervent Ability")) {
                    return;
                }

                if ((session.damageTaken += event.getValueBeforeAllReduction()) >= DAMAGE_TO_TAKE) {
                    session.damageTaken = 0;
                    session.damageBoost = Math.min(MAX_STACKS, session.damageBoost + 1);

                    if (session.stackCooldown == null || !player.getCooldownManager().hasCooldown(session.stackCooldown)) {
                        RegularCooldown<LegendaryFervent> regularCooldown = new RegularCooldown<>(
                                "Fervent 1",
                                "FER 1",
                                LegendaryFervent.class,
                                null,
                                player,
                                CooldownTypes.WEAPON,
                                cooldownManager -> {
                                },
                                cooldownManager -> {
                                    session.stackCooldown = null;
                                    session.damageBoost = 0;
                                },
                                DURATION * 20
                        );
                        regularCooldown.addModifier(
                                Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                                (e, currentDamageValue) -> {
                                    currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getTitleName(), 1 + session.damageBoost * DAMAGE_BOOST / 100f);
                                }
                        );
                        session.stackCooldown = regularCooldown;
                        player.getCooldownManager().addCooldown(regularCooldown);
                    } else {
                        session.stackCooldown.setTicksLeft(DURATION * 20);
                        session.stackCooldown.setName("Fervent " + session.damageBoost);
                        session.stackCooldown.setNameAbbreviation("FER " + session.damageBoost);
                    }
                }
            }

        });

        new GameRunnable(player.getGame()) {

            int shiftTickTime = 0;

            @Override
            public void run() {
                if (passiveCooldown > 0) {
                    passiveCooldown--;
                    if (passiveCooldown <= 0) {
                        shiftTickTime = 0;
                    }
                    return;
                }
                if (session.stackCooldown == null || !player.getCooldownManager().hasCooldown(session.stackCooldown) || !session.stackCooldown.getName().equals("Fervent 3")) {
                    return;
                }
                if (player.isSneaking()) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, .5f + .05f * shiftTickTime);
                    shiftTickTime++;
                    if (shiftTickTime == 20) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        player.getCooldownManager().removeCooldown(session.stackCooldown);
                        player.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Fervent Ability",
                                "FERVENT",
                                LegendaryFervent.class,
                                null,
                                player,
                                CooldownTypes.WEAPON,
                                cooldownManager -> {
                                },
                                cooldownManager -> {
                                },
                                (ABILITY_DURATION + ABILITY_DURATION_PER_UPGRADE * getTitleLevel()) * 20
                        ).addModifier(
                                Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                                (event, currentDamageValue) -> {
                                    if (!event.getSource().equals(player)) {
                                        return;
                                    }
                                    if (event.isHealingInstance()) {
                                        return;
                                    }
                                    if (!event.getCause().contains("Strike")) {
                                        return;
                                    }

                                    float strikeDamageBoost = 1 + (ABILITY_STRIKE_DAMAGE_BOOST + ABILITY_STRIKE_DAMAGE_BOOST_PER_UPGRADE * getTitleLevel()) / 100f;
                                    currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getTitleName(), strikeDamageBoost);
                                }
                        ));
                        passiveCooldown = 40 * GameRunnable.SECOND;
                    }
                } else {
                    shiftTickTime = 0;
                }
            }
        }.runTaskTimer(0, 0);
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 190;
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
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(ABILITY_STRIKE_DAMAGE_BOOST + ABILITY_STRIKE_DAMAGE_BOOST_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(ABILITY_STRIKE_DAMAGE_BOOST + ABILITY_STRIKE_DAMAGE_BOOST_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(ABILITY_DURATION + ABILITY_DURATION_PER_UPGRADE * getTitleLevel()),
                        formatTitleUpgrade(ABILITY_DURATION + ABILITY_DURATION_PER_UPGRADE * getTitleLevelUpgraded()
                        )
                )
        );
    }

    @Override
    public int getCounter() {
        return passiveCooldown / 20;
    }

}

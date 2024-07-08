package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.google.common.util.concurrent.AtomicDouble;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LegendaryFervent extends AbstractLegendaryWeapon implements PassiveCounter {

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
                                        " health (Post damage reduction). Maximum 3 stacks.",
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
    protected float getMeleeDamageMaxValue() {
        return 190;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        this.passiveCooldown = 0;
        final AtomicDouble damageTaken = new AtomicDouble(0);
        final AtomicInteger damageBoost = new AtomicInteger(0);
        final AtomicReference<RegularCooldown<LegendaryFervent>> cooldown = new AtomicReference<>(null);

        player.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHealing(WarlordsDamageHealingEvent event) {
                if (!event.getSource().equals(player)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                if (player.getCooldownManager().hasCooldownFromName("Fervent Ability")) {
                    if (!event.getCause().contains("Strike")) {
                        return;
                    }
                    float strikeDamageBoost = 1 + (ABILITY_STRIKE_DAMAGE_BOOST + ABILITY_STRIKE_DAMAGE_BOOST_PER_UPGRADE * getTitleLevel()) / 100f;
                    event.setMin(event.getMin() * strikeDamageBoost);
                    event.setMax(event.getMax() * strikeDamageBoost);
                }
            }

            @EventHandler
            public void onDamageHealingFinal(WarlordsDamageHealingFinalEvent event) {
                if (!event.getWarlordsEntity().equals(player)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                if (damageTaken.addAndGet(event.getValue()) >= DAMAGE_TO_TAKE) {
                    damageTaken.set(0);
                    damageBoost.set(Math.min(MAX_STACKS, damageBoost.get() + 1));

                    if (cooldown.get() == null || !player.getCooldownManager().hasCooldown(cooldown.get())) {
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
                                    cooldown.set(null);
                                    damageBoost.set(0);
                                },
                                DURATION * 20
                        ) {
                            @Override
                            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                return currentDamageValue * (1 + damageBoost.get() * DAMAGE_BOOST / 100f);
                            }
                        };
                        cooldown.set(regularCooldown);
                        player.getCooldownManager().addCooldown(regularCooldown);
                    } else {
                        cooldown.get().setTicksLeft(DURATION * 20);
                        cooldown.get().setName("Fervent " + damageBoost.get());
                        cooldown.get().setNameAbbreviation("FER " + damageBoost.get());
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
                if (cooldown.get() == null || !player.getCooldownManager().hasCooldown(cooldown.get()) || !cooldown.get().getName().equals("Fervent 3")) {
                    return;
                }
                if (player.isSneaking()) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, .5f + .05f * shiftTickTime);
                    shiftTickTime++;
                    if (shiftTickTime == 20) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        player.getCooldownManager().removeCooldown(cooldown.get());
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
    public LegendaryTitles getTitle() {
        return LegendaryTitles.FERVENT;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 170;
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
    public int getCounter() {
        return passiveCooldown / 20;
    }
}

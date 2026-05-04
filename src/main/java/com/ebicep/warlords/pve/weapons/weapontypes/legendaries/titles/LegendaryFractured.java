package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LegendaryFractured extends AbstractLegendaryWeapon {

    public static final int STORED_CRIT_DAMAGE = 30;
    public static final int STORED_CRIT_DAMAGE_PER_UPGRADE = 5;
    public static final float DETONATION_DELAY = 3;
    public static final float DETONATION_DELAY_DECREASE_PER_UPGRADE = .2f;

    public LegendaryFractured() {
    }

    public LegendaryFractured(UUID uuid) {
        super(uuid);
    }

    public LegendaryFractured(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Critical hits fracture enemies, storing ", NamedTextColor.GRAY)
                .append(formatTitleUpgrade(getStoredCritDamage(), "%"))
                .append(Component.text(" of the final damage dealt. After "))
                .append(formatTitleUpgrade(getDetonationDelay(), "s"))
                .append(Component.text(", the stored damage detonates as true damage."));
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(getStoredCritDamage(), "%"),
                        formatTitleUpgrade(getStoredCritDamageUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(getDetonationDelay(), "s"),
                        formatTitleUpgrade(getDetonationDelayUpgraded(), "s")
                )
        );
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.FRACTURED;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 150;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 180;
    }

    @Override
    protected float getCritChanceValue() {
        return 25;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 250;
    }

    @Override
    protected float getHealthBonusValue() {
        return 450;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 6;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 10;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getTitleName(),
                null,
                LegendaryFractured.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cooldownManager -> {
                },
                false
        ).addModifier(Modifier.ON_OUTGOING_DAMAGE, (event, currentDamageValue, isCrit) -> {
            if (!event.isDamageInstance()) {
                return;
            }
            if (!isCrit) {
                return;
            }
            if (event.getSource() != player) {
                return;
            }
            if (event.getWarlordsEntity() == player) {
                return;
            }
            if (event.getFlags().contains(InstanceFlags.RECURSIVE)) {
                return;
            }
            if (currentDamageValue <= 0) {
                return;
            }

            WarlordsEntity target = event.getWarlordsEntity();
            float storedDamage = currentDamageValue * getStoredCritDamage() / 100f;
            int delayTicks = Math.round(getDetonationDelay() * GameRunnable.SECOND);

            new GameRunnable(player.getGame()) {

                @Override
                public void run() {
                    if (!player.isActive() || player.isDead()) {
                        return;
                    }
                    if (!target.isActive() || target.isDead()) {
                        return;
                    }

                    target.addInstance(InstanceBuilder
                            .damage()
                            .cause(getTitleName())
                            .source(player)
                            .value(storedDamage)
                            .critChance(0)
                            .critMultiplier(100)
                            .flags(
                                    InstanceFlags.RECURSIVE,
                                    InstanceFlags.TRUE_DAMAGE,
                                    InstanceFlags.IGNORE_CRIT_MODIFIERS
                            )
                    );

                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1, 1.4f);
                }
            }.runTaskLater(delayTicks);
        }));
    }

    private int getStoredCritDamage() {
        return STORED_CRIT_DAMAGE + STORED_CRIT_DAMAGE_PER_UPGRADE * getTitleLevel();
    }

    private int getStoredCritDamageUpgraded() {
        return STORED_CRIT_DAMAGE + STORED_CRIT_DAMAGE_PER_UPGRADE * getTitleLevelUpgraded();
    }

    private float getDetonationDelay() {
        return DETONATION_DELAY - DETONATION_DELAY_DECREASE_PER_UPGRADE * getTitleLevel();
    }

    private float getDetonationDelayUpgraded() {
        return DETONATION_DELAY - DETONATION_DELAY_DECREASE_PER_UPGRADE * getTitleLevelUpgraded();
    }

}

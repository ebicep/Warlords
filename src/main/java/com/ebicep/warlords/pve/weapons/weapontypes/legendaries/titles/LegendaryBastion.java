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
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.springframework.data.annotation.Transient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LegendaryBastion extends AbstractLegendaryWeapon {

    public static final int AURA_RADIUS_BLOCKS = 8;

    public static final float ALLY_DR_PERCENT_BASE = 20f;
    public static final float ALLY_DR_INC_PER_LEVEL = 2f;

    public static final int REDIRECT_RATIO_PERCENT = 50;

    public static final float REDIRECT_CAP_PERCENT_BASE = 8f;
    public static final float REDIRECT_CAP_INC_PER_LEVEL = 1f;

    @Transient
    private final Set<UUID> auraAttached = ConcurrentHashMap.newKeySet();
    @Transient
    private double redirectUsedThisSecond = 0.0;
    @Transient
    private int capResetTicks = 20;
    @Transient
    private double redirectCapCached = 0.0;

    public LegendaryBastion() {

    }

    public LegendaryBastion(java.util.UUID uuid) {
        super(uuid);
    }

    public LegendaryBastion(AbstractLegendaryWeapon copy) {
        super(copy);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Create a Bastion aura of " + AURA_RADIUS_BLOCKS + " blocks around you, allies inside take ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(getAllyDrPercent(), "%"))
                        .append(Component.text(" less damage. " + REDIRECT_RATIO_PERCENT + "% of damage prevented by the aura is redirected to you, up to ", NamedTextColor.GRAY))
                        .append(formatTitleUpgrade(getRedirectCapPercent(), "%"))
                        .append(Component.text(" of your max health per second.", NamedTextColor.GRAY));
    }

    private float getAllyDrPercent() {
        return ALLY_DR_PERCENT_BASE + ALLY_DR_INC_PER_LEVEL * getTitleLevel();
    }

    private float getRedirectCapPercent() {
        return REDIRECT_CAP_PERCENT_BASE + REDIRECT_CAP_INC_PER_LEVEL * getTitleLevel();
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.BASTION;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 150;
    }

    @Override
    protected float getHealthBonusValue() {
        return 1000;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 6;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return 4;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 4;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Bastion",
                null,
                LegendaryBastion.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cooldownManager -> {},
                false
        ));

        new GameRunnable(player.getGame()) {
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    redirectUsedThisSecond = 0;
                    capResetTicks = 20;
                    redirectCapCached = 0;
                    auraAttached.clear();
                    return;
                }

                redirectCapCached = player.getMaxHealth() * (getRedirectCapPercent() / 100.0);

                if (--capResetTicks <= 0) {
                    redirectUsedThisSecond = 0;
                    capResetTicks = 20;
                }

                List<WarlordsEntity> teammates = PlayerFilter.playingGame(player.getGame()).stream()
                                                             .filter(wp -> wp != player && wp.isTeammate(player) && !wp.isDead())
                                                             .toList();

                Set<UUID> current = new HashSet<>();
                Location pl = player.getLocation();
                for (WarlordsEntity ally : teammates) {
                    if (ally.getLocation().distanceSquared(pl) <= (AURA_RADIUS_BLOCKS * AURA_RADIUS_BLOCKS)) {
                        current.add(ally.getUuid());
                        if (auraAttached.add(ally.getUuid())) {
                            attachAuraToAlly(player, ally);
                        }
                    }
                }

                auraAttached.removeIf(id -> !current.contains(id));
            }
        }.runTaskTimer(0, 1);
    }

    private void attachAuraToAlly(WarlordsEntity owner, WarlordsEntity ally) {
        ally.getCooldownManager().removeCooldownByName("Bastion Ally");
        ally.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Bastion Ally",
                null,
                LegendaryBastion.class,
                null,
                ally,
                CooldownTypes.WEAPON,
                cooldownManager -> {},
                false
        ).addModifier(Modifier.INCOMING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
            if (owner.isDead()) {
                return;
            }

            Location ol = owner.getLocation();
            Location al = ally.getLocation();

            if (al.distanceSquared(ol) > (AURA_RADIUS_BLOCKS * AURA_RADIUS_BLOCKS)) {
                return;
            }

            float dr = getAllyDrPercent() / 100f;
            float reduced = currentDamageValue.getCalculatedValue() * (1f - dr);
            float prevented = currentDamageValue.getCalculatedValue() - reduced;

            if (prevented > 0) {
                double redirectCapRemain = Math.max(0.0, owner.getMaxHealth() * (getRedirectCapPercent() / 100.0) - redirectUsedThisSecond);
                double toRedirect = Math.min(redirectCapRemain, prevented * (REDIRECT_RATIO_PERCENT / 100.0));
                if (toRedirect > 0) {
                    owner.addInstance(InstanceBuilder
                            .damage()
                            .cause("Bastion")
                            .source(ally)
                            .min((float) toRedirect)
                            .max((float) toRedirect)
                            .flags(InstanceFlags.RECURSIVE, InstanceFlags.REFLECTIVE_DAMAGE, InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST)
                    );
                    redirectUsedThisSecond += toRedirect;
                }
            }
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getTitleName(), 1f - dr);
                }
        ));
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
        return 175;
    }

    @Override
    public java.util.List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(ALLY_DR_PERCENT_BASE + ALLY_DR_INC_PER_LEVEL * getTitleLevel(), "%"),
                        formatTitleUpgrade(ALLY_DR_PERCENT_BASE + ALLY_DR_INC_PER_LEVEL * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(REDIRECT_CAP_PERCENT_BASE + REDIRECT_CAP_INC_PER_LEVEL * getTitleLevel(), "%"),
                        formatTitleUpgrade(REDIRECT_CAP_PERCENT_BASE + REDIRECT_CAP_INC_PER_LEVEL * getTitleLevelUpgraded(), "%")
                )
        );
    }

}

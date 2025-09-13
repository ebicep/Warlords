package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.springframework.data.annotation.Transient;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LegendaryBastion extends AbstractLegendaryWeapon implements PassiveCounter {

    public static final int AURA_RADIUS_BLOCKS = 8;

    public static final float ALLY_DR_PERCENT_BASE = 10f;
    public static final float ALLY_DR_INC_PER_LEVEL = 2f;

    public static final int REDIRECT_RATIO_PERCENT = 25;

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

    public LegendaryBastion(java.util.UUID uuid) { super(uuid); }
    public LegendaryBastion(AbstractLegendaryWeapon copy) { super(copy); }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Create a Bastion aura of " + AURA_RADIUS_BLOCKS + " blocks around you, allies inside take ", NamedTextColor.GRAY)
                .append(formatTitleUpgrade(getAllyDrPercent(), "%"))
                .append(Component.text(" less damage. " + REDIRECT_RATIO_PERCENT + "% of damage prevented by the aura is redirected to you, up to ", NamedTextColor.GRAY))
                .append(formatTitleUpgrade(getRedirectCapPercent(), "%"))
                .append(Component.text(" of your max health per second.", NamedTextColor.GRAY));
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
                cm -> {},
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
                    if (ally.getLocation().getWorld() != pl.getWorld()) continue;
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
        ally.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Bastion Aura",
                null,
                LegendaryBastion.class,
                null,
                ally,
                CooldownTypes.WEAPON,
                cm -> {},
                false
        ) {
            @Override
            public float modifyDamageAfterInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (owner.isDead()) return currentDamageValue;
                Location ol = owner.getLocation();
                Location al = ally.getLocation();
                if (ol.getWorld() != al.getWorld()) return currentDamageValue;
                if (al.distanceSquared(ol) > (AURA_RADIUS_BLOCKS * AURA_RADIUS_BLOCKS)) return currentDamageValue;

                float dr = getAllyDrPercent() / 100f;
                float reduced = currentDamageValue * (1f - dr);
                float prevented = currentDamageValue - reduced;

                if (prevented > 0) {
                    double redirectCapRemain = Math.max(0.0, owner.getMaxHealth() * (getRedirectCapPercent() / 100.0) - redirectUsedThisSecond);
                    double toRedirect = Math.min(redirectCapRemain, prevented * (REDIRECT_RATIO_PERCENT / 100.0));
                    if (toRedirect > 0) {
                        WarlordsEntity src = event.getSource() != null ? event.getSource() : ally;
                        owner.addInstance(com.ebicep.warlords.player.ingame.instances.InstanceBuilder
                                .damage()
                                .cause("Bastion")
                                .source(src)
                                .min((float) toRedirect)
                                .max((float) toRedirect)
                        );
                        redirectUsedThisSecond += toRedirect;
                    }
                }

                return reduced;
            }
        });
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
    protected float getMeleeDamageMaxValue() { return 180; }
    @Override
    protected float getCritChanceValue()     { return 25; }
    @Override
    protected float getCritMultiplierValue() { return 175; }
    @Override
    protected float getHealthBonusValue()    { return 1000; }
    @Override
    protected float getSpeedBonusValue()     { return 6; }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 4;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return 4;
    }

    @Override
    public int getCounter() {
        if (redirectCapCached <= 0) return 0;
        double remaining = Math.max(0.0, redirectCapCached - redirectUsedThisSecond);
        return (int) Math.ceil((remaining / redirectCapCached) * 100.0);
    }
}

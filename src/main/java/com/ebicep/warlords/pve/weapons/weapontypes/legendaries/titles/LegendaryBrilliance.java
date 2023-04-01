package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.springframework.data.annotation.Transient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class LegendaryBrilliance extends AbstractLegendaryWeapon implements PassiveCooldown {

    public static final int HEALING_BOOST = 25;
    public static final float HEALING_BOOST_PER_UPGRADE = 6.25f;
    public static final int COOLDOWN = 30;
    public static final float COOLDOWN_INCREASE_PER_UPGRADE = -1.5f;

    @Transient
    private final AtomicReference<Instant> lastActivated = new AtomicReference<>(Instant.now().minus(COOLDOWN, ChronoUnit.SECONDS));

    public LegendaryBrilliance() {
    }

    public LegendaryBrilliance(UUID uuid) {
        super(uuid);
    }

    public LegendaryBrilliance(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_SPIDERS_BURROW, 1L);
        return baseCost;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        float healBoost = 1 + (HEALING_BOOST + HEALING_BOOST_PER_UPGRADE * getTitleLevel()) / 100f;
        // using runnable not on dmg event bc player can have <30% hp without taking dmg after cooldown is refreshed
        new GameRunnable(player.getGame()) {

            @Override
            public void run() {
                if (Instant.now().isBefore(lastActivated.get())) {
                    return;
                }
                if (player.getHealth() < player.getMaxHealth() * .3) {
                    giveHealingBoostCooldown();
                }
            }

            private void giveHealingBoostCooldown() {
                lastActivated.set(Instant.now().plus((long) (COOLDOWN + COOLDOWN_INCREASE_PER_UPGRADE * getTitleLevel()), ChronoUnit.SECONDS));
                player.getCooldownManager().addCooldown(new RegularCooldown<>(
                        "Brilliance",
                        "BRILL",
                        LegendaryBrilliance.class,
                        null,
                        player,
                        CooldownTypes.WEAPON,
                        cooldownManager -> {
                        },
                        200
                ) {

                    // incoming healing
                    @Override
                    public float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                        return currentHealValue * 1.25f;
                    }

                    // outgoing healing
                    @Override
                    public float doBeforeHealFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                        return currentHealValue * healBoost;
                    }
                });
            }

        }.runTaskTimer(200, 20);
    }


    @Override
    public String getPassiveEffect() {
        float outgoingHealingBoost = HEALING_BOOST + HEALING_BOOST_PER_UPGRADE * getTitleLevel();
        float cooldown = COOLDOWN + COOLDOWN_INCREASE_PER_UPGRADE * getTitleLevel();
        return "When HP falls below 30%, incoming healing increases by 50% and outgoing healing increases by " +
                formatTitleUpgrade(outgoingHealingBoost, "%") +
                " for 10s. Can be triggered once every " + formatTitleUpgrade(cooldown, "s") + ".";
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.BRILLIANCE;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 180;
    }

    @Override
    protected float getHealthBonusValue() {
        return 1200;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 8;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return 3;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 5;
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
        return 170;
    }

    @Override
    public List<Pair<String, String>> getPassiveEffectUpgrade() {
        return Arrays.asList(new Pair<>(
                        formatTitleUpgrade(HEALING_BOOST + HEALING_BOOST_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(HEALING_BOOST + HEALING_BOOST_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(COOLDOWN + COOLDOWN_INCREASE_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(COOLDOWN + COOLDOWN_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                )
        );
    }

    @Override
    public int getTickCooldown() {
        if (Instant.now().isBefore(lastActivated.get())) {
            return (int) ChronoUnit.SECONDS.between(Instant.now(), lastActivated.get()) * 20;
        }
        return 0;
    }
}

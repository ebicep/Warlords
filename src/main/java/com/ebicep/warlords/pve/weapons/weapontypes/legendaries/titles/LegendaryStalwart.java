package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import org.bukkit.ChatColor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class LegendaryStalwart extends AbstractLegendaryWeapon {

    public static int REDUCTION_DURATION = 5;
    public static int COOLDOWN = 30;

    public LegendaryStalwart() {
    }

    public LegendaryStalwart(UUID uuid) {
        super(uuid);
    }

    public LegendaryStalwart(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getPassiveEffect() {
        return "For every 10% of HP under 80%, gain an additional 7.5% damage reduction. Maximum 30% Damage Reduction." +
                "\n\nIf your HP is currently higher than 80% and you will die from the next source of damage, your " +
                "health will be set to 5% of your max HP and gain 99% damage reduction for 5 seconds. Can be triggered every 30 seconds.";
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 160;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        AtomicReference<Instant> lastActivated = new AtomicReference<>(Instant.now().minus(COOLDOWN, ChronoUnit.SECONDS));

        player.getCooldownManager().addCooldown(
                new PermanentCooldown<>(
                        "Stalwart",
                        null,
                        LegendaryStalwart.class,
                        null,
                        player,
                        CooldownTypes.BUFF,
                        cooldownManager -> {

                        },
                        false
                ) {

                    @Override
                    public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        if (player.getHealth() >= player.getMaxHealth() * .7) {
                            return currentDamageValue;
                        }
                        if (player.getHealth() <= player.getMaxHealth() * .4) {
                            return currentDamageValue * .7f;
                        }
                        float percentBelowMax = 1 - player.getHealth() / player.getMaxHealth();
                        float reduction = (float) (((int) (percentBelowMax * 10) - 2) * .075);
                        return currentDamageValue * (1 - reduction);
                    }

                    @Override
                    public float modifyDamageAfterAllFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                        if (player.getHealth() <= player.getMaxHealth() * .8) {
                            return currentDamageValue;
                        }
                        if (player.getHealth() - currentDamageValue > 0) {
                            return currentDamageValue;
                        }
                        if (Instant.now().isBefore(lastActivated.get())) {
                            return currentDamageValue;
                        }
                        lastActivated.set(Instant.now().plus(COOLDOWN, ChronoUnit.SECONDS));
                        player.setHealth(player.getMaxBaseHealth() * .05f);
                        player.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Stalwart",
                                "STALWART",
                                LegendaryDivine.class,
                                null,
                                player,
                                CooldownTypes.BUFF,
                                cooldownManager -> {
                                },
                                REDUCTION_DURATION * 20
                        ) {
                            @Override
                            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                return currentDamageValue * .01f;
                            }
                        });
                        player.sendMessage(ChatColor.GREEN + "Triggered Stalwart! +99% damage reduction for 5s.");
                        return 0;
                    }
                }
        );

    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.STALWART;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 140;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 160;
    }

    @Override
    protected float getHealthBonusValue() {
        return 1000;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 7;
    }
}

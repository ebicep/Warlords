package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class LegendaryJuggernaut extends AbstractLegendaryWeapon {

    public static final int BOOST = 10;
    public static final float BOOST_INCREASE_PER_UPGRADE = 1.25f;
    public static final int BOOST_CAP = 5;
    public static final int KILLS_PER_BOOST = 100;

    public LegendaryJuggernaut() {
    }

    public LegendaryJuggernaut(UUID uuid) {
        super(uuid);
    }

    public LegendaryJuggernaut(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
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
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        player.getGame().registerEvents(new Listener() {

            final AtomicInteger killCounter = new AtomicInteger();
            final AtomicInteger boosts = new AtomicInteger();
            final float healthBoost = player.getMaxBaseHealth() * getCalculatedBoost();
            PermanentCooldown<LegendaryJuggernaut> cooldown = null;

            @EventHandler
            public void onDeath(WarlordsDeathEvent event) {
                if (boosts.get() > BOOST_CAP) {
                    return;
                }
                WarlordsEntity killer = event.getKiller();

                if (killer == player) {
                    if (killCounter.incrementAndGet() >= KILLS_PER_BOOST) {
                        killCounter.set(0);
                        if (boosts.incrementAndGet() > BOOST_CAP) {
                            return;
                        }
                        boostDamageHealth();
                    }
                }
            }

            private void boostDamageHealth() {
                if (cooldown == null || !player.getCooldownManager().hasCooldown(cooldown)) {
                    player.getCooldownManager().addCooldown(cooldown = new PermanentCooldown<>(
                            "Juggernaut 1",
                            null,
                            LegendaryJuggernaut.class,
                            null,
                            player,
                            CooldownTypes.WEAPON,
                            cooldownManager -> {
                            },
                            false
                    ) {
                        @Override
                        public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            return currentDamageValue * (1 + boosts.get() * getCalculatedBoost());
                        }

                    });
                } else {
                    cooldown.setName("Juggernaut " + boosts.get());
                }
                player.setMaxBaseHealth(player.getMaxBaseHealth() + healthBoost);
            }
        });
    }

    private float getCalculatedBoost() {
        return (BOOST + BOOST_INCREASE_PER_UPGRADE * getTitleLevel()) / 100f;
    }

    @Override
    public String getPassiveEffect() {
        return "Gain " + formatTitleUpgrade(BOOST + BOOST_INCREASE_PER_UPGRADE * getTitleLevel(), "%") +
                " Damage and Health every 100 kills. Capped at 500 kills.";
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
        return 800;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 5;
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
    public List<Pair<String, String>> getPassiveEffectUpgrade() {
        return Collections.singletonList(new Pair<>(
                formatTitleUpgrade(BOOST + BOOST_INCREASE_PER_UPGRADE * getTitleLevel(), "%"),
                formatTitleUpgrade(BOOST + BOOST_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
        ));
    }
}

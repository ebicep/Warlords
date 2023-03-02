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

import java.util.*;

public class LegendaryJuggernaut extends AbstractLegendaryWeapon {

    public static final int BOOST = 10;
    public static final float BOOST_INCREASE_PER_UPGRADE = 1.25f;
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
            final float healthBoost = player.getMaxBaseHealth() * getCalculatedBoost();

            @EventHandler
            public void onDeath(WarlordsDeathEvent event) {
                WarlordsEntity killer = event.getKiller();

                if (killer == player) {
                    if (KILL_MILESTONES.contains(player.getMinuteStats().total().getKills())) {
                        player.setMaxBaseHealth(player.getMaxBaseHealth() + healthBoost);
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
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                int playerKills = player.getMinuteStats().total().getKills();
                for (int i = KILL_MILESTONES.size() - 1; i >= 0; i--) {
                    int killMilestone = KILL_MILESTONES.get(i);
                    if (playerKills >= killMilestone) {
                        return currentDamageValue * (1 + (getBoost() * (i + 1)) / 100f);
                    }
                }
                return currentDamageValue;
            }

        });
    }

    private float getBoost() {
        return BOOST + BOOST_INCREASE_PER_UPGRADE * getTitleLevel();
    }

    private float getCalculatedBoost() {
        return getBoost() / 100f;
    }

    @Override
    public String getPassiveEffect() {
        return "Gain a " + formatTitleUpgrade(BOOST + BOOST_INCREASE_PER_UPGRADE * getTitleLevel(), "%") +
                " Damage and Health boost when you hit the following kill milestones:\n\n" +
                "100 Kills\n" +
                "200 Kills\n" +
                "400 Kills\n" +
                "800 Kills\n" +
                "1600 Kills";
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

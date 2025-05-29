package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BurstChain implements SpecBoostManager.SpecBoost<BurstChain> {

    private float velocityIncreasePercentage;
    private float damageIncrease;
    private float radiusIncrease;
    private int guaranteedCrit;

    @Override
    public void init() {
        this.velocityIncreasePercentage = getValue("velocityIncreasePercentage", float.class);
        this.damageIncrease = getValue("damageIncrease", float.class);
        this.radiusIncrease = getValue("radiusIncrease", float.class);
        this.guaranteedCrit = getValue("guaranteedCrit", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "burstChain";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(velocityIncreasePercentage, damageIncrease, radiusIncrease, guaranteedCrit);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public BurstChain get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private final Map<UUID, Integer> flameBurstHit = new HashMap<>();
        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(FlameBurst.class).forEach(flameBurst -> {
                flameBurst.getProjectileSpeed().addMultiplicativeModifierAdd("Spec Boost", (velocityIncreasePercentage + 100) / 100);
                flameBurst.getDamageValues().getFlameBurstDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", damageIncrease / 100)
                );
                flameBurst.getSplashRadius().addAdditiveModifier("Spec Boost", radiusIncrease);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onDamageHeal(WarlordsDamageHealingEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!event.getCause().equals("Flame Burst")) {
                return;
            }
            Integer hitCount = flameBurstHit.get(event.getUUID());
            if (hitCount != null && hitCount >= guaranteedCrit) {
                return;
            }
            flameBurstHit.put(event.getUUID(), (hitCount == null ? 0 : hitCount) + 1);
            event.setCritChance(100);
        }

    }

}
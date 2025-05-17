package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ArcaneShield;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.flags.Unstunnable;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.event.EventHandler;

import java.util.List;

public class ArcaneShatter implements SpecBoostManager.SpecBoost {

    private float energyPerMelee;
    private float healthIncrease;
    private float range;
    private int stunTicks;

    @Override
    public void init() {
        this.energyPerMelee = getValue("energyPerMelee", float.class);
        this.healthIncrease = getValue("healthIncrease", float.class);
        this.range = getValue("range", float.class);
        this.stunTicks = getValue("stunTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "arcaneShatter";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(energyPerMelee, healthIncrease, range, stunTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
            playerClass.setEnergyPerHit(playerClass.getEnergyPerHit() + energyPerMelee);
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost (Base)", healthIncrease);
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
            playerClass.setEnergyPerHit(playerClass.getEnergyPerHit() - energyPerMelee);
            warlordsPlayer.getHealth().removeModifier("Spec Boost (Base)");
        }

        @EventHandler
        public void onWarlordsArcaneShieldBrokenEvent(ArcaneShield.WarlordsArcaneShieldBrokenEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            PlayerFilter.entitiesAround(warlordsEntity, range, range, range)
                        .aliveEnemiesOf(warlordsEntity)
                        .forEach(we -> {
                            if (we instanceof WarlordsNPC warlordsNPC && !(warlordsNPC.getMob() instanceof Unstunnable)) {
                                warlordsNPC.setStunTicks(stunTicks);
                            } else if (we instanceof WarlordsPlayer warlordsPlayer) {
                                warlordsPlayer.stun();
                                new GameRunnable(warlordsPlayer.getGame()) {
                                    @Override
                                    public void run() {
                                        warlordsPlayer.unstun();
                                    }
                                }.runTaskLater(stunTicks);
                            }
                        });
        }

    }

}

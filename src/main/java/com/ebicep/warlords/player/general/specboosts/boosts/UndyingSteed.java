package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.CripplingStrike;
import com.ebicep.warlords.events.player.ingame.WarlordsRespawnEvent;
import com.ebicep.warlords.game.option.pvp.HorseOption;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;

import java.util.List;

public class UndyingSteed implements SpecBoostManager.SpecBoost<UndyingSteed> {

    private float cripplingStrikeDamageIncreasePercent;
    private float horseHealth;

    @Override
    public void init() {
        this.cripplingStrikeDamageIncreasePercent = getValue("cripplingStrikeDamageIncreasePercent", float.class);
        this.horseHealth = getValue("horseHealth", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "undyingSteed";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(cripplingStrikeDamageIncreasePercent, horseHealth);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public UndyingSteed get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(CripplingStrike.class).forEach(cripplingStrike -> {
                cripplingStrike.getDamageValues().getStrikeDamage().forEachValue(floatModifiable ->
                        floatModifiable.addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Spec Boost", cripplingStrikeDamageIncreasePercent / 100)
                );
            });
            for (HorseOption horseOption : warlordsEntity.getGame().getOption(HorseOption.class)) {
                horseOption.getHorseForPlayer(warlordsEntity).getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", horseHealth);
            }
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            for (HorseOption horseOption : warlordsEntity.getGame().getOption(HorseOption.class)) {
                horseOption.getHorseForPlayer(warlordsEntity).getHealth().removeModifier("Spec Boost");
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onWarlordsRespawn(WarlordsRespawnEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            for (HorseOption horseOption : warlordsEntity.getGame().getOption(HorseOption.class)) {
                horseOption.getHorseForPlayer(warlordsEntity).setCurrentCooldown(0);
            }
        }

    }

}

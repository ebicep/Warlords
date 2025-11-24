package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.DivineBlessing;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;

import java.util.List;

public class HolyNova implements SpecBoostManager.SpecBoost<HolyNova> {

    private float divineBlessingFarRangeBlocks;
    private float divineBlessingHealingIncreasePercentFar;
    private int divineBlessingDamageToEnemies;

    @Override
    public void init() {
        this.divineBlessingFarRangeBlocks = getValue("divineBlessingFarRangeBlocks", float.class);
        this.divineBlessingHealingIncreasePercentFar = getValue("divineBlessingHealingIncreasePercentFar", float.class);
        this.divineBlessingDamageToEnemies = getValue("divineBlessingDamageToEnemies", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "holyNova";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                divineBlessingFarRangeBlocks,
                divineBlessingHealingIncreasePercentFar,
                divineBlessingDamageToEnemies
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public HolyNova get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                return;
            }
            if (!(cooldown.getCooldownObject() instanceof DivineBlessing.DivineBlessingData data) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            cooldown.addModifier(Modifier.MODIFY_OUTGOING_HEALING, (e, currentHealValue) -> {
                        if (e.getCause().equals("Divine Blessing") &&
                                e.getWarlordsEntity().getLocation().distanceSquared(warlordsEntity.getLocation()) > divineBlessingFarRangeBlocks * divineBlessingFarRangeBlocks
                        ) {
                            currentHealValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE,
                                    getStringName(),
                                    AbstractAbility.convertToMultiplicationDecimal(divineBlessingHealingIncreasePercentFar)
                            );
                        }
                    }
            );
            regularCooldown.addTriConsumer((cd, ticksLeft, ticksElapsed) -> {
                if (ticksElapsed == data.getDivineBlessing().getPostHealthTickDelay()) {
                    PlayerFilter.playingGame(warlordsEntity.getGame()).aliveEnemiesOf(warlordsEntity).forEach(teammate -> {
                        teammate.playSound(teammate.getLocation(), "shaman.earthlivingweapon.impact", 1, 0.55f);
                        teammate.playSound(teammate.getLocation(), "arcanist.divineblessing.impact", 0.2f, 1.75f);
                        teammate.addInstance(InstanceBuilder
                                .damage()
                                .cause(getStringName())
                                .source(warlordsEntity)
                                .value(divineBlessingDamageToEnemies)
                        );
                    });
                }
            });
        }

    }

}

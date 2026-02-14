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
    private int divineBlessingEnemiesHit;

    @Override
    public void init() {
        this.divineBlessingFarRangeBlocks = getValue("divineBlessingFarRangeBlocks", float.class);
        this.divineBlessingHealingIncreasePercentFar = getValue("divineBlessingHealingIncreasePercentFar", float.class);
        this.divineBlessingDamageToEnemies = getValue("divineBlessingDamageToEnemies", int.class);
        this.divineBlessingEnemiesHit = getValue("divineBlessingEnemiesHit", int.class);
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
                divineBlessingDamageToEnemies,
                divineBlessingEnemiesHit
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
                            currentHealValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                    getStringName(),
                                    AbstractAbility.convertToMultiplicationDecimal(divineBlessingHealingIncreasePercentFar)
                            );
                        }
                    }
            );
            DivineBlessing divineBlessing = data.getDivineBlessing();
            regularCooldown.addTriConsumer((cd, ticksLeft, ticksElapsed) -> {
                if (ticksElapsed == divineBlessing.getPostHealthTickDelay()) {
                    PlayerFilter.playingGame(warlordsEntity.getGame())
                                .aliveEnemiesOf(warlordsEntity)
                                .closestFirst(warlordsEntity)
                                .limit(divineBlessingEnemiesHit)
                                .forEach(enemy -> {
                                    enemy.playSound(enemy.getLocation(), "shaman.earthlivingweapon.impact", 1, 0.55f);
                                    enemy.playSound(enemy.getLocation(), "arcanist.divineblessing.impact", 0.2f, 1.75f);
                                    if (enemy.onHorse()) {
                                        divineBlessing.getAbilityStats().setNumberOfDismounts(divineBlessing.getAbilityStats().getNumberOfDismounts() + 1);
                                    }
                                    enemy.addInstance(InstanceBuilder
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

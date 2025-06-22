package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.DivineBlessing;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.HealingInstance;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.event.EventHandler;

import java.util.List;

public class HolyNova implements SpecBoostManager.SpecBoost<HolyNova> {

    private float divineBlessingHealingIncreasePercentFar;
    private int divineBlessingDamageToEnemies;
    private float divineBlessingCooldownIncreasePercent;
    private float divineBlessingFarRangeBlocks;

    @Override
    public void init() {
        this.divineBlessingHealingIncreasePercentFar = getValue("divineBlessingHealingIncreasePercentFar", float.class);
        this.divineBlessingDamageToEnemies = getValue("divineBlessingDamageToEnemies", int.class);
        this.divineBlessingCooldownIncreasePercent = getValue("divineBlessingCooldownIncreasePercent", float.class);
        this.divineBlessingFarRangeBlocks = getValue("divineBlessingFarRangeBlocks", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "holyNova";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                divineBlessingHealingIncreasePercentFar,
                divineBlessingDamageToEnemies,
                divineBlessingCooldownIncreasePercent,
                divineBlessingFarRangeBlocks
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
            warlordsPlayer.getAbilitiesMatching(DivineBlessing.class).forEach(divineBlessing -> {
                divineBlessing.getCooldown().addMultiplicativeModifierAdd("Spec Boost", divineBlessingCooldownIncreasePercent / 100);
            });
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
            cooldown.addExtraHealingInstance(new HealingInstance() {
                @Override
                public float modifyHealingFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                    if (event.getCause().equals("Divine Blessing") &&
                            event.getWarlordsEntity().getLocation().distanceSquared(warlordsEntity.getLocation()) > divineBlessingFarRangeBlocks * divineBlessingFarRangeBlocks
                    ) {
                        return currentHealValue * AbstractAbility.convertToMultiplicationDecimal(divineBlessingHealingIncreasePercentFar);
                    }
                    return currentHealValue;
                }
            });
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

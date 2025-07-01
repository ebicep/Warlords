package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.AstralPlague;
import com.ebicep.warlords.abilities.PoisonousHex;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFlag;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.DamageInstance;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

public class WitheringPlague implements SpecBoostManager.SpecBoost<WitheringPlague> {

    private float damageIncrease;

    @Override
    public void init() {
        this.damageIncrease = getValue("damageIncrease", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "witheringPlague";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damageIncrease);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public WitheringPlague get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(AstralPlague.class).forEach(astralPlague -> {
                astralPlague.setPierceShields(false);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!warlordsEntity.getCooldownManager().hasCooldown(AstralPlague.class) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            cooldown.addExtraDamageInstance(new DamageInstance() {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    WarlordsEntity victim = event.getWarlordsEntity();
                    if (victim.getSpecClass().specType != SpecType.DAMAGE && victim.getSpecClass().specType != SpecType.HEALER) {
                        return currentDamageValue;
                    }
                    int hexStacks = (int) new CooldownFilter<>(victim, RegularCooldown.class).filterCooldownClass(PoisonousHex.class).stream().count();
                    PoisonousHex hex = PoisonousHex.getFromHex(victim);
                    int maxStacks = hex.getMaxStacks();
                    if (hexStacks < maxStacks) {
                        return currentDamageValue;
                    }
                    return currentDamageValue * AbstractAbility.convertToMultiplicationDecimal(damageIncrease);
                }
            });
        }

    }

}
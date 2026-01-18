package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Particle;

import java.util.List;

public class Hourglass extends BaseSet {

    private int cooldownReductionPercent;
    private float freezeIntervalSeconds;
    private float freezeDurationSeconds;

    @Override
    public void init() {
        super.init();
        this.cooldownReductionPercent = getValue("cooldownReductionPercent", int.class);
        this.freezeIntervalSeconds = getValue("freezeIntervalSeconds", float.class);
        this.freezeDurationSeconds = getValue("freezeDurationSeconds", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "hourglass";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(cooldownReductionPercent, freezeIntervalSeconds, freezeDurationSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
                ability.getCooldown().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, getName(), -cooldownReductionPercent / 100f);
            }
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Hourglass",
                    null,
                    Hourglass.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {},
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (warlordsPlayer.isDead() || warlordsPlayer.getGame().getState() instanceof EndState) {
                            return;
                        }
                        if (ticksElapsed > 0 && ticksElapsed % (freezeIntervalSeconds * 20) == 0) {
                            EffectUtils.playCylinderAnimation(warlordsPlayer.getLocation(), 1.05, Particle.ITEM_SNOWBALL, 3);
                            warlordsPlayer.setStunTicks((int) (freezeDurationSeconds * 20));
                        }
                    }
            ));
        }

    }

}
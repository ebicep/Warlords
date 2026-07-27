package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.util.Vector;

import java.util.List;

public class SilentExecution extends BaseSet {

    private int damageIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.damageIncreasePercent = getValue("damageIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "silentExecution";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damageIncreasePercent);
    }

    private boolean isBehind(WarlordsPlayer attacker, WarlordsEntity target) {
        if (attacker.getWorld() != target.getWorld()) {
            return false;
        }
        Vector facing = target.getLocation().getDirection().setY(0);
        Vector toAttacker = attacker.getLocation().toVector().subtract(target.getLocation().toVector()).setY(0);
        if (facing.lengthSquared() == 0 || toAttacker.lengthSquared() == 0) {
            return false;
        }
        return facing.normalize().dot(toAttacker.normalize()) < -0.5;
    }

    public class Bonus implements SetBonus.Bonus {
        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(), null, SilentExecution.class, null, warlordsPlayer, CooldownTypes.ITEM,
                    cooldownManager -> {}, false
            ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                if (!isBehind(warlordsPlayer, event.getWarlordsEntity())) {
                    return;
                }
                currentDamageValue.addModifier(
                        FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                        getName(),
                        1 + damageIncreasePercent / 100f
                );
            }));
        }
    }
}

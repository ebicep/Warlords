package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.delta.AerialAegis;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Olympic extends BaseSet {

    private int jumpHeight;
    private int airResist;

    @Override
    public void init() {
        super.init();
        this.jumpHeight = getValue("jumpHeight", int.class);
        this.airResist = getValue("airResist", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "olympic";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(jumpHeight, airResist);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            new GameRunnable(warlordsPlayer.getGame()) {
                @Override
                public void run() {
                    warlordsPlayer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 120, jumpHeight, true, false));
                }
            }.runTaskTimer(0, 100);
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    AerialAegis.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {

                    },
                    false
            ).addModifier(
                    Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE,
                    (event, currentDamageValue) -> {
                        if (!warlordsPlayer.getEntity().isOnGround()) {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), airResist / 100f);
                        }
                    }
            ));
        }

    }

}
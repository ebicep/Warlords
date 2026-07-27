package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
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
    @Override public void init() { super.init(); jumpHeight = getValue("jumpHeight", int.class); airResist = getValue("airResist", int.class); }
    @Override public String getConfigFieldName() { return "olympic"; }
    @Override public Bonus create() { return new Bonus(); }
    @Override public List<Object> getVariables() { return List.of(jumpHeight, airResist); }
    public class Bonus implements SetBonus.Bonus {
        @Override public void apply(WarlordsPlayer player) {
            new GameRunnable(player.getGame()) { @Override public void run() { player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 120, jumpHeight, true, false)); } }.runTaskTimer(0, 100);
            player.getCooldownManager().addCooldown(new PermanentCooldown<>(getName(), null, Olympic.class, null, player, CooldownTypes.ITEM, manager -> {}, false)
                    .addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, value) -> {
                        if (!player.getEntity().isOnGround()) value.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 1 - airResist / 100f);
                    }));
        }
    }
}

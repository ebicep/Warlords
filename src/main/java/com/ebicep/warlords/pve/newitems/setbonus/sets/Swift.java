package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.player.ingame.motionsystem.speed.BaseToWalkingSpeedValueModifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import java.util.List;

public class Swift extends BaseSet {
    private int movementSpeedPercent;
    private int critChanceIncreasePercent;
    @Override public void init(){super.init();movementSpeedPercent=getValue("movementSpeedPercent",int.class);critChanceIncreasePercent=getValue("critChanceIncreasePercent",int.class);}
    @Override public String getConfigFieldName(){return "swift";}
    @Override public Bonus create(){return new Bonus();}
    @Override public List<Object> getVariables(){return List.of(movementSpeedPercent,critChanceIncreasePercent);}
    public class Bonus implements SetBonus.Bonus{
        @Override public void apply(WarlordsPlayer player){
            player.getCooldownManager().addCooldown(new PermanentCooldown<>(getName(),null,Swift.class,null,player,CooldownTypes.ITEM,m->{},false).addModifier(Modifier.MODIFY_OUTGOING_CRIT_CHANCE,(event,value)->{
                if(!(event.getAbility() instanceof WeaponAbilityIcon))return;
                float speedPercent=Math.max(0,(player.getSpeed().getLastValue()/BaseToWalkingSpeedValueModifier.BASE_PLAYER_WALK_SPEED-1)*100);
                float bonus=(float)Math.floor(speedPercent/movementSpeedPercent)*critChanceIncreasePercent;
                if(bonus>0)value.addModifier(FloatModifiable.ModifierType.ADDITIVE,getName(),bonus);
            }));
        }
    }
}

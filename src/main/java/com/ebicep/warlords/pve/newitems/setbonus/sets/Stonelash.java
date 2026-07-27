package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import java.util.List;

public class Stonelash extends BaseSet {
    private boolean knockbackImmune;
    private int energyGainPenaltyPercent;
    @Override public void init(){super.init();knockbackImmune=getValue("knockbackImmune",boolean.class);energyGainPenaltyPercent=getValue("energyGainPenaltyPercent",int.class);}
    @Override public String getConfigFieldName(){return "stonelash";}
    @Override public Bonus create(){return new Bonus();}
    @Override public List<Object> getVariables(){return List.of(energyGainPenaltyPercent);}
    public class Bonus implements SetBonus.Bonus{
        @Override public void apply(WarlordsPlayer player){float multiplier=1-energyGainPenaltyPercent/100f;player.getKnockback().addBaseModifier(knockbackImmune?100:0);player.getCooldownManager().addCooldown(new PermanentCooldown<>(getName(),null,Stonelash.class,null,player,CooldownTypes.ITEM,m->{},false).addModifier(Modifier.ENERGY_GAIN_PER_TICK,value->value.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,getName(),multiplier)).addModifier(Modifier.ENERGY_GAIN_PER_HIT,value->value.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,getName(),multiplier)));}
    }
}

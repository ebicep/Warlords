package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import java.util.List;

public class Soulforged extends BaseSet {
    private int healthThreshold, energyPerSecondBonusPercent, energyRegenDisabledBelowHealthPercent;
    @Override public void init(){super.init();healthThreshold=getValue("healthThreshold",int.class);energyPerSecondBonusPercent=getValue("energyPerSecondBonusPercent",int.class);energyRegenDisabledBelowHealthPercent=getValue("energyRegenDisabledBelowHealthPercent",int.class);}
    @Override public String getConfigFieldName(){return "soulforged";}
    @Override public Bonus create(){return new Bonus();}
    @Override public List<Object> getVariables(){return List.of(healthThreshold,energyPerSecondBonusPercent,energyRegenDisabledBelowHealthPercent);}
    public class Bonus implements SetBonus.Bonus{
        @Override public void apply(WarlordsPlayer player){player.getCooldownManager().addCooldown(new PermanentCooldown<>(getName(),null,Soulforged.class,null,player,CooldownTypes.ITEM,m->{},false).addModifier(Modifier.ENERGY_GAIN_PER_TICK,value->{if(player.isDead()||player.getMaxHealth()<=0)return;float health=player.getCurrentHealth()/player.getMaxHealth()*100f;if(health<energyRegenDisabledBelowHealthPercent)value.addModifier(FloatModifiable.ModifierType.OVERRIDING,getName(),0);else if(health>=healthThreshold)value.addModifier(FloatModifiable.ModifierType.ADDITIVE,getName(),energyPerSecondBonusPercent/20f);}));}
    }
}

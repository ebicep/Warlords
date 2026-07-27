package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import java.util.List;

public class ForsakenFlux extends BaseSet {
    private int nonUltimateCooldownReductionPercent, primaryDamagePenaltyPercent, primaryHealingPenaltyPercent;
    @Override public void init(){super.init();nonUltimateCooldownReductionPercent=getValue("nonUltimateCooldownReductionPercent",int.class);primaryDamagePenaltyPercent=getValue("primaryDamagePenaltyPercent",int.class);primaryHealingPenaltyPercent=getValue("primaryHealingPenaltyPercent",int.class);}
    @Override public String getConfigFieldName(){return "forsakenFlux";}
    @Override public Bonus create(){return new Bonus();}
    @Override public List<Object> getVariables(){return List.of(nonUltimateCooldownReductionPercent,primaryDamagePenaltyPercent,primaryHealingPenaltyPercent);}
    public class Bonus implements SetBonus.Bonus{
        @Override public void apply(WarlordsPlayer player){
            for(AbstractAbility ability:player.getAbilities())if(!(ability instanceof OrangeAbilityIcon))ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,getName(),1-nonUltimateCooldownReductionPercent/100f);
            player.getCooldownManager().addCooldown(new PermanentCooldown<>(getName(),null,ForsakenFlux.class,null,player,CooldownTypes.ITEM,m->{},false)
                    .addModifier(Modifier.MODIFY_OUTGOING_HEALING,(event,value)->{if(event.getAbility() instanceof WeaponAbilityIcon)value.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,getName(),1-primaryHealingPenaltyPercent/100f);})
                    .addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,(event,value)->{if(event.getAbility() instanceof WeaponAbilityIcon)value.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,getName(),1-primaryDamagePenaltyPercent/100f);}));
        }
    }
}

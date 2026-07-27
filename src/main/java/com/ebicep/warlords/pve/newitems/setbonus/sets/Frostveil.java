package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Frostveil extends BaseSet {
    private int freezeChance,duration,frozenDamageBoost;
    @Override public void init(){super.init();freezeChance=getValue("freezeChance",int.class);duration=getValue("duration",int.class);frozenDamageBoost=getValue("frozenDamageBoost",int.class);}
    @Override public String getConfigFieldName(){return "frostveil";}
    @Override public Bonus create(){return new Bonus();}
    @Override public List<Object> getVariables(){return List.of(freezeChance,duration,frozenDamageBoost);}
    public class Bonus implements SetBonus.Bonus{
        @Override public void apply(WarlordsPlayer player){player.getCooldownManager().addCooldown(new PermanentCooldown<>(getName(),null,Frostveil.class,null,player,CooldownTypes.ITEM,m->{},false)
                .addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,(event,value)->{if(event.getWarlordsEntity() instanceof WarlordsNPC npc&&npc.getStunTicks()>0)value.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,getName(),1+frozenDamageBoost/100f);})
                .addModifier(Modifier.ON_OUTGOING_DAMAGE,(event,value,crit)->{if(event.getFlags().contains(InstanceFlags.DOT)||!(event.getWarlordsEntity() instanceof WarlordsNPC npc)||npc.getMob() instanceof BossLike||ThreadLocalRandom.current().nextDouble()>freezeChance/100.0)return;npc.setStunTicks(duration*20);}));}
    }
}

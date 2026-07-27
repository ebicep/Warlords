package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Sound;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Oathkeeper extends BaseSet {
    private int meleeDamageIncreasePercent,meleeAttackTwiceChancePercent;
    @Override public void init(){super.init();meleeDamageIncreasePercent=getValue("meleeDamageIncreasePercent",int.class);meleeAttackTwiceChancePercent=getValue("meleeAttackTwiceChancePercent",int.class);}
    @Override public String getConfigFieldName(){return "oathkeeper";}
    @Override public Bonus create(){return new Bonus();}
    @Override public List<Object> getVariables(){return List.of(meleeDamageIncreasePercent,meleeAttackTwiceChancePercent);}
    public class Bonus implements SetBonus.Bonus{
        @Override public void apply(WarlordsPlayer player){player.getCooldownManager().addCooldown(new PermanentCooldown<>(getName(),null,Oathkeeper.class,null,player,CooldownTypes.ITEM,m->{},false).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,(event,value)->{if(!event.getCause().isEmpty())return;value.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,getName(),1+meleeDamageIncreasePercent/100f);if(event.getFlags().contains(InstanceFlags.RECURSIVE)||ThreadLocalRandom.current().nextDouble()>meleeAttackTwiceChancePercent/100.0)return;new GameRunnable(player.getGame()){@Override public void run(){player.playSound(player.getLocation(),Sound.BLOCK_BEACON_ACTIVATE,2,1.5f);event.getWarlordsEntity().addInstance(InstanceBuilder.damage().cause(event.getCause()).source(event.getSource()).min(event.getMin().getBaseValue()).max(event.getMax().getBaseValue()).critChance(event.getCritChance().getBaseValue()).critMultiplier(event.getCritMultiplier().getBaseValue()).flags(InstanceFlags.RECURSIVE));}}.runTaskLater(4);}));}
    }
}

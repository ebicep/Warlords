package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Ambulance extends BaseSet {
    private int allyHealthThresholdPercent,movementSpeedBonusPercent,healingBonusToAllyPercent;
    @Override public void init(){super.init();allyHealthThresholdPercent=getValue("allyHealthThresholdPercent",int.class);movementSpeedBonusPercent=getValue("movementSpeedBonusPercent",int.class);healingBonusToAllyPercent=getValue("healingBonusToAllyPercent",int.class);}
    @Override public String getConfigFieldName(){return "ambulance";}
    @Override public Bonus create(){return new Bonus();}
    @Override public List<Object> getVariables(){return List.of(allyHealthThresholdPercent,movementSpeedBonusPercent,healingBonusToAllyPercent);}
    public class Bonus implements SetBonus.Bonus{
        private final Set<WarlordsEntity> lowHealthAllies=new HashSet<>();
        @Override public void apply(WarlordsPlayer player){String speedName=getName()+" Speed";player.getCooldownManager().addCooldown(new PermanentCooldown<>(getName(),null,Ambulance.class,null,player,CooldownTypes.ITEM,m->{},false,(cooldown,ticks)->{if(ticks%10!=0)return;lowHealthAllies.forEach(a->a.removePotionEffect(PotionEffectType.GLOWING));lowHealthAllies.clear();PlayerFilter.entitiesAround(player,100,100,100).aliveTeammatesOfExcludingSelf(player).filter(a->a.getCurrentHealth()/a.getMaxHealth()*100f<=allyHealthThresholdPercent).forEach(a->{lowHealthAllies.add(a);a.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,15,1,false,false,false));});WarlordsEntity closest=PlayerFilter.entitiesAround(player,100,100,100).aliveTeammatesOfExcludingSelf(player).filter(lowHealthAllies::contains).closestFirst(player).findFirstOrNull();player.getSpeed().removeModifier(speedName);if(closest!=null&&isMovingToward(player,closest))player.addSpeedModifier(player,speedName,movementSpeedBonusPercent,15);}).addModifier(Modifier.MODIFY_OUTGOING_HEALING,(event,value)->{if(lowHealthAllies.contains(event.getWarlordsEntity()))value.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,getName(),1+healingBonusToAllyPercent/100f);}));}
        private boolean isMovingToward(WarlordsPlayer player,WarlordsEntity ally){Vector movement=player.getEntity().getVelocity().setY(0),toAlly=ally.getLocation().toVector().subtract(player.getLocation().toVector()).setY(0);return movement.lengthSquared()>=0.0001&&toAlly.lengthSquared()>0&&movement.normalize().dot(toAlly.normalize())>0.25;}
    }
}

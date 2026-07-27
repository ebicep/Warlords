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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import java.util.List;

public class Sacrifice extends BaseSet {
    private int selfReviveHealthPercent,allyHealthReductionPercent;
    private float reviveCooldownSeconds;
    @Override public void init(){super.init();selfReviveHealthPercent=getValue("selfReviveHealthPercent",int.class);allyHealthReductionPercent=getValue("allyHealthReductionPercent",int.class);reviveCooldownSeconds=getValue("reviveCooldownSeconds",float.class);}
    @Override public String getConfigFieldName(){return "sacrifice";}
    @Override public Bonus create(){return new Bonus();}
    @Override public List<Object> getVariables(){return List.of(selfReviveHealthPercent,allyHealthReductionPercent,reviveCooldownSeconds);}
    public class Bonus implements SetBonus.Bonus{
        private int cooldownTicks;
        @Override public void apply(WarlordsPlayer player){player.getCooldownManager().addCooldown(new PermanentCooldown<>(getName(),null,Sacrifice.class,null,player,CooldownTypes.ITEM,m->{},false,(cooldown,ticks)->{if(cooldownTicks>0)cooldownTicks--;}).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_ALL_MODIFIERS,(event,value,crit)->{if(cooldownTicks>0||player.getCurrentHealth()-value.getCalculatedValue()>0)return;WarlordsEntity ally=PlayerFilter.entitiesAround(player,100,100,100).aliveTeammatesOfExcludingSelf(player).closestFirst(player).findFirstOrNull();if(ally==null)return;value.addModifier(FloatModifiable.ModifierType.OVERRIDING,getName(),0);player.setCurrentHealth(player.getMaxHealth()*selfReviveHealthPercent/100f);ally.setCurrentHealth(Math.max(1,ally.getCurrentHealth()*(1-allyHealthReductionPercent/100f)));cooldownTicks=Math.round(reviveCooldownSeconds*20);player.playSound(player.getLocation(),Sound.ITEM_TOTEM_USE,1,.5f);ally.playSound(ally.getLocation(),Sound.ENTITY_WITHER_SPAWN,1,.5f);player.sendMessage(Component.text("You sacrificed "+ally.getName()+" to the unholy gods!",NamedTextColor.RED));}));}
    }
}

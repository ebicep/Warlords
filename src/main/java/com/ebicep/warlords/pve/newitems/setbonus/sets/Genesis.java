package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import java.util.List;

public class Genesis extends BaseSet {
    private int healthThreshold,maxHealthDamageMultiplier,cooldownSeconds;
    @Override public void init(){super.init();healthThreshold=getValue("healthThreshold",int.class);maxHealthDamageMultiplier=getValue("maxHealthDamageMultiplier",int.class);cooldownSeconds=getValue("cooldownSeconds",int.class);}
    @Override public String getConfigFieldName(){return "genesis";}
    @Override public Bonus create(){return new Bonus();}
    @Override public List<Object> getVariables(){return List.of(healthThreshold,maxHealthDamageMultiplier,cooldownSeconds);}
    public class Bonus implements SetBonus.Bonus{
        private int cooldownTicks;
        @Override public void apply(WarlordsPlayer player){player.getCooldownManager().addCooldown(new PermanentCooldown<>(getName(),null,Genesis.class,null,player,CooldownTypes.ITEM,m->{},false,(cooldown,ticks)->{if(cooldownTicks>0)cooldownTicks--;}).addModifier(Modifier.ON_INCOMING_DAMAGE,(event,damage,crit)->{if(cooldownTicks>0||player.isDead())return;float threshold=player.getMaxHealth()*healthThreshold/100f,predicted=player.getCurrentHealth()-damage;if(player.getCurrentHealth()<=threshold||predicted>=threshold)return;cooldownTicks=cooldownSeconds*20;Utils.playGlobalSound(player.getLocation(),Sound.BLOCK_BEACON_ACTIVATE,5,.7f);new GameRunnable(player.getGame()){@Override public void run(){if(player.isDead())return;EffectUtils.playFirework(player.getLocation(),FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.WHITE).withTrail().build());EffectUtils.strikeLightningInCylinder(player.getLocation(),10,false);PlayerFilter.entitiesAround(player,10,10,10).aliveEnemiesOf(player).forEach(enemy->enemy.addInstance(InstanceBuilder.damage().cause(getName()).source(player).value(player.getMaxHealth()*maxHealthDamageMultiplier/100f).flags(InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST)));}}.runTaskLater(40);}));}
    }
}

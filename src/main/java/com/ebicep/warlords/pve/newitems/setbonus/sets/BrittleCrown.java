package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class BrittleCrown extends BaseSet {
    private int insigniaGainBonusPercent,insigniaLossOnHitPercent;
    @Override public void init(){super.init();insigniaGainBonusPercent=getValue("insigniaGainBonusPercent",int.class);insigniaLossOnHitPercent=getValue("insigniaLossOnHitPercent",int.class);}
    @Override public String getConfigFieldName(){return "brittleCrown";}
    @Override public Bonus create(){return new Bonus();}
    @Override public List<Object> getVariables(){return List.of(insigniaGainBonusPercent,insigniaLossOnHitPercent);}
    public class Bonus implements SetBonus.Bonus{
        @Override public void apply(WarlordsPlayer player){AtomicBoolean killWindow=new AtomicBoolean(false);player.getGame().registerEvents(new Listener(){@EventHandler public void onDeath(WarlordsDeathEvent event){if(!Objects.equals(event.getKiller(),player)||event.getWarlordsEntity().getTeam().equals(player.getTeam()))return;killWindow.set(true);new GameRunnable(player.getGame()){@Override public void run(){killWindow.set(false);}}.runTaskLater(1);}@EventHandler public void onCurrency(WarlordsAddCurrencyEvent event){if(event.getWarlordsEntity().equals(player)&&killWindow.get())event.setCurrencyToAdd(event.getCurrencyToAdd()*(1+insigniaGainBonusPercent/100f));}});player.getCooldownManager().addCooldown(new PermanentCooldown<>(getName(),null,BrittleCrown.class,null,player,CooldownTypes.ITEM,m->{},false).addModifier(Modifier.ON_INCOMING_DAMAGE,(event,value,crit)->{int loss=(int)Math.floor(player.getCurrency()*insigniaLossOnHitPercent/100f);player.setCurrency(Math.max(0,player.getCurrency()-loss));player.sendMessage(Component.text("You lost "+loss+" ❂ insignia!",NamedTextColor.RED));}));}
    }
}

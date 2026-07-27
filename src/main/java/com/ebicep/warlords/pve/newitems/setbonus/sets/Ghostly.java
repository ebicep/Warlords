package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Ghostly extends BaseSet {
    private int dodgeChance,maxStacks;
    private float bonusPerStackPercent,stackDurationSeconds;
    @Override public void init(){super.init();dodgeChance=getValue("dodgeChance",int.class);bonusPerStackPercent=getValue("bonusPerStackPercent",float.class);stackDurationSeconds=getValue("stackDurationSeconds",float.class);maxStacks=getValue("maxStacks",int.class);}
    @Override public String getConfigFieldName(){return "ghostly";}
    @Override public Bonus create(){return new Bonus();}
    @Override public List<Object> getVariables(){return List.of(dodgeChance,bonusPerStackPercent,stackDurationSeconds,maxStacks);}
    public class Bonus implements SetBonus.Bonus{
        private final Deque<Integer> expirations=new ArrayDeque<>();
        private int elapsed;
        @Override public void apply(WarlordsPlayer player){
            player.getCooldownManager().addCooldown(new PermanentCooldown<>(getName(),null,Ghostly.class,null,player,CooldownTypes.ITEM,m->{},false,(cooldown,ticks)->{elapsed++;while(!expirations.isEmpty()&&expirations.peekFirst()<=elapsed)expirations.removeFirst();})
                    .addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,(event,value)->applyStacks(value))
                    .addModifier(Modifier.MODIFY_OUTGOING_HEALING,(event,value)->applyStacks(value)));
            player.getGame().registerEvents(new Listener(){@EventHandler public void onDamage(WarlordsDamageHealingEvent event){if(!event.getWarlordsEntity().equals(player)||event.isHealingInstance()||ThreadLocalRandom.current().nextDouble()>=dodgeChance/100.0)return;if(expirations.size()>=maxStacks)expirations.removeFirst();expirations.addLast(elapsed+Math.round(stackDurationSeconds*20));player.sendMessage(Component.text("Your "+getName()+" dodged ",NamedTextColor.GREEN).append(event.getSource().getColoredName()).append(Component.text("'s attack.")));event.setCancelled(true);}});
        }
        private void applyStacks(com.ebicep.warlords.util.warlords.modifiablevalues.MultiFloatModifiable value){if(!expirations.isEmpty())value.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,getName(),1+expirations.size()*bonusPerStackPercent/100f);}
    }
}

package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Gravemind extends BaseSet {
    public static final ItemStack CHESTPLATE=Utils.applyColorTo(Material.LEATHER_CHESTPLATE,255,200,0),LEGGINGS=Utils.applyColorTo(Material.LEATHER_LEGGINGS,255,200,0),BOOTS=Utils.applyColorTo(Material.LEATHER_BOOTS,255,200,0);
    private int summonChance;
    private float duration;
    @Override public void init(){super.init();summonChance=getValue("summonChance",int.class);duration=getValue("duration",float.class);}
    @Override public String getConfigFieldName(){return "gravemind";}
    @Override public Bonus create(){return new Bonus();}
    @Override public List<Object> getVariables(){return List.of(summonChance,duration);}
    public class Bonus implements SetBonus.Bonus{
        @Override public void apply(WarlordsPlayer player){player.getGame().registerEvents(new Listener(){@EventHandler public void onDeath(WarlordsDeathEvent event){if(!Objects.equals(event.getKiller(),player)||event.getWarlordsEntity().getTeam().equals(player.getTeam())||ThreadLocalRandom.current().nextDouble()>summonChance/100.0)return;spawnMob(player,event.getWarlordsEntity().getLocation());}});}
    }
    private void spawnMob(WarlordsPlayer player,Location location){Optional<PveOption> optional=player.getGame().getOption(PveOption.class).stream().findFirst();if(optional.isEmpty()||player.getTeam()==null)return;PveOption pve=optional.get();player.playSound(player.getLocation(),Sound.ENTITY_ZOMBIE_VILLAGER_CURE,.5f,.5f);player.sendMessage(Component.text("Your Gravemind summoned a mob for "+(int)duration+" seconds!",NamedTextColor.GREEN));AbstractMob mob=Mob.ZOMBIE_SWORDSMAN.createMob(location);mob.setEquipment(new Utils.SimpleEntityEquipment(HeadUtils.getHead(player.getUuid()),CHESTPLATE,LEGGINGS,BOOTS,mob.getEquipment().getItemInMainHand(),mob.getEquipment().getItemInOffHand()));mob.updateEquipment();Summoner.registerSummon(mob,player);pve.spawnNewMob(mob,player.getTeam());new GameRunnable(player.getGame()){@Override public void run(){if(pve.getMobs().contains(mob)){mob.getWarlordsNPC().die(mob.getWarlordsNPC(),WarlordsDeathEvent.DeathInfoBuilder.create().setForced(true));pve.despawnMob(mob);}}}.runTaskLater((long)(20*duration));}
}

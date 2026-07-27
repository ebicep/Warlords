package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.GameRunnable;
import java.util.List;

public class Regenerate extends BaseSet {
    @Override public String getConfigFieldName(){return "regenerate";}
    @Override public Bonus create(){return new Bonus();}
    @Override public List<Object> getVariables(){return List.of();}
    public class Bonus implements SetBonus.Bonus{
        @Override public void apply(WarlordsPlayer player){
            new GameRunnable(player.getGame()){
                private int ticks;
                @Override public void run(){
                    if(player.isDead())return;
                    player.setRegenTickTimer(1);
                    ticks++;
                    if(ticks%40!=0||player.getCurrentHealth()>=player.getMaxHealth())return;
                    int regen=ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES,"regenHealth",int.class);
                    player.getRegenPerSecond().setBaseValue(regen);
                    player.getRegenPerSecond().refresh();
                    player.setCurrentHealth(Math.min(player.getMaxHealth(),player.getCurrentHealth()+player.getRegenPerSecond().getCalculatedValue()));
                }
            }.runTaskTimer(0,1);
        }
    }
}

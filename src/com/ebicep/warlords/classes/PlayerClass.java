package com.ebicep.warlords.classes;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class PlayerClass {

    protected AbstractAbility weapon;
    protected AbstractAbility red;
    protected AbstractAbility purple;
    protected AbstractAbility blue;
    protected AbstractAbility orange;
    protected Player player;

    public PlayerClass(AbstractAbility weapon,AbstractAbility red, AbstractAbility purple, AbstractAbility blue, AbstractAbility orange, Player player) {
        this.weapon = weapon;
        this.red = red;
        this.purple = purple;
        this.blue = blue;
        this.orange = orange;
        this.player = player;
    }

    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if(player.getInventory().getHeldItemSlot() == 0) {
            weapon.onActivate(e);
        } else if(player.getInventory().getHeldItemSlot() == 1) {
            red.onActivate(e);
        } else if(player.getInventory().getHeldItemSlot() == 2) {
            purple.onActivate(e);
        } else if(player.getInventory().getHeldItemSlot() == 3) {
            blue.onActivate(e);
        } else if(player.getInventory().getHeldItemSlot() == 4) {
            orange.onActivate(e);
        }
    }

    public AbstractAbility getWeapon() {
        return weapon;
    }

    public void setWeapon(AbstractAbility weapon) {
        this.weapon = weapon;
    }

    public AbstractAbility getRed() {
        return red;
    }

    public void setRed(AbstractAbility red) {
        this.red = red;
    }

    public AbstractAbility getPurple() {
        return purple;
    }

    public void setPurple(AbstractAbility purple) {
        this.purple = purple;
    }

    public AbstractAbility getBlue() {
        return blue;
    }

    public void setBlue(AbstractAbility blue) {
        this.blue = blue;
    }

    public AbstractAbility getOrange() {
        return orange;
    }

    public void setOrange(AbstractAbility orange) {
        this.orange = orange;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}

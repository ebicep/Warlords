package com.ebicep.warlords.player;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.util.PlayerFilter;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CooldownManager {

    private WarlordsPlayer warlordsPlayer;
    private List<Cooldown> cooldowns;
    private int totalCooldowns = 0;

    public CooldownManager(WarlordsPlayer warlordsPlayer) {
        this.warlordsPlayer = warlordsPlayer;
        cooldowns = new ArrayList<>();
    }

    public boolean hasCooldownFromName(String name) {
        return cooldowns.stream().anyMatch(cooldown -> cooldown.getName().equalsIgnoreCase(name));
    }


    public boolean hasCooldown(Class cooldownClass) {
        return cooldowns.stream().anyMatch(cooldown -> cooldown.getCooldownClass() == cooldownClass);
    }

    public boolean hasCooldown(Object cooldownObject) {
        return cooldowns.stream().anyMatch(cooldown -> cooldown.getCooldownObject() == cooldownObject);
    }

    public List<Cooldown> getCooldownFromName(String name) {
        return cooldowns.stream().filter(cooldown -> cooldown.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
    }

    public List<Cooldown> getCooldown(Class cooldownClass) {
        return cooldowns.stream().filter(cooldown -> cooldown.getCooldownClass() == cooldownClass).collect(Collectors.toList());
    }

    public Optional<Cooldown> getCooldown(Object cooldownObject) {
        return cooldowns.stream().filter(cooldown -> cooldown.getCooldownObject() == cooldownObject).findAny();
    }

    public List<Cooldown> getCooldown(String name) {
        return cooldowns.stream().filter(cooldown -> cooldown.getActionBarName().contains(name)).collect(Collectors.toList());
    }

    public List<Cooldown> getCooldown(Class cooldownClass, String name) {
        return cooldowns.stream().filter(cooldown -> cooldown.getCooldownClass() == cooldownClass && cooldown.getActionBarName().contains(name)).collect(Collectors.toList());
    }

    public void reduceCooldowns() {
        for (int i = 0; i < cooldowns.size(); i++) {
            Cooldown cooldown = cooldowns.get(i);
            String name = cooldown.getActionBarName();
            Class cooldownClass = cooldown.getCooldownClass();
            Object cooldownObject = cooldown.getCooldownObject();

            cooldown.subtractTime(.05f);

            if (cooldown.getTimeLeft() <= 0) {
                if (cooldownClass == Intervene.class) {
                    warlordsPlayer.sendMessage("§c\u00AB§7 " + cooldown.getFrom().getName() + "'s §eIntervene §7has expired!");
                /*} else if (cooldownClass == UndyingArmy.class) {
                    if (!((UndyingArmy) cooldownObject).isArmyDead(warlordsPlayer.getUuid())) {
                        int healing = (int) ((warlordsPlayer.getMaxHealth() - warlordsPlayer.getHealth()) * .35 + 200);
                        warlordsPlayer.addHealth(cooldown.getFrom(), "Undying Army", healing, healing, -1, 100, false);

                        for (Player player1 : warlordsPlayer.getWorld().getPlayers()) {
                            player1.playSound(warlordsPlayer.getLocation(), "paladin.holyradiance.activation", 0.5f, 1);
                        }
                    }*/
                } else if (cooldownClass == ArcaneShield.class && getCooldown(ArcaneShield.class).size() == 1) {
                    if (warlordsPlayer.getEntity() instanceof Player) {
                        ((EntityLiving) ((CraftPlayer) warlordsPlayer.getEntity()).getHandle()).setAbsorptionHearts(0);
                    }
                } else if (cooldownClass == Soulbinding.class && getCooldown(Soulbinding.class).size() == 1) {
                    if (warlordsPlayer.getEntity() instanceof Player) {
                        ((CraftPlayer) warlordsPlayer.getEntity()).getInventory().getItem(0).removeEnchantment(Enchantment.OXYGEN);
                    }
                }

                if (name.equals("WND")) {
                    warlordsPlayer.sendMessage(ChatColor.GRAY + "You are no longer " + ChatColor.RED + "wounded" + ChatColor.GRAY + ".");
                } else if (name.equals("CRIP")) {
                    warlordsPlayer.sendMessage(ChatColor.GRAY + "You are no longer " + ChatColor.RED + "crippled" + ChatColor.GRAY + ".");
                }

                if (cooldownClass == OrbsOfLife.class) {
                    if (((OrbsOfLife) cooldownObject).getSpawnedOrbs().isEmpty()) {
                        cooldowns.remove(i);
                        i--;
                    } else {
                        if (!cooldown.isHidden()) {
                            cooldown.setHidden(true);
                        }
                    }
                } else {
                    cooldowns.remove(i);
                    i--;
                }


            }
        }
    }

    public List<Cooldown> getCooldowns() {
        return cooldowns;
    }

    public int getTotalCooldowns() {
        return totalCooldowns;
    }

    public List<Cooldown> getBuffCooldowns() {
        return cooldowns.stream().filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.BUFF).collect(Collectors.toList());
    }

    public void removeBuffCooldowns() {
        cooldowns.removeIf(cd -> cd.getCooldownType() == CooldownTypes.BUFF);
    }

    public List<Cooldown> getDebuffCooldowns() {
        return cooldowns.stream().filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.DEBUFF).collect(Collectors.toList());
    }

    public void removeDebuffCooldowns() {
        cooldowns.removeIf(cd -> cd.getCooldownType() == CooldownTypes.DEBUFF);
    }

    public List<Cooldown> getAbilityCooldowns() {
        return cooldowns.stream().filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.ABILITY).collect(Collectors.toList());
    }

    public void removeAbilityCooldowns() {
        cooldowns.removeIf(cd -> cd.getCooldownType() == CooldownTypes.ABILITY);
    }

    public void addCooldown(String name, Class cooldownClass, Object cooldownObject, String actionBarName, float timeLeft, WarlordsPlayer from, CooldownTypes cooldownType) {
        this.totalCooldowns++;
        cooldowns.add(new Cooldown(name, cooldownClass, cooldownObject, actionBarName, timeLeft, from, cooldownType));
    }

    public void addCooldown(Cooldown cooldown) {
        this.totalCooldowns++;
        cooldowns.add(cooldown);
    }

    public void incrementCooldown(Cooldown cooldown, int amount, int maxTime) {
        if(hasCooldownFromName(cooldown.getName())) {
            Cooldown cd = getCooldownFromName(cooldown.getName()).get(0);
            if(cd.getTimeLeft() + amount >= maxTime) {
                cd.setTimeLeft(maxTime);
            } else {
                cd.subtractTime(-amount);
            }
        } else {
            addCooldown(cooldown);
        }
    }

    public void removeCooldown(Class cooldownClass) {
        cooldowns.removeIf(cd -> cd.getCooldownClass() == cooldownClass);
    }

    public void removeCooldown(Object cooldownObject) {
        cooldowns.removeIf(cd -> cd.getCooldownObject() == cooldownObject);
    }

    public void clearCooldowns() {
        cooldowns.removeIf(cd ->
                cd.getCooldownClass() != OrbsOfLife.class
        );
        PlayerFilter.playingGame(warlordsPlayer.getGame()).teammatesOf(warlordsPlayer).forEach(wp -> {
            wp.getCooldownManager().getCooldowns().removeIf(cd -> cd.getFrom() == warlordsPlayer && cd.getCooldownClass() == Intervene.class);
        });
    }

    public boolean hasBoundPlayer(WarlordsPlayer warlordsPlayer) {
        for (Cooldown cooldown : getCooldown(Soulbinding.class)) {
            if (((Soulbinding) cooldown.getCooldownObject()).hasBoundPlayer(warlordsPlayer)) {
                return true;
            }
        }
        return false;
    }

    public int getNumberOfBoundPlayersLink(WarlordsPlayer warlordsPlayer) {
        int counter = 0;
        for (Cooldown cooldown : getCooldown(Soulbinding.class)) {
            if (((Soulbinding) cooldown.getCooldownObject()).hasBoundPlayerLink(warlordsPlayer)) {
                counter++;
            }
        }
        incrementCooldown(new Cooldown("Anti KB", null, null, "ANTI", counter, this.warlordsPlayer, CooldownTypes.BUFF), counter, 3);
        return counter;
    }

    public boolean checkUndyingArmy(boolean popped) {
        for (Cooldown cooldown : getCooldown(UndyingArmy.class)) {
            if (popped) {
                //returns true if any undying is popped
                if (((UndyingArmy) cooldown.getCooldownObject()).isArmyDead(warlordsPlayer.getUuid())) {
                    return true;
                }
            } else {
                //return true if theres any unpopped armies
                if (!((UndyingArmy) cooldown.getCooldownObject()).isArmyDead(warlordsPlayer.getUuid())) {
                    return true;
                }
            }

        }
        //if popped returns false - all undying armies are not popped (there is no popped armies)
        //if !popped return false - all undying armies are popped (there is no unpopped armies)
        return false;
    }

}



package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.abilties.ArcaneShield;
import com.ebicep.warlords.classes.abilties.Intervene;
import com.ebicep.warlords.classes.abilties.Soulbinding;
import com.ebicep.warlords.classes.abilties.UndyingArmy;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CooldownManager {

    private WarlordsPlayer warlordsPlayer;
    private List<Cooldown> cooldowns;

    public CooldownManager(WarlordsPlayer warlordsPlayer) {
        this.warlordsPlayer = warlordsPlayer;
        cooldowns = new ArrayList<>();
    }

    public boolean hasCooldown(Class cooldownClass) {
        return cooldowns.stream().anyMatch(cooldown -> cooldown.getCooldownClass() == cooldownClass);
    }

    public List<Cooldown> getCooldown(Class cooldownClass) {
        return cooldowns.stream().filter(cooldown -> cooldown.getCooldownClass() == cooldownClass).collect(Collectors.toList());
    }

    public List<Cooldown> getCooldown(String name) {
        return cooldowns.stream().filter(cooldown -> cooldown.getName().contains(name)).collect(Collectors.toList());
    }

    public void reduceCooldowns() {
        for (int i = 0; i < cooldowns.size(); i++) {
            Cooldown cooldown = cooldowns.get(i);
            Class cooldownClass = cooldown.getCooldownClass();
            Object cooldownObject = cooldown.getCooldownObject();

            cooldown.subtractTime(.05f);

            if (cooldown.getTimeLeft() <= 0) {
                if (cooldownClass == Intervene.class) {
                    warlordsPlayer.sendMessage("§c\u00AB§7 " + cooldown.getFrom().getName() + "'s §eIntervene §7has expired!");
                } else if (cooldownClass == UndyingArmy.class) {
                    if (!((UndyingArmy) cooldownObject).isArmyDead()) {
                        int healing = (int) ((warlordsPlayer.getMaxHealth() - warlordsPlayer.getHealth()) * .35 + 200);
                        warlordsPlayer.addHealth(cooldown.getFrom(), "Undying Army", healing, healing, -1, 100);

                        for (Player player1 : warlordsPlayer.getWorld().getPlayers()) {
                            player1.playSound(warlordsPlayer.getLocation(), "paladin.holyradiance.activation", 0.5f, 1);
                        }
                    }
                } else if (cooldownClass == ArcaneShield.class) {
                    if (warlordsPlayer.getEntity() instanceof Player) {
                        ((EntityLiving) ((CraftPlayer) warlordsPlayer.getEntity()).getHandle()).setAbsorptionHearts(0);
                    }
                }
                cooldowns.remove(i);
                i--;
            }
        }
    }

    public List<Cooldown> getCooldowns() {
        return cooldowns;
    }

    public List<Cooldown> getBuffCooldowns() {
        return cooldowns.stream().filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.BUFF).collect(Collectors.toList());
    }

    public List<Cooldown> getDebuffCooldowns() {
        return cooldowns.stream().filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.DEBUFF).collect(Collectors.toList());
    }

    public List<Cooldown> getAbilityCooldowns() {
        return cooldowns.stream().filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.ABILITY).collect(Collectors.toList());
    }

    public void addCooldown(Class cooldownClass, Object cooldownObject, String name, float timeLeft, WarlordsPlayer from, CooldownTypes cooldownType) {
        cooldowns.add(new Cooldown(cooldownClass, cooldownObject, name, timeLeft, from, cooldownType));
    }

    public void addCooldown(Cooldown cooldown) {
        cooldowns.add(cooldown);
    }

    public void clearCooldowns() {
        cooldowns.clear();
        for (WarlordsPlayer value : Warlords.getPlayers().values()) {
            value.getCooldownManager().getCooldowns().removeIf(cd -> cd.getFrom() == warlordsPlayer && (cd.getCooldownClass() == Intervene.class));
        }
    }

    public boolean hasBoundPlayer(WarlordsPlayer warlordsPlayer) {
        for (Cooldown cooldown : getCooldown(Soulbinding.class)) {
            if (((Soulbinding) cooldown.getCooldownObject()).hasBoundPlayer(warlordsPlayer)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkUndyingArmy(boolean popped) {
        for (Cooldown cooldown : getCooldown(UndyingArmy.class)) {
            if (popped) {
                //returns true if any undying is popped
                if (((UndyingArmy) cooldown.getCooldownObject()).isArmyDead()) {
                    return true;
                }
            } else {
                //return true if theres any unpopped armies
                if (!((UndyingArmy) cooldown.getCooldownObject()).isArmyDead()) {
                    return true;
                }
            }

        }
        //if popped returns false - all undying armies are not popped (there is no popped armies)
        //if !popped return false - all undying armies are popped (there is no unpopped armies)
        return false;
    }

}



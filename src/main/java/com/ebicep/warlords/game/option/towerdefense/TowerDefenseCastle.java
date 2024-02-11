package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TowerDefenseCastle {

    private final Team team;
    private final Location location;
    private final List<TextDisplay> display = new ArrayList<>();
    private final FloatModifiable maxHealth;
    private float currentHealth;

    public TowerDefenseCastle(Team team, Location location, float maxHealth) {
        this.team = team;
        this.location = location;
        this.maxHealth = new FloatModifiable(maxHealth);
        this.currentHealth = maxHealth;
    }

    public void displayInit() {
        this.display.add(location.getWorld().spawn(location, TextDisplay.class, textDisplay -> {
            textDisplay.setBillboard(Display.Billboard.CENTER);
            textDisplay.text(Component.text(NumberFormat.addCommaAndRound(currentHealth), getHealthColor()));
        }));
        this.display.add(location.getWorld().spawn(location.clone().add(0, .5, 0), TextDisplay.class, textDisplay -> {
            textDisplay.setBillboard(Display.Billboard.CENTER);
            textDisplay.text(Component.text(team.name + " Castle", team.getTeamColor()));
        }));
    }

    private TextColor getHealthColor() {
        float healthRatio = currentHealth / maxHealth.getCalculatedValue();
        if (healthRatio > 0.5) {
            return NamedTextColor.GREEN;
        } else if (healthRatio > 0.25) {
            return NamedTextColor.YELLOW;
        } else {
            return NamedTextColor.RED;
        }
    }

    public boolean takeDamage(AbstractMob mob) {
        float minMeleeDamage = mob.getMinMeleeDamage();
        float maxMeleeDamage = mob.getMaxMeleeDamage();
        float randomDamage = ThreadLocalRandom.current().nextFloat() * (maxMeleeDamage - minMeleeDamage) + minMeleeDamage;
        currentHealth -= randomDamage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            update();
            return true;
        }
        update();
        return false;
    }

    public boolean isDestroyed() {
        return currentHealth <= 0;
    }

    public void update() {
        TextDisplay textDisplay = display.get(0);
        if (currentHealth <= 0) {
            textDisplay.text(Component.text("DESTROYED", NamedTextColor.RED, TextDecoration.BOLD));
        } else {
            textDisplay.text(Component.text(NumberFormat.addCommaAndRound(currentHealth), getHealthColor()));
        }
    }

    public void cleanup() {
        display.forEach(TextDisplay::remove);
    }

    public Team getTeam() {
        return team;
    }
}

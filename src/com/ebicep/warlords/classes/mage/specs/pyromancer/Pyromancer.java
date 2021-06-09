package com.ebicep.warlords.classes.mage.specs.pyromancer;

import com.ebicep.warlords.classes.abilties.ArcaneShield;
import com.ebicep.warlords.classes.abilties.Inferno;
import com.ebicep.warlords.classes.abilties.Projectile;
import com.ebicep.warlords.classes.abilties.TimeWarp;
import com.ebicep.warlords.classes.mage.AbstractMage;
import org.bukkit.entity.Player;

public class Pyromancer extends AbstractMage {
    public Pyromancer(Player player) {
        super(player, 5200, 305, 20, 14, 0,
                new Projectile("Fireball", -344, -433, 0, 70, 20, 175,
                        "§7Shoot a fireball that will explode\n" +
                        "§7for §c344 §7- §c433 §7damage. A\n" +
                        "§7direct hit will cause the enemy\n" +
                        "§7to take an additional §c15% §7extra\n" +
                        "§7damage.\n" +
                        "''\n" +
                        "§7Has an optimal range of §e50 §7blocks.", 50),

                new Projectile("Flame Burst", -557, -753, 10, 60, 25, 185,
                        "§7Launch a flame burst that will explode\n" +
                        "§7for §c557 §7- §c753 §7damage. The critical\n" +
                        "§7chance increases by §c1% §7for each\n" +
                        "§7travelled block. Up to 100%.", 50),
                new TimeWarp(),
                new ArcaneShield(),
                new Inferno());
    }
}

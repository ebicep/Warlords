package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.AbstractTotemBase;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class LightningRod extends AbstractAbility {

    private final int energyRestore = 160;
    private final int knockbackRadius = 5;

    public LightningRod() {
        super("Lightning Rod", 0, 0, 31.32f, 0, -1, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Call down an energizing bolt of lightning\n" +
                "§7upon yourself, restoring §a30% §7health and\n" +
                "§e" + energyRestore + " §7energy and knock all nearby enemies\n" +
                "§7in a §e" + knockbackRadius + " §7block radius back.";
    }


    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.addEnergy(wp, name, energyRestore);
        Utils.playGlobalSound(player.getLocation(), "shaman.lightningrod.activation", 2, 1);

        new FallingBlockWaveEffect(wp.getLocation(), knockbackRadius, 1, Material.RED_ROSE, (byte) 5).play();
        player.getWorld().spigot().strikeLightningEffect(wp.getLocation(), true);

        wp.addHealingInstance(
                wp,
                name,
                (wp.getMaxHealth() * .3f),
                (wp.getMaxHealth() * .3f),
                critChance,
                critMultiplier,
                false,
                false
        );

        for (WarlordsEntity knockbackTarget : PlayerFilter
                .entitiesAround(player, knockbackRadius, knockbackRadius, knockbackRadius)
                .aliveEnemiesOf(wp)
        ) {
            final Location loc = knockbackTarget.getLocation();
            final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.5).setY(0.35);
            knockbackTarget.setVelocity(v, false);
        }

        // pulsedamage
        List<CapacitorTotem> totemDownAndClose = AbstractTotemBase.getTotemsDownAndClose(wp, wp.getEntity(), CapacitorTotem.class);
        totemDownAndClose.forEach(capacitorTotem -> {
            ArmorStand totem = capacitorTotem.getTotem();

            Utils.playGlobalSound(totem.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
            player.playSound(player.getLocation(), "shaman.chainlightning.impact", 2, 1);

            capacitorTotem.pulseDamage();
            capacitorTotem.addProc();
        });


        return true;
    }
}

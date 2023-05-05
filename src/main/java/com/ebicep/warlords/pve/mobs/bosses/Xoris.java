package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

public class Xoris extends AbstractZombie implements BossMob {

    boolean test = false;

    public Xoris(Location spawnLocation) {
        super(
                spawnLocation,
                "Xoris",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.IRON_QUEEN),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 90, 0, 90),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 90, 0, 90),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 90, 0, 90),
                        Weapons.SILVER_PHANTASM_SWORD_3.getItem()
                ),
                60000,
                0.2f,
                20,
                2000,
                3000
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        ChatUtils.sendTitleToGamePlayers(
                getWarlordsNPC().getGame(),
                Component.text(getWarlordsNPC().getName(), NamedTextColor.GRAY),
                Component.text("Empress of the Envoy Legion", NamedTextColor.DARK_PURPLE),
                20, 30, 20
        );
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (test) {
            Location loc = warlordsNPC.getLocation();
            int counter = 0;
            if (ticksElapsed % 4 == 0) {
                counter++;
                loc.setYaw(counter);
                EffectUtils.playHelixAnimation(loc, 20, Particle.FLAME, 1, 8);
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity mob, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        test = true;
    }
}
package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.util.java.NumberFormat;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class WarlordsPlayerDisguised extends WarlordsPlayer {

    private WarlordsNPC npc;
    private MobDisguise disguise;

    public WarlordsPlayerDisguised(Player player, WarlordsNPC npc) {
        super(npc.getLocation(), player, npc.getGame(), npc.getTeam());
        this.npc = npc;
        EntityEquipment ee = npc.getMob().getEquipment();
        AbstractMob mob = npc.getMob();
        this.disguise = new MobDisguise(DisguiseType.getType(mob.getMobRegistry().entityType));
        disguise.getWatcher().setCustomNameVisible(true);
        DisguiseAPI.disguiseToAll(player, disguise);

        this.name = npc.getName();
        this.specClass = npc.specClass;
        this.spec = npc.spec;
        this.health = npc.getHealth();
        this.speed.getModifiers().clear();
        this.speed.getModifiers().addAll(npc.speed.getModifiers());
        this.cooldownManager = npc.cooldownManager;
        this.weapon = new CommonWeapon(player.getUniqueId()) {
            {
                selectedWeaponSkin = ee == null ? null :
                                     Arrays.stream(Weapons.VALUES)
                                           .filter(weapons -> weapons.getItem().getType() == ee.getItemInMainHand().getType())
                                           .findAny()
                                           .orElse(null);
            }

            @Override
            public float getMeleeDamageMin() {
                return npc.getMinMeleeDamage();
            }

            @Override
            public float getMeleeDamageMax() {
                return npc.getMaxMeleeDamage();
            }

            @Override
            public float getCritChance() {
                return 0;
            }

            @Override
            public float getCritMultiplier() {
                return 0;
            }

            @Override
            public float getHealthBonus() {
                return 0;
            }
        };

        setSpawnGrave(false);
        updateInventory(false);

        PlayerInventory inventory = player.getInventory();
        inventory.setHelmet(ee.getHelmet());
        inventory.setChestplate(ee.getChestplate());
        inventory.setLeggings(ee.getLeggings());
        inventory.setBoots(ee.getBoots());
    }

    @Override
    public void runEveryTick() {
        super.runEveryTick();
        disguise.getWatcher().setCustomName(LegacyComponentSerializer.legacySection().serialize(
                npc.getMobNamePrefix()
                   .append(Component.text(NumberFormat.addCommaAndRound(this.getCurrentHealth()) + "❤",
                           NamedTextColor.RED,
                           TextDecoration.BOLD
                   ))));
    }

    @Override
    public void die(@Nullable WarlordsEntity attacker) {
        super.die(attacker);
        Warlords.removePlayer2(uuid);
        game.getPlayers().put(uuid, null);
        if (entity instanceof Player player) {
            DisguiseAPI.undisguiseToAll(player);
        }
    }
}

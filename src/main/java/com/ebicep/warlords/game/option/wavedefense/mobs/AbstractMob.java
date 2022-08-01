package com.ebicep.warlords.game.option.wavedefense.mobs;

import com.ebicep.customentities.nms.pve.CustomEntity;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.Mob;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.UnaryOperator;

public abstract class AbstractMob<T extends CustomEntity<?>> implements Mob {

    protected final T entity;
    protected final EntityInsentient entityInsentient;
    protected final LivingEntity livingEntity;
    protected final Location spawnLocation;
    protected final String name;
    protected final EntityEquipment ee;
    protected final int maxHealth;
    protected final float walkSpeed;
    protected final int damageResistance;
    protected final float minMeleeDamage;
    protected final float maxMeleeDamage;

    protected WarlordsNPC warlordsNPC;

    public AbstractMob(T entity, Location spawnLocation, String name, EntityEquipment ee, int maxHealth, float walkSpeed, int damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        this.entity = entity;
        this.spawnLocation = spawnLocation;
        this.name = name;
        this.ee = ee;
        this.maxHealth = maxHealth;
        this.walkSpeed = walkSpeed;
        this.damageResistance = damageResistance;
        this.minMeleeDamage = minMeleeDamage;
        this.maxMeleeDamage = maxMeleeDamage;

        entity.spawn(spawnLocation);

        this.entityInsentient = entity.get();
        this.entityInsentient.persistent = true;

        this.livingEntity = (LivingEntity) entityInsentient.getBukkitEntity();
        if (ee != null) {
            livingEntity.getEquipment().setBoots(ee.getBoots());
            livingEntity.getEquipment().setLeggings(ee.getLeggings());
            livingEntity.getEquipment().setChestplate(ee.getChestplate());
            livingEntity.getEquipment().setHelmet(ee.getHelmet());
            livingEntity.getEquipment().setItemInHand(ee.getItemInHand());
        } else {
            livingEntity.getEquipment().setHelmet(new ItemStack(Material.BARRIER));
        }
    }

    public WarlordsNPC toNPC(Game game, Team team, UUID uuid) {
        this.warlordsNPC = new WarlordsNPC(
                uuid,
                name,
                Weapons.ABBADON,
                livingEntity,
                game,
                team,
                Specializations.PYROMANCER,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
        onSpawn();
        game.addNPC(warlordsNPC);
        return warlordsNPC;
    }

    public AbstractMob<T> prependOperation(UnaryOperator<WarlordsNPC> mapper) {
        mapper.apply(this.warlordsNPC);
        return this;
    }

    public abstract void onSpawn();

    public abstract void whileAlive();

    public abstract void onAttack(WarlordsEntity attacker, WarlordsEntity receiver);

    public abstract void onDeath(Location deathLocation, WaveDefenseOption waveDefenseOption);

    public WarlordsNPC getWarlordsNPC() {
        return warlordsNPC;
    }
}

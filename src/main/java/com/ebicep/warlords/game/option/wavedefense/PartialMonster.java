package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.customentities.nms.pve.CustomEntity;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface PartialMonster {
    static PartialMonster fromEntity(LivingEntity entity) {
        return (game, team, uuid) -> game.addNPC(new WarlordsNPC(
                uuid,
                "Enemy",
                Weapons.ABBADON,
                entity,
                game,
                team,
                Specializations.PYROMANCER
        ));
    }

    static PartialMonster fromEntity(String name, LivingEntity entity) {
        return (game, team, uuid) -> game.addNPC(new WarlordsNPC(
                uuid,
                name,
                Weapons.ABBADON,
                entity,
                game,
                team,
                Specializations.PYROMANCER
        ));
    }

    static PartialMonster fromEntity(Class<? extends LivingEntity> clazz, String name, Location loc, EntityEquipment ee) {
        return (game, team, uuid) -> game.addNPC(new WarlordsNPC(
                uuid,
                name,
                Weapons.ABBADON,
                WarlordsNPC.spawnEntity(clazz, loc, ee),
                game,
                team,
                Specializations.PYROMANCER
        ));
    }

    static PartialMonster fromEntity(Class<? extends LivingEntity> clazz, String name, Location loc, EntityEquipment ee, int maxHealth, float walkSpeed, Specializations spec) {
        return (game, team, uuid) -> game.addNPC(new WarlordsNPC(
                uuid,
                name,
                Weapons.ABBADON,
                WarlordsNPC.spawnEntity(clazz, loc, ee),
                game,
                team,
                spec,
                maxHealth,
                walkSpeed
        ));
    }

    static <T extends EntityInsentient & CustomEntity<?>> PartialMonster fromCustomEntity(
            Class<T> clazz,
            Supplier<T> create,
            Consumer<T> onCreate,
            String name,
            Location loc,
            EntityEquipment ee
    ) {
        return (game, team, uuid) -> game.addNPC(new WarlordsNPC(
                uuid,
                name,
                Weapons.ABBADON,
                WarlordsNPC.spawnCustomEntity(clazz, create, onCreate, loc, ee),
                game,
                team,
                Specializations.PYROMANCER
        ));
    }

    static <T extends EntityInsentient & CustomEntity<?>> PartialMonster fromCustomEntity(
            Class<T> clazz,
            Supplier<T> create,
            Consumer<T> onCreate,
            String name,
            Location loc,
            EntityEquipment ee,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        return (game, team, uuid) -> game.addNPC(new WarlordsNPC(
                uuid,
                name,
                Weapons.ABBADON,
                WarlordsNPC.spawnCustomEntity(clazz, create, onCreate, loc, ee),
                game,
                team,
                Specializations.PYROMANCER,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        ));
    }

    static <T extends EntityInsentient & CustomEntity<?>> PartialMonster fromCustomEntity(
            Class<T> clazz,
            Supplier<T> create,
            Consumer<T> onCreate,
            String name,
            Location loc,
            EntityEquipment ee,
            Specializations spec,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        return (game, team, uuid) -> game.addNPC(new WarlordsNPC(
                uuid,
                name,
                Weapons.ABBADON,
                WarlordsNPC.spawnCustomEntity(clazz, create, onCreate, loc, ee),
                game,
                team,
                spec,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        ));
    }

    WarlordsEntity toNPC(Game game, Team team, UUID uuid);

    default PartialMonster prependOperation(UnaryOperator<WarlordsEntity> mapper) {
        return (game, team, uuid) -> mapper.apply(this.toNPC(game, team, uuid));
    }
}

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

import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;

public interface PartialMonster {
    WarlordsEntity toNPC(Game game, Team team, UUID uuid);

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
    static PartialMonster fromEntity(Class<? extends LivingEntity> clazz, String name, Location loc, EntityEquipment ee, int maxHealth, Specializations spec) {
        return (game, team, uuid) -> game.addNPC(new WarlordsNPC(
                uuid,
                name,
                Weapons.ABBADON,
                WarlordsNPC.spawnEntity(clazz, loc, ee),
                game,
                team,
                spec,
                maxHealth
        ));
    }

    static <T extends EntityInsentient & CustomEntity> PartialMonster fromCustomEntity(Class<T> clazz, String name, Location loc, EntityEquipment ee) {
        return (game, team, uuid) -> game.addNPC(new WarlordsNPC(
                uuid,
                name,
                Weapons.ABBADON,
                Objects.requireNonNull(WarlordsNPC.spawnCustomEntity(clazz, loc, ee)),
                game,
                team,
                Specializations.PYROMANCER
        ));
    }

    static <T extends EntityInsentient & CustomEntity> PartialMonster fromCustomEntity(Class<T> clazz, String name, Location loc, EntityEquipment ee, int maxHealth, Specializations spec) {
        return (game, team, uuid) -> game.addNPC(new WarlordsNPC(
                uuid,
                name,
                Weapons.ABBADON,
                Objects.requireNonNull(WarlordsNPC.spawnCustomEntity(clazz, loc, ee)),
                game,
                team,
                spec,
                maxHealth
        ));
    }

    default PartialMonster prependOperation(UnaryOperator<WarlordsEntity> mapper) {
        return (game, team, uuid) -> mapper.apply(this.toNPC(game, team, uuid));
    }
}

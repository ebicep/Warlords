package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.ExperienceGainOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.SpawnpointOption;
import com.ebicep.warlords.game.option.cuboid.AbstractCuboidOption;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.pve.CurrencyOnEventOption;
import com.ebicep.warlords.game.option.pve.ItemOption;
import com.ebicep.warlords.game.option.pve.onslaught.OnslaughtOption;
import com.ebicep.warlords.game.option.pve.rewards.CoinGainOption;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.RandomSpawnWave;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.StaticWaveList;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class IllusionPhantom extends GameMap {

    public IllusionPhantom() {
        super(
                "Illusion Phantom",
                6,
                1,
                30 * SECOND,
                "IllusionPhantom",
                3,
                GameMode.ONSLAUGHT
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 68, 0.5), Team.BLUE).asOption());
        options.add(SpawnpointOption.forTeam(loc.addXYZ(0, 68, 0), Team.BLUE));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(12.5, 68, 11.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(16.5, 68, 15.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(16.5, 68, -15.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(12.5, 68, -12.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-11.5, 68, -12.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-15.5, 68, -16.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-11.5, 68, 11.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-15.5, 68, 15.5), Team.RED));

        options.add(new RespawnWaveOption(1, 20, 10));
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));
        options.add(new CurrencyOnEventOption()
                .onKill(250, true)
                .startWith(15000)
        );
        options.add(new OnslaughtOption(Team.RED, new StaticWaveList()
                .add(0, new RandomSpawnWave(Component.text("EASY", NamedTextColor.GREEN))
                        .add(0.8, Mob.ZOMBIE_LANCER)
                        .add(0.2, Mob.ZOMBIE_LAMENT)
                        .add(0.05, Mob.SKELETAL_MAGE)
                        .add(0.1, Mob.SLIMY_ANOMALY)
                        .add(0.02, Mob.GOLEM_APPRENTICE)
                )
                .add(5, new RandomSpawnWave(Component.text("MEDIUM", NamedTextColor.YELLOW))
                        .add(0.6, Mob.ZOMBIE_LANCER)
                        .add(0.25, Mob.ZOMBIE_LAMENT)
                        .add(0.25, Mob.ZOMBIE_SWORDSMAN)
                        .add(0.05, Mob.SKELETAL_MAGE)
                        .add(0.1, Mob.SLIMY_ANOMALY)
                        .add(0.02, Mob.CELESTIAL_OPUS)
                        .add(0.02, Mob.GOLEM_APPRENTICE)
                )
                .add(10, new RandomSpawnWave(Component.text("HARD", NamedTextColor.GOLD))
                        .add(0.4, Mob.ZOMBIE_LANCER)
                        .add(0.3, Mob.ZOMBIE_LAMENT)
                        .add(0.3, Mob.ZOMBIE_SWORDSMAN)
                        .add(0.02, Mob.SKELETAL_MESMER)
                        .add(0.02, Mob.OVERGROWN_ZOMBIE)
                        .add(0.02, Mob.CELESTIAL_SWORD_WIELDER)
                        .add(0.02, Mob.CELESTIAL_BOW_WIELDER)
                        .add(0.02, Mob.GOLEM_APPRENTICE)
                )
                .add(15, new RandomSpawnWave(Component.text("INSANE", NamedTextColor.RED))
                        .add(0.4, Mob.ZOMBIE_LANCER)
                        .add(0.4, Mob.ZOMBIE_LAMENT)
                        .add(0.4, Mob.ZOMBIE_SWORDSMAN)
                        .add(0.02, Mob.SKELETAL_MESMER)
                        .add(0.01, Mob.OVERGROWN_ZOMBIE)
                        .add(0.05, Mob.CELESTIAL_OPUS)
                        .add(0.02, Mob.CELESTIAL_SWORD_WIELDER)
                        .add(0.02, Mob.CELESTIAL_BOW_WIELDER)
                        .add(0.02, Mob.PIG_ALLEVIATOR)
                        .add(0.02, Mob.GOLEM_APPRENTICE)
                )
                .add(20, new RandomSpawnWave(Component.text("EXTREME", NamedTextColor.DARK_RED))
                        .add(0.5, Mob.ZOMBIE_LAMENT)
                        .add(0.5, Mob.ZOMBIE_SWORDSMAN)
                        .add(0.02, Mob.SKELETAL_MESMER)
                        .add(0.07, Mob.OVERGROWN_ZOMBIE)
                        .add(0.07, Mob.CELESTIAL_OPUS)
                        .add(0.07, Mob.CELESTIAL_SWORD_WIELDER)
                        .add(0.07, Mob.CELESTIAL_BOW_WIELDER)
                        .add(0.07, Mob.PIG_ALLEVIATOR)
                        .add(0.04, Mob.PIG_PARTICLE)
                        .add(0.07, Mob.GOLEM_APPRENTICE)
                )
                .add(25, new RandomSpawnWave(Component.text("NIGHTMARE", NamedTextColor.LIGHT_PURPLE))
                        .add(0.3, Mob.SLIME_GUARD)
                        .add(0.5, Mob.ZOMBIE_SWORDSMAN)
                        .add(0.02, Mob.SKELETAL_MESMER)
                        .add(0.07, Mob.OVERGROWN_ZOMBIE)
                        .add(0.07, Mob.CELESTIAL_OPUS)
                        .add(0.07, Mob.CELESTIAL_SWORD_WIELDER)
                        .add(0.07, Mob.CELESTIAL_BOW_WIELDER)
                        .add(0.07, Mob.PIG_ALLEVIATOR)
                        .add(0.04, Mob.PIG_PARTICLE)
                        .add(0.07, Mob.GOLEM_APPRENTICE)
                        .add(0.07, Mob.ARACHNO_VENARI)
                )
                .add(30, new RandomSpawnWave(Component.text("INSOMNIA", NamedTextColor.DARK_PURPLE))
                        .add(0.6, Mob.SLIME_GUARD)
                        .add(0.1, Mob.SCRUPULOUS_ZOMBIE)
                        .add(0.02, Mob.SKELETAL_MESMER)
                        .add(0.07, Mob.OVERGROWN_ZOMBIE)
                        .add(0.07, Mob.CELESTIAL_OPUS)
                        .add(0.07, Mob.CELESTIAL_SWORD_WIELDER)
                        .add(0.07, Mob.CELESTIAL_BOW_WIELDER)
                        .add(0.07, Mob.PIG_ALLEVIATOR)
                        .add(0.04, Mob.PIG_PARTICLE)
                        .add(0.07, Mob.GOLEM_APPRENTICE)
                        .add(0.07, Mob.ARACHNO_VENARI)
                )
                .add(35, new RandomSpawnWave(0, 5 * SECOND, Component.text("VANGUARD", NamedTextColor.GRAY))
                        .add(0.4, Mob.SLIME_GUARD)
                        .add(0.1, Mob.SCRUPULOUS_ZOMBIE)
                        .add(0.02, Mob.SKELETAL_MESMER)
                        .add(0.07, Mob.OVERGROWN_ZOMBIE)
                        .add(0.07, Mob.CELESTIAL_OPUS)
                        .add(0.07, Mob.CELESTIAL_SWORD_WIELDER)
                        .add(0.07, Mob.CELESTIAL_BOW_WIELDER)
                        .add(0.05, Mob.PIG_ALLEVIATOR)
                        .add(0.02, Mob.PIG_PARTICLE)
                        .add(0.07, Mob.GOLEM_APPRENTICE)
                        .add(0.1, Mob.ZOMBIE_KNIGHT)
                        .add(0.1, Mob.RIFT_WALKER)
                )
                .add(40, new RandomSpawnWave(0, 5 * SECOND, Component.text("DEMISE", NamedTextColor.RED, TextDecoration.BOLD))
                        .add(0.2, Mob.SLIME_GUARD)
                        .add(0.1, Mob.SCRUPULOUS_ZOMBIE)
                        .add(0.05, Mob.SKELETAL_MESMER)
                        .add(0.1, Mob.OVERGROWN_ZOMBIE)
                        .add(0.2, Mob.CELESTIAL_OPUS)
                        .add(0.07, Mob.CELESTIAL_SWORD_WIELDER)
                        .add(0.07, Mob.CELESTIAL_BOW_WIELDER)
                        .add(0.07, Mob.PIG_ALLEVIATOR)
                        .add(0.04, Mob.PIG_PARTICLE)
                        .add(0.1, Mob.GOLEM_APPRENTICE)
                        .add(0.2, Mob.ZOMBIE_KNIGHT)
                        .add(0.1, Mob.RIFT_WALKER)
                        .add(0.1, Mob.NIGHTMARE_ZOMBIE)
                )
                .add(45, new RandomSpawnWave(0, 5 * SECOND, Component.text("??????", NamedTextColor.BLACK, TextDecoration.OBFUSCATED))
                        .add(0.3, Mob.SLIME_GUARD)
                        .add(0.1, Mob.SCRUPULOUS_ZOMBIE)
                        .add(0.05, Mob.SKELETAL_MESMER)
                        .add(0.1, Mob.OVERGROWN_ZOMBIE)
                        .add(0.2, Mob.CELESTIAL_OPUS)
                        .add(0.07, Mob.CELESTIAL_SWORD_WIELDER)
                        .add(0.07, Mob.CELESTIAL_BOW_WIELDER)
                        .add(0.05, Mob.PIG_ALLEVIATOR)
                        .add(0.02, Mob.PIG_PARTICLE)
                        .add(0.1, Mob.GOLEM_APPRENTICE)
                        .add(0.2, Mob.ZOMBIE_KNIGHT)
                        .add(0.1, Mob.RIFT_WALKER)
                        .add(0.2, Mob.NIGHTMARE_ZOMBIE)
                )
        ));
        options.add(new ItemOption());
        options.add(new CoinGainOption()
                .guildCoinInsigniaConvertBonus(1000)
        );
        options.add(new ExperienceGainOption()
                .playerExpPer(160)
                .guildExpPer(5)
        );

        return options;
    }

}
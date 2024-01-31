package com.ebicep.warlords.game.maps;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.cuboid.AbstractCuboidOption;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.pve.CurrencyOnEventOption;
import com.ebicep.warlords.game.option.pve.ItemOption;
import com.ebicep.warlords.game.option.pve.ReadyUpOption;
import com.ebicep.warlords.game.option.pve.rewards.CoinGainOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WinByMaxWaveClearOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffectOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.TartarusOption;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.FixedSpawnWave;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.StaticWaveList;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.java.Pair;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class Tartarus extends GameMap {

    public Tartarus() {
        super(
                "Tartarus",
                4,
                2,
                120 * SECOND,
                "Tartarus",
                3,
                GameMode.EVENT_WAVE_DEFENSE
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TextOption.Type.CHAT_CENTERED.create(
                Component.text(getMapName(), NamedTextColor.WHITE, TextDecoration.BOLD),
                Component.empty(),
                Component.text("Kill the gods as fast as possible!", NamedTextColor.YELLOW, TextDecoration.BOLD),
                Component.empty()
        ));
        options.add(TextOption.Type.TITLE.create(
                0,
                Component.text("Grace Period!", NamedTextColor.GREEN),
                Component.text("Buy upgrades and prepare", NamedTextColor.YELLOW)
        ));

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(108.5, 33, 61.5), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(108.5, 33, 61.5), Team.RED).asOption());

        options.add(SpawnpointOption.forTeam(loc.addXYZ(108.5, 33, 61.5), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(8.5, 23, 5.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 23, 13.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-7.5, 23, 5.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(8.5, 23, -10.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 23, -18.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-7.5, 23, -10.5), Team.RED));

        options.add(new PowerupOption(loc.addXYZ(137.5, 34.5, 87.5), PowerupOption.PowerUp.COOLDOWN, 180, 30));
        options.add(new PowerupOption(loc.addXYZ(137.5, 34.5, 37.5), PowerupOption.PowerUp.DAMAGE, 180, 30));
        options.add(new PowerupOption(loc.addXYZ(87.5, 34.5, 37.5), PowerupOption.PowerUp.HEALING, 180, 30));
        options.add(new PowerupOption(loc.addXYZ(87.5, 34.5, 87.5), PowerupOption.PowerUp.SPEED, 90, 30));

        options.add(new RespawnWaveOption(2, 1, 20));
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

        options.add(new WinByMaxWaveClearOption());

        ReadyUpOption readyUpOption = new ReadyUpOption("Charon") {
            @Override
            protected void createNPC(@Nonnull Game game) {
                npc = NPCManager.NPC_REGISTRY.createNPC(EntityType.PLAYER, "ready-up");
                npc.getOrAddTrait(ReadyUpTrait.class).setReadyUpOption(this);
                // https://minesk.in/29ee19b22b7d421d86f10f5017f7abe5
                SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
                skinTrait.setSkinPersistent(
                        "Charon",
                        "bksbQ5KevV4Bjm7cJHkCa6D2BCPSB7KLQZ7wO2+omZGw93NcEkalzl6sjRDyLYft4TPxa7QsXr7tDnHqLcrgkGM9tlNIlKLN6wtJ/WJjUIo5SOsm09JVuRtohQH4HRbu5fIhaoFZhkywsetZmJHW3ZNMR8ErgojpUdg6UqaUuL6DMZWTcbTBpDHOWmM9GVwDPArHrsFMD11L5BlDdKsUgbIbvmHdOep8oXx4PujVP5G2GWDEHc9j4XIZN8PpR9t5PajyAOoXMcUJXFf8d5QJXMWTT1VDqPy0Q17aAM87xjzASJbXxBrvMgcV2bkhkmwifxMAEuvrHAXdpjRBBNz+5iPDwFmjs4XNWvdy/Z6idrPcfJuD9qDSq3V6SGqfoFw4b4FUOg2K5T/MCIttp8SGL2cN52uuirlFwk+oalrOidhhbB8YoEMpYLp06aU6MSTVBKL8uIjB/yOjVX40664ciOJf+GPAhXYnVBTYHqrVu4rUf3FJkNbfROo6if0oM6EVzJ+FMCEbNTmhtVZGQ6ljJ/pVR+Qy/EMUbU+lLHTVZ28DVnwTZ8XcuNUX6c+hnVfyic+yy7m3lxhmd6Wthsrr3JwZfw19wakqapJQ1j7bYAny/oH3AqMQqBDW6sIOIwtJgttPGVtqHn41iXfcP23zr85gJjl5tiFrRH3qNiTVpFI=",
                        "ewogICJ0aW1lc3RhbXAiIDogMTYwODU2NTczOTQyNywKICAicHJvZmlsZUlkIiA6ICJmMTA0NzMxZjljYTU0NmI0OTkzNjM4NTlkZWY5N2NjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJ6aWFkODciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI4NThkYTE0MDE2YjRhNDVhMGMzNzRlMmI4M2QxYzY3MTJiNzIwNzNiODEyM2QzNTlmOTQxNmVmNGJhZjk5NSIKICAgIH0KICB9Cn0="
                );

                npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
                npc.spawn(game.getLocations().addXYZ(119.5, 33, 72.5, 135, 0));
            }
        };
        options.add(readyUpOption);

        List<Location> bossSpawnLocations = new ArrayList<>();
        bossSpawnLocations.add(loc.addXYZ(100.5, 33, 51.5, -90, 0));
        bossSpawnLocations.add(loc.addXYZ(125.5, 33, 40.5, 0, 0));
        bossSpawnLocations.add(loc.addXYZ(144.5, 33, 69.5, 180, 0));
        bossSpawnLocations.add(loc.addXYZ(114.5, 33, 96.5, 180, 0));
        bossSpawnLocations.add(loc.addXYZ(88.5, 33, 76.5, -90, 0));
        Collections.shuffle(bossSpawnLocations);
        WaveDefenseOption waveDefenseOption = new WaveDefenseOption(Team.RED, new StaticWaveList()
                .add(1, new FixedSpawnWave(60 * SECOND, -1, null)
                        .add(Mob.EVENT_HADES, bossSpawnLocations.get(0))
                        .add(Mob.EVENT_POSEIDON, bossSpawnLocations.get(1))
                        .add(Mob.EVENT_ZEUS, bossSpawnLocations.get(2))
                ), DifficultyIndex.EVENT, 1
        ) {
            @Override
            public void register(@Nonnull Game game) {
                super.register(game);
                game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(SCOREBOARD_PRIORITY - 2, "wave") {
                    @Nonnull
                    @Override
                    public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                        return Collections.singletonList(Component.text("Event: ").append(Component.text(getMapName(), NamedTextColor.GREEN)));
                    }
                });
            }

            @Override
            public List<Component> getWaveScoreboard(WarlordsPlayer player) {
                return Collections.emptyList();
            }

            @Override
            public float getSpawnCountMultiplier(int playerCount) {
                return 1;
            }

            @Override
            protected Pair<Float, Component> getWaveOpening() {
                return new Pair<>(.8f, Component.text(""));
            }

            @Override
            protected void onSpawnDelayChange(int newTickDelay) {
                switch (newTickDelay) {
                    case 960 -> readyUpOption.sendNPCMessage(Component.text("Hades has powers of resurrection given to him by the eternal flames.", NamedTextColor.YELLOW));
                    case 720 -> readyUpOption.sendNPCMessage(Component.text("Poseidon retaliates and responds negatively to the loss of his brothers.", NamedTextColor.YELLOW));
                    case 480 -> readyUpOption.sendNPCMessage(Component.text("Zeus sends his brothers into battle as he leads the gods to ruin.", NamedTextColor.YELLOW));
                    case 240 -> readyUpOption.sendNPCMessage(Component.text("The order in which the brothers fall will determine their fate as well as yours!",
                            NamedTextColor.YELLOW
                    ));
                    case 0 -> {
                        readyUpOption.sendNPCMessage(Component.text("Good luck, you'll need it.", NamedTextColor.YELLOW));
                        readyUpOption.getNpc().destroy();
                        getGame().forEachOnlinePlayer((p, t) -> {
                            p.showTitle(Title.title(
                                    Component.text("Fight!", NamedTextColor.GREEN),
                                    Component.text("Organization is key.", NamedTextColor.YELLOW),
                                    Title.Times.times(Ticks.duration(0), Ticks.duration(30), Ticks.duration(20))
                            ));
                        });
                    }
                }
            }

            @Override
            protected void modifyStats(WarlordsNPC warlordsNPC) {
                warlordsNPC.getMob().onSpawn(this);

                int playerCount = playerCount();
                boolean fourManPlus = playerCount >= 4;
                float healthMultiplier = .5f * playerCount; // 1 / 1.5 / 2
                float damageMultiplier = fourManPlus ? 1.30f : 1;

                if (fourManPlus) {
                    healthMultiplier += .15f;
                }

                float newBaseHealth = warlordsNPC.getMaxBaseHealth() * healthMultiplier;
                warlordsNPC.setMaxHealthAndHeal(newBaseHealth);
                warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                        "Scaling",
                        null,
                        GameMap.class,
                        null,
                        warlordsNPC,
                        CooldownTypes.INTERNAL,
                        cooldownManager -> {

                        },
                        false
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * damageMultiplier;
                    }
                });
            }
        };
        options.add(waveDefenseOption);
        readyUpOption.setWhenAllReady(() -> {
            waveDefenseOption.setCurrentDelay(1);
            readyUpOption.getNpc().destroy();
        });
        options.add(new ItemOption());
        options.add(new WinAfterTimeoutOption(600, 50, "spec"));
        options.add(new TartarusOption());
//            options.add(new SafeZoneOption(1));
        options.add(new EventPointsOption()
                .reduceScoreOnAllDeath(30, Team.BLUE)
                .onPerMobKill(Mob.EVENT_ZEUS, 5000)
                .onPerMobKill(Mob.EVENT_POSEIDON, 5000)
                .onPerMobKill(Mob.EVENT_HADES, 5000)

        );
        options.add(new CurrencyOnEventOption()
                .startWith(750000)
                .onKill(500)
        );
        options.add(new CoinGainOption()
                .clearMobCoinValueAndSet("Greek Gods Killed", new LinkedHashMap<>() {{
                    put("Zeus", 100L);
                    put("Poseidon", 100L);
                    put("Hades", 100L);
                }})
                .playerCoinPerXSec(150, 10)
                .guildCoinInsigniaConvertBonus(1000)
                .guildCoinPerXSec(1, 1)
                .disableCoinConversionUpgrade()
        );
        options.add(new ExperienceGainOption()
                .playerExpGameWinBonus(2500)
                .playerExpPerXSec(15, 10)
                .guildExpPerXSec(4, 10)
        );
        options.add(new FieldEffectOption(options, FieldEffectOption.FieldEffect.TYCHE_PROSPERITY));

        return options;
    }

}
package com.ebicep.warlords.game.option.raid;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RaidOption implements Option, PveOption {

    private SimpleScoreboardHandler scoreboard;
    private final Set<AbstractMob<?>> mobs = new HashSet<>();
    private Raid raid;
    private Game game;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            player.setInPve(true);
            if (player.getEntity() instanceof Player) {
                game.setPlayerTeam((OfflinePlayer) player.getEntity(), Team.BLUE);
                player.setTeam(Team.BLUE);
                player.updateArmor();
            }
            DatabaseManager.getPlayer(player.getUuid(), databasePlayer -> {
                Optional<AbstractWeapon> optionalWeapon = databasePlayer.getPveStats().getWeaponInventory()
                        .stream()
                        .filter(AbstractWeapon::isBound)
                        .filter(abstractWeapon -> abstractWeapon.getSpecializations() == player.getSpecClass())
                        .findFirst();
                optionalWeapon.ifPresent(abstractWeapon -> {
                    WarlordsPlayer warlordsPlayer = (WarlordsPlayer) player;

                    ((WarlordsPlayer) player).getCosmeticSettings().setWeaponSkin(abstractWeapon.getSelectedWeaponSkin());
                    warlordsPlayer.setWeapon(abstractWeapon);
                    abstractWeapon.applyToWarlordsPlayer(warlordsPlayer, this);
                    player.updateEntity();
                });
            });
        }
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                //spawnNewMob(new Physira(new Location(game.getLocations().getWorld(), 711.5, 7, 179.5)));
            }
        }.runTaskLater(60);
    }

    @Override
    public int playerCount() {
        return 0;
    }

    @Override
    public Set<AbstractMob<?>> getMobs() {
        return null;
    }

    @Override
    public int getTicksElapsed() {
        return 0;
    }

    public void spawnNewMob(AbstractMob<?> abstractMob) {
        //abstractMob.toNPC(game, Team.RED, UUID.randomUUID());
        game.addNPC(abstractMob.getWarlordsNPC());
        mobs.add(abstractMob);
    }

    @Override
    public void spawnNewMob(AbstractMob<?> mob, Team team) {

    }

    @Override
    public PveRewards<?> getRewards() {
        return null;
    }

    @Override
    public ConcurrentHashMap<AbstractMob<?>, Integer> getMobsMap() {
        return null;
    }

    @Override
    public Game getGame() {
        return null;
    }
}
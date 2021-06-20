package com.ebicep.warlords.maps;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.player.ArmorManager;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ItemBuilder;
import com.ebicep.warlords.util.PacketUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.Banner;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.MetadataValueAdapter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class FlagManager implements Listener {

    public static final String FLAG_DAMAGE_MULTIPLIER = "flag-damage-multiplier";
    private static final int SCORE_FLAG_POINTS = 250;

    private final FlagInfo red;
    private final FlagInfo blue;

    public FlagInfo getRed() {
        return red;
    }

    public FlagInfo getBlue() {
        return blue;
    }

    private final FlagRenderer redRenderer;
    private final FlagRenderer blueRenderer;

    private final BukkitTask task;
    private int respawnTimer = -1;

    public FlagManager(Location redFlagRespawn, Location blueFlagRespawn) {
        this.red = new FlagInfo(Team.RED, redFlagRespawn);
        this.blue = new FlagInfo(Team.BLUE, blueFlagRespawn);

        this.redRenderer = new FlagRenderer(red);
        this.blueRenderer = new FlagRenderer(blue);

        final Warlords plugin = Warlords.getInstance();
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            boolean hasScored = false;
            if (
                    this.red.getFlag() instanceof SpawnFlagLocation &&
                            this.blue.getFlag() instanceof PlayerFlagLocation &&
                            this.blue.getFlag().getLocation().distanceSquared(this.red.getSpawnLocation()) < 2.5 * 2.5
            ) {
                // Red scores a capture
                PlayerFlagLocation playerFlagLocation = (PlayerFlagLocation) this.blue.getFlag();
                Bukkit.broadcastMessage("§c" + playerFlagLocation.getPlayer().getName() + " §ehas captured the §9BLUE §eflag!");
                for (Player player1 : Warlords.game.getPlayersProtected().keySet()) {
                    PacketUtils.sendTitle(player1, "", "§c" + playerFlagLocation.getPlayer().getName() + " §ehas captured the §9BLUE §eflag!", 0, 60, 0);
                }
                Warlords.game.addRedPoints(SCORE_FLAG_POINTS);
                hasScored = true;

                for (Player player1 : playerFlagLocation.getPlayer().getWorld().getPlayers()) {
                    Warlords.getPlayer(player1).getScoreboard().updatePoints();
                    if (Warlords.game.isRedTeam(player1)) {
                        player1.playSound(playerFlagLocation.getLocation(), "ctf.enemyflagcaptured", 500, 1);
                    } else {
                        player1.playSound(playerFlagLocation.getLocation(), "ctf.enemycapturedtheflag", 500, 1);
                    }
                }
            }

            if (
                    this.blue.getFlag() instanceof SpawnFlagLocation &&
                            this.red.getFlag() instanceof PlayerFlagLocation &&
                            this.red.getFlag().getLocation().distanceSquared(this.blue.getSpawnLocation()) < 2 * 2
            ) {
                // Blue scores a capture
                PlayerFlagLocation playerFlagLocation = (PlayerFlagLocation) this.red.getFlag();
                Bukkit.broadcastMessage("§9" + playerFlagLocation.getPlayer().getName() + " §ehas captured the §cRED §eflag!");
                for (Player player1 : Warlords.game.getPlayersProtected().keySet()) {
                    PacketUtils.sendTitle(player1, "", "§9" + playerFlagLocation.getPlayer().getName() + " §ehas captured the §cRED §eflag!", 0, 60, 0);
                }
                Warlords.getPlayer(playerFlagLocation.getPlayer()).addFlagCap();
                Warlords.game.addBluePoints(SCORE_FLAG_POINTS);
                hasScored = true;

                for (Player player1 : playerFlagLocation.getPlayer().getWorld().getPlayers()) {
                    Warlords.getPlayer(player1).getScoreboard().updatePoints();
                    if (!Warlords.game.isRedTeam(player1)) {
                        player1.playSound(playerFlagLocation.getLocation(), "ctf.enemyflagcaptured", 500, 1);
                    } else {
                        player1.playSound(playerFlagLocation.getLocation(), "ctf.enemycapturedtheflag", 500, 1);
                    }
                }
            }

            if (hasScored) {
                respawnTimer = 15 * 20;
                this.blue.setFlag(new WaitingFlagLocation(this.blue.getSpawnLocation()));
                this.red.setFlag(new WaitingFlagLocation(this.red.getSpawnLocation()));
            }

            if (respawnTimer == 0) {
                respawnTimer--;
                this.blue.setFlag(new SpawnFlagLocation(this.blue.getSpawnLocation()));
                this.red.setFlag(new SpawnFlagLocation(this.red.getSpawnLocation()));
            } else if (respawnTimer > 0) {
                respawnTimer--;
            }

            this.redRenderer.checkRender();
            this.blueRenderer.checkRender();

        }, 1, 1);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDeath(WarlordsDeathEvent event) {
        Player player = event.getPlayer().getPlayer();

        if (
                red.getFlag() instanceof PlayerFlagLocation &&
                        ((PlayerFlagLocation) red.getFlag()).getPlayer().equals(player)

        ) {
            red.doOtherTeamInteraction(player);
        }
        if (
                blue.getFlag() instanceof PlayerFlagLocation &&
                        ((PlayerFlagLocation) blue.getFlag()).getPlayer().equals(player)
        ) {
            blue.doOtherTeamInteraction(player);
        }
    }

    public void dropFlag(Player player) {
        if (
                red.getFlag() instanceof PlayerFlagLocation &&
                        ((PlayerFlagLocation) red.getFlag()).getPlayer().equals(player)
        ) {
            red.doOtherTeamInteraction(player);
        }
        if (
                blue.getFlag() instanceof PlayerFlagLocation &&
                        ((PlayerFlagLocation) blue.getFlag()).getPlayer().equals(player)
        ) {
            blue.doOtherTeamInteraction(player);
        }
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FlagInfo info = Warlords.game.isRedTeam(player) ? red : blue;
        if (
                info.getFlag() instanceof PlayerFlagLocation &&
                        ((PlayerFlagLocation) info.getFlag()).getPlayer().equals(player)
        ) {
            info.doOtherTeamInteraction(player);
        }
    }

    @EventHandler
    public void onArmorStandBreak(EntityDamageByEntityEvent event) {

        Entity entity = event.getEntity();

        if (entity instanceof ArmorStand && entity.getCustomName() != null && entity.getCustomName().contains("FLAG")) {
            event.setCancelled(true);
            Player player = (Player) event.getDamager();
            Team standTeam = (Team) entity.getMetadata("TEAM").stream().map(MetadataValue::value).filter(v -> v instanceof Team).findAny().orElse(null);
            if (standTeam == null) {
                return;
            }

            FlagInfo info = standTeam == Team.RED ? this.red : this.blue;
            Team team = Warlords.game.isRedTeam(player) ? Team.RED : Team.BLUE;

            if (team == info.getTeam()) {
                info.doSameTeamInteraction(player);
            } else {
                info.doOtherTeamInteraction(player);

                ChatColor color = (info.getTeam() == Team.RED ? ChatColor.BLUE : ChatColor.RED);
                ChatColor color2 = (info.getTeam() == Team.RED ? ChatColor.RED : ChatColor.BLUE);
                Bukkit.broadcastMessage(color + player.getPlayerListName() + " §ehas picked up the " + color2 + info.getTeam() + " §eflag!");
                for (Player player1 : Warlords.game.getPlayersProtected().keySet()) {
                    PacketUtils.sendTitle(player1, "", color + player.getPlayerListName() + " §ehas picked up the " + color2 + info.getTeam() + " §eflag!", 0, 60, 0);
                }
                for (Player player1 : player.getWorld().getPlayers()) {
                    if (Warlords.game.isRedTeam(player1) == (info.getTeam() == Team.RED)) {
                        player1.playSound(player.getLocation(), "ctf.friendlyflagtaken", 500, 1);
                    } else {
                        player1.playSound(player.getLocation(), "ctf.enemyflagtaken", 500, 1);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (Warlords.hasPlayer(player)) {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
            if (player.getInventory().getHeldItemSlot() == 8 && e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                warlordsPlayer.setTeamFlagCompass(!warlordsPlayer.isTeamFlagCompass());
            }
        }
    }

    public void stop() {
        this.blueRenderer.reset();
        this.redRenderer.reset();
        this.task.cancel();
        HandlerList.unregisterAll(this);
    }

    public static class FlagInfo {
        private FlagLocation flag;
        private final Location spawnLocation;
        private final Team team;

        public FlagInfo(Team team, Location spawnLocation) {
            this.team = team;
            this.spawnLocation = spawnLocation;
            this.flag = new SpawnFlagLocation(this.spawnLocation);
        }

        public FlagLocation getFlag() {
            return flag;
        }

        public Location getSpawnLocation() {
            return spawnLocation;
        }

        public Team getTeam() {
            return team;
        }

        private void doSameTeamInteraction(Player player) {
            for (Player player1 : Warlords.getPlayers().keySet()) {
                Warlords.getPlayer(player1).getScoreboard().updateFlagStatus();
            }
            FlagLocation newLoc = flag.afterSameTeamInteraction(player, spawnLocation);
            if (newLoc != null) {
                this.flag = newLoc;
            }
        }

        private void doOtherTeamInteraction(Player attacker) {
            for (Player player1 : Warlords.getPlayers().keySet()) {
                Warlords.getPlayer(player1).getScoreboard().updateFlagStatus();
            }
            FlagLocation newLoc = flag.otherTeamInteraction(attacker);
            if (newLoc != null) {
                this.flag = newLoc;
            }
        }

        private void setFlag(FlagLocation flag) {
            this.flag = flag;
        }


    }

    public interface FlagLocation {
        Location getLocation();

        FlagLocation afterSameTeamInteraction(Player player, Location ownTeamSpawnLocation);

        FlagLocation otherTeamInteraction(Player player);
    }

    abstract static class AbstractLocationBasedFlagLocation implements FlagManager.FlagLocation {

        protected final Location location;

        public AbstractLocationBasedFlagLocation(Location location) {
            this.location = location;
            location.setX(location.getBlockX() + 0.5);
            location.setY(location.getBlockY());
            location.setZ(location.getBlockZ() + 0.5);
        }

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public FlagLocation afterSameTeamInteraction(Player player, Location ownTeamSpawnLocation) {
            return null;
        }

        @Override
        public FlagLocation otherTeamInteraction(Player player) {
            return null;
        }

    }

    public static class GroundFlagLocation extends AbstractLocationBasedFlagLocation implements FlagLocation {
        public GroundFlagLocation(Location location) {
            super(location);
        }

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public FlagLocation afterSameTeamInteraction(Player player, Location ownTeamSpawnLocation) {

            ChatColor color = Warlords.game.getPlayerTeam(player).teamColor();
            Bukkit.broadcastMessage(color + player.getPlayerListName() + " §ehas returned the " + color + Warlords.game.getPlayerTeam(player) + " §eflag!");
            for (Player player1 : Warlords.game.getPlayersProtected().keySet()) {
                PacketUtils.sendTitle(player1, "", color + player.getPlayerListName() + " §ehas returned the " + color + Warlords.game.getPlayerTeam(player) + " §eflag!", 0, 60, 0);
            }
            Warlords.getPlayer(player).addFlagReturn();

            for (Player player1 : player.getWorld().getPlayers()) {
                player1.playSound(player.getLocation(), "ctf.flagreturned", 500, 1);
            }

            return new SpawnFlagLocation(ownTeamSpawnLocation);
        }

        @Override
        public FlagLocation otherTeamInteraction(Player player) {
            return new PlayerFlagLocation(player);
        }
    }

    public static class SpawnFlagLocation extends AbstractLocationBasedFlagLocation {

        public SpawnFlagLocation(Location location) {
            super(location);
        }

        @Override
        public FlagLocation otherTeamInteraction(Player player) {
            return new PlayerFlagLocation(player);
        }
    }

    public static class WaitingFlagLocation extends AbstractLocationBasedFlagLocation {

        public WaitingFlagLocation(Location location) {
            super(location);
        }
    }

    public static class PlayerFlagLocation implements FlagLocation {

        private final Player player;
        private int modifier;

        public PlayerFlagLocation(Player player) {
            this.player = player;
        }

        @Override
        public Location getLocation() {
            return player.getLocation();
        }

        public Player getPlayer() {
            return player;
        }

        public int getModifier() {
            return modifier;
        }

        public void setModifier(int modifier) {
            this.modifier = modifier;
        }

        @Override
        public FlagLocation afterSameTeamInteraction(Player player, Location ownTeamSpawnLocation) {
            return null;
        }

        @Override
        public FlagLocation otherTeamInteraction(Player player) {
            return new GroundFlagLocation(this.getLocation());
        }

    }

    static class FlagRenderer {

        private final FlagInfo info;
        private int timer = 0;
        private final List<Player> affectedPlayers = new ArrayList<>();
        private final List<Entity> renderedArmorStands = new ArrayList<>();
        private final List<Block> renderedBlocks = new ArrayList<>();
        private final List<Runnable> runningTasksCancel = new ArrayList<>();
        private FlagLocation lastLocation;

        public FlagRenderer(FlagInfo info) {
            this.info = info;
        }

        public void checkRender() {
            if (this.lastLocation != info.getFlag()) {
                this.render();
                for (WarlordsPlayer player : Warlords.getPlayers().values()) {
                    player.getScoreboard().updateFlagStatus();
                }
            }

            if (timer <= 0 && !(info.getFlag() instanceof WaitingFlagLocation)) {
                timer = 20;
                float offset = info.getFlag() instanceof PlayerFlagLocation ? 1.5F : 0.5F;
                info.getFlag().getLocation().getWorld().playEffect(info.getFlag().getLocation().clone().add(0, offset, 0), Effect.STEP_SOUND, info.getTeam() == Team.RED ? Material.REDSTONE_BLOCK.getId() : Material.LAPIS_BLOCK.getId());
            }

            timer--;
        }


        public void render() {

            FlagLocation old = this.lastLocation;

            if (this.lastLocation != null) {
                this.reset();
            }

            this.lastLocation = info.getFlag();
            final Warlords plugin = Warlords.getInstance();

            if (this.lastLocation instanceof GroundFlagLocation || this.lastLocation instanceof SpawnFlagLocation) {

                Block block = this.lastLocation.getLocation().getBlock();

                for (int i = 0; !block.isEmpty() && block.getType() != Material.STANDING_BANNER && i < 4; i++) {
                    block = block.getRelative(0, 1, 0);
                }

                if (block.isEmpty() || block.getType() == Material.STANDING_BANNER) {
                    renderedBlocks.add(block);
                    block.setType(Material.STANDING_BANNER);
                    org.bukkit.block.Banner banner = (org.bukkit.block.Banner) block.getState();
                    banner.setBaseColor(info.getTeam() == Team.BLUE ? DyeColor.BLUE : DyeColor.RED);
                    banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.SKULL));
                    banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_TOP));
                    banner.update();
                    MaterialData newData = block.getState().getData();
                    BlockFace dir;

                    if (banner.getWorld().getBlockAt(block.getLocation().add(0, 0, -5)).getType() == Material.AIR) {
                        dir = BlockFace.NORTH;
                    } else if (banner.getWorld().getBlockAt(block.getLocation().add(0, 0, 5)).getType() == Material.AIR) {
                        dir = BlockFace.SOUTH;
                    } else if (banner.getWorld().getBlockAt(block.getLocation().add(-5, 0, 0)).getType() == Material.AIR) {
                        dir = BlockFace.WEST;
                    } else if (banner.getWorld().getBlockAt(block.getLocation().add(5, 0, 0)).getType() == Material.AIR) {
                        dir = BlockFace.EAST;
                    } else {
                        dir = BlockFace.SOUTH;
                    }
                    ((Banner) newData).setFacingDirection(dir);
                    block.setData(newData.getData());
                }

                if (this.lastLocation instanceof GroundFlagLocation) {
                    if (old instanceof PlayerFlagLocation) {
                        PlayerFlagLocation playerFlagLocation = (PlayerFlagLocation) old;
                        String flag = info.getTeam() == Team.RED ? ChatColor.RED + "RED" : ChatColor.BLUE + "BLUE";
                        ChatColor playerColor = info.getTeam().enemy().teamColor();
                        Bukkit.broadcastMessage(playerColor + playerFlagLocation.getPlayer().getName() + " §ehas dropped the " + flag + "§e flag!");
                        for (Player player1 : Warlords.game.getPlayersProtected().keySet()) {
                            PacketUtils.sendTitle(player1, "", playerColor + playerFlagLocation.getPlayer().getName() + " §ehas dropped the " + flag + "§e flag!", 0, 60, 0);
                        }

                    }
                    this.runningTasksCancel.add(new BukkitRunnable() {
                        @Override
                        public void run() {
                            FlagRenderer.this.info.setFlag(new SpawnFlagLocation(info.getSpawnLocation()));
                        }

                    }.runTaskLater(plugin, 15 * 20)::cancel);
                }

                ArmorStand stand = this.lastLocation.getLocation().getWorld().spawn(block.getLocation().add(.5, 0, .5), ArmorStand.class);
                renderedArmorStands.add(stand);
                stand.setGravity(false);
                stand.setCanPickupItems(false);
                stand.setCustomName(info.getTeam() == Team.BLUE ? ChatColor.BLUE + "BLU FLAG" : ChatColor.RED + "RED FLAG");
                stand.setCustomNameVisible(true);
                stand.setMetadata("TEAM", new FixedMetadataValue(plugin, info.getTeam()));
                stand.setVisible(false);

            } else if (this.lastLocation instanceof PlayerFlagLocation) {

                PlayerFlagLocation flag = (PlayerFlagLocation) this.lastLocation;
                Player player = ((PlayerFlagLocation) this.lastLocation).getPlayer();
                this.affectedPlayers.add(player);

                ItemStack item = new ItemStack(Material.BANNER);
                BannerMeta banner = (BannerMeta) item.getItemMeta();
                banner.setBaseColor(info.getTeam() == Team.BLUE ? DyeColor.BLUE : DyeColor.RED);
                banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.SKULL));
                banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_TOP));
                item.setItemMeta(banner);
                player.getInventory().setHelmet(item);

                DamageMultiplier multiplier = new DamageMultiplier(plugin, flag, info);
                player.setMetadata(FLAG_DAMAGE_MULTIPLIER, multiplier);
                BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, multiplier, 30 * 20, 30 * 20);
                this.runningTasksCancel.add(() -> {
                    task.cancel();
                    player.removeMetadata(FLAG_DAMAGE_MULTIPLIER, plugin);
                });
                runningTasksCancel.add(Warlords.getPlayer(player).getSpeed().changeCurrentSpeed("FLAG", -20, 0));
                player.getInventory().setItem(6, new ItemBuilder(Material.BANNER, 1).name("§aDrop Flag").get());
            }
        }

        public void reset() {
            this.lastLocation = null;
            for (Block b : renderedBlocks) {
                b.setType(Material.AIR);
            }
            renderedBlocks.clear();

            for (Entity e : renderedArmorStands) {
                e.remove();
            }

            renderedArmorStands.clear();
            for (Player p : affectedPlayers) {
                ArmorManager.resetArmor(p, Classes.getSelected(p));
                p.getInventory().setItem(6, null);
            }

            affectedPlayers.clear();
            for (Runnable t : runningTasksCancel) {
                t.run();
            }

            runningTasksCancel.clear();
        }
    }

    static class DamageMultiplier extends MetadataValueAdapter implements Runnable {

        private final PlayerFlagLocation flag;
        private final FlagInfo info;

        public DamageMultiplier(Plugin man, PlayerFlagLocation flag, FlagInfo info) {
            super(man);
            this.flag = flag;
            this.info = info;
        }

        @Override
        public void invalidate() {
        }

        @Override
        public Object value() {
            return 1 + flag.getModifier() / 100f;
        }

        @Override
        public void run() {
            flag.setModifier(flag.getModifier() + 10);
            ChatColor color = (info.getTeam() == Team.RED ? ChatColor.RED : ChatColor.BLUE);
            Bukkit.broadcastMessage("§eThe " + color + info.getTeam() + " §eflag carrier now takes §c" + flag.getModifier() + "§c% §eincreased damage!");

            for (WarlordsPlayer player : Warlords.getPlayers().values()) {
                player.getScoreboard().updateFlagStatus();
            }
        }
    }
}
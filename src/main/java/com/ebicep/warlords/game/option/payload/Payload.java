package com.ebicep.warlords.game.option.payload;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class Payload {

    protected static final double MOVE_RADIUS = 3;
    private static final int BOSS_BAR_FILL_SPACE = 45;
    private static final int BOSS_BAR_ESCORT_SPACE = 6;
    @Nonnull
    private final Game game;
    @Nonnull
    protected final PayloadBrain brain;
    private final PayloadRenderer renderer;
    private final BossBar bossBar;
    @Nonnull
    private final Team escortingTeam;


    public Payload(@Nonnull Game game, @Nonnull PayloadBrain brain, PayloadRenderer renderer, @Nonnull Team escortingTeam) {
        this.game = game;
        this.brain = brain;
        this.renderer = renderer;
        this.escortingTeam = escortingTeam;
        this.bossBar = BossBar.bossBar(Component.empty(), 0, escortingTeam.bossBarColor, BossBar.Overlay.NOTCHED_10);
        renderer.init(game);
    }

    public boolean tick(int ticksElapsed) {
        int netEscorting = getNetEscorting(brain.getCurrentLocation());
        if (netEscorting != 0) {
            boolean reachedEnd = brain.tick(netEscorting);
            if (reachedEnd) {
                return true;
            }
        }
        renderEffects(ticksElapsed);
        showBossBar(netEscorting);
        return false;
    }

    public void renderEffects(int ticksElapsed) {
        if (renderer != null) {
            Location newLocation = brain.getCurrentLocation();
            renderer.move(newLocation);
            renderer.playEffects(ticksElapsed, brain.getCurrentLocation().clone().add(0, .7, 0), MOVE_RADIUS);
        }
    }

    public void cleanup() {
        renderer.cleanup();
        hideBossBar();
    }

    protected int getNetEscorting(Location oldLocation) {
        int escorting = 0;
        int nonEscorting = 0;
        for (WarlordsEntity warlordsEntity : PlayerFilterGeneric
                .entitiesAround(oldLocation, MOVE_RADIUS, MOVE_RADIUS, MOVE_RADIUS)
        ) {
            if (warlordsEntity.getTeam() == escortingTeam) {
                escorting++;
            } else {
                nonEscorting++;
            }
        }
        return escorting - nonEscorting;
    }

    protected void showBossBar(int netEscorting) {
        float progress = (float) (brain.getCurrentPathIndex() / brain.getPath().size());
        String pushing = "";
        boolean escorting = netEscorting > 0;
        if (escorting) {
            // https://en.wikipedia.org/wiki/List_of_Unicode_characters#Unicode_symbols:~:text=assigned%20code%20points-,Enclosed%20Alphanumerics,-%5Bedit%5D
            // https://www.compart.com/en/unicode/search?q=Dingbat+Negative+Circled+#characters
            // String unicodeNumber = String.valueOf(Character.toChars((netEscorting <= 20 ? 0x2460 : 0x2470) + netEscorting - 1));
            String unicodeNumber = String.valueOf(Character.toChars((netEscorting <= 10 ? 0x2776 : 0x24E1) + netEscorting - 1));
            pushing = ">>>" + (netEscorting > 20 ? netEscorting : unicodeNumber); // ⋙ 〉 ≫ ① ②
        }
        bossBar.name(Component.textOfChildren(
                Component.text(" ".repeat((int) (progress * BOSS_BAR_FILL_SPACE) + (escorting ? BOSS_BAR_ESCORT_SPACE : 0))),
                Component.text("🄿", escortingTeam.teamColor),
                Component.text(pushing),
                Component.text(" ".repeat((int) ((1 - progress) * BOSS_BAR_FILL_SPACE)))
        ));
        bossBar.progress(MathUtils.clamp(progress, 0, 1));
        game.forEachOnlinePlayer((player, team) -> player.showBossBar(bossBar));
    }

    private void hideBossBar() {
        game.forEachOnlinePlayer((player, team) -> player.hideBossBar(bossBar));
    }

    @Nonnull
    public PayloadBrain getBrain() {
        return brain;
    }
}

package com.ebicep.warlords.game.option.payload;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.List;

public class Payload {

    protected static final double MOVE_RADIUS = 4;
    private static final int BOSS_BAR_FILL_SPACE = 45;
    private static final int BOSS_BAR_ESCORT_SPACE = 6;
    private static final int BOSS_BAR_CONTEST_SPACE = 24;
    @Nonnull
    protected final PayloadBrain brain;
    protected boolean contested = false;
    @Nonnull
    private final Game game;
    private final PayloadRenderer renderer;
    private final BossBar bossBar;
    @Nonnull
    private final Team escortingTeam;


    public Payload(@Nonnull Game game, @Nonnull PayloadBrain brain, PayloadRenderer renderer, @Nonnull Team escortingTeam) {
        this.game = game;
        this.brain = brain;
        this.renderer = renderer;
        this.escortingTeam = escortingTeam;
        this.bossBar = BossBar.bossBar(Component.empty(), 0, escortingTeam.getBossBarColor(), BossBar.Overlay.PROGRESS);
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

    public void renderEffects(int ticksElapsed) {
        if (renderer != null) {
            Location newLocation = brain.getCurrentLocation().clone();
            renderer.move(newLocation);
            renderer.playEffects(ticksElapsed, newLocation.add(0, .7, 0), MOVE_RADIUS);
        }
    }

    protected void showBossBar(int netEscorting) {
        List<PayloadBrain.PathEntry> brainPath = brain.getPath();
        if (brainPath.isEmpty()) {
            return;
        }
        float progress = (float) (brain.getMappedPathIndex() / brainPath.get(brainPath.size() - 1).mappedIndex());
        String pushing = "";
        int leftPadding = 0;
        int escortingAbs = Math.abs(netEscorting);
        if (escortingAbs > 0 && !(netEscorting < 0 && progress == 0)) {
            leftPadding = BOSS_BAR_ESCORT_SPACE;
            // https://en.wikipedia.org/wiki/List_of_Unicode_characters#Unicode_symbols:~:text=assigned%20code%20points-,Enclosed%20Alphanumerics,-%5Bedit%5D
            // https://www.compart.com/en/unicode/search?q=Dingbat+Negative+Circled+#characters
            // String unicodeNumber = String.valueOf(Character.toChars((netEscorting <= 20 ? 0x2460 : 0x2470) + netEscorting - 1));
            boolean forward = netEscorting > 0;
            String unicodeNumber = String.valueOf(Character.toChars((escortingAbs <= 10 ? 0x2776 : 0x24E1) + escortingAbs - 1));
            pushing = (forward ? ">>>" : "<<<") + (escortingAbs > 20 ? escortingAbs : unicodeNumber); // ⋙ 〉 ≫ ① ②
        } else if (contested) {
            leftPadding = BOSS_BAR_CONTEST_SPACE;
            pushing += " > CONTESTED! <";
        }
        bossBar.name(Component.textOfChildren(
                Component.text(" ".repeat((int) (progress * BOSS_BAR_FILL_SPACE) + leftPadding)),
                Component.text("🄿", escortingTeam.getTeamColor()),
                Component.text(pushing).decoration(TextDecoration.BOLD, contested),
                Component.text(" ".repeat((int) ((1 - progress) * BOSS_BAR_FILL_SPACE)))
        ));
        bossBar.progress(MathUtils.clamp(progress, 0, 1));
        game.forEachOnlinePlayer((player, team) -> player.showBossBar(bossBar));
    }

    public void cleanup() {
        renderer.cleanup();
        hideBossBar();
    }

    private void hideBossBar() {
        game.forEachOnlinePlayer((player, team) -> player.hideBossBar(bossBar));
    }

    @Nonnull
    public PayloadBrain getBrain() {
        return brain;
    }

    public PayloadRenderer getRenderer() {
        return renderer;
    }
}

package com.ebicep.warlords.tablist;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Produces a tab row for a specific viewer. Return {@code null} to omit the row for that viewer
 * (does not consume a layout slot).
 */
@FunctionalInterface
public interface TabEntrySource {

    @Nullable
    TabEntry resolve(UUID viewer);
}

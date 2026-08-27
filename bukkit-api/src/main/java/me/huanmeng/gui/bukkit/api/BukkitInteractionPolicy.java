package me.huanmeng.gui.bukkit.api;

public final class BukkitInteractionPolicy {
    private final boolean cancelEmptyClicks;
    private final boolean cancelBottomClicks;
    private final boolean cancelMoveToGui;
    private final boolean cancelHotbarSwap;
    private final boolean cancelOffhandSwap;

    public BukkitInteractionPolicy(boolean cancelEmptyClicks, boolean cancelBottomClicks,
                                   boolean cancelMoveToGui, boolean cancelHotbarSwap,
                                   boolean cancelOffhandSwap) {
        this.cancelEmptyClicks = cancelEmptyClicks;
        this.cancelBottomClicks = cancelBottomClicks;
        this.cancelMoveToGui = cancelMoveToGui;
        this.cancelHotbarSwap = cancelHotbarSwap;
        this.cancelOffhandSwap = cancelOffhandSwap;
    }

    public static BukkitInteractionPolicy defaults() {
        return new BukkitInteractionPolicy(true, true, true, true, true);
    }

    public boolean cancelEmptyClicks() {
        return cancelEmptyClicks;
    }

    public boolean cancelBottomClicks() {
        return cancelBottomClicks;
    }

    public boolean cancelMoveToGui() {
        return cancelMoveToGui;
    }

    public boolean cancelHotbarSwap() {
        return cancelHotbarSwap;
    }

    public boolean cancelOffhandSwap() {
        return cancelOffhandSwap;
    }
}

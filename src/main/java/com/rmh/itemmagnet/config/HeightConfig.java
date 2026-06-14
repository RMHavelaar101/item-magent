package com.rmh.itemmagnet.config;

public final class HeightConfig {

    private final boolean useYRange;
    private final int minY;
    private final int maxY;
    private final UndergroundConfig underground;
    private final SurfaceConfig surface;

    public HeightConfig(boolean useYRange, int minY, int maxY, UndergroundConfig underground, SurfaceConfig surface) {
        this.useYRange = useYRange;
        this.minY = minY;
        this.maxY = maxY;
        this.underground = underground;
        this.surface = surface;
    }

    public boolean isUseYRange() {
        return useYRange;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public UndergroundConfig getUnderground() {
        return underground;
    }

    public SurfaceConfig getSurface() {
        return surface;
    }
}

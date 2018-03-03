package net.thegaminghuskymc.sandboxgame.world.border;

import net.thegaminghuskymc.sgf.fml.relauncher.Side;
import net.thegaminghuskymc.sgf.fml.relauncher.SideOnly;

public enum EnumBorderStatus
{
    GROWING(4259712),
    SHRINKING(16724016),
    STATIONARY(2138367);

    private final int color;

    private EnumBorderStatus(int color)
    {
        this.color = color;
    }

    /**
     * Retrieve the color that the border should be while in this state
     */
    @SideOnly(Side.CLIENT)
    public int getColor()
    {
        return this.color;
    }
}
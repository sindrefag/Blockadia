package net.thegaminghuskymc.sandboxgame.block;

public class BlockPressurPlate extends BlockCube {

    public BlockPressurPlate(int blockID) {
        super(blockID);
    }

    @Override
    public String getUnlocalizedName() {
        return null;
    }

    @Override
    public String getRegistryName() {
        return null;
    }

    @Override
    public boolean isOpaque() {
        return (false);
    }

    @Override
    public boolean hasTransparency() {
        return (true);
    }

    @Override
    public boolean isVisible() {
        return (true);
    }

}
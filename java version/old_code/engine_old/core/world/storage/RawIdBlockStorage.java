package team.hdt.blockadia.engine.core.world.storage;

import org.jdom2.Element;
import team.hdt.blockadia.engine_rewrite.client.blocks.BlockType;

public class RawIdBlockStorage implements RawBlockStorage {
    private final byte[][][] data;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;

    public RawIdBlockStorage(int sizeX, int sizeY, int sizeZ) {
        this.data = new byte[sizeX][sizeY][sizeZ];
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    /*@Override
    public void setBlockUnchecked(int x, int y, int z, BlockType type) {
        int id = BlockRegistry.REGISTRY.getId(type.getIdentifier()) & 0xFF;
        this.data[x][y][z] = (byte) id;
    }

    @Override
    public BlockType getBlockUnchecked(int x, int y, int z) {
        byte id = this.data[x][y][z];
        return BlockRegistry.registries.get(id & 0xFF);
    }*/

    @Override
    public void setBlockUnchecked(int x, int y, int z, BlockType type) {

    }

    @Override
    public BlockType getBlockUnchecked(int x, int y, int z) {
        return null;
    }

    @Override
    public int getSizeX() {
        return this.sizeX;
    }

    @Override
    public int getSizeY() {
        return this.sizeY;
    }

    @Override
    public int getSizeZ() {
        return this.sizeZ;
    }

    @Override
    public Element toXML(Element element) {
        element.setAttribute("sizeX", sizeX + "");
        element.setAttribute("sizeY", 100 + "");
        element.setAttribute("sizeZ", sizeZ + "");
        Element data = new Element("data");
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < 100; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    Element block = new Element("block");
                    block.setAttribute("x", x + "");
                    block.setAttribute("y", y + "");
                    block.setAttribute("z", z + "");
                    block.setText(this.data[x][y][z] + "");
                    data.addContent(block);
                }
            }
        }
        element.addContent(data);
        return element;
    }

}
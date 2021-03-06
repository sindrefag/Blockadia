package team.hdt.blockadia.engine.core.world.storage;

import org.jdom2.Element;
import team.hdt.blockadia.engine.client.util.Nonnull;
import team.hdt.blockadia.engine.core.world.chunk.Chunk;
import team.hdt.blockadia.engine.core.world.chunk.ChunkPos;

public class ChunkHolder {

    Chunk chunkIn;
    long[] pos;

    public ChunkHolder(Chunk chunk, long x, long y) {
        this.chunkIn = chunk;
        this.pos = new long[]{
                x,
                y
        };
    }

    public ChunkHolder(Chunk chunk, @Nonnull ChunkPos pos) {
        this(chunk, pos.getPosX(), pos.getPosZ());
    }

    /**
     * @return the chunk it's holding
     */
    public Chunk getChunk() {
        return chunkIn;
    }

    /**
     * @return the chunk pos in an array; { x, y }
     */
    public long[] getPos() {
        return pos;
    }

    public Element toXML() {
        Element element = new Element("chunk");
        Element data = new Element("data");
        data.setAttribute("x", pos[0] + "");
        data.setAttribute("y", pos[1] + "");
        element.addContent(data);
        Element blockdata = new Element("blockdata");
        blockdata = chunkIn.storage.toXML(blockdata);
        element.addContent(blockdata);
        return element;
    }
}

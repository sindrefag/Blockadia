package team.hdt.blockadia.engine.core.world.chunk;

public class ChunkPos {

    private long posX, posZ;

    public ChunkPos(long x, long z) {
        this.posX = x;
        this.posZ = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChunkPos) {
            return (((ChunkPos) obj).posX == this.posX) && (((ChunkPos) obj).posZ == this.posZ);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 3 * 2 * (int) (posX + posZ);
    }

    public long getPosX() {
        return posX;
    }

    public long getPosZ() {
        return posZ;
    }

    public ChunkPos[] getSurroundings() {
        return new ChunkPos[]{
                new ChunkPos(posX - 1, posZ),
                new ChunkPos(posX + 1, posZ),
                new ChunkPos(posX, posZ + 1),
                new ChunkPos(posX, posZ - 1)
        };
    }
}

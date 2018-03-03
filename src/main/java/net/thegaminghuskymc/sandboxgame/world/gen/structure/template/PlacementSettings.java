package net.thegaminghuskymc.sandboxgame.world.gen.structure.template;

import net.thegaminghuskymc.sandboxgame.block.Block;
import net.thegaminghuskymc.sandboxgame.util.math.ChunkPos;
import net.thegaminghuskymc.sandboxgame.world.gen.structure.StructureBoundingBox;
import net.thegaminghuskymc.sandboxgame.util.Mirror;
import net.thegaminghuskymc.sandboxgame.util.Rotation;
import net.thegaminghuskymc.sandboxgame.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Random;

public class PlacementSettings {
    private Mirror mirror = Mirror.NONE;
    private Rotation rotation = Rotation.NONE;
    private boolean ignoreEntities;
    /**
     * the type of block in the world that will get replaced by the structure
     */
    @Nullable
    private Block replacedBlock;
    /**
     * the chunk the structure is within
     */
    @Nullable
    private ChunkPos chunk;
    /**
     * the bounds the structure is contained within
     */
    @Nullable
    private StructureBoundingBox boundingBox;
    private boolean ignoreStructureBlock = true;
    private float integrity = 1.0F;
    @Nullable
    private Random random;
    @Nullable
    private Long setSeed;

    public PlacementSettings copy() {
        PlacementSettings placementsettings = new PlacementSettings();
        placementsettings.mirror = this.mirror;
        placementsettings.rotation = this.rotation;
        placementsettings.ignoreEntities = this.ignoreEntities;
        placementsettings.replacedBlock = this.replacedBlock;
        placementsettings.chunk = this.chunk;
        placementsettings.boundingBox = this.boundingBox;
        placementsettings.ignoreStructureBlock = this.ignoreStructureBlock;
        placementsettings.integrity = this.integrity;
        placementsettings.random = this.random;
        placementsettings.setSeed = this.setSeed;
        return placementsettings;
    }

    public PlacementSettings setChunk(ChunkPos chunkPosIn) {
        this.chunk = chunkPosIn;
        return this;
    }

    public PlacementSettings setSeed(@Nullable Long seedIn) {
        this.setSeed = seedIn;
        return this;
    }

    public PlacementSettings setRandom(@Nullable Random randomIn) {
        this.random = randomIn;
        return this;
    }

    public Mirror getMirror() {
        return this.mirror;
    }

    public PlacementSettings setMirror(Mirror mirrorIn) {
        this.mirror = mirrorIn;
        return this;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public PlacementSettings setRotation(Rotation rotationIn) {
        this.rotation = rotationIn;
        return this;
    }

    public Random getRandom(@Nullable BlockPos seed) {
        if (this.random != null) {
            return this.random;
        } else if (this.setSeed != null) {
            return this.setSeed.longValue() == 0L ? new Random(System.currentTimeMillis()) : new Random(this.setSeed.longValue());
        } else if (seed == null) {
            return new Random(System.currentTimeMillis());
        } else {
            int i = seed.getX();
            int j = seed.getZ();
            return new Random((long) (i * i * 4987142 + i * 5947611) + (long) (j * j) * 4392871L + (long) (j * 389711) ^ 987234911L);
        }
    }

    public float getIntegrity() {
        return this.integrity;
    }

    public PlacementSettings setIntegrity(float integrityIn) {
        this.integrity = integrityIn;
        return this;
    }

    public boolean getIgnoreEntities() {
        return this.ignoreEntities;
    }

    public PlacementSettings setIgnoreEntities(boolean ignoreEntitiesIn) {
        this.ignoreEntities = ignoreEntitiesIn;
        return this;
    }

    @Nullable
    public Block getReplacedBlock() {
        return this.replacedBlock;
    }

    public PlacementSettings setReplacedBlock(Block replacedBlockIn) {
        this.replacedBlock = replacedBlockIn;
        return this;
    }

    @Nullable
    public StructureBoundingBox getBoundingBox() {
        if (this.boundingBox == null && this.chunk != null) {
            this.setBoundingBoxFromChunk();
        }

        return this.boundingBox;
    }

    public PlacementSettings setBoundingBox(StructureBoundingBox boundingBoxIn) {
        this.boundingBox = boundingBoxIn;
        return this;
    }

    public boolean getIgnoreStructureBlock() {
        return this.ignoreStructureBlock;
    }

    public PlacementSettings setIgnoreStructureBlock(boolean ignoreStructureBlockIn) {
        this.ignoreStructureBlock = ignoreStructureBlockIn;
        return this;
    }

    void setBoundingBoxFromChunk() {
        this.boundingBox = this.getBoundingBoxFromChunk(this.chunk);
    }

    @Nullable
    private StructureBoundingBox getBoundingBoxFromChunk(@Nullable ChunkPos pos) {
        if (pos == null) {
            return null;
        } else {
            int i = pos.x * 16;
            int j = pos.z * 16;
            return new StructureBoundingBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
        }
    }
}
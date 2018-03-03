package net.thegaminghuskymc.sandboxgame.tileentity;

import net.thegaminghuskymc.sandboxgame.block.Block;
import net.thegaminghuskymc.sandboxgame.block.BlockChest;
import net.thegaminghuskymc.sandboxgame.block.state.IBlockState;
import net.thegaminghuskymc.sandboxgame.chrash.CrashReportCategory;
import net.thegaminghuskymc.sandboxgame.chrash.ICrashReportDetail;
import net.thegaminghuskymc.sandboxgame.init.Blocks;
import net.thegaminghuskymc.sandboxgame.registries.RegistryNamespaced;
import net.thegaminghuskymc.sandboxgame.util.math.AxisAlignedBB;
import net.thegaminghuskymc.sandboxgame.util.text.ITextComponent;
import net.thegaminghuskymc.sandboxgame.world.World;
import net.thegaminghuskymc.sandboxgame.nbt.NBTTagCompound;
import net.thegaminghuskymc.sandboxgame.util.EnumFacing;
import net.thegaminghuskymc.sandboxgame.util.Mirror;
import net.thegaminghuskymc.sandboxgame.util.ResourceLocation;
import net.thegaminghuskymc.sandboxgame.util.Rotation;
import net.thegaminghuskymc.sandboxgame.util.math.BlockPos;
import net.thegaminghuskymc.sgf.common.capabilities.Capability;
import net.thegaminghuskymc.sgf.common.capabilities.CapabilityDispatcher;
import net.thegaminghuskymc.sgf.common.capabilities.ICapabilitySerializable;
import net.thegaminghuskymc.sgf.event.ForgeEventFactory;
import net.thegaminghuskymc.sgf.fml.common.FMLLog;
import net.thegaminghuskymc.sgf.fml.relauncher.Side;
import net.thegaminghuskymc.sgf.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public abstract class TileEntity implements ICapabilitySerializable<NBTTagCompound> {
    /**
     */
    public static final AxisAlignedBB INFINITE_EXTENT_AABB = new AxisAlignedBB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final RegistryNamespaced<ResourceLocation, Class<? extends TileEntity>> REGISTRY = new RegistryNamespaced<>();

    static {
        register("sign", TileEntitySign.class);
        register("bed", TileEntityBed.class);
    }

    /**
     * the instance of the world the tile entity is in.
     */
    protected World world;
    protected BlockPos pos = BlockPos.ORIGIN;
    protected boolean tileEntityInvalid;
    /**
     * the Block type that this TileEntity is contained within
     */
    protected Block blockType;
    private int blockMetadata = -1;
    private boolean isVanilla = getClass().getName().startsWith("net.minecraft.");
    private NBTTagCompound customTileData;
    private CapabilityDispatcher capabilities;

    public TileEntity() {
        capabilities = ForgeEventFactory.gatherCapabilities(this);
    }

    public static void register(String id, Class<? extends TileEntity> clazz) {
        REGISTRY.putObject(new ResourceLocation(id), clazz);
    }

    @Nullable
    public static ResourceLocation getKey(Class<? extends TileEntity> clazz) {
        return REGISTRY.getNameForObject(clazz);
    }

    @Nullable
    public static TileEntity create(World worldIn, NBTTagCompound compound) {
        TileEntity tileentity = null;
        String s = compound.getString("id");
        Class<? extends TileEntity> oclass = null;

        try {
            oclass = (Class) REGISTRY.getObject(new ResourceLocation(s));

            if (oclass != null) {
                tileentity = oclass.newInstance();
            }
        } catch (Throwable throwable1) {
            LOGGER.error("Failed to create block entity {}", s, throwable1);
            FMLLog.log.error("A TileEntity {}({}) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
                    s, oclass == null ? null : oclass.getName(), throwable1);
        }

        if (tileentity != null) {
            try {
                tileentity.setWorldCreate(worldIn);
                tileentity.readFromNBT(compound);
            } catch (Throwable throwable) {
                LOGGER.error("Failed to load data for block entity {}", s, throwable);
                FMLLog.log.error("A TileEntity {}({}) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
                        s, oclass.getName(), throwable);
                tileentity = null;
            }
        } else {
            LOGGER.warn("Skipping BlockEntity with id {}", (Object) s);
        }

        return tileentity;
    }

    /**
     * Returns the worldObj for this tileEntity.
     */
    public World getWorld() {
        return this.world;
    }

    /**
     * Sets the worldObj for this tileEntity.
     */
    public void setWorld(World worldIn) {
        this.world = worldIn;
    }

    /**
     * Returns true if the worldObj isn't null.
     */
    public boolean hasWorld() {
        return this.world != null;
    }

    public void readFromNBT(NBTTagCompound compound) {
        this.pos = new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z"));
        if (compound.hasKey("ForgeData")) this.customTileData = compound.getCompoundTag("ForgeData");
        if (this.capabilities != null && compound.hasKey("ForgeCaps"))
            this.capabilities.deserializeNBT(compound.getCompoundTag("ForgeCaps"));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return this.writeInternal(compound);
    }

    private NBTTagCompound writeInternal(NBTTagCompound compound) {
        ResourceLocation resourcelocation = REGISTRY.getNameForObject(this.getClass());

        if (resourcelocation == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        } else {
            compound.setString("id", resourcelocation.toString());
            compound.setInteger("x", this.pos.getX());
            compound.setInteger("y", this.pos.getY());
            compound.setInteger("z", this.pos.getZ());
            if (this.customTileData != null) compound.setTag("ForgeData", this.customTileData);
            if (this.capabilities != null) compound.setTag("ForgeCaps", this.capabilities.serializeNBT());
            return compound;
        }
    }

    protected void setWorldCreate(World worldIn) {
    }

    /*@Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return null;
    }*/

    public int getBlockMetadata() {
        if (this.blockMetadata == -1) {
            IBlockState iblockstate = this.world.getBlockState(this.pos);
            this.blockMetadata = iblockstate.getBlock().getMetaFromState(iblockstate);
        }

        return this.blockMetadata;
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty() {
        if (this.world != null) {
            IBlockState iblockstate = this.world.getBlockState(this.pos);
            this.blockMetadata = iblockstate.getBlock().getMetaFromState(iblockstate);
            this.world.markChunkDirty(this.pos, this);

            if (this.getBlockType() != Blocks.AIR) {
                this.world.updateComparatorOutputLevel(this.pos, this.getBlockType());
            }
        }
    }

    /**
     * Returns the square of the distance between this entity and the passed in coordinates.
     */
    public double getDistanceSq(double x, double y, double z) {
        double d0 = (double) this.pos.getX() + 0.5D - x;
        double d1 = (double) this.pos.getY() + 0.5D - y;
        double d2 = (double) this.pos.getZ() + 0.5D - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 4096.0D;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setPos(BlockPos posIn) {
        this.pos = posIn.toImmutable();
    }

    /**
     * Gets the block type at the location of this entity (client-only).
     */
    public Block getBlockType() {
        if (this.blockType == null && this.world != null) {
            this.blockType = this.world.getBlockState(this.pos).getBlock();
        }

        return this.blockType;
    }

    /**
     * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
     */
    public NBTTagCompound getUpdateTag() {
        return this.writeInternal(new NBTTagCompound());
    }

    public boolean isInvalid() {
        return this.tileEntityInvalid;
    }

    /**
     * invalidates a tile entity
     */
    public void invalidate() {
        this.tileEntityInvalid = true;
    }

    /**
     * validates a tile entity
     */
    public void validate() {
        this.tileEntityInvalid = false;
    }

    /**
     * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
     * clientside.
     */
    public boolean receiveClientEvent(int id, int type) {
        return false;
    }

    // -- BEGIN FORGE PATCHES --
    /**
     * Called when you receive a TileEntityData packet for the location this
     * TileEntity is currently in. On the client, the NetworkManager will always
     * be the remote server. On the server, it will be whomever is responsible for
     * sending the packet.
     *
     * @param net The NetworkManager the packet originated from
     * @param pkt The data packet
     */
    /*public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
    }*/

    public void updateContainingBlockInfo() {
        this.blockType = null;
        this.blockMetadata = -1;
    }

    public void addInfoToCrashReport(CrashReportCategory reportCategory) {
        reportCategory.addDetail("Name", new ICrashReportDetail<String>() {
            public String call() throws Exception {
                return TileEntity.REGISTRY.getNameForObject(TileEntity.this.getClass()) + " // " + TileEntity.this.getClass().getCanonicalName();
            }
        });

        if (this.world != null) {
            CrashReportCategory.addBlockInfo(reportCategory, this.pos, this.getBlockType(), this.getBlockMetadata());
            reportCategory.addDetail("Actual block type", () -> {
                int i = Block.getIdFromBlock(TileEntity.this.world.getBlockState(TileEntity.this.pos).getBlock());

                try {
                    return String.format("ID #%d (%s // %s)", i, Block.getBlockById(i).getUnlocalizedName(), Block.getBlockById(i).getClass().getCanonicalName());
                } catch (Throwable var3) {
                    return "ID #" + i;
                }
            });
            reportCategory.addDetail("Actual block data value", () -> {
                IBlockState iblockstate = TileEntity.this.world.getBlockState(TileEntity.this.pos);
                int i = iblockstate.getBlock().getMetaFromState(iblockstate);

                if (i < 0) {
                    return "Unknown? (Got " + i + ")";
                } else {
                    String s = String.format("%4s", Integer.toBinaryString(i)).replace(" ", "0");
                    return String.format("%1$d / 0x%1$X / 0b%2$s", i, s);
                }
            });
        }
    }

    public boolean onlyOpsCanSetNbt() {
        return false;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    @Nullable
    public ITextComponent getDisplayName() {
        return null;
    }

    public void rotate(Rotation rotationIn) {
    }

    public void mirror(Mirror mirrorIn) {
    }

    /**
     * Called when the chunk's TE update tag, gotten from {@link #getUpdateTag()}, is received on the client.
     * <p>
     * Used to handle this tag in a special way. By default this simply calls {@link #readFromNBT(NBTTagCompound)}.
     *
     * @param tag The {@link NBTTagCompound} sent from {@link #getUpdateTag()}
     */
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    /**
     * Called when the chunk this TileEntity is on is Unloaded.
     */
    public void onChunkUnload() {
    }

    /**
     * Called from Chunk.setBlockIDWithMetadata and Chunk.fillChunk, determines if this tile entity should be re-created when the ID, or Metadata changes.
     * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
     *
     * @param world    Current world
     * @param pos      Tile's world position
     * @param oldState The old ID of the block
     * @return true forcing the invalidation of the existing TE, false not to invalidate the existing TE
     */
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return isVanilla ? (oldState.getBlock() != newSate.getBlock()) : oldState != newSate;
    }

    public boolean shouldRenderInPass(int pass) {
        return pass == 0;
    }

    /**
     * at this location.
     *
     * @return an appropriately size {@link AxisAlignedBB} for the {@link TileEntity}
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        Block type = getBlockType();
        BlockPos pos = getPos();
        if (type == Blocks.ENCHANTING_TABLE) {
            bb = new AxisAlignedBB(pos, pos.add(1, 1, 1));
        } else if (type == Blocks.CHEST || type == Blocks.TRAPPED_CHEST) {
            bb = new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
        } else if (type == Blocks.STRUCTURE_BLOCK) {
            bb = INFINITE_EXTENT_AABB;
        }
        return bb;
    }

    /**
     * Checks if this tile entity knows how to render its 'breaking' overlay effect.
     * If this returns true, The TileEntitySpecialRenderer will be called again with break progress set.
     *
     * @return True to re-render tile with breaking effect.
     */
    public boolean canRenderBreaking() {
        Block block = this.getBlockType();
        return (block instanceof BlockChest);
    }

    /**
     * Gets a {@link NBTTagCompound} that can be used to store custom data for this tile entity.
     * It will be written, and read from disc, so it persists over world saves.
     *
     * @return A compound tag for custom data
     */
    public NBTTagCompound getTileData() {
        if (this.customTileData == null) {
            this.customTileData = new NBTTagCompound();
        }
        return this.customTileData;
    }

    /**
     * Determines if the player can overwrite the NBT data of this tile entity while they place it using a ItemStack.
     * Added as a fix for MC-75630 - Exploit with signs and command blocks
     *
     * @return True to prevent NBT copy, false to allow.
     */
    public boolean restrictNBTCopy() {
        return false;
    }

    /**
     * Override instead of adding {@code if (firstTick)} stuff in update.
     */
    public void onLoad() {
        // NOOP
    }

    /**
     * If the TileEntitySpecialRenderer associated with this TileEntity can be batched in with another renderers, and won't access the GL state.
     * If TileEntity returns true, then TESR should have the same functionality as (and probably extend) the FastTESR class.
     */
    public boolean hasFastRenderer() {
        return false;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capabilities == null ? false : capabilities.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capabilities == null ? null : capabilities.getCapability(capability, facing);
    }

    public void deserializeNBT(NBTTagCompound nbt) {
        this.readFromNBT(nbt);
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound ret = new NBTTagCompound();
        this.writeToNBT(ret);
        return ret;
    }
}
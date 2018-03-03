package net.timmy.timmylib.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.thegaminghuskymc.huskylib2.lib.utils.ProxyRegistry;

public class BlockModTrapdoor extends BlockTrapDoor implements IModBlock {

    private final String[] variants;
    private final String bareName;

    public BlockModTrapdoor(String name) {
        super(Material.WOOD);

        setHardness(3.0F);
        setSoundType(SoundType.WOOD);

        variants = new String[]{name};
        bareName = name;

        setUnlocalizedName(name);
        useNeighborBrightness = true;
    }

    
    @Override
    public Block setUnlocalizedName( String name) {
        super.setUnlocalizedName(name);
        setRegistryName(getPrefix(), name);
        ProxyRegistry.register(this);
        ProxyRegistry.register(new ItemBlock(this).setRegistryName(new ResourceLocation(this.getPrefix(), name)));
        return this;
    }

    @Override
    public String getBareName() {
        return bareName;
    }

    @Override
    public String[] getVariants() {
        return variants;
    }

    @Override
    public ItemMeshDefinition getCustomMeshDefinition() {
        return null;
    }

    @Override
    public EnumRarity getBlockRarity(ItemStack stack) {
        return EnumRarity.COMMON;
    }

    @Override
    public IProperty[] getIgnoredProperties() {
        return new IProperty[0];
    }

    @Override
    public IProperty getVariantProp() {
        return null;
    }

    @Override
    public Class getVariantEnum() {
        return null;
    }

}

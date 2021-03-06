package team.hdt.blockadia.engine.core.world.biomes;

import team.hdt.blockadia.engine_rewrite.client.blocks.BlockType;
import team.hdt.blockadia.engine.core.init.Biomes;
import team.hdt.blockadia.engine.core.init.Blocks;
import team.hdt.blockadia.engine.core.util.Identifier;
import team.hdt.blockadia.engine.core.world.gen.interfaces.IBiome;
import team.hdt.blockadia.engine.core.world.gen.interfaces.IForest;
import team.hdt.blockadia.engine.core.world.gen.interfaces.ILayer;
import team.hdt.blockadia.engine.core.world.gen.interfaces.ITree;
import team.hdt.blockadia.engine.core.world.gen.trees.BasicTree;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BiomeForest extends Biome implements IBiome, IForest {

    public Identifier identifier = new Identifier("forest");

    public BiomeForest() {
        super(new BiomeProperties("Forest"));
    }

    //TODO: finish tree gen.
    @Override
    public ILayer getLayer(int y, Random random) {
        return y < 50 ? new BiomeForest.LayerSolid(Blocks.OAK_PLANKS) : new ILayer.LayerAir(32, 32);
    }

    /**
     * Get's the chance of getting a {@link IBiome} generated next to this biome
     *
     * @return a {@link HashMap} with all the possible neighbour biomes and their chances.
     */
    @Override
    public Map<IBiome, Integer> getChanceMap() {
        HashMap<IBiome, Integer> chanceMap = new HashMap<>();
        chanceMap.put(Biomes.PLAINS, 1);
        chanceMap.put(Biomes.DESERT, 1);
        return chanceMap;
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public float getTreeDensity() {
        return 1.0f;
    }

    @Override
    public ITree getTreeGen() {
        return new BasicTree(Blocks.OAK_LOG, Blocks.OAK_LEAVES);
    }

    private class LayerSolid implements ILayer {

        BlockType type;

        LayerSolid(BlockType type) {
            this.type = type;
        }

        @Override
        public int getWidth() {
            return 32;
        }

        @Override
        public int getHeight() {
            return 32;
        }

        @Override
        public BlockType getBlockAtPos(int x, int z) {
            return type;
        }
    }

}
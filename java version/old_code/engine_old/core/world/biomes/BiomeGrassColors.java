package team.hdt.blockadia.old_engine_code_1.core.world.biomes;

import com.google.common.collect.Maps;
import team.hdt.blockadia.old_engine_code_1.core.init.Biomes;
import team.hdt.blockadia.old_engine_code_1.core.world.gen.interfaces.IBiome;

import java.awt.*;
import java.util.Map;
import java.util.function.Function;

public class BiomeGrassColors implements Function<IBiome, Color> {

    private final Map<IBiome, Color> biomeColors = Maps.newHashMap();

    public BiomeGrassColors() {
        biomeColors.put(Biomes.DESERT, new Color(0xa9834f));
        biomeColors.put(Biomes.NETHER, new Color(0xb42c2c));
    }

    @Override
    public Color apply(IBiome iBiome) {
        return biomeColors.get(iBiome);
    }

}
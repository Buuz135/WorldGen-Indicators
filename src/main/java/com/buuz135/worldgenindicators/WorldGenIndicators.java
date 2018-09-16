package com.buuz135.worldgenindicators;

import com.buuz135.worldgenindicators.api.IChecker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

@Mod(
        modid = WorldGenIndicators.MOD_ID,
        name = WorldGenIndicators.MOD_NAME,
        version = WorldGenIndicators.VERSION
)
public class WorldGenIndicators {

    //TODO Block Dependents: Surface, Block Relative, Surface Relative

    public static final String MOD_ID = "worldgenindicators";
    public static final String MOD_NAME = "WorldGenIndicators";
    public static final String VERSION = "1.0";

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static WorldGenIndicators INSTANCE;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new WorldGenIndicatorsGenerator(), Integer.MAX_VALUE);
    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }

    public static class WorldGenIndicatorsGenerator implements IWorldGenerator {

        @Override
        public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
            Chunk chunk = chunkProvider.getLoadedChunk(chunkX, chunkZ);
            if (chunk != null) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int i = 0; i < world.getTopSolidOrLiquidBlock(new BlockPos(chunkX * 16 + x, i, chunkZ * 16 + z)).getY() + 4; i++) {
                            if (check(world, chunkX, chunkZ, x, z, i)) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        private boolean check(World world, int chunkX, int chunkZ, int x, int z, int i) {
            for (IChecker iChecker : WorldGenManager.checkerList) {
                BlockPos pos = new BlockPos(chunkX * 16 + x, i, chunkZ * 16 + z);
                if (iChecker.isValid(world, pos) && world.rand.nextDouble() < iChecker.getWorkingChance()) {
                    iChecker.getRandomIndicator(world.rand).generate(world, pos);
                    return true;
                }
            }
            return false;
        }
    }
}

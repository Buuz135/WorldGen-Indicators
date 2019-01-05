/*
 * This file is part of Worldgen Indicators.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.worldgenindicators;

import com.buuz135.worldgenindicators.api.IChecker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Mod(
        modid = WorldGenIndicators.MOD_ID,
        name = WorldGenIndicators.MOD_NAME,
        version = WorldGenIndicators.VERSION
)
@Mod.EventBusSubscriber
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
    public static Logger LOGGER;
    public static ConcurrentHashMap<Pair<World, BlockPos>, IChecker> CACHED_CHECKER = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            long time = System.currentTimeMillis();
            int checked = 0;
            for (Pair<Integer, Integer> integerIntegerPair : WorldGenIndicatorsGenerator.CHUNKS_TO_CHECK.keySet()) {
                World world = WorldGenIndicatorsGenerator.CHUNKS_TO_CHECK.get(integerIntegerPair);
                WorldGenIndicatorsGenerator.checkSection(world, integerIntegerPair.getLeft(), integerIntegerPair.getRight());
                WorldGenIndicatorsGenerator.CHUNKS_TO_CHECK.remove(integerIntegerPair);
                ++checked;
                if (checked >= WorldGenManager.CHECKING_OPERATIONS_TICK) {
                    if (System.currentTimeMillis() - time > 60 && WorldGenManager.TIME_LOGGER)
                        LOGGER.warn("WorldGenIndicators checking is taking to long! (" + (System.currentTimeMillis() - time) + "ms). Current section checking: " + WorldGenManager.CHECKING_OPERATIONS_TICK +
                                ". You can change that by running 'WorldGenManager.setCheckingOperationsTick(int amount)' in the script. You can disable this by running 'WorldGenManager.disableLogger()'");
                    break;
                }
            }
        }
        if (event.phase == TickEvent.Phase.END) {
            CACHED_CHECKER.keySet().iterator().forEachRemaining(worldBlockPosPair -> {
                if (worldBlockPosPair.getLeft().isBlockLoaded(worldBlockPosPair.getRight())) {
                    IChecker checker = CACHED_CHECKER.get(worldBlockPosPair);
                    checker.getRandomIndicator(worldBlockPosPair.getLeft().rand).generate(worldBlockPosPair.getLeft(), worldBlockPosPair.getRight(), checker);
                    CACHED_CHECKER.remove(worldBlockPosPair);
                }
            });
        }
    }

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new WorldGenIndicatorsGenerator(), Integer.MAX_VALUE);
        LOGGER = event.getModLog();
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

        public static ConcurrentHashMap<Pair<Integer, Integer>, World> CHUNKS_TO_CHECK = new ConcurrentHashMap<>();

        public static boolean check(World world, int x, int z, int i) {
            BlockPos pos = new BlockPos(x, i, z);
            for (IChecker iChecker : WorldGenManager.checkerList) {
                if (iChecker.isValid(world, pos) && world.rand.nextDouble() < iChecker.getWorkingChance()) {
                    CACHED_CHECKER.put(Pair.of(world, pos), iChecker);
                    return true;
                }
            }
            return false;
        }

        public static void checkSection(World world, int posX, int posZ) {
            for (int x = 0; x < 4; ++x) {
                for (int z = 0; z < 16; ++z) {
                    for (int i = 0; i < world.getTopSolidOrLiquidBlock(new BlockPos(posX + x, i, posZ + z)).getY() + 4; i++) {
                        if (WorldGenIndicatorsGenerator.check(world, posX + x, posZ + z, i)) break;
                    }
                }
            }
        }

        @Override
        public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
            if (!CHUNKS_TO_CHECK.containsKey(Pair.of(chunkX, chunkZ)))
                CHUNKS_TO_CHECK.put(Pair.of(chunkX * 16 + 8, chunkZ * 16 + 8), world);
            CHUNKS_TO_CHECK.put(Pair.of(chunkX * 16 + 8 + 4, chunkZ * 16 + 8), world);
            CHUNKS_TO_CHECK.put(Pair.of(chunkX * 16 + 8 + 8, chunkZ * 16 + 8), world);
            CHUNKS_TO_CHECK.put(Pair.of(chunkX * 16 + 8 + 12, chunkZ * 16 + 8), world);
        }
    }
}

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
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    @Mod.Instance(MOD_ID)
    public static WorldGenIndicators INSTANCE;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new WorldGenIndicatorsGenerator(), Integer.MAX_VALUE);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }

    public enum ReturnType {
        MISSING,
        LUCKY,
        UNLUCKY;
    }

    public static class WorldGenIndicatorsGenerator implements IWorldGenerator {

        private static ReturnType check(World world, int x, int z, int i) {
            BlockPos pos = new BlockPos(x, i, z);
            boolean valid = false;
            for (IChecker iChecker : WorldGenManager.checkerList) {
                if (iChecker.isValid(world, pos)) {
                    valid = true;
                    if (world.rand.nextDouble() < iChecker.getWorkingChance()) {
                        iChecker.getRandomIndicator(world.rand).generate(world, pos, iChecker);
                        return ReturnType.LUCKY;
                    }
                }
            }
            return valid ? ReturnType.UNLUCKY : ReturnType.MISSING;
        }

        private static void checkSection(World world, int posX, int posZ) {
            List<IBlockState> checked = new ArrayList<>();
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    for (int i = 0; i < world.getTopSolidOrLiquidBlock(new BlockPos(posX + x, i, posZ + z)).getY() + 4; i++) {
                        IBlockState blockState = world.getBlockState(new BlockPos(posX + x, i, posZ + z));
                        if (!checked.contains(blockState)) {
                            ReturnType type = WorldGenIndicatorsGenerator.check(world, posX + x, posZ + z, i);
                            if (type == ReturnType.LUCKY) break;
                            if (type == ReturnType.MISSING) checked.add(blockState);
                        }
                    }
                }
            }
        }

        @Override
        public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
            checkSection(world, chunkX * 16 + 1, chunkZ * 16 + 1);
        }
    }
}

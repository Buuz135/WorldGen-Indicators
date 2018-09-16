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
package com.buuz135.worldgenindicators.indicator;

import com.buuz135.worldgenindicators.api.IIndicator;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("mods.worldgenindicators.SurfaceIndicator")
public class SurfaceIndicator implements IIndicator {

    private List<IBlock> blockList;

    public SurfaceIndicator() {
        this.blockList = new ArrayList<>();
    }

    @ZenMethod
    public static SurfaceIndicator create() {
        return new SurfaceIndicator();
    }

    @ZenMethod
    @Override
    public IIndicator add(IBlock block) {
        blockList.add(block);
        return this;
    }

    @Override
    public void generate(World world, BlockPos original) {
        IBlock selected = blockList.get(world.rand.nextInt(blockList.size()));
        int y = world.getTopSolidOrLiquidBlock(new BlockPos(original.getX(), 0, original.getZ())).getY();
        BlockPos pos = new BlockPos(original.getX(), y, original.getZ());
        if (!world.getBlockState(pos).getMaterial().isLiquid() && world.isAirBlock(pos)) {
            world.setBlockState(pos, Block.getBlockFromName(selected.getDefinition().getId()).getStateFromMeta(selected.getMeta()));
        }
    }
}

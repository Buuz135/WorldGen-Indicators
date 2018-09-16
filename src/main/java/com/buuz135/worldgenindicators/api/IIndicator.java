package com.buuz135.worldgenindicators.api;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
public interface IIndicator {

    @ZenMethod
    IIndicator add(IBlock block);

    void generate(World world, BlockPos pos);
}

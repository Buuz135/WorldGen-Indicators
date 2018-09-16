package com.buuz135.worldgenindicators.api;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Random;

@ZenRegister
public interface IChecker<T> {

    double getWorkingChance();

    boolean isValid(World world, BlockPos pos);

    @ZenMethod
    IChecker<T> addValid(T valid);

    @ZenMethod
    IChecker<T> addIndicator(IIndicator indicator);

    IIndicator getRandomIndicator(Random random);

}

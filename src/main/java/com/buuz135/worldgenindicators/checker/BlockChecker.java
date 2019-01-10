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
package com.buuz135.worldgenindicators.checker;

import com.buuz135.worldgenindicators.api.IChecker;
import com.buuz135.worldgenindicators.api.IIndicator;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.mc1120.block.MCWorldBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ZenRegister
@ZenClass("mods.worldgenindicators.BlockChecker")
public class BlockChecker implements IChecker {

    private double chance;
    private List<IBlock> blockList;
    private List<IBlock> blockWhitelist;
    private List<IBlock> blockBlacklist;
    private List<IIndicator> indicators;

    public BlockChecker(double chance) {
        this.chance = chance;
        this.blockList = new ArrayList<>();
        this.indicators = new ArrayList<>();
        this.blockWhitelist = new ArrayList<>();
        this.blockBlacklist = new ArrayList<>();
    }

    @ZenMethod
    public static BlockChecker create(double chance) {
        return new BlockChecker(chance);
    }

    @Override
    public double getWorkingChance() {
        return chance;
    }

    @Override
    public boolean isValid(World world, BlockPos pos) {
        MCWorldBlock worldBlock = new MCWorldBlock(world, pos.getX(), pos.getY(), pos.getZ());
        for (IBlock block : blockList) {
            if (worldBlock.matches(block)) return true;
        }
        return false;
    }

    @Override
    public boolean isPlacementValid(World world, BlockPos pos) {
        IBlock block = new MCWorldBlock(world, pos.getX(), pos.getY(), pos.getZ());
        return blockWhitelist.isEmpty() ? blockBlacklist.stream().noneMatch(b -> b.matches(block)) : blockWhitelist.stream().anyMatch(b -> b.matches(block));
    }

    @ZenMethod
    @Override
    public IChecker addValid(IBlock valid) {
        this.blockList.add(valid);
        return this;
    }

    @ZenMethod
    @Override
    public IChecker addIndicator(IIndicator indicator) {
        this.indicators.add(indicator);
        return this;
    }

    @ZenMethod
    @Override
    public IChecker addWhitelistEntry(IBlock block) {
        this.blockWhitelist.add(block);
        return this;
    }

    @ZenMethod
    @Override
    public IChecker addBlacklistEntry(IBlock block) {
        this.blockBlacklist.add(block);
        return this;
    }

    @Override
    public IIndicator getRandomIndicator(Random random) {
        return this.indicators.get(random.nextInt(this.indicators.size()));
    }
}

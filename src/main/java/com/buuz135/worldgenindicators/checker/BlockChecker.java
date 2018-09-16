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
public class BlockChecker implements IChecker<IBlock> {

    private double chance;
    private List<IBlock> blockList;
    private List<IIndicator> indicators;

    public BlockChecker(double chance) {
        this.chance = chance;
        this.blockList = new ArrayList<>();
        this.indicators = new ArrayList<>();
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

    @Override
    public IIndicator getRandomIndicator(Random random) {
        return this.indicators.get(random.nextInt(this.indicators.size()));
    }
}

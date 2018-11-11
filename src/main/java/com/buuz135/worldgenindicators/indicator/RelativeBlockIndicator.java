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
@ZenClass("mods.worldgenindicators.RelativeBlockIndicator")
public class RelativeBlockIndicator implements IIndicator {

    private final int relative;
    private List<IBlock> blockList;

    public RelativeBlockIndicator(int relative) {
        this.relative = relative;
        this.blockList = new ArrayList<>();
    }

    @ZenMethod
    public static RelativeBlockIndicator create(int relative) {
        return new RelativeBlockIndicator(relative);
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
        BlockPos pos = original.add(0, relative, 0);
        world.setBlockState(pos, Block.getBlockFromName(selected.getDefinition().getId()).getStateFromMeta(selected.getMeta()), 2);
    }
}

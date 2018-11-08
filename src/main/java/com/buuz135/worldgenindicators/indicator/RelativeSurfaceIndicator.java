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
@ZenClass("mods.worldgenindicators.RelativeSurfaceIndicator")
public class RelativeSurfaceIndicator implements IIndicator {

    private final int relative;
    private List<IBlock> blockList;

    public RelativeSurfaceIndicator(int relative) {
        this.relative = relative;
        this.blockList = new ArrayList<>();
    }

    @ZenMethod
    public static RelativeSurfaceIndicator create(int relative) {
        return new RelativeSurfaceIndicator(relative);
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
        BlockPos pos = new BlockPos(original.getX(), y + relative, original.getZ());
        if (!world.getBlockState(pos).getMaterial().isLiquid() && world.isAirBlock(pos)) {
            world.setBlockState(pos, Block.getBlockFromName(selected.getDefinition().getId()).getStateFromMeta(selected.getMeta()), 2);
        }
    }
}

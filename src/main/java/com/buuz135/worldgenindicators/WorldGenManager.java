package com.buuz135.worldgenindicators;

import com.buuz135.worldgenindicators.api.IChecker;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("mods.worldgenindicators.WorldGenManager")
public class WorldGenManager {

    public static List<IChecker> checkerList = new ArrayList<>();

    @ZenMethod
    public static void addChecker(IChecker checker) {
        checkerList.add(checker);
    }
}

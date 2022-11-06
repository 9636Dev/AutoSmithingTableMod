package io.github.hw9636.autosmithingtable.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ASTConfig {
    public static class Common
    {
        private static final int defaultMaxEnergyStored = 100_000;
        private static final int defaultTicksPerCraft = 20;
        private static final int defaultEnergyPerTick = 40;

        public final ForgeConfigSpec.ConfigValue<Integer> maxEnergyStored;
        public final ForgeConfigSpec.ConfigValue<Integer> ticksPerCraft;
        public final ForgeConfigSpec.ConfigValue<Integer> energyPerTick;


        public Common(ForgeConfigSpec.Builder builder)
        {
            this.maxEnergyStored = builder.comment("Max Energy Stored, range 100 - 100,000,000, default 100,000")
                    .defineInRange("maxEnergyStored", defaultMaxEnergyStored, 100,100_000_000);

            this.ticksPerCraft = builder.comment("Ticks per craft in ticks, range 1 - 2048, default 20")
                    .defineInRange("ticksPerCraft", defaultTicksPerCraft, 1, 2048);

            this.energyPerTick = builder.comment("Energy per tick of crafting, range 1 - 8192, default 40")
                    .defineInRange("energyPerTick", defaultEnergyPerTick, 1, 8192);

        }
    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }

}

package com.baran.techedout.init;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber
public class Config {

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_POWER = "power";
    public static final String SUBCATEGORY_GENERATOR = "generator";

    public static ForgeConfigSpec SERVER_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.IntValue GENERATOR_MAXPOWER;
    public static ForgeConfigSpec.IntValue GENERATOR_GENERATE;
    public static ForgeConfigSpec.IntValue GENERATOR_SEND;

    public static ForgeConfigSpec.DoubleValue ROTATION_SPEED;


    static {

        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        CLIENT_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        ROTATION_SPEED = CLIENT_BUILDER.comment("Rotation speed of the magic block").defineInRange("rotationSpeed", 100.0, 0.0, 1000000.0);
        CLIENT_BUILDER.pop();

        SERVER_BUILDER.comment("Power settings").push(CATEGORY_POWER);

        setupGeneratorConfig(SERVER_BUILDER, CLIENT_BUILDER);

        SERVER_BUILDER.pop();


        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupGeneratorConfig(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Generator settings").push(SUBCATEGORY_GENERATOR);

        GENERATOR_MAXPOWER = SERVER_BUILDER.comment("Maximum power for the generator")
                .defineInRange("maxPower", 10000, 0, Integer.MAX_VALUE);
        GENERATOR_GENERATE = SERVER_BUILDER.comment("Power generation per tick")
                .defineInRange("generate", 10, 0, Integer.MAX_VALUE);
        GENERATOR_SEND = SERVER_BUILDER.comment("Power to send per tick")
                .defineInRange("send", 100, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
    }


}

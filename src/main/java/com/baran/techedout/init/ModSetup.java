package com.baran.techedout.init;

import com.baran.techedout.TechedOut;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod.EventBusSubscriber(modid = TechedOut.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup {
	
	public static final ItemGroup ITEM_GROUP = new ItemGroup(TechedOut.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Registration.GENERATOR.get());
        }
    };
	
    public static void init(final FMLCommonSetupEvent event) {
    	
    }

    @SubscribeEvent
    public static void serverLoad(FMLServerStartingEvent event) {
    }

}
package dev.geri.crescentcurrency;

import dev.geri.crescentcurrency.blocks.pressblock.Screen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@Environment(EnvType.CLIENT)
public class CrescentCurrencyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(CrescentCurrency.getScreenHandler(), Screen::new); // Register custom crafting screen
    }

}
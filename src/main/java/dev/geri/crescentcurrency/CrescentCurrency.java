package dev.geri.crescentcurrency;

import dev.geri.crescentcurrency.blocks.pressblock.PressBlock;
import dev.geri.crescentcurrency.blocks.pressblock.ScreenHandler;
import dev.geri.crescentcurrency.utils.CoinItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class CrescentCurrency implements ModInitializer {

    public static final String MODID = "crescentcurrency";
    private static final Logger logger = LoggerFactory.getLogger("CrescentCurrency");
    private static ScreenHandlerType<ScreenHandler> screenHandler;

    private static final LinkedHashMap<String, Item> items = new LinkedHashMap<>() {{
        this.put("copper_coin", new CoinItem(new FabricItemSettings().rarity(Rarity.COMMON).group(ItemGroup.MISC), CoinItem.Tier.COPPER));
        this.put("silver_coin", new CoinItem(new FabricItemSettings().rarity(Rarity.COMMON).group(ItemGroup.MISC), CoinItem.Tier.SILVER));
        this.put("gold_coin", new CoinItem(new FabricItemSettings().rarity(Rarity.RARE).group(ItemGroup.MISC), CoinItem.Tier.GOLD));
        this.put("platinum_coin", new CoinItem(new FabricItemSettings().rarity(Rarity.EPIC).group(ItemGroup.MISC), CoinItem.Tier.PLATINUM));
    }};

    private static final LinkedHashMap<String, Map.Entry<Block, BlockItem>> blocks = new LinkedHashMap<>() {{
        Block pressBlock = new PressBlock(FabricBlockSettings.copyOf(Blocks.CRAFTING_TABLE));
        this.put("press", Map.entry(pressBlock, new BlockItem(pressBlock, new Item.Settings().group(ItemGroup.MISC))));
    }};

    @Override
    public void onInitialize() {

        // Register events
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(MODID, "press_request"), (server, player, handler, buf, responseSender) -> server.execute(() -> {
            if (!(player.currentScreenHandler instanceof ScreenHandler press)) return;
            press.onPress();
        }));

        // Register handler
        CrescentCurrency.screenHandler = ScreenHandlerRegistry.registerSimple(new Identifier(MODID, "press"), ScreenHandler::new);

        // Register blocks
        for (Map.Entry<String, Map.Entry<Block, BlockItem>> entry : blocks.entrySet()) {
            Registry.register(Registry.BLOCK, new Identifier(MODID, entry.getKey()), entry.getValue().getKey());
            Registry.register(Registry.ITEM, new Identifier(MODID, entry.getKey()), entry.getValue().getValue());
        }

        // Register items
        for (Map.Entry<String, Item> entry : items.entrySet()) {
            Registry.register(Registry.ITEM, new Identifier(MODID, entry.getKey()), entry.getValue());
        }

    }

    @Nullable
    public static ScreenHandlerType<ScreenHandler> getScreenHandler() {
        return screenHandler;
    }

    @Nullable
    public static Item getItem(String name) {
        return items.get(name);
    }

}


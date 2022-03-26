package dev.geri.crescentcurrency.blocks.pressblock;

import dev.geri.crescentcurrency.utils.CoinItem;
import dev.geri.crescentcurrency.CrescentCurrency;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class ScreenHandler extends net.minecraft.screen.ScreenHandler {

    private final Inventory input;
    private final Inventory result;
    private final Inventory inventory;
    private final ScreenHandlerContext context;
    private final ItemStack barrierItem;
    private final PlayerEntity player;

    // Called on the client when it wants to open the screenHandler
    public ScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    // Called from the BlockEntity on the server without calling the other constructor
    public ScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(CrescentCurrency.getScreenHandler(), syncId);
        this.inventory = new SimpleInventory(11);
        this.context = context;
        this.player = playerInventory.player;
        this.barrierItem = Items.BARRIER.getDefaultStack();
        this.barrierItem.setCustomName(Text.of("§cInvalid recipe!"));

        // Ensure compatibility
        ScreenHandler.checkSize(inventory, 11);
        this.inventory.onOpen(playerInventory.player);

        // Create input
        this.input = new SimpleInventory(10) {
            @Override
            public void markDirty() {
                ScreenHandler.this.onContentChanged(this);
            }
        };

        int i = 0;
        int j = 0;

        // Inputs
        for (i = 0; i < 2; ++i) {
            for (j = 0; j < 5; j++) {
                Slot slot = new Slot(input, j + i * 5, 39 + j * 18, 21 + i * 18) {
                    @Override
                    public boolean canInsert(ItemStack itemStack) {
                        return itemStack.getItem() instanceof CoinItem;
                    }
                };
                this.addSlot(slot);
            }
        }

        // Create output
        this.result = new CraftingResultInventory() {
            @Override
            public void markDirty() {
                ScreenHandler.this.onContentChanged(this);
            }
        };
        this.addSlot(new Slot(this.result, 0, 8, 67) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return this.getStack().getItem() != null && this.getStack().getItem() != Items.BARRIER;
            }

        });

        // Player inventory
        for (i = 0; i < 3; ++i) for (j = 0; j < 9; ++j) this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 95 + i * 18));

        // Hotbar
        for (i = 0; i < 9; ++i) this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 153));
    }

    // Shift click
    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {

        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot.hasStack()) {

            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();

        }

        return newStack;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.context.run((world, pos) -> {
            this.dropInventory(player, this.input);
            this.dropInventory(player, this.result);
        });
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public void onPress() {

        if (Screen.hasShiftDown()) {
            while (true) {
                Item item = this.getRecipe();
                if (item == null) {
                    break;
                } else this.handleItem(item);
            }
        } else {
            Item item = this.getRecipe();
            if (item == null) {
                this.handleInvalid();
            } else this.handleItem(item);
        }

    }

    private void handleInvalid() {
        this.player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 0.1f, 1);
        if (!result.isEmpty() && result.getStack(0).getItem() != Items.BARRIER) this.context.run((world, pos) -> this.dropInventory(player, this.result));
        this.result.setStack(0, barrierItem);
    }

    private void handleItem(Item item) {

        CoinItem.Tier tier = null;
        if (item instanceof CoinItem coinItem) {
            tier = coinItem.getHigherTier();
        }

        Item coin = Items.AIR;
        if (tier != null) {
            switch (tier) {
                case COPPER -> coin = CrescentCurrency.getItem("copper_coin");
                case SILVER -> coin = CrescentCurrency.getItem("silver_coin");
                case GOLD -> coin = CrescentCurrency.getItem("gold_coin");
                case PLATINUM -> coin = CrescentCurrency.getItem("platinum_coin");
            }
        }

        for (int i = 0; i < input.size(); i++) {
            ItemStack itemStack = input.getStack(i);
            itemStack.decrement(1);
        }

        this.player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 0.1f, 2);

        ItemStack resultItem = result.getStack(0);
        if (resultItem != null && !resultItem.isEmpty()) {
            if (resultItem.getItem() == coin) {
                resultItem.increment(1);
                return;
            } else {
                if (result.getStack(0).getItem() != Items.BARRIER) this.context.run((world, pos) -> this.dropInventory(player, this.result));
            }
        }

        this.result.setStack(0, coin.getDefaultStack());
    }

    private Item getRecipe() {
        int found = 0;
        Item previous = null;

        for (int i = 0; i < input.size(); i++) {
            ItemStack itemStack = input.getStack(i);

            if (previous == null || itemStack != null && itemStack.getItem() instanceof CoinItem && itemStack.getItem() == previous) {
                found++;
            }

            if (itemStack != null) previous = itemStack.getItem();
            else previous = null;
        }

        if (found < 10) {
            return null;
        } else {
            return previous;
        }
    }

}
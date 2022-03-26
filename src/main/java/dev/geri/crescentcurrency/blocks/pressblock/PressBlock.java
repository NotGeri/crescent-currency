package dev.geri.crescentcurrency.blocks.pressblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PressBlock extends Block {

    public PressBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) player.openHandledScreen(new Factory(pos));
        return ActionResult.SUCCESS;
    }

    private record Factory(BlockPos pos) implements NamedScreenHandlerFactory {

        @Override
        public Text getDisplayName() {
            return new TranslatableText("crescentcurrency.gui.title");
        }

        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            return new dev.geri.crescentcurrency.blocks.pressblock.ScreenHandler(syncId, inv, ScreenHandlerContext.create(player.world, pos));
        }
    }

}


package com.dremixam.immediatedeparture.validator;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import com.dremixam.immediatedeparture.ImmediateDeparture;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ImmediateDepartureBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ImmediateDeparture.MOD_ID, Registries.BLOCK);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ImmediateDeparture.MOD_ID, Registries.ITEM);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(ImmediateDeparture.MOD_ID, Registries.BLOCK_ENTITY_TYPE);
    private static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(ImmediateDeparture.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<TicketValidatorBlock> TICKET_VALIDATOR = BLOCKS.register("ticket_validator",
        () -> new TicketValidatorBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .strength(3.5f)
            .sound(SoundType.METAL)
            .noOcclusion()));

    public static final RegistrySupplier<TicketValidatorItem> TICKET_VALIDATOR_ITEM = ITEMS.register("ticket_validator",
        () -> new TicketValidatorItem(TICKET_VALIDATOR.get(), new Item.Properties()));

    // Icon-only item, used as the creative tab's button icon; not appended to the tab's contents.
    public static final RegistrySupplier<Item> TICKET_ICON_ITEM = ITEMS.register("ticket_icon", () -> new Item(new Item.Properties()));

    @SuppressWarnings("DataFlowIssue")
    public static final RegistrySupplier<BlockEntityType<TicketValidatorBlockEntity>> TICKET_VALIDATOR_BLOCK_ENTITY = BLOCK_ENTITIES.register(
        "ticket_validator", () -> BlockEntityType.Builder.of(TicketValidatorBlockEntity::new, TICKET_VALIDATOR.get()).build(null));

    // Own creative tab rather than folding into a vanilla one.
    public static final RegistrySupplier<CreativeModeTab> TAB = TABS.register("main",
        () -> CreativeTabRegistry.create(Component.translatable("itemGroup.create_immediate_departure"),
            () -> new ItemStack(TICKET_ICON_ITEM.get())));

    private ImmediateDepartureBlocks() {
    }

    public static void register() {
        BLOCKS.register();
        ITEMS.register();
        BLOCK_ENTITIES.register();
        TABS.register();
        CreativeTabRegistry.append(TAB, TICKET_VALIDATOR_ITEM);
    }
}

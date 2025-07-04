package com.cleanroommc.modularui.widgets.slot;

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.widget.IVanillaSlot;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.drawable.text.TextRenderer;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.integration.nei.NEIIngredientProvider;
import com.cleanroommc.modularui.screen.ClientScreenHandler;
import com.cleanroommc.modularui.mixins.early.minecraft.GuiAccessor;
import com.cleanroommc.modularui.mixins.early.minecraft.GuiContainerAccessor;
import com.cleanroommc.modularui.mixins.early.minecraft.GuiScreenAccessor;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetSlotTheme;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.utils.GlStateManager;
import com.cleanroommc.modularui.utils.MouseData;
import com.cleanroommc.modularui.utils.NumberFormat;
import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.cleanroommc.modularui.widget.Widget;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemSlot extends Widget<ItemSlot> implements IVanillaSlot, Interactable, NEIIngredientProvider {

    public static final int SIZE = 18;

    public static ItemSlot create(boolean phantom) {
        return phantom ? new PhantomItemSlot() : new ItemSlot();
    }

    private ItemSlotSH syncHandler;

    public ItemSlot() {
        tooltip().setAutoUpdate(true);//.setHasTitleMargin(true);
        tooltipBuilder(tooltip -> {
            if (!isSynced()) return;
            ItemStack stack = getSlot().getStack();
            buildTooltip(stack, tooltip);
        });
    }

    @Override
    public void onInit() {
        if (getScreen().isOverlay()) {
            throw new IllegalStateException("Overlays can't have slots!");
        }
        size(SIZE);
    }

    @Override
    public boolean isValidSyncHandler(SyncHandler syncHandler) {
        this.syncHandler = castIfTypeElseNull(syncHandler, ItemSlotSH.class);
        return this.syncHandler != null;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        boolean shouldBeEnabled = areAncestorsEnabled();
        if (shouldBeEnabled != getSlot().func_111238_b()) {
            this.syncHandler.setEnabled(shouldBeEnabled, true);
        }
    }

    @Override
    public void draw(ModularGuiContext context, WidgetTheme widgetTheme) {
        if (this.syncHandler == null) return;
        RenderHelper.enableGUIStandardItemLighting();
        drawSlot(getSlot());
        RenderHelper.disableStandardItemLighting();
        drawOverlay();
    }

    protected void drawOverlay() {
        if (isHovering()) {
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.colorMask(true, true, true, false);
            GuiDraw.drawRect(1, 1, 16, 16, getSlotHoverColor());
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.disableBlend();
        }
    }

    @Override
    public void drawForeground(ModularGuiContext context) {
        RichTooltip tooltip = getTooltip();
        if (tooltip != null && isHoveringFor(tooltip.getShowUpTimer())) {
            tooltip.draw(getContext(), getSlot().getStack());
        }
    }

    public void buildTooltip(ItemStack stack, RichTooltip tooltip) {
        if (stack == null) return;
        tooltip.addFromItem(stack);
    }

    @Override
    public WidgetSlotTheme getWidgetThemeInternal(ITheme theme) {
        return theme.getItemSlotTheme();
    }

    public int getSlotHoverColor() {
        WidgetTheme theme = getWidgetTheme(getContext().getTheme());
        if (theme instanceof WidgetSlotTheme slotTheme) {
            return slotTheme.getSlotHoverColor();
        }
        return ITheme.getDefault().getItemSlotTheme().getSlotHoverColor();
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        ClientScreenHandler.clickSlot(getScreen(), getSlot());
        return Result.SUCCESS;
    }

    @Override
    public boolean onMouseRelease(int mouseButton) {
        ClientScreenHandler.releaseSlot();
        return true;
    }

    @Override
    public void onMouseDrag(int mouseButton, long timeSinceClick) {
        ClientScreenHandler.dragSlot(timeSinceClick);
    }

    public ModularSlot getSlot() {
        return this.syncHandler.getSlot();
    }

    @Override
    public Slot getVanillaSlot() {
        return this.syncHandler.getSlot();
    }

    @Override
    public boolean handleAsVanillaSlot() {
        return true;
    }

    @Override
    public @NotNull ItemSlotSH getSyncHandler() {
        if (this.syncHandler == null) {
            throw new IllegalStateException("Widget is not initialised!");
        }
        return this.syncHandler;
    }

    public ItemSlot slot(ModularSlot slot) {
        this.syncHandler = new ItemSlotSH(slot);
        setSyncHandler(this.syncHandler);
        return this;
    }

    public ItemSlot slot(IItemHandlerModifiable itemHandler, int index) {
        return slot(new ModularSlot(itemHandler, index));
    }

    @SideOnly(Side.CLIENT)
    private void drawSlot(ModularSlot slotIn) {
        GuiScreen guiScreen = getScreen().getScreenWrapper().getGuiScreen();
        if (!(guiScreen instanceof GuiContainer))
            throw new IllegalStateException("The gui must be an instance of GuiContainer if it contains slots!");
        GuiContainerAccessor acc = (GuiContainerAccessor) guiScreen;
        RenderItem renderItem = GuiScreenAccessor.getItemRender();
        ItemStack itemstack = slotIn.getStack();
        boolean isDragPreview = false;
        boolean flag1 = slotIn == acc.getClickedSlot() && acc.getDraggedStack() != null && !acc.getIsRightMouseClick();
        ItemStack itemstack1 = guiScreen.mc.thePlayer.inventory.getItemStack();
        int amount = -1;
        String format = null;

        if (!getSyncHandler().isPhantom()) {
            if (slotIn == acc.getClickedSlot() && acc.getDraggedStack() != null && acc.getIsRightMouseClick() && itemstack != null) {
                itemstack = itemstack.copy();
                itemstack.stackSize /= 2;
            } else if (acc.getDragSplitting() && acc.getDragSplittingSlots().contains(slotIn) && itemstack1 != null) {
                if (acc.getDragSplittingSlots().size() == 1) {
                    return;
                }

                // canAddItemToSlot
                if (Container.func_94527_a(slotIn, itemstack1, true) && getScreen().getContainer().canDragIntoSlot(slotIn)) {
                    itemstack = itemstack1.copy();
                    isDragPreview = true;
                    // computeStackSize
                    Container.func_94525_a(acc.getDragSplittingSlots(), acc.getDragSplittingLimit(), itemstack, slotIn.getStack() == null ? 0 : slotIn.getStack().stackSize);
                    int k = Math.min(itemstack.getMaxStackSize(), slotIn.getSlotStackLimit());

                    if (itemstack.stackSize > k) {
                        amount = k;
                        format = EnumChatFormatting.YELLOW.toString();
                        itemstack.stackSize = k;
                    }
                } else {
                    acc.getDragSplittingSlots().remove(slotIn);
                    acc.invokeUpdateDragSplitting();
                }
            }
        }

        // makes sure items of different layers don't interfere with each other visually
        float z = getContext().getCurrentDrawingZ() + 100;
        ((GuiAccessor) guiScreen).setZLevel(z);
        renderItem.zLevel = z;

        if (!flag1) {
            if (isDragPreview) {
                GuiDraw.drawRect(1, 1, 16, 16, -2130706433);
            }

            if (itemstack != null) {
                GlStateManager.enableRescaleNormal();
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.enableDepth();
                // render the item itself
                renderItem.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), itemstack, 1, 1);
                GuiDraw.afterRenderItemAndEffectIntoGUI(itemstack);
                GlStateManager.disableRescaleNormal();
                if (amount < 0) {
                    amount = itemstack.stackSize;
                }
                GuiDraw.drawStandardSlotAmountText(amount, format, getArea());

                int cachedCount = itemstack.stackSize;
                itemstack.stackSize = 1; // required to not render the amount overlay
                // render other overlays like durability bar
                renderItem.renderItemOverlayIntoGUI(((GuiScreenAccessor) guiScreen).getFontRenderer(), Minecraft.getMinecraft().getTextureManager(), itemstack, 1, 1, null);
                itemstack.stackSize = cachedCount;
                GlStateManager.disableDepth();
            }
        }

        ((GuiAccessor) guiScreen).setZLevel(0f);
        renderItem.zLevel = 0f;
    }

    @Override
    public @Nullable ItemStack getStackForNEI() {
        return this.syncHandler.getSlot().getStack();
    }
}

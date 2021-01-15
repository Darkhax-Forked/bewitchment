package moriyashiine.bewitchment.api.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PoppetItem extends Item {
	public final boolean worksInShelf;
	
	public PoppetItem(Settings settings, boolean worksInShelf) {
		super(settings);
		this.worksInShelf = worksInShelf;
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (stack.hasTag() && stack.getOrCreateTag().contains("OwnerName")) {
			tooltip.add(new LiteralText(stack.getOrCreateTag().getString("OwnerName")).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
		}
	}
}

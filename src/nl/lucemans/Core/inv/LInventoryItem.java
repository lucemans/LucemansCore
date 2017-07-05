package nl.lucemans.Core.inv;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import nl.lucemans.Core.item.Item;

public class LInventoryItem {

	public Item item;
	public ItemStack item2;
	public Runnable runLClick;
	public Runnable runRClick;
	
	public LInventoryItem()
	{
		item = new Item();
	}
	
	public LInventoryItem(Material mat, String name)
	{
		item = new Item(mat, name);
	}
	
	public ItemStack getItem()
	{
		if (item != null)
			return item.getItem();
		if (item2 != null)
			return item2;
		return new ItemStack(Material.BEDROCK);
	}
	
	public void onClick()
	{
		Bukkit.getLogger().info("LItemClick");
		if (runLClick != null)
			runLClick.run();
	}
	
	public void onRightClick()
	{
		Bukkit.getLogger().info("LItemRightClick");
		if (runRClick != null)
			runRClick.run();
	}
}

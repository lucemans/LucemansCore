package nl.lucemans.Core.inv;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import nl.lucemans.Core.LucemansCore;

public class LInventory {

	public Inventory inv;
	public HashMap<Integer, LInventoryItem> items = new HashMap<>();
	
	public LInventory(Integer size, String name)
	{
		inv = Bukkit.getServer().createInventory(null, size, LucemansCore.getINSTANCE().parse(name));
	}
	
	public void onTick()
	{
		inv.clear();
		for (Integer slot : items.keySet())
			if (slot < inv.getSize())
				inv.setItem(slot, items.get(slot).getItem());
	}
	
	public void openInventory(Player p)
	{
		onTick();
		p.openInventory(inv);
	}
	
	public void onClick(Integer slot, boolean left)
	{
		if (items.get(slot) != null)
		{
			if (left)
				items.get(slot).onClick();
			else
				items.get(slot).onRightClick();
		}
	}
}

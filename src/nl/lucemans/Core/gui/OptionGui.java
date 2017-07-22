package nl.lucemans.Core.gui;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.lucemans.Core.LucemansCore;
import nl.lucemans.Core.inv.LInventory;
import nl.lucemans.Core.inv.LInventoryItem;

public class OptionGui {
	
	private String name;
	private String path;
	private LInventoryItem quest;
	private LInventoryItem opt1;
	private LInventoryItem opt2;
	private LInventoryItem opt3;
	private boolean canCancel;
	private boolean big;
	
	public OptionGui(String path, String name, LInventoryItem quest, LInventoryItem opt1, LInventoryItem opt2, Runnable onCancel, boolean big)
	{
		this(path, name, quest, opt1, opt2, merge(new LInventoryItem(Material.BARRIER, "&c&lCancel"), onCancel), big);
	}
	
	public OptionGui(String path, String name, LInventoryItem quest, LInventoryItem opt1, LInventoryItem opt2, LInventoryItem opt3, boolean big)
	{
		this.path = path;
		this.name = name;
		this.quest = quest;
		this.opt1 = opt1;
		this.opt2 = opt2;
		this.opt3 = opt3;
		this.big = big;
		canCancel = true;
	}
	
	public void openInventory(Player p)
	{
		LInventory linv = new LInventory(9*(big ? 6 : 1), name);
		
		if (quest != null)
			linv.items.put((big ? 9*2+4 : 4), quest);
		if (opt3 != null)
			linv.items.put((big ? 9*4+4 : 8), opt3);
		if (big)
		{
			linv.items = fill(linv.items, 2, 2, 3, 5, opt1);
			linv.items = fill(linv.items, 7, 2, 8, 5, opt2);
		}
		else
		{
			linv.items.put(5, opt2);
			linv.items.put(3, opt1);
		}
		
		LucemansCore.main.linvs.put(path, linv);
		LucemansCore.main.linvs.get(path).openInventory(p);
	}
	
	private HashMap<Integer, LInventoryItem> fill(HashMap<Integer, LInventoryItem> items, Integer startX, Integer startY, Integer endX, Integer endY, LInventoryItem item)
	{
		int h = 1;
		int w = 1;
		HashMap<Integer, LInventoryItem> over = new HashMap<Integer, LInventoryItem>();
		while (w <= endX)
		{
			h = 1;
			while (h <= endY)
			{
				if (w >= startX && w <= endX && h >= startY && h <= endY)
				{
					LInventoryItem item2 = new LInventoryItem();
					item2.item = null;
					item2.item2 = item.getItem().clone();
					item2.runLClick = item.runLClick;
					item2.runRClick = item.runRClick;
					over.put((h-1)*9+w-1, item2);
				}
				h++;
			}
			w++;
		}
		for (Integer i : over.keySet())
			items.put(i, over.get(i));
		return items;
	}
	
	private static LInventoryItem merge(LInventoryItem item, Runnable run)
	{
		item.runLClick = run;
		return item;
	}
}

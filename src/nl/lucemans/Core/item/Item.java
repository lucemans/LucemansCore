package nl.lucemans.Core.item;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import nl.lucemans.Core.LucemansCore;

public class Item {

	public String name = "";
	public Material type;
	public Integer amount;
	public ArrayList<String> lore;
	public Recipe r;
	public ItemMeta meta;
	public byte damage = 0;
	
	public Item(Material mat, String _name, String[] _lore, Integer _amount)
	{
		this(mat, _name, _lore, _amount, null);
	}
	
	public Item(Material mat, String _name, String[] _lore, Integer _amount, ItemMeta _meta) {
		type = mat;
		name = _name;
		for (String str : _lore)
			lore.add(str);
		amount = _amount;
		meta = _meta;
	}
	
	public Item()
	{
		this(Material.BEDROCK, "#Error");
	}
	
	public Item(Material mat, String _name, String[] _lore)
	{
		this(mat, _name, _lore, 1);
	}
	
	public Item(Material mat, String _name)
	{
		this(mat, _name, new String[]{});
	}

	public ItemStack getItem()
	{
		ItemStack item = new ItemStack(type, amount, damage);
		ItemMeta meta = item.getItemMeta();
		
		if (this.meta != null)
			meta = this.meta;
		
		meta.setDisplayName(LucemansCore.getINSTANCE().parse(name));
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		return item;
	}
}

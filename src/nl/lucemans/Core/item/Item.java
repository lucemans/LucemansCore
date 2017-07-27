package nl.lucemans.Core.item;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import nl.lucemans.Core.LucemansCore;

public class Item {

	public String name = "";
	public Material type;
	public Integer amount;
	public ArrayList<String> lore;
	public Recipe r;
	public ItemMeta meta;
	public byte damage = 0;
	public HashMap<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
	public ArrayList<ItemFlag> flags = new ArrayList<ItemFlag>();
	
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
		
		meta.setDisplayName(LucemansCore.getINSTANCE().parse("&r"+name));
		meta.setLore(lore);
		for (ItemFlag flag : flags)
			meta.addItemFlags(flag);
		
		item.setItemMeta(meta);
		for (Enchantment ench : enchants.keySet())
			item.addUnsafeEnchantment(ench, enchants.get(ench));
		
		return item;
	}
	
	public Item setLore(ArrayList<String> lore)
	{
		this.lore = lore;
		return this;
	}
	
	public Item setDamage(byte damage)
	{
		this.damage = damage;
		return this;
	}
	
	public Item setAmount(Integer amount)
	{
		this.amount = amount;
		return this;
	}
	
	public Item putEnchant(Enchantment ench, Integer level)
	{
		this.enchants.put(ench, level);
		return this;
	}
	
	public Item addFlag(ItemFlag flag)
	{
		this.flags.add(flag);
		return this;
	}
}

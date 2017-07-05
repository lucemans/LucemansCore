package nl.lucemans.Core.settings;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import nl.lucemans.Core.Main;

public class InventorySaver {


	public File invFile;
	public FileConfiguration invs;
	public Main m;
	
	public void init(Main m)
	{
		try{
		this.m = m;
		
		if (!m.getDataFolder().exists())
		{
			try {
				m.getDataFolder().createNewFile();
			} catch (IOException e)
			{
				m.getLogger().severe(ChatColor.RED + "Could not create config folder");
			}
		}
		
		invFile = new File(m.getDataFolder(), "inventories.yml");
		
		if (!invFile.exists())
		{
			try {
				invFile.createNewFile();
			} catch (IOException e)
			{
				m.getLogger().severe(ChatColor.RED + "Could not create inventories.yml");
			}
		}
		
		invs = YamlConfiguration.loadConfiguration(invFile);
		}catch(Exception e)
		{
			
			e.printStackTrace();
		}
	}
	
	public void shutdown()
	{
		saveFile();
	}
	
	/*** Utils */
	
	public void saveFile()
	{
		try{
			invs.save(invFile);
		} catch (Exception e) {
			m.getLogger().severe(ChatColor.RED + "Could not save inventories.yml");
		}
	}
	
	public void reloadFile()
	{
		invs = YamlConfiguration.loadConfiguration(invFile);
	}

	public ItemStack getItem(String key) {
		try{
			ItemStack str = invs.getItemStack(key);
			if (str != null)
				return str;
			return null;
		}catch(Exception e){
			m.getLogger().severe("LucemansCore had a problem loading a item from the \"inventories.yml\". If this issue keeps occuring it is recommended to delete the \"inventories.yml\" file.");
			e.printStackTrace();
		};
		return null;
	}
	
	public void saveItem(ItemStack item, String key)
	{
		invs.set(key, item);
	}
	
	
	public HashMap<Integer, ItemStack> getAllItems(String key)
	{
		HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
		if (invs.getConfigurationSection(key) != null)
		for (String path : invs.getConfigurationSection(key).getKeys(false))
		{
			ItemStack item = getItem(key+"."+path);
			if (item != null)
				items.put(Integer.parseInt(path), item);
		}
		return items;
	}
	
	public FileConfiguration getLang()
	{
		return invs;
	}

	public void setDefaultLang(String s, String k) {
		String str = invs.getString(s);
		if (str == "" || str == null)
		{
			invs.set(s, k);
			// save file
			saveFile();
		}
	}

	public void wipe(String key) {
		invs.set(key, null);
	}
	
	public void saveInventory(Inventory inv, String path)
	{
		wipe(path);
		Integer slot = 0;
		while (slot < inv.getSize())
		{
			if (inv.getItem(slot) != null)
				saveItem(inv.getItem(slot), path+"."+slot);
			slot++;
		}
		saveFile();
	}
	
	public void loadInventory(String path, Inventory inv)
	{
		HashMap<Integer, ItemStack> items = getAllItems(path);
		for (Integer slot : items.keySet())
			inv.setItem(slot, items.get(slot));
	}
}

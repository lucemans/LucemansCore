package nl.lucemans.Core.gui;

import java.util.function.BiFunction;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import net.minecraft.server.v1_10_R1.EntityHuman;
import nl.lucemans.Core.LucemansCore;
import nl.lucemans.Core.item.Item;

public class AnvilGui2 implements Listener {

	public Inventory inv;
	public Player p;
	public BiFunction<Player, String, String> biFunction;
	
	public AnvilGui2(Plugin pl, Player p, String input_1, String input_2, String output_2, BiFunction<Player, String, String> biFunction)
	{
		this.p = p;
		this.biFunction = biFunction;
		
		Item input1 = new Item(Material.PAPER, input_1);
		
		inv = Bukkit.createInventory(null, InventoryType.ANVIL);
		inv.setItem(Slot.INPUT_LEFT, input1.getItem());
		
		if (LucemansCore.getINSTANCE().main.debug)
			Bukkit.getLogger().info("Registering ANVILGUI events");
		Bukkit.getPluginManager().registerEvents(this, pl);
		
		p.openInventory(inv);
	}
	
	public void closeInventory()
	{
		//CraftEventFactory.handleInventoryCloseEvent((EntityHuman) p);
		//HandlerList.unregisterAll(this);
	}
	
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getInventory().equals(inv)) {
            e.setCancelled(true);
            final Player clicker = (Player) e.getWhoClicked();
            if(e.getRawSlot() == Slot.OUTPUT) {
                final ItemStack clicked = e.getCurrentItem();
                if(clicked == null || clicked.getType() == Material.AIR)
                {
                	e.setCancelled(true);
            		if (LucemansCore.getINSTANCE().main.debug)
            			Bukkit.getLogger().info("Clicked = null");
                	return;
                }
                final String ret = biFunction.apply(clicker, clicked.hasItemMeta() ? clicked.getItemMeta().getDisplayName() : clicked.getType().toString());
                if(ret != null) {
                    final ItemMeta meta = clicked.getItemMeta();
                    meta.setDisplayName(ret);
                    clicked.setItemMeta(meta);
                    inv.setItem(e.getRawSlot(), clicked);
                } else closeInventory();
            }
        }
    }
    
	/*@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		Bukkit.getLogger().info("ANVILGUI EVENT CLICK");
		if (event.getInventory().equals(inv))
		{
			Bukkit.getLogger().info("Anvil Input click event");
			event.setCancelled(true);
			final Player clicker = (Player) event.getWhoClicked();
			if (event.getRawSlot() == Slot.OUTPUT)
			{
				Bukkit.getLogger().info("Anvil Input output click event!");
				ItemStack clicked = inv.getItem(event.getRawSlot());
				if (clicked == null)
					clicked = event.getCursor();
				if (clicked == null || clicked.getType() == Material.AIR)
				{
					event.setCancelled(true);
				}
				String ret = biFunction.apply(clicker, clicked.hasItemMeta() ? clicked.getItemMeta().getDisplayName() : clicked.getType().toString());
				if (ret != null){
					final ItemMeta meta = clicked.getItemMeta();
					meta.setDisplayName(ret);
					clicked.setItemMeta(meta);
					inv.setItem(event.getRawSlot(), clicked);
					Bukkit.getLogger().info("Anvil Input failed");
				}
				else
				{
					closeInventory();
					Bukkit.getLogger().info("Anvil Input success");
				}
			}
		}
	}
	*/
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		if (event.getInventory().equals(inv)) closeInventory();
	}
	
	public static class Slot {
		
		public static final int INPUT_LEFT = 0;
		
		public static final int INPUT_RIGHT = 1;
		
		public static final int OUTPUT = 2;
	}
}

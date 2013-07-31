/*
 *  Copyright:
 *  2013 Darius Mewes
 */

package de.timolia.net;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class TimoliaNet extends JavaPlugin {

	public static final String PREFIX = ChatColor.YELLOW + "[TNet] " + ChatColor.RESET;

	public void onEnable() {
		config();

		String liliHost;
		if (!(liliHost = getConfig().getString("lili.host")).equalsIgnoreCase(""))
			Bukkit.getPluginManager().registerEvents(new LiliHandler(liliHost), this);
		else
			System.out.println("[TNet] Der Host für Lili wurde nicht angegeben!");

		getCommand("facebook").setExecutor(new facebookCmd(getConfig().getString("facebook.siteID"), getConfig().getString("facebook.token"), getConfig().getBoolean("facebook.bcastingame")));
		WebPlayerList.startTimer(this);
	}

	public void onDisable() {
		WebPlayerList.stopTimer();
	}

	private void config() {
		getConfig().addDefault("lili.host", "");
		getConfig().addDefault("webplayerlist.refreshDelay", 60);
		getConfig().addDefault("webplayerlist.url", "");
		getConfig().addDefault("webplayerlist.securehash", "");
		getConfig().addDefault("facebook.siteID", "");
		getConfig().addDefault("facebook.token", "");
		getConfig().addDefault("facebook.bcastingame", true);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

}
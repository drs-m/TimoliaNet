/*
 *  Copyright:
 *  2013 Darius Mewes
 */

package de.timolia.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class WebPlayerList {

	private static BukkitTask task;
	private static String last;

	public static void startTimer(TimoliaNet instance) {
		final String host = instance.getConfig().getString("webplayerlist.url");
		final String secureHash = instance.getConfig().getString("webplayerlist.securehash");
		int refreshDelay = instance.getConfig().getInt("webplayerlist.refreshDelay");

		if (host.equalsIgnoreCase("")) {
			System.out.println("[TNet] Bitte gib die URL fuer WebPlayerList in der config an!");
			return;
		}

		if (task == null) {
			task = Bukkit.getScheduler().runTaskTimer(instance, new Runnable() {
				public void run() {
					String players = "";
					if (Bukkit.getOnlinePlayers().length > 0) {
						for (Player p : Bukkit.getOnlinePlayers())
							players += p.getName() + ",";

						players = players.substring(0, players.length() - 1);
					} else
						players = "0";

					if (!players.equalsIgnoreCase(last)) {
						try {
							URL url = new URL(host + "?players=" + players + "&securehash=" + secureHash);
							HttpURLConnection conn = (HttpURLConnection) url.openConnection();
							BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
							String response = reader.readLine();
							reader.close();
							last = players;
							if (!response.equalsIgnoreCase("done"))
								System.out.println("Unregelmaessigkeit bei WebPlayerList: " + response);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}, 10 * 20, refreshDelay * 20);
		}
	}

	public static void stopTimer() {
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

}

/*
 *  Copyright:
 *  2013 Darius Mewes
 */

package de.timolia.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class LiliHandler implements Listener {

	private final String host;
	private final String rgx = "^(https?:\\/\\/)?([a-zA-Z0-9][a-zA-Z0-9\\.-]*)\\.([a-zA-Z\\.]{2,6})([\\/\\?#]+[()-\\/\\w\\\\\\?=\\.&#%]*)?$";
	private final String rgx2 = "^(https?:\\/\\/)?(www.)?s.timolia.de(\\/.*)?$";

	public LiliHandler(String host) {
		this.host = host;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		String[] words = event.getMessage().split(" ");
		boolean changed = false;
		for (int i = 0; i < words.length; i++) {
			if (isLink(words[i]) && words[i].length() > 17) {
				try {
					String params = "url=" + URLEncoder.encode(words[i], "UTF-8") + "&user=" + event.getPlayer().getName();
					URL url = new URL(host + "add.php");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("POST");
					conn.setDoOutput(true);
					conn.setRequestProperty("User-Agent", "Mozilla/5.0");
					DataOutputStream o = new DataOutputStream(conn.getOutputStream());
					o.writeBytes(params);
					o.flush();
					o.close();

					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String line = in.readLine();
					if (line.charAt(0) == '0') {
						words[i] = host.substring(7) + line.substring(2);
						changed = true;
					}

					in.close();
					o.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (changed) {
				String newMsg = "";
				for (String one : words)
					newMsg += one + " ";

				event.setMessage(newMsg);
			}
		}
	}

	private boolean isLink(String tx) {
		return tx.matches(this.rgx) && !tx.matches(this.rgx2);
	}

}
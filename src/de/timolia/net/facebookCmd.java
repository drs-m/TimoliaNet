/*
 *  Copyright:
 *  2013 Darius Mewes
 */

package de.timolia.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class facebookCmd implements CommandExecutor {

	private final String siteID;
	private final String token;
	private final boolean bcast;

	public facebookCmd(String siteID, String token, boolean bcast) {
		this.siteID = siteID;
		this.token = token;
		this.bcast = bcast;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("socialbridge.facebook")) {
			sender.sendMessage(TimoliaNet.PREFIX + "Du hast keine Berechtigung für diesen Befehl!");
			return true;
		}

		if (this.token.equalsIgnoreCase("") || this.siteID.equalsIgnoreCase("")) {
			sender.sendMessage(TimoliaNet.PREFIX + "Der token oder die ID wurde nicht in der config gefunden!");
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(TimoliaNet.PREFIX + "Bitte gib eine Nachricht an!");
			return true;
		}

		String msg = "";
		for (String one : args)
			msg += one + " ";

		try {
			URL url = new URL("https://graph.facebook.com/" + siteID + "/feed");
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			conn.setDoOutput(true);
			String params = "access_token=" + token + "&message=" + URLEncoder.encode(msg, "UTF-8");
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(params);
			out.flush();
			out.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line, response = "";
			while ((line = reader.readLine()) != null)
				response += line + "\n";
			reader.close();
			response = response.substring(0, response.length() - 2);
			sender.sendMessage(TimoliaNet.PREFIX + "Der Text wurde gepostet!");
			System.out.println("****** FACEBOOK RESPONSE ******");
			System.out.println(response);
			if (this.bcast)
				Bukkit.broadcastMessage(TimoliaNet.PREFIX + "Neuer Facebook-Post: " + msg);
		} catch (Exception e) {
			sender.sendMessage(TimoliaNet.PREFIX + "Der Text konnte nicht gepostet werden. Mehr Details in der Konsole.");
			e.printStackTrace();
		}

		return true;
	}

}
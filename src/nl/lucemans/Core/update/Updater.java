package nl.lucemans.Core.update;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;

public class Updater {

	public String downloadLink = "http://www.spigotmc.org/resources/lucemanscore.404020/download?version=";
	public String latestVersion;
	
	private boolean checkHigher(String currentVersion, String newVersion)
	{
		String current = toReadable(currentVersion);
		String newVers = toReadable(newVersion);
		return current.compareTo(newVers) < 0;
	}
	
	public void checkUpdate(String currentVersion)
	{
		String version = getSpigotVersion();
		if (version == null)
			return;
		if (!checkHigher(currentVersion, version))
			return;
		
		latestVersion = version;
	}
	
	public String getLatestVersion()
	{
		return latestVersion;
	}
	
	@SuppressWarnings("resource")
	public String getSpigotVersion()
	{
		try{
			HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
            con.getOutputStream().write(
                    ("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=40402").getBytes("UTF-8"));
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            return version;
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public String toReadable(String version)
	{
		String[] split = Pattern.compile(".", Pattern.LITERAL).split(version.replace("v", ""));
		version = "";
		for (String s : split) {
			version += String.format("%4s", s);
		}
		
		return version;
	}
	
	public String getRawVersion()
	{
		/*try{
		URL u = new URL("http://www.spigotmc.org/resources/lucemanscore.40402/");
		URLConnection conn = u.openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						conn.getInputStream()));
		StringBuffer buffer = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			buffer.append(inputLine);
		in.close();
		
		String html = buffer.toString();
		Document doc = Jsoup.parse(html);
		Element link = doc.select("#inner").first();
		String elink = link.attr("href");
		return elink;
		}catch(Exception e)
		{
			e.printStackTrace();
		}*/
		return "";
	}
}

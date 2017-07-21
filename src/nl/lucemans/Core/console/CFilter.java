package nl.lucemans.Core.console;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

public class CFilter implements Filter{

	@Override
	public boolean isLoggable(LogRecord record) {
		
		String message = record.getMessage();
		
		Pattern pattern = Pattern.compile(".*hi.*");
		Matcher matcher = pattern.matcher(message);
		if (!message.equalsIgnoreCase("CHECKING CONFIG"))
			Bukkit.getLogger().info("CHECKING CONFIG");
		
		if (matcher.find())
		{
			// if (filter.replace)
			/*{
				return false;
			}*/
			message = message.replaceAll(".*hi.*", "REPLACER");
		}
		record.setMessage(message);
		return true;
	}

}

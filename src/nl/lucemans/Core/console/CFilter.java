package nl.lucemans.Core.console;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

public class CFilter implements Filter{

	@Override
	public boolean isLoggable(LogRecord record) {
		
		record.setMessage(record.getMessage() + " -");
		return true;
	}

}

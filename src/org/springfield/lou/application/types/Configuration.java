package org.springfield.lou.application.types;

import org.springfield.fs.Fs;

public class Configuration {
	private static String server = Fs.getNode("/domain/euscreenxl/user/admin/config/publicationbuilder").getProperty("server");
	private static String libServer =  Fs.getNode("/domain/euscreenxl/user/admin/config/publicationbuilder").getProperty("embedLib");
	private static String domain = "/domain/euscreenxl";
	public static String getServer() {
		return server;
	}
	
	public static String getLibServer() {
		return libServer;
	}	
	
	public static String getDomain() {
		return domain;
	}	
}

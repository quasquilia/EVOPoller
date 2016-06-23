package it.guarascio.evopoller.evorequests;

import it.guarascio.evopoller.meteo.MeteoProvider;


public class RequestFactory {
	
	private static boolean useAuth;
	private static String url;
	private static String username;
	private static String password;
	private static MeteoProvider meteoProvider = null;
	public static IEVORequest createRequest() {
		if (useAuth) {
			return new SSHRequest(url, username, password, meteoProvider);
		}
		return new HttpRequest(url, meteoProvider);
	}
	public static boolean isUseAuth() {
		return useAuth;
	}
	public static void setUseAuth(boolean useAuth) {
		RequestFactory.useAuth = useAuth;
	}
	public static String getUrl() {
		return url;
	}
	public static void setUrl(String url) {
		RequestFactory.url = url;
	}
	public static String getUsername() {
		return username;
	}
	public static void setUsername(String username) {
		RequestFactory.username = username;
	}
	public static String getPassword() {
		return password;
	}
	public static void setPassword(String password) {
		RequestFactory.password = password;
	}
	public static MeteoProvider getMeteoProvider() {
		return meteoProvider;
	}
	public static void setMeteoProvider(MeteoProvider meteoProvider) {
		RequestFactory.meteoProvider = meteoProvider;
	}
}

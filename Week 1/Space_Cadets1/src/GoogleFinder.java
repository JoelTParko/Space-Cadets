import java.net.*;
import java.io.*;
public class GoogleFinder {
	
	public String getName() throws Exception{
		System.out.print("Enter a name:");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
		return in.readLine();
	}
	public String GetURL(String name) throws Exception{
		String siteURL;
		name = name.replace(" ", "+");
		URL googleSearch = new URL("http://www.google.co.uk/search?q=" + name);
		URLConnection con = googleSearch.openConnection();
		con.addRequestProperty("User-Agent", "Google Chrome/36");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		while((siteURL = in.readLine()) != null) {
			if(siteURL.contains("class=\"r\"")) {
				break;
			}
		}
		in.close();
		siteURL = siteURL.substring(siteURL.indexOf("class=\"r\"")); 
		siteURL = siteURL.substring(26, siteURL.indexOf("&amp"));
		return siteURL;
	}
}

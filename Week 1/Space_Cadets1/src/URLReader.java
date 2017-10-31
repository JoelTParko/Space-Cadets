import java.net.*;
import java.io.*;

public class URLReader {
	
	public String getID() throws Exception{ //Returns user input from console
		System.out.print("Enter ID:");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
		return in.readLine();
	}
 
	public String getName(String id) throws Exception { //Finds the name of the person assosiated with the given ID
		String name;
		URL address = new URL("http://www.ecs.soton.ac.uk/people/" + id);
		BufferedReader in = new BufferedReader(new InputStreamReader(address.openStream()));
		while((name = in.readLine()) != null) {
			if(name.contains("<title>")) {
				break;
			}
		}
		in.close();
		name = name.substring(11, name.indexOf('|')-1);
		return name;
	}
}
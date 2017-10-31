import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NameFinder {
	public static void main(String[] args) throws Exception{
		do 	printMenu(); while(!getOption());
	}
	public static void printMenu() {
		System.out.println("Choose an option");
		System.out.println("1. Find a name from an email");
		System.out.println("2. Find a website from a name");
		System.out.println("3. Quit");
		System.out.print("Enter here:");
	}
	public static boolean getOption() throws Exception{
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		switch(input.readLine()) {
			case "1": //Finds the name of a indivdual based on their Southampton username
				String id;
				URLReader reader = new URLReader();
				id = reader.getID(); //Stores the users input into the variable 'id'
				System.out.println(reader.getName(id)); //outputs the name of the person who matches the inputed ID
				break;
			case "2": //Finds the url the first website displayed on google when the given input is put into its search engine
				String name;
				GoogleFinder gf = new GoogleFinder();
				name = gf.getName(); //Retrieves a name to do a google with from user
				System.out.println(gf.GetURL(name)); //Outputs the url of the first website that comes up after googling the given name
				break;
			case "3":
				return true;
			default:
				System.out.println("Please only input 1, 2, or 3");
			}
			
		return false;
	}

}

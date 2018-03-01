import java.util.*;
import java.io.*;

public class SearchItLinear{
	public static void main(String[] args){
		List <Person> linear = new ArrayList();
		//adding the entries from the file to the list
		try{
			BufferedReader f = new BufferedReader(new FileReader("testdata"));
			String s = f.readLine();
			while (s != null){
				String [] splt = s.split("\\|");
				Person p = new Person(splt[2], splt[0], splt[1]);
				linear.add(p);

				s = f.readLine();
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}

		boolean found = false;
		String file = "20 entries";
		List <Person> searchLst = new ArrayList();
		//printing the person if he/she is found in the list using a linear search
		try{
			BufferedReader f = new BufferedReader(new FileReader(file));
			String s = f.readLine();
			while (s != null){
				String splt [] = s.split("\\|");
				Person pFind = new Person(splt[2], splt[0], splt[1]);
								
				for(Person p : linear){
					if(p.getName().equals(pFind.getName())){
					System.out.println(p.toString());
					found = true;
					break;
					}
				}

				if (!found)
					System.out.println("Not found");
				found = false;

				s=f.readLine();
			}
		}
		catch(Exception ex){ex.printStackTrace();}
	}
}

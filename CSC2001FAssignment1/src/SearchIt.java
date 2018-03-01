import java.util.*;
import java.io.*;

public class SearchIt{
	public static void main(String[] args){
		
		BinarySearchTree bst = new BinarySearchTree(); //adding to binary search tree
		try{
			BufferedReader f = new BufferedReader(new FileReader("testdata"));
			String s = f.readLine();
			while (s != null){
				String [] splt = s.split("\\|");
				Person p = new Person(splt[2], splt[0], splt[1]);
				bst.insert(p);
				s = f.readLine();
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}

		String file = "20 entries";
		List <Person> searchLst = new ArrayList();
		//getting info from file and searching for each entry in bst
		try{
			BufferedReader f = new BufferedReader(new FileReader(file));
			String s = f.readLine();
			while (s != null){
				String splt [] = s.split("\\|");
				Person pers = new Person(splt[2], splt[0], splt[1]);
				
				if(bst.find(pers)!=null)
					System.out.println(pers.toString());
				else
					System.out.println("Not found");
				s=f.readLine();
			}
		}
		catch(Exception ex){ex.printStackTrace();}
	}
}


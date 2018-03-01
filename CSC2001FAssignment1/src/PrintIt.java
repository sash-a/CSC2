import java.util.*;
import java.lang.*;
import java.io.*;

public class PrintIt{

	public static void main(String[] args){

		BinarySearchTree bst = new BinarySearchTree();
		//runs through the test data file and adds all rows to the binary tree
		String dir = "testdata";
		try{
			BufferedReader f = new BufferedReader(new FileReader(dir));
			String s = f.readLine();
			while (s != null){
				String [] splt = s.split("\\|");
				Person p = new Person(splt[2], splt[0], splt[1]);
				bst.insert(p);

				s=f.readLine();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		//prints the binary tree in order
		bst.inOrder();
	}
}

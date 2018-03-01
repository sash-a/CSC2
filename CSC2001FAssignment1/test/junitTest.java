import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class junitTest{
	//only testing methods from binary search tree class that I used in the project
	@Test
	public void testFindAndInsert(){
		BinarySearchTree bst = new BinarySearchTree();
		Person p = new Person("test", "test", "test");
		bst.insert(p);
		assetTrue(bst.find(p));
	}

	@Test
	public void testPerson(){
		Person p = new Person ("name","address", "tel");
		assertEquals("address|tel|name", p.toString());	
	}

}

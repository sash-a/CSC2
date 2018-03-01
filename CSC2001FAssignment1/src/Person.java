public class Person
{
		private String name;
		private String address;
		private String telNum;
	
		public Person(){
			name = "";
			address = "";
			telNum = "";
		}
		

		/**
		*@param name of the person
		*@param address of the person
		*@param telephone number of the person
		*/
		public Person(String name, String address, String telNum){
			this.name = name;
			this.address = address;
			this.telNum = telNum;
		}
		/**
		*Returns the string of the address telephone number and name separated by a "|"
		*@return address|telephone number|name
		*/
		public String toString(){
			return address+"|"+telNum+"|"+name;
		}
		/**
		*@return name
		*/
		public String getName(){
			return this.name;
		}
}

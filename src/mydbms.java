import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class mydbms {
	static String[] indexTable = {"BAT", "FIELD", "PITCH", "PLAYER", "TEAM"}; // table to be read
	static String[] selectAttr, fromEntity, whereCondition; // each part of SQL
	static int selectN, fromN, whereN; // numbers of each part of SQL
	static List<Condi> Conditions = new ArrayList<Condi>();
	static List<Table> tables = new ArrayList<Table>();
	static List<String> total_attr = new ArrayList<String>();
	static Table result;
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader (new InputStreamReader(System.in));
		while(true){
			//Initialize
			Conditions.clear();
			tables.clear();
			total_attr.clear();
			
			System.out.print("Input SQL query ('quit' to exit): ");
			String line = br.readLine();
			
			if("quit".equals(line)){
				System.out.print("quit... ");
				break;
			}
			
			if(line.contains(" IN")){
				String IN_Part = line.substring(line.lastIndexOf("IN"));
				IN_Part = IN_Part.substring(IN_Part.indexOf("SELECT"));
				System.out.println("Nested query: " + IN_Part);
				//先做完nested query
				if(readQuery(IN_Part)){
					build_tables();
					parseWHERE();
					Retrieve();
				}
				int nested_target = result.findAttr_in_table(selectAttr[0]);
				//System.out.println("Nested select attribute: " + selectAttr[0]);
				
				String Outer_Part;
				if(line.contains("NOT IN")){
					Outer_Part = line.substring(0,line.indexOf("NOT IN"));
					if(readQuery(Outer_Part)){
						build_tables();
						int out_target = tables.get(1).findAttr_in_table(whereCondition[0]);
						Table table1 = result;
						Table table2 = tables.get(1);
						parseSELECT(table2);
						tables.add(NOT_IN(table1, table2, nested_target, out_target));
						tables.remove(table1);
						tables.remove(table2);
						result = tables.get(0);
						parseSELECT(result);
						System.out.println("Answer by nested query.");
					}
				}else{
					Outer_Part = line.substring(0,line.indexOf("IN"));
					if(readQuery(Outer_Part)){
						build_tables();
						int out_target = tables.get(1).findAttr_in_table(whereCondition[0]);
						Table table1 = result;
						Table table2 = tables.get(1);
						parseSELECT(table2);
						tables.add(join(table1, table2, nested_target, out_target));
						tables.remove(table1);
						tables.remove(table2);
						result = tables.get(0);
						parseSELECT(result);
						System.out.println("Answer by nested query.");
					}
				}
				
				
			}else if(readQuery(line)){
				build_tables();
				parseWHERE();
				Retrieve();
				parseSELECT(result);
				System.out.println("Answer by single query.");
			}
			
		}
	}
	
	static boolean readQuery(String query){
		int index = query.indexOf("SELECT");
		if(index == -1){
			System.out.println("Incorrect SQL Query...");				
			return false;				
		}
		index = query.indexOf("FROM");
		if(index == -1){
			System.out.println("Incorrect SQL Query...");				
			return false;
		}
		
		//get SELECT values
		selectAttr = query.substring(6, index).replace(" ","").split(",");
		selectN = selectAttr.length;
		//get the string after"FROM"
		query = query.substring(index+4);
		
		index = query.indexOf("WHERE");
		if(index == -1){
			//get FROM values
			fromEntity = query.replace(" ","").split(",");
			fromN = fromEntity.length;
			whereN = 0;
		}else{
			//get FROM values
			fromEntity = query.substring(0,index).replace(" ","").split(",");
			fromN = fromEntity.length;
			//get WHERE values
			whereCondition = query.substring(index+5).replace(" ","").split("AND");
			whereN = whereCondition.length;
		}
		
		return true;
	}
	
	static void build_tables() throws IOException{
		//1.建立tables;每個table內包含attributes、tuples  2.將From內的所有table的attributes加入total_attr裡     
		for(int i=0; i<fromN; i++){
			BufferedReader readfile = new BufferedReader(new FileReader(fromEntity[i]+".txt"));
			Table t = new Table();
			String tmp = readfile.readLine();
			String[] s = tmp.split("\t");
			for(int j=0;j<s.length;j++){
				t.attributes.add(s[j].replace(" ",""));
				total_attr.add(s[j].replace(" ",""));
			}
			while(true){
				tmp = readfile.readLine();
				if(tmp == null){
					tables.add(t);
					break;
				}else{
					s = tmp.split("\t");
					ArrayList<String> tuple = new ArrayList<String>();
					for(int j=0;j<s.length;j++){
						tuple.add(s[j].replace(" ",""));
					}
					t.tuples.add(tuple);
				}
			}
			readfile.close();
		}
	}
	
	
	// divide the condition into three parts: lefthand operand, operator, righthand operand
	static void parseWHERE(){
		//System.out.println("Parsing WHERE conditions...");
		for(int i=0;i<whereN;i++){
			Condi condi1 = new Condi(whereCondition[i]);
			//System.out.println(condi1.getSentence());
			//System.out.println(condi1.getLefthand());
			//System.out.println(condi1.getOperator());			
			//System.out.println(condi1.getRighthand());
			//System.out.println(condi1.getType());
			Conditions.add(condi1);
		}
	}
	
	static void Retrieve() {
		for(int i=0;i<whereN;i++){
			if(Conditions.get(i).type==1){	//select type
				int target_table = -1;
				int target_index = -1;
				for(int j=0;j<tables.size();j++){
					target_index = tables.get(j).findAttr_in_table(Conditions.get(i).lefthand);
					if(target_index != -1){
						target_table = j;
						break;
					}
				}
				
				if(target_table==-1){
					System.out.println("Condtion's lefthand is not found in every tables' attributes.");
					return;
				}

				tables.get(target_table).remove(target_index, Conditions.get(i));
			}else if(Conditions.get(i).type==2){	//join type
				int target1_table = -1;
				int target1_index = -1;
				int target2_table = -1;
				int target2_index = -1;
				for(int j=0;j<tables.size();j++){
					target1_index = tables.get(j).findAttr_in_table(Conditions.get(i).lefthand);
					if(target1_index != -1){
						target1_table = j;
						break;
					}
				}
				if(target1_table==-1){
					System.out.println("Condtion's lefthand is not found in every tables' attributes.");
					return;
				}
				for(int j=0;j<tables.size();j++){
					target2_index = tables.get(j).findAttr_in_table(Conditions.get(i).righthand);
					if(target2_index != -1){
						target2_table = j;
						break;
					}
				}
				if(target2_table==-1){
					System.out.println("Condtion's righthand is not found in every tables' attributes.");
					return;
				}
				
				Table table1 = tables.get(target1_table);
				Table table2 = tables.get(target2_table);
				tables.add(join(table1, table2, target1_index, target2_index));
				tables.remove(table1);
				tables.remove(table2);
				
			}
		}
		
		//tables.get(0).printOnScreen();
		//parseSELECT(tables.get(0));
		result = tables.get(0);
		
	}
	
	static void parseSELECT(Table resultTable){
		//第0個attribute為"*"時,代表select all attributes in this table
		if(selectAttr[0].equals("*")){
			selectN = resultTable.attributes.size();
			selectAttr = new String[selectN];
			for(int i=0;i<selectN;i++){
				selectAttr[i] = resultTable.attributes.get(i);
			}
		}
		
		
		if(!Aggregate.isAggregate(selectAttr)){
			resultTable.printOnScreen();
		}else{
			new Aggregate(selectAttr, resultTable);
		}
		
	}
	
	static Table join(Table table1, Table table2, int targetAttr1, int targetAttr2){
		Table t = new Table();
		for(int i=0;i<table1.attributes.size();i++)
			t.attributes.add(table1.attributes.get(i));
		for(int i=0;i<table2.attributes.size();i++)
			t.attributes.add(table2.attributes.get(i)); //append table2's attributes to table1's
		
		for(int i=0;i<table1.tuples.size();i++){
			for(int j=0;j<table2.tuples.size();j++){
				if(table1.tuples.get(i).get(targetAttr1).equals(table2.tuples.get(j).get(targetAttr2))){
					ArrayList<String> tuple = new ArrayList<String>();
					for(int k=0;k<table1.tuples.get(i).size();k++)
						tuple.add(table1.tuples.get(i).get(k));
					for(int k=0;k<table2.tuples.get(j).size();k++)
						tuple.add(table2.tuples.get(j).get(k));
					t.tuples.add(tuple);
				}
			}
		}
		return t;
	}
	
	static Table NOT_IN(Table innerTable, Table outerTable, int innerAttr, int outerAttr){
		Table t = new Table(outerTable);
		
		Iterator<ArrayList<String>> iter = t.tuples.iterator();
		while (iter.hasNext()) {
			   ArrayList<String> current_tuple = iter.next();
			   for(int i=0;i<innerTable.tuples.size();i++){
				   if(current_tuple.get(outerAttr).equals(innerTable.tuples.get(i).get(innerAttr)))
						   iter.remove();
			   }			
		}
		
		return t;
	}

}

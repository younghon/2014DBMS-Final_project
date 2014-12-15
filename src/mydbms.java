import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.SSLContext;



public class mydbms {
	
	static String[] indexTable = {"BAT", "FIELD", "PITCH", "PLAYER", "TEAM"}; // table to be read
	static String[] selectAttr, fromEntity, whereCondition; // each part of SQL
	static int selectN, fromN, whereN; // numbers of each part of SQL
	static List<condi> condition = new ArrayList<condi>();
	static List<String> total_attr = new ArrayList<String>(); //FROM table attributes
	static List<table> tables = new ArrayList<table>();
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader (new InputStreamReader(System.in));
		String line;
		int index;
		
		while(true){
			condition.clear();
			tables.clear();
			System.out.print("Input SQL query ('quit' to exit): ");
			line = br.readLine();
			
			if("quit".equals(line)){
				System.out.print("quit... ");
				break;
			}
			
			index = line.indexOf("SELECT");
			if(index == -1){
				System.out.println("Incorrect SQL Query...");				
				continue;				
			}
			index = line.indexOf("FROM");
			if(index == -1){
				System.out.println("Incorrect SQL Query...");				
				continue;
			}
			
			//get SELECT values
			selectAttr = line.substring(6, index).replace(" ","").split(",");
			selectN = selectAttr.length;
			//get the string after"FROM"
			line = line.substring(index+4);
			
			index = line.indexOf("WHERE");
			if(index == -1){
				//get FROM values
				fromEntity = line.replace(" ","").split(",");
				fromN = fromEntity.length;
				whereN = 0;
			}else{
				//get FROM values
				fromEntity = line.substring(0,index).replace(" ","").split(",");
				fromN = fromEntity.length;
				//get WHERE values
				whereCondition = line.substring(index+5).replace(" ","").split("AND");
				whereN = whereCondition.length;
			}
			
			//1.將From內的所有table的attributes加入total_attr裡      2.建立tables;每個table內包含attributes、tuples
			for(int i=0; i<fromN; i++){
				BufferedReader readfile = new BufferedReader(new FileReader(fromEntity[i]+".txt"));
				table t = new table();
				String tmp = readfile.readLine();
				String[] s = tmp.split("\t");
				for(int j=0;j<s.length;j++){
					total_attr.add(s[j].replace(" ",""));
					t.attibutes.add(s[j].replace(" ",""));
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
				
			}
			
			
			parseWHERE();
			Retrieve();
		}
	}
	
	
	static void Retrieve() throws IOException{
		//Debug
		System.out.print("Select attributes:");
		for(int i=0;i<selectN;i++){
			System.out.print(selectAttr[i]+" ");
		}
		System.out.println();
		if(fromN==1){
			BufferedReader br = new BufferedReader(new FileReader(fromEntity[0]+".txt"));
			while(true)
		    {
		        String line = br.readLine();
		        if(line == null)
		           break;
		        System.out.println(line);
		    }
		}
		System.out.println();
		System.out.println();
				
		
		if(selectN==1 && selectAttr[0].equals("*")){
			selectN = tables.get(0).attibutes.size();
			selectAttr = new String[selectN];
			for(int i=0;i<selectN;i++){
				selectAttr[i] = tables.get(0).attibutes.get(i);
			}
		}
		
		//select from one table on some conditions
		for(int i=0;i<whereN;i++){
			boolean find_matched_operand = false;
			if(condition.get(i).lefthand==null){
				System.out.println("Condition's lefthand is not found.(Invalid operator)");
				return;
			}
			for(int j=0;j<tables.get(0).attibutes.size();j++){
				if(condition.get(i).lefthand.equals(tables.get(0).attibutes.get(j))){  //找到table的第j個attribute是要檢查的項目
					find_matched_operand = true;
					Iterator<ArrayList<String>> iter = tables.get(0).tuples.iterator();//檢查每個tuple是否符合condition,不符合的tuple從tuples中remove
					while (iter.hasNext()) {
					   ArrayList<String> current_tuple = iter.next();
					   switch (condition.get(i).operator) {
							case "=":
								//System.out.println(current_tuple.get(j)+" =? "+condition.get(i).righthand);
								if(!current_tuple.get(j).equals(condition.get(i).righthand)){
									iter.remove();
								}
								break;
							case "<":
								//System.out.println(current_tuple.get(j)+" <? "+condition.get(i).righthand);
								if(!(Double.parseDouble(current_tuple.get(j))<Double.parseDouble(condition.get(i).righthand))){
									iter.remove();
								}
								break;
							case ">":
								//System.out.println(current_tuple.get(j)+" >? "+condition.get(i).righthand);
								if(!(Double.parseDouble(current_tuple.get(j))>Double.parseDouble(condition.get(i).righthand))){
									iter.remove();
								}
								break;
							case "<=":
								//System.out.println(current_tuple.get(j)+" <=? "+condition.get(i).righthand);
								if(Double.parseDouble(current_tuple.get(j))>Double.parseDouble(condition.get(i).righthand)){
									iter.remove();
								}
								break;
							case ">=":
								//System.out.println(current_tuple.get(j)+" >=? "+condition.get(i).righthand);
								if(Double.parseDouble(current_tuple.get(j))<Double.parseDouble(condition.get(i).righthand)){
									iter.remove();
								}
								break;
							case "!=":
								//System.out.println(current_tuple.get(j)+" !=? "+condition.get(i).righthand);
								if(current_tuple.get(j).equals(condition.get(i).righthand)){
									iter.remove();
								}
								break;
	
							default:
								System.out.println("Invalid operator occurs in conditions.");
								break;
						}
					}
				}	
			}
			if(!find_matched_operand){
				System.out.println("Incorrect condition: Some attributes are not found in tables.");
				return;
			}
		}
		for(int i=0;i<selectN;i++){
			System.out.print(selectAttr[i]+"\t");
		}
		System.out.println();
		for(int i=0;i<tables.get(0).tuples.size();i++){
			for(int j=0;j<selectN;j++){
				for(int k=0;k<tables.get(0).attibutes.size();k++){
					if(selectAttr[j].equals(tables.get(0).attibutes.get(k)))
						System.out.print(tables.get(0).tuples.get(i).get(k)+"\t");
				}
			}
			System.out.println();
		}
		
		
		
		
	}
	
	
	
	// divide the condition into three parts: lefthand operand, operator, righthand operand
	static void parseWHERE(){
		System.out.println("Parsing WHERE conditions...");
		for(int i=0;i<whereN;i++){
			condi condi1 = new condi(whereCondition[i]);
			//System.out.println(condi1.getSentence());
			//System.out.println(condi1.getLefthand());
			//System.out.println(condi1.getOperator());			
			//System.out.println(condi1.getRighthand());
			//System.out.println(condi1.getKind());
			condition.add(condi1);
		}
	}
	
}


class table{
	List<String> attibutes = new ArrayList<String>();
	List<ArrayList<String>> tuples = new ArrayList<ArrayList<String>>();	
}

class condi {
	static String[] operator_type = {"!=", ">=", ">", "<=", "<", "="};
	String sentence;
	String lefthand = null;
	String righthand = null;
	int kind; /* 1:select, 2:join */
	String operator = null;  

	public condi(String sentence){
		this.sentence = sentence;
		findOperator();
		if(operator!=null){	//如果operator parse失敗，後面的function也不必做了
			findHands();
			check_attr();
		}else{
			System.out.println("Incorrect operator is used..");
		}
	}
	
	public String getOperator() {
		return operator;
	}

	public String getLefthand() {
		return lefthand;
	}

	public String getRighthand() {
		return righthand;
	}

	public String getSentence() {
		return sentence;
	}

	public int getKind() {
		return kind;
	}

	private void findOperator(){
		for (int i=0; i<operator_type.length;i++) {
			if(this.sentence.contains(operator_type[i])){
				this.operator = operator_type[i];
				return;
			}
		}
	}
	
	private void findHands(){
		String [] s = this.sentence.split(this.operator);
		this.lefthand = s[0].replace(" ","");
		this.righthand = s[1].replace(" ","");
	}
	
	private void check_attr(){
		this.kind = 1;
		for(int i=0;i<mydbms.total_attr.size();i++){				//if RHS is a attribute, then set kind = 2; otherwise, set kind = 1
			if(this.righthand.equals(mydbms.total_attr.get(i))){
				this.kind = 2;
			}
		}
	}
	
}




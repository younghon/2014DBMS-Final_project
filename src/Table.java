import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Table {
	List<String> attributes = new ArrayList<String>();
	List<ArrayList<String>> tuples = new ArrayList<ArrayList<String>>();
	
	
	public Table(){	
		
	}
	
	public Table(Table table){
		for (int i=0; i<table.attributes.size(); i++){
		   this.attributes.add(new String(table.attributes.get(i)));
		}
		for (int i=0; i<table.tuples.size(); i++){
			ArrayList<String> tuple = new ArrayList<String>();
			for (int j=0; j<table.tuples.get(i).size(); j++){
				tuple.add(new String(table.tuples.get(i).get(j)));
			}
			this.tuples.add(tuple);
		}
	}
	
	public int findAttr_in_table(String operand){
		for(int i=0;i<this.attributes.size();i++){
			if(operand.equals(this.attributes.get(i))){
				return i; //operand is found in tables and return attribute's index
			}
		}
		return -1; //not found in every tables
	}
	
	public void printOnScreen(){
		int selectN = mydbms.selectN;
		String[] selectAttr = mydbms.selectAttr;
		
		for(int i=0;i<selectN;i++){
			System.out.print(selectAttr[i]+"\t");
		}
		System.out.println();
		for(int i=0;i<this.tuples.size();i++){
			for(int j=0;j<selectN;j++){
				int index = this.findAttr_in_table(selectAttr[j]);
				if(index != -1){
					System.out.print(this.tuples.get(i).get(index)+"\t");
				}else{
					System.out.println("Incorrect selection: Some attributes are not found in resultTable.");
					return;
				}
			}
			System.out.println();
		}
	}
	
	public void remove(int targetIndex, Condi condition){
		Iterator<ArrayList<String>> iter = this.tuples.iterator();//檢查每個tuple是否符合condition,不符合的tuple從tuples中remove
		while (iter.hasNext()) {
		   ArrayList<String> current_tuple = iter.next();
		   switch (condition.operator) {
				case "=":
					//System.out.println(current_tuple.get(j)+" =? "+condition.get(i).righthand);
					if(!current_tuple.get(targetIndex).equals(condition.righthand)){
						iter.remove();
					}
					break;
				case "<":
					//System.out.println(current_tuple.get(j)+" <? "+condition.get(i).righthand);
					if(!(Double.parseDouble(current_tuple.get(targetIndex))<Double.parseDouble(condition.righthand))){
						iter.remove();
					}
					break;
				case ">":
					//System.out.println(current_tuple.get(j)+" >? "+condition.get(i).righthand);
					if(!(Double.parseDouble(current_tuple.get(targetIndex))>Double.parseDouble(condition.righthand))){
						iter.remove();
					}
					break;
				case "<=":
					//System.out.println(current_tuple.get(j)+" <=? "+condition.get(i).righthand);
					if(Double.parseDouble(current_tuple.get(targetIndex))>Double.parseDouble(condition.righthand)){
						iter.remove();
					}
					break;
				case ">=":
					//System.out.println(current_tuple.get(j)+" >=? "+condition.get(i).righthand);
					if(Double.parseDouble(current_tuple.get(targetIndex))<Double.parseDouble(condition.righthand)){
						iter.remove();
					}
					break;
				case "!=":
					//System.out.println(current_tuple.get(j)+" !=? "+condition.get(i).righthand);
					if(current_tuple.get(targetIndex).equals(condition.righthand)){
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

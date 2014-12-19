import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Aggregate {
	static String[] aggregate_function_type = {"COUNT", "SUM", "MAX", "MIN", "AVG"};
	static List<Aggregate> aggrList = new ArrayList<Aggregate>();
	String function_type;
	String Attribute;
	double value;
	
	public Aggregate(String[] selectAttribute, Table resultTable){
		// TODO Auto-generated constructor stub
		if(parseAggregate(selectAttribute, resultTable)){
			doAggregate(resultTable);
			print();
		}
	}	
	
	public Aggregate(String func, String Attr){
		this.function_type = func;
		this.Attribute =Attr;
	}
	
	private boolean parseAggregate(String[] selectAttribute, Table resultTable){
		for (int i=0; i<selectAttribute.length;i++){
			if(selectAttribute[i].contains("(") && selectAttribute[i].contains(")")){
				String[] tmp = selectAttribute[i].split("\\(");
				String operand = tmp[1].substring(0,tmp[1].length()-1);
				//Aggregate function's operand is not found in resultTable and is not "*"
				if(resultTable.findAttr_in_table(operand)==-1 && !operand.equals("*")){
					System.out.println("Error emerges when parsing Aggregate function...");
					System.out.println("Aggregate function's operand is not found in resultTable.");
					return false;
				}
				switch (tmp[0]) {
					case "COUNT":
						aggrList.add(new Aggregate("COUNT", operand));
						break;
					case "SUM":
						aggrList.add(new Aggregate("SUM", operand));							
						break;
					case "MAX":
						aggrList.add(new Aggregate("MAX", operand));						
						break;
					case "MIN":
						aggrList.add(new Aggregate("MIN", operand));					
						break;
					case "AVG":
						aggrList.add(new Aggregate("AVG", operand));					
						break;
					default:
						System.out.println("Error emerges when parsing Aggregate function...");
						System.out.println("Invalid aggregate function type:"+ tmp[0]);
						return false;
				}
			}else{
				System.out.println("Error emerges when parsing Aggregate function...");
				System.out.println("There is not found '(' or ')' in Aggregate function.");
				return false;
			}
		}
		return true;
	}
	
	public void doAggregate(Table resultTable){
		for(int i=0;i<aggrList.size();i++){
			int AttrIndex = resultTable.findAttr_in_table(aggrList.get(i).Attribute);
			switch (aggrList.get(i).function_type) {
				case "COUNT":
					if(aggrList.get(i).Attribute.equals("*")){
						aggrList.get(i).value = new Double(resultTable.tuples.size());
					}
					//System.out.println("value=" + aggrList.get(i).value);	
					break;
				case "SUM":	
					for(int j=0;j<resultTable.tuples.size();j++){
						aggrList.get(i).value += new Double(resultTable.tuples.get(j).get(AttrIndex));	
					}
					//System.out.println("value=" + aggrList.get(i).value);
					break;
				case "MAX":	
					{
						List<Double> tmpDoubles = new ArrayList<Double>();
						for(int j=0;j<resultTable.tuples.size();j++){
							tmpDoubles.add(new Double(resultTable.tuples.get(j).get(AttrIndex)));
						}
						Collections.sort(tmpDoubles);
						aggrList.get(i).value = tmpDoubles.get(tmpDoubles.size()-1);
					}
					//System.out.println("value=" + aggrList.get(i).value);
					break;
				case "MIN":		
					{
						List<Double> tmpDoubles = new ArrayList<Double>();
						for(int j=0;j<resultTable.tuples.size();j++){
							tmpDoubles.add(new Double(resultTable.tuples.get(j).get(AttrIndex)));
						}
						Collections.sort(tmpDoubles);
						aggrList.get(i).value = tmpDoubles.get(0);
					}
					//System.out.println("value=" + aggrList.get(i).value);
					break;
				case "AVG":		
					for(int j=0;j<resultTable.tuples.size();j++){
						aggrList.get(i).value += new Double(resultTable.tuples.get(j).get(AttrIndex));	
					}
					aggrList.get(i).value = aggrList.get(i).value/new Double(resultTable.tuples.size());
					//System.out.println("value=" + aggrList.get(i).value);
					break;
				default:
					break;
			}
		}
	}
	
	public void print(){
		for(int i=0;i<aggrList.size();i++){
			System.out.print(aggrList.get(i).function_type + "("+aggrList.get(i).Attribute+ ")\t");
		}
		System.out.println();
		
		for(int i=0;i<aggrList.size();i++){
			System.out.print(aggrList.get(i).value+"\t");
		}
		System.out.println();
		aggrList.clear();
	}
	
	static boolean isAggregate(String[] selectAttribute){
		for(int i=0;i<selectAttribute.length;i++){
			for(int j=0;j<aggregate_function_type.length;j++){
				if(selectAttribute[i].contains(aggregate_function_type[j])&&
						selectAttribute[i].contains("(")&&selectAttribute[i].contains(")")){
					return true;
				}
			}				
		}
		return false;
	}
	
	
}

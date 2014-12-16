class Condi {
	static String[] operator_type = {"!=", ">=", ">", "<=", "<", "="};
	String sentence;
	String lefthand = null;
	String righthand = null;
	int type; /* 1:select, 2:join */
	String operator = null;  

	public Condi(String sentence){
		this.sentence = sentence;
		findOperator();
		if(operator!=null){	//如果operator parse成功，才做後面的function
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

	public int getType() {
		return type;
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
		this.type = 1;
		for(int i=0;i<mydbms.total_attr.size();i++){				//if RHS is a attribute, then set type = 2; otherwise, set type = 1
			if(this.righthand.equals(mydbms.total_attr.get(i))){
				this.type = 2;
			}
		}
	}
	
}
package com.csv.calculation;

public class Node {

	private String contents;
	private Type type;
	private static String FLOAT_OR_INT = "(([0-9]+)|([0-9]+)\\.([0-9]+))";

	@SuppressWarnings("unused")
	private Node() {
	}

	public Node(String contents) throws InvalidNodeException {
		this.contents = contents;

		for (Type type : Type.values()) {
			if (doesNodeMatchType(type)) {
				this.type = type;
				break;
			}
		}
		if (this.type == null) {
			throw new InvalidNodeException("This node does not match a valid format");
		}
	}

	public enum Type {
		SIMPLE_VALUE(FLOAT_OR_INT), 
		CELL_REFERENCE("=[A-Z]([1-9]|[1-9][0-9]+)"), 
		CELL_ANY_REFERENCE("[A-Z]([1-9]|[1-9][0-9]+)"), 
		CELL_REFERENCE_OPERATION("[A-Z]([1-9]|[1-9][0-9]+)"+ "[( )]" + FLOAT_OR_INT + "[( )]" + "([\\+|\\-|\\/|\\*])"),
		OPERATION_CELL_REFERENCE(FLOAT_OR_INT+ "[( )]" + "[A-Z]([1-9]|[1-9][0-9]+)" + "[( )]" + "([\\+|\\-|\\/|\\*])"),
		CELL_ANY_REFERENCE_OPERATION("[A-Z]([1-9]|[1-9][0-9]+)"+ "[( )]" + "[A-Z]([1-9]|[1-9][0-9]+)" + "[( )]" + "([\\+|\\-|\\/|\\*])"),
		OPERATION_INT_THREE(FLOAT_OR_INT + "[( )]" + FLOAT_OR_INT + "[( )]" + FLOAT_OR_INT),
		OPERATION_INT_TWO(FLOAT_OR_INT + "[( )]" + FLOAT_OR_INT),
		OPERATION("(=)" + FLOAT_OR_INT + "[( )]" + FLOAT_OR_INT + "[( )]" + "([\\+|\\-|\\/|\\*])"),
		OPERATION_ANY(FLOAT_OR_INT + "[( )]" + FLOAT_OR_INT + "[( )]" + "([\\+|\\-|\\/|\\*])"),
		OPERATION_WITH_CELL_REF("(=)(" + FLOAT_OR_INT + "|" + "[A-Z]([1-9]|[1-9][0-9]+)" + ")[( )]" + FLOAT_OR_INT + "[( )]" + "([\\+|\\-|\\/|\\*])"),
		CELL_ANY_REFERENCE_OPERATION_ANY("[A-Z]([1-9]|[1-9][0-9]+)"+ "[( )]" + "[A-Z]([1-9]|[1-9][0-9]+)" + "[( )]" + "([\\+|\\-|\\/|\\*])"+ "[( )]" + "[A-Z]([1-9]|[1-9][0-9]+)"+ "[( )]"+"([\\+|\\-|\\/|\\*])"),
		OPERATION_WITH_CELL_REF_ANY("[A-Z]([1-9]|[1-9][0-9]+)" + ")[( )]" + FLOAT_OR_INT + "[( )]" + "([\\+|\\-|\\/|\\*])"),
        ARITHSYMBOL("([\\+|\\-|\\/|\\*])");
		Type(String regex) {
			this.regex = regex;
		}

		private String regex;
	}

	private boolean doesNodeMatchType(Type type) {
		return this.contents.matches(type.regex);
	}

	public Type getType() {
		return this.type;
	}
	
	public String getContents() {
		return this.contents;
	}

	public String getReferencedKey() throws InvalidNodeException {
		if(!(this.type == Type.CELL_REFERENCE )){
			throw new InvalidNodeException("Attempt to get referenced cell failed");
		}
		return this.contents.substring(1);
	}
	
	public String getReferencedKeyAny() throws InvalidNodeException {
		if(!(this.type == Type.CELL_ANY_REFERENCE)){
			throw new InvalidNodeException("Attempt to get referenced cell failed");
		}
		return this.contents.substring(0);
	}
	
	public String getReferencedKeyOperation(Integer count) throws InvalidNodeException {
		if(!(this.type == Type.CELL_ANY_REFERENCE_OPERATION || this.type == Type.CELL_REFERENCE_OPERATION || this.type == Type.CELL_ANY_REFERENCE_OPERATION_ANY)){
			throw new InvalidNodeException("Attempt to get referenced cell failed");
		}
		String[]  split=this.contents.split(" ");
		return split[count];
	}

	public Node performCalculation() throws InvalidNodeException {
		if(!(this.type == Type.OPERATION)){
			throw new InvalidNodeException("Attempt to perform calculation failed");
		}
		
		//Chop off the equals sign, and split into the operands and operator
		String[] operation = this.contents.substring(1).split(" ");
		Float operand1 = Float.parseFloat(operation[0]);
		Float operand2 = Float.parseFloat(operation[1]);
		String operator = operation[2];

		Float result = null;
		if (operator.equals("+")) {
			result = operand1 + operand2;
		} else if (operator.equals("-")) {
			result = operand1 - operand2;
		} else if (operator.equals("*")) {
			result = operand1 * operand2;
		} else if (operator.equals("/")) {
			if(operand2.equals(0)){
				throw new IllegalArgumentException("Cannot divide by zero");
			}
			result = operand1 / operand2;
		}

		return new Node(result.toString());
	}
	
	public Node performCalculationAny() throws InvalidNodeException {
		if(!(this.type == Type.OPERATION_ANY)){
			throw new InvalidNodeException("Attempt to perform calculation failed");
		}
		
		String[] operation = this.contents.substring(0).split(" ");
		Float operand1 = Float.parseFloat(operation[0]);
		Float operand2 = Float.parseFloat(operation[1]);
		String operator = operation[2];

		Float result = null;
		if (operator.equals("+")) {
			result = operand1 + operand2;
		} else if (operator.equals("-")) {
			result = operand1 - operand2;
		} else if (operator.equals("*")) {
			result = operand1 * operand2;
		} else if (operator.equals("/")) {
			if(operand2.equals(0)){
				throw new IllegalArgumentException("Cannot divide by zero");
			}
			result = operand1 / operand2;
		}

		return new Node(result.toString());
	}
	
	public Node performCalculationOperationAny(String value) throws InvalidNodeException {
		if(!(this.type == Type.CELL_ANY_REFERENCE_OPERATION)){
			throw new InvalidNodeException("Attempt to perform calculation failed");
		}
		
		String[] operation = value.split(" ");
		Float operand1 = Float.parseFloat(operation[0]);
		Float operand2 = Float.parseFloat(operation[1]);
		String operator = operation[2];

		Float result = null;
		if (operator.equals("+")) {
			result = operand1 + operand2;
		} else if (operator.equals("-")) {
			result = operand1 - operand2;
		} else if (operator.equals("*")) {
			result = operand1 * operand2;
		} else if (operator.equals("/")) {
			if(operand2.equals(0)){
				throw new IllegalArgumentException("Cannot divide by zero");
			}
			result = operand1 / operand2;
		}

		return new Node(result.toString());
	}
	
	
	public Node performCalculationOperation(String value) throws InvalidNodeException {
		String[] operation = value.split(" ");
		Float operand1 = Float.parseFloat(operation[0]);
		Float operand2 = Float.parseFloat(operation[1]);
		String operator = operation[2];
		Float operand3 = Float.parseFloat(operation[3]);
		String operator2 = operation[4];

		Float result = null;
		if (operator.equals("+")) {
			result = operand1 + operand2;
		} else if (operator.equals("-")) {
			result = operand1 - operand2;
			
		} else if (operator.equals("*")) {
			result = operand1 * operand2;
		} else if (operator.equals("/")) {
			if(operand2.equals(0)){
				throw new IllegalArgumentException("Cannot divide by zero");
			}
			result = operand1 / operand2;
		}

		if (operator2.equals("+")) {
			result = result + operand3;
		} else if (operator2.equals("-")) {
			result = result - operand3;
			
		} else if (operator2.equals("*")) {
			result=result-operand3;
			
		} else if (operator2.equals("/")) {
			if(operand2.equals(0)){
				throw new IllegalArgumentException("Cannot divide by zero");
			}
			result=result/operand3;
		}
		return new Node(result.toString());
	}

	public Node cellReferenceOperation(String[] operation2, String contents2) throws InvalidNodeException {
				String[] operation = operation2;
				Float operand1 = Float.parseFloat(contents2);
				Float operand2 = Float.parseFloat(operation[1]);
				String operator = operation[2];

				Float result = null;
				if (operator.equals("+")) {
					result = operand1 + operand2;
				} else if (operator.equals("-")) {
					result = operand1 - operand2;
				} else if (operator.equals("*")) {
					result = operand1 * operand2;
				} else if (operator.equals("/")) {
					if(operand2.equals(0)){
						throw new IllegalArgumentException("Cannot divide by zero");
					}
					result = operand1 / operand2;
				}

				return new Node(result.toString());
	}
	
	
}

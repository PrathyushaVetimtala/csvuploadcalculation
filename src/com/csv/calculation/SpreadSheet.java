package com.csv.calculation;

import java.util.HashMap;

import com.csv.calculation.Node.Type;

public class SpreadSheet extends HashMap<String, Node>{

	private static final long serialVersionUID = 1L;
	
	public int numberOfRows;
	public int numberOfColumns;
	
	public SpreadSheet(String inputCSV){
		//Split the input on the CRLF character
		String[] rows = inputCSV.split("\\r?\\n");
		
		try{
			int rowNumber=0;
			int columnNumber=0;
			//Iterate over all rows in the table
			for (rowNumber=1; rowNumber <= rows.length; ++rowNumber){
				
				String currentRow = rows[rowNumber - 1];
				//Split the current row into columns on the comma character
				String[] columns = currentRow.split(",\\s*");
				
				//Iterate over all columns in the row
				for(columnNumber=1; columnNumber<=columns.length; ++columnNumber){
					//Create a new "node" and add it to our map
					String nodeContent = columns[columnNumber - 1];
					Node node = new Node(nodeContent);
					this.put(getNodeKey(columnNumber, rowNumber), node);
				}
			}
			//Keep track of the number of rows and columns in this spreadsheet
			this.numberOfRows=rowNumber-1;
			this.numberOfColumns=columnNumber-1;
			
		}catch(InvalidNodeException e){
			System.out.println("Failed to parse node at: " + e.nodeKey);
		}
	}

	//This method iterates over the rows and columns of this spreadsheet
	//It processes the nodes one by one, following references if it finds them
	public void processSpreadsheet() {
		for (int rowNumber = 1; rowNumber <= this.numberOfRows; rowNumber++) {
			for (int columnNumber = 1; columnNumber <= this.numberOfColumns; columnNumber++) {
				String currentKey = getNodeKey(columnNumber, rowNumber);
				Node currentNode = this.get(currentKey);
				try {
					process(currentKey, currentNode);
				} catch (InvalidNodeException e) {
					System.out.println("Failed to process node at" + currentKey);
				}
			}
		}
	}
	
	private boolean doesMatchType(String content) {
		return content.contains("=") || content.contains("+") || content.contains("-") || content.contains("*") || content.contains("/") ||  content.contains(" ");
	}
	
	public String getAsCsv() {
		StringBuilder csvString = new StringBuilder();
		for (int rowNumber = 1; rowNumber <= this.numberOfRows; rowNumber++) {
			//For all but the first row, we need to prepend with CRLF
			if(rowNumber!=1){csvString.append("\n");}
			for (int columnNumber = 1; columnNumber <= this.numberOfColumns; columnNumber++) {
				//For all but the first column, we need to prepend with ", "
				if(columnNumber!=1){csvString.append(", ");}
				String currentKey = getNodeKey(columnNumber, rowNumber);
				Node currentNode = this.get(currentKey);
				if (doesMatchType(currentNode.getContents())) {
					csvString.append("#ERR");
				}else {
					csvString.append(currentNode.getContents());
				}
			}
		}
		
		return csvString.toString();
	}
	
	private void process(String currentKey, Node currentNode) throws InvalidNodeException {
		switch (currentNode.getType()){
			case SIMPLE_VALUE:
				this.put(currentKey, currentNode);
				break;
			case CELL_REFERENCE:
				String referencedKey = currentNode.getReferencedKey();
				Node referencedNode = this.get(referencedKey);
				if(referencedNode == null){
					throw new InvalidNodeException("The referenced node was not found in the matrix", referencedKey);
				}
				process(referencedKey, referencedNode);
				Node updatedReferencedNode = this.get(referencedKey);
				this.put(currentKey, updatedReferencedNode);
				break;
			case CELL_ANY_REFERENCE:
				String referencedKey1 = currentNode.getReferencedKeyAny();
				Node referencedNode1 = this.get(referencedKey1);
				if(referencedNode1 == null){
					throw new InvalidNodeException("The referenced node was not found in the matrix", referencedKey1);
				}
				process(referencedKey1, referencedNode1);
				Node updatedReferencedNode1 = this.get(referencedKey1);
				this.put(currentKey, updatedReferencedNode1);
				break;
			case CELL_ANY_REFERENCE_OPERATION:
				StringBuilder  builder=new StringBuilder();
				String referencedKey2 = currentNode.getReferencedKeyOperation(0);
				Node referencedNode2 = this.get(referencedKey2);
				if(referencedNode2 == null){
					throw new InvalidNodeException("The referenced node was not found in the matrix", referencedKey2);
				}
				process(referencedKey2, referencedNode2);
				Node updatedReferencedNode2 = this.get(referencedKey2);
				builder.append(updatedReferencedNode2.getContents()+" ");
				
				String referencedKey3= currentNode.getReferencedKeyOperation(1);
				Node referencedNode3 = this.get(referencedKey3);
				if(referencedNode3 == null){
					throw new InvalidNodeException("The referenced node was not found in the matrix", referencedKey2);
				}
				process(referencedKey3, referencedNode3);
				Node updatedReferencedNode3= this.get(referencedKey3);
				builder.append(updatedReferencedNode3.getContents()+" ");
				String referencedKey4= currentNode.getReferencedKeyOperation(2);
				builder.append(referencedKey4);
				this.put(currentKey, currentNode.performCalculationOperationAny(builder.toString()));
				break;
			case OPERATION_WITH_CELL_REF:
				String[] operation = currentNode.getContents().substring(1).split(" ");
				String keyToLookup=operation[0];
				
				Node referenced = this.get(keyToLookup);
				process(keyToLookup, referenced);
				
				Node updatedRef = this.get(keyToLookup);
				
				Node newNode = new Node("=" + updatedRef.getContents() + " " + operation[1] + " " + operation[2]);
				process(currentKey, newNode);
				break;
			case OPERATION_WITH_CELL_REF_ANY:
				String[] operation1 = currentNode.getContents().substring(1).split(" ");
				String keyToLookup1=operation1[0];
				
				Node referenced1 = this.get(keyToLookup1);
				process(keyToLookup1, referenced1);
				
				Node updatedRef1 = this.get(keyToLookup1);
				
				Node newNode1 = new Node( updatedRef1.getContents() + " " + operation1[1] + " " + operation1[2]);
				process(currentKey, newNode1);
				break;
			case CELL_REFERENCE_OPERATION:
				String[] operation2 = currentNode.getContents().substring(0).split(" ");
				
				String referencedKey6 = currentNode.getReferencedKeyOperation(0);
				Node referencedNode4= this.get(referencedKey6);
				if(referencedNode4 == null){
					throw new InvalidNodeException("The referenced node was not found in the matrix", referencedKey6);
				}
				process(referencedKey6, referencedNode4);
				Node updatedReferencedNode5= this.get(referencedKey6);
				this.put(currentKey, currentNode.cellReferenceOperation(operation2,updatedReferencedNode5.getContents()));
				break;
			case CELL_ANY_REFERENCE_OPERATION_ANY:
				StringBuilder  builderAirth=new StringBuilder();
				String referencedKey5 = currentNode.getReferencedKeyOperation(0);
				Node referencedNode5 = this.get(referencedKey5);
				if(referencedNode5 == null){
					throw new InvalidNodeException("The referenced node was not found in the matrix", referencedKey5);
				}
				process(referencedKey5, referencedNode5);
				Node updatedReferencedNode6= this.get(referencedKey5);
				builderAirth.append(updatedReferencedNode6.getContents()+" ");
				
				String referencedKey7= currentNode.getReferencedKeyOperation(1);
				Node referencedNode6= this.get(referencedKey7);
				if(referencedNode6 == null){
					throw new InvalidNodeException("The referenced node was not found in the matrix", referencedKey7);
				}
				process(referencedKey7, referencedNode6);
				Node updatedReferencedNode7= this.get(referencedKey7);
				builderAirth.append(updatedReferencedNode7.getContents()+" ");
				
				String referencedKey8= currentNode.getReferencedKeyOperation(2);
				builderAirth.append(referencedKey8+" ");
				
				String referencedKey9 = currentNode.getReferencedKeyOperation(3);
				Node referencedNode8 = this.get(referencedKey9);
				if(referencedNode8 == null){
					throw new InvalidNodeException("The referenced node was not found in the matrix", referencedKey9);
				}
				process(referencedKey9, referencedNode8);
				Node updatedReferencedNode9 = this.get(referencedKey9);
				builderAirth.append(updatedReferencedNode9.getContents()+" ");
				
				String referencedKey10= currentNode.getReferencedKeyOperation(4);
				builderAirth.append(referencedKey10);
				
				this.put(currentKey, currentNode.performCalculationOperation(builderAirth.toString()));
				break;
			case OPERATION:
				//If it's an operation, perform the calculation
				this.put(currentKey, currentNode.performCalculation());
				break;
			case OPERATION_ANY:
				//If it's an operation, perform the calculation
				this.put(currentKey, currentNode.performCalculationAny());
				break;
		}
	}
	
	private static String getNodeKey(int columnNumber, int rowNumber) {
		return (char)(columnNumber + 64) + String.valueOf(rowNumber);
	}
}

package com.csv.calculation;


class MatrixRPNCalculator {
	public static void main(String[] args) throws InvalidNodeException {
		
		if(args.length != 1)
	    {
	        System.out.println("Usage: java MatrixRPNCalculator csvInputFileName");
	        System.exit(0);
	    }
		
		String inputCsv = IOUtils.getInput(args[0]);
		
		SpreadSheet spreadSheet = new SpreadSheet(inputCsv);
		
		spreadSheet.processSpreadsheet();
		
		IOUtils.sendOutput(spreadSheet.getAsCsv());
		
		
	}
}

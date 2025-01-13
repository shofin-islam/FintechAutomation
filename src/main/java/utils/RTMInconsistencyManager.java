package utils;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class RTMInconsistencyManager {

	static String baseDir = System.getProperty("user.dir");
	static String fileDir = baseDir+ "/src/main/resources/files/excel_comparator/";

	public static void main(String[] args) throws Exception {  

		String resultDir = createDynamicFolder(fileDir);

		/* Generate unique bug links file */

		String executionSuiteFile = fileDir + "NCC Services Request Execution Suite Round 2.xlsx";
		// Get unique bug links and write to file
		String bugLinksFile = getUniqueBugLinksWithSheetNames(executionSuiteFile,resultDir);
		System.out.println("Unique bug links written to: " + bugLinksFile);
		processSheetsForNAAndFail(executionSuiteFile,resultDir);

		//File Handler

		// Copy the file
		handleFile(executionSuiteFile, resultDir, false);

		// Move the file
		//        handleFile(executionSuiteFile, resultDir, true);

		// File paths and sheet names

		//        String ex1FilePath = baseDir + "20241230_184804_UniqueBugLinks.xlsx";
		String ex1FilePath = bugLinksFile;
		String ex2FilePath = fileDir + "BS23_FinTech_Projects_Work_packages_2025-01-1220250112-24055-pmwfzs.xlsx";
		String outputDir = resultDir;
		String ex1SheetName = "UniqueBugLinks";
		String ex2SheetName = "Work packages";

		// Load EX1 and EX2 files
		Workbook ex1Workbook = new XSSFWorkbook(new FileInputStream(ex1FilePath));
		Workbook ex2Workbook = new XSSFWorkbook(new FileInputStream(ex2FilePath));

		// Get sheets
		Sheet ex1Sheet = ex1Workbook.getSheet(ex1SheetName);
		Sheet ex2Sheet = ex2Workbook.getSheet(ex2SheetName);

		// Read IDs from EX1 (Column A) into a Set
		Set<String> ex1Ids = new HashSet<>();
		for (Row row : ex1Sheet) {
			if (row.getRowNum() == 0) continue; // Skip header
			Cell cell = row.getCell(0);
			if (cell != null) {
				String cellValue = getCellValueAsString(cell);
				ex1Ids.add(cellValue.trim());
			}
		}

		// Add comparison column to EX1
		addComparisonColumn(ex1Sheet, ex1Ids, 0, ex2Sheet, 0, "EX1");

		// Add comparison column to EX2
		addComparisonColumn(ex2Sheet, ex1Ids, 0, ex1Sheet, 0, "EX2");

		// Generate dynamic file names
		String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
		String ex1OutputPath = outputDir + "EX1_Compared_" + timestamp + ".xlsx";
		String ex2OutputPath = outputDir + "EX2_Compared_" + timestamp + ".xlsx";

		// Write to new files
		try (FileOutputStream ex1Out = new FileOutputStream(ex1OutputPath);
				FileOutputStream ex2Out = new FileOutputStream(ex2OutputPath)) {
			ex1Workbook.write(ex1Out);
			ex2Workbook.write(ex2Out);
		}

		ex1Workbook.close();
		ex2Workbook.close();

		System.out.println("Comparison complete. Files saved:");
		System.out.println(ex1OutputPath);
		System.out.println(ex2OutputPath);

	}

	private static void addComparisonColumn(Sheet sheet, Set<String> comparisonSet, int idColumnIndex,
			Sheet compareSheet, int compareIdColumnIndex, String label) {
		Row headerRow = sheet.getRow(0);
		int newColumnIndex = headerRow.getLastCellNum();
		headerRow.createCell(newColumnIndex).setCellValue("Comparison Result");

		for (Row row : sheet) {
			if (row.getRowNum() == 0) continue; // Skip header
			Cell idCell = row.getCell(idColumnIndex);
			Cell comparisonCell = row.createCell(newColumnIndex);

			if (idCell != null) {
				String id = getCellValueAsString(idCell).trim();
				if (comparisonSet.contains(id)) {
					comparisonCell.setCellValue("Exist");
				} else {
					comparisonCell.setCellValue("Not Exist");
				}
			} else {
				comparisonCell.setCellValue("Not Exist");
			}
		}
	}



	private static String getCellValueAsString(Cell cell) {
		if (cell == null) {
			return ""; // Return empty string for null cells
		}
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue().toString(); // Handle date cells if needed
			}
			return String.valueOf((long) cell.getNumericCellValue()); // Convert numeric to text
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula(); // Optional: Handle formulas
		default:
			return ""; // Return empty string for unsupported cell types
		}
	}

	public static String getUniqueBugLinksWithSheetNames(String filePath, String outputDir) throws IOException {
		Set<String> uniqueBugLinks = new LinkedHashSet<>();
		List<List<String>> outputData = new ArrayList<>();
		// Updated header sequence
		outputData.add(Arrays.asList("Bug ID", "Sheet Name", "Bug Link", "Status"));

		try (FileInputStream fis = new FileInputStream(new File(filePath));
				Workbook workbook = new XSSFWorkbook(fis)) {

			for (Sheet sheet : workbook) {
				String sheetName = sheet.getSheetName();
				if (sheetName.equalsIgnoreCase("cover_page") || sheetName.equalsIgnoreCase("uniq_items")) {
					continue;
				}

				for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) { // Skip header row
					Row row = sheet.getRow(rowIndex);
					if (row == null) continue;

					// Get Bug Link from Column K
					Cell bugLinkCell = row.getCell(10); // Column K
					String bugLink = getCellValueAsString(bugLinkCell);

					// Get Status from Column I
					Cell statusCell = row.getCell(8); // Column I
					String status = getCellValueAsString(statusCell);

					if (!bugLink.isEmpty() && uniqueBugLinks.add(bugLink)) {
						String bugId = getBugIdFromLink(bugLink);
						// Updated data row sequence
						outputData.add(Arrays.asList(bugId, sheetName, bugLink, status));
					}
				}
			}
		}

		String outputFile =  outputDir+ dynamicDateTime() + "_UniqueBugLinks.xlsx";
		writeListToExcel(outputFile, "UniqueBugLinks", outputData);

		return outputFile;
	}



	public static void writeListToExcel(String filePath, String sheetName, List<List<String>> data) throws IOException {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet(sheetName);

			for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
				Row row = sheet.createRow(rowIndex);
				List<String> rowData = data.get(rowIndex);

				for (int colIndex = 0; colIndex < rowData.size(); colIndex++) {
					Cell cell = row.createCell(colIndex);
					cell.setCellValue(rowData.get(colIndex));
				}
			}

			try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
				workbook.write(fos);
			}
		}
	}

	private static String dynamicDateTime() {
		return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	}

	private static String getBugIdFromLink(String bugLink) {
		String[] parts = bugLink.split("/");
		return parts.length > 0 ? parts[parts.length - 1] : "";
	}


	public static void processSheetsForNAAndFail(String filePath, String outputDir) throws IOException {
	    List<List<String>> outputData = new ArrayList<>();
	    outputData.add(Arrays.asList("Sheet Name", "Test Title", "Matched Type", "Extracted Value"));

	    try (FileInputStream fis = new FileInputStream(new File(filePath));
	         Workbook workbook = new XSSFWorkbook(fis)) {

	        for (Sheet sheet : workbook) {
	            String sheetName = sheet.getSheetName();

	            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) { // Skip header row
	                Row row = sheet.getRow(rowIndex);
	                if (row == null) continue;

	                Cell statusCell = row.getCell(8); // Column I
	                if (statusCell == null) continue;

	                String statusValue = getCellValueAsString(statusCell).trim(); // Trim to remove extra spaces
	                if (statusValue.equalsIgnoreCase("Pass") || statusValue.equalsIgnoreCase("Fail") || statusValue.equalsIgnoreCase("Failed")) {
	                    String matchedType = null;
	                    String extractedValue = null;
	                    String testItem = null;

	                    Cell testTitleCell = row.getCell(5); // Column F
	                    if (testTitleCell != null) {
	                        testItem = getCellValueAsString(testTitleCell);
	                    }

	                    if ("NA".equalsIgnoreCase(statusValue)) {
	                        matchedType = "NA";
	                        Cell valueComment = row.getCell(9); // Column J
	                        if (valueComment != null) {
	                            extractedValue = getCellValueAsString(valueComment);
	                        }
	                    } else if ("Fail".equalsIgnoreCase(statusValue)) {
	                        matchedType = "Fail";
	                        Cell valueBugs = row.getCell(10); // Column K
	                        if (valueBugs != null) {
	                            extractedValue = getCellValueAsString(valueBugs);
	                        }
	                    } else if ("Failed".equalsIgnoreCase(statusValue)) {
	                        matchedType = "Failed";
	                        Cell valueCell = row.getCell(10); // Column K
	                        if (valueCell != null) {
	                            extractedValue = getCellValueAsString(valueCell);
	                        }
	                    }

	                    // Add the matched data to the output if a match was found
	                    if (matchedType != null && extractedValue != null) {
	                        outputData.add(Arrays.asList(sheetName, testItem, matchedType, extractedValue));
	                    }
	                }
	            }
	        }
	    }

	    // Write output to a new Excel file
	    String outputFile = outputDir + dynamicDateTime() + "_NA_Fail_Failed_Report.xlsx";
	    writeListToExcel(outputFile, "NA_Fail_Failed_Report", outputData);

	    System.out.println("NA Fail Failed Report written to: " + outputFile);
	}



	public static String createDynamicFolder(String mydir) {
		// Format the current date and time
		String dynamicDateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		// Construct the folder path
		String folderPath = mydir + "Results_" + dynamicDateTime + File.separator;

		// Create the folder
		File folder = new File(folderPath);
		if (!folder.exists()) {
			boolean created = folder.mkdirs();
			if (!created) {
				throw new RuntimeException("Failed to create directory: " + folderPath);
			}
		}

		// Return the folder path
		return folderPath;
	}

	public static void handleFile(String sourceFilePath, String destinationDirPath, boolean move) throws IOException {
		// Create File objects for the source file and destination directory
		File sourceFile = new File(sourceFilePath);
		File destinationDir = new File(destinationDirPath);

		// Ensure the source file exists
		if (!sourceFile.exists() || !sourceFile.isFile()) {
			throw new IOException("Source file does not exist or is not a valid file: " + sourceFile.getAbsolutePath());
		}

		// Ensure the destination directory exists, create it if not
		if (!destinationDir.exists()) {
			boolean dirCreated = destinationDir.mkdirs();
			if (!dirCreated) {
				throw new IOException("Failed to create destination directory: " + destinationDir.getAbsolutePath());
			}
		}

		// Create a Path object for the destination file
		Path destinationPath = new File(destinationDir, sourceFile.getName()).toPath();

		// Perform the action (copy or move)
		if (move) {
			Files.move(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("File moved successfully from " + sourceFile.getAbsolutePath() + " to " + destinationPath);
		} else {
			Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("File copied successfully from " + sourceFile.getAbsolutePath() + " to " + destinationPath);
		}
	}

}

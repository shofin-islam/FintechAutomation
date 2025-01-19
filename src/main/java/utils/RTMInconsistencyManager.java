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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class RTMInconsistencyManager {

	static String baseDir = System.getProperty("user.dir");
	static String fileDir = baseDir+ "/src/main/resources/files/excel_comparator/";

	public static void main(String[] args) throws Exception {  

		String ParentDir = createDynamicFolder(fileDir,"Results",false);
		String resultDir = createDynamicFolder(ParentDir,"Output",true);

		/* Generate unique bug links file */

		String executionSuiteFile = fileDir + "NCC Services Request Execution Suite Round 2.xlsx";
		// Get unique bug links and write to file
		String bugLinkFileSheetName = "UniqueBugLinks";
		String bugLinksFile = getUniqueBugLinksWithSheetNames(executionSuiteFile,bugLinkFileSheetName,resultDir);
		System.out.println("Unique bug links written to: " + bugLinksFile);
		processSheetsForNAAndFail(executionSuiteFile,resultDir);

		//File Handler

		// Copy the file
		handleFile(executionSuiteFile, resultDir, false);

		// Move the file
		//        handleFile(executionSuiteFile, resultDir, true);

		// File paths and sheet names

		//        String ex1FilePath = baseDir + "20241230_184804_UniqueBugLinks.xlsx";
		String bugLinkFile = bugLinksFile;
		String openProjectData = fileDir + "OP_Bug_List_Service_Request.xlsx";
		String outputDir = resultDir;
		
		String openProjectSheet = "Work packages";

		// Load EX1 and EX2 files
		Workbook ex1Workbook = new XSSFWorkbook(new FileInputStream(bugLinkFile));
		Workbook ex2Workbook = new XSSFWorkbook(new FileInputStream(openProjectData));

		// Get sheets
		Sheet ex1Sheet = ex1Workbook.getSheet(bugLinkFileSheetName);
		Sheet ex2Sheet = ex2Workbook.getSheet(openProjectSheet);

		// Read IDs from EX1 (Column A) into a Set
		Set<String> ex1Ids = getIdsFromSheet(ex1Sheet, 0);
		System.out.println("ex1Ids count"+ ex1Ids.size());

			
		// Generate dynamic file names
		String timestamp = dynamicDateTime();
		
		String comparedFilePath = outputDir + "Compared_" + timestamp + ".xlsx";
		String missingBugDetailsFilePath = outputDir + "MissingBugDetails_" + timestamp + ".xlsx";
		
				
		compareAndAddColumn(ex2Sheet, 0, ex1Ids, missingBugDetailsFilePath);

		// Write to new files
		try (FileOutputStream comp = new FileOutputStream(comparedFilePath)) {
			ex2Workbook.write(comp);
			
		}

		ex1Workbook.close();
		ex2Workbook.close();

		System.out.println("Comparison complete. Files saved:");
		System.out.println(comparedFilePath);

	}

	public static Set<String> getIdsFromSheet(Sheet sheetName, int columnIndex) throws IOException {
		Set<String> ids = new HashSet<>();

		if (sheetName == null) throw new IllegalArgumentException("Sheet " + sheetName + " does not exist.");

		for (Row row : sheetName) {
			if (row.getRowNum() == 0) continue; // Skip header row
			Cell cell = row.getCell(columnIndex);
			if (cell != null) {
				String cellValue = getCellValueAsString(cell).trim();
				if (!cellValue.isEmpty()) ids.add(cellValue);
			}
		}
		return ids;
	}

	public static void compareAndAddColumn(Sheet sheet, int idColumnIndex,
			Set<String> comparisonSet, String outputFilePath) throws IOException {
		
		List<List<String>> missingBugDetails = new ArrayList<>();
		// Updated header sequence
		missingBugDetails.add(Arrays.asList("Bug ID", "Subject", "Status", "Author", "Link"));
		
		try {
			Row headerRow = sheet.getRow(0);
			int comparisonColumnIndex = headerRow.getLastCellNum(); // Add at the last column
			System.out.println("last column index "+comparisonColumnIndex);
			headerRow.createCell(comparisonColumnIndex).setCellValue("Comparison Result");

			for (Row row : sheet) {
				if (row.getRowNum() == 0) continue; // Skip header row
				Cell idCell = row.getCell(idColumnIndex);
				Cell comparisonCell = row.createCell(comparisonColumnIndex);

				if (idCell != null) {
					String id = getCellValueAsString(idCell).trim();
					if (comparisonSet.contains(id)) {
						comparisonCell.setCellValue("Exist");
					} else {
						comparisonCell.setCellValue("Not Exist");
						missingBugDetails.add(Arrays.asList(id,getCellValueAsString(row.getCell(1)),getCellValueAsString(row.getCell(3)),getCellValueAsString(row.getCell(4)),"https://fintech-bs23.xyz/wp/"+id));
					}
				} else {
					comparisonCell.setCellValue("Not Executed");
				}
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		try {
			writeListToExcel(outputFilePath, "bug_details", missingBugDetails);
		} catch (Exception e) {
			// TODO: handle exception
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

	public static String getUniqueBugLinksWithSheetNames(String filePath,String outputFileSheetName, String outputDir) throws IOException {
		Set<String> uniqueBugLinks = new LinkedHashSet<>();
		List<List<String>> outputData = new ArrayList<>();
		// Updated header sequence
		outputData.add(Arrays.asList("Bug ID", "Sheet Name", "Bug Link", "Status"));

		try (FileInputStream fis = new FileInputStream(new File(filePath));
				Workbook workbook = new XSSFWorkbook(fis)) {

			for (Sheet sheet : workbook) {
				String sheetName = sheet.getSheetName();
				if (sheetName.equalsIgnoreCase("cover_page") || sheetName.equalsIgnoreCase("cover page") || sheetName.equalsIgnoreCase("uniq_items")) {
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

				    // Use regex to find all valid URLs in the cell, even if concatenated
				    if (!bugLink.isEmpty()) {
				        // Enhanced regex to match valid URLs
				        Pattern urlPattern = Pattern.compile("(https?://[\\w.-]+(/[\\w./-]*)?)");
				        Matcher matcher = urlPattern.matcher(bugLink);

				        // Extract all URLs using the matcher
				        while (matcher.find()) {
				            String extractedLink = matcher.group().trim(); // Get each matched URL
				            if (!extractedLink.isEmpty() && uniqueBugLinks.add(extractedLink)) {
				                String bugId = getBugIdFromLink(extractedLink);
				                // Updated data row sequence
				                outputData.add(Arrays.asList(bugId, sheetName, extractedLink, status));
				            }
				        }
				    }
				}


			}
		}

		String outputFile =  outputDir+ dynamicDateTime() + "_UniqueBugLinks.xlsx";
		writeListToExcel(outputFile,outputFileSheetName, outputData);

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
					if (statusValue.equalsIgnoreCase("NA") || statusValue.equalsIgnoreCase("Fail") || statusValue.equalsIgnoreCase("Failed")) {
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



	public static String createDynamicFolder(String baseDir, String folderName, boolean isDynamic) {
	    // Format the current date and time
	    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

	    // Construct the folder path
	    String folderPath = baseDir + folderName + (isDynamic ? timestamp : "") + File.separator;

	    // Create the folder if dynamic flag is true
	    if (isDynamic) {
	        File folder = new File(folderPath);
	        if (!folder.exists() && !folder.mkdirs()) {
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

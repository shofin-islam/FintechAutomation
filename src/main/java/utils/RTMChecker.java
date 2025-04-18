package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RTMChecker {

	static String baseDir; // Base directory for files outside the project
	static String timestamp;
	static JsonNode config;
	static String version = "1.1";
	static Integer rtmBugLinkColumnIndex;
	static Integer rtmStatusColumnIndex;
	static Integer rtmCommentColumnIndex;

	public static void main(String[] args) throws Exception {

		System.out.println("Executing RTM Checker Version: "+version);

		// Initialize base directory (external location for inputs and outputs)
		//    	baseDir = Paths.get(System.getProperty("user.dir")).getParent().toString() + File.separator;
		baseDir = System.getProperty("user.dir") + File.separator;
		timestamp = dynamicDateTime();
		// Load the JSON configuration file
		config = loadConfig(baseDir + "compare.json");

		// Extract file paths and configuration values from JSON
		String executionSuiteFile = baseDir + config.get("executionSuite").asText();
		String openProjectData = baseDir + config.get("openProjectData").asText();
		String bugLinkFileSheetName = config.get("UniqueBugLinksSheetName").asText();
		String openProjectSheet = config.get("openProjectSheetName").asText();
		String uniqueBugsIdColumnIndex = config.get("uniqueBugsIdColumnIndex").asText();
		String openProjectIdColumnIndex = config.get("openProjectIdColumnIndex").asText();
		rtmBugLinkColumnIndex = config.get("rtmBugLinkColumnIndex").asInt();
		rtmStatusColumnIndex = config.get("rtmStatusColumnIndex").asInt();
		rtmCommentColumnIndex = config.get("rtmCommentColumnIndex").asInt();

		// Set up output directories outside the project
		String parentDir = createDynamicFolder(baseDir, "Results", false);
		String resultDir = createDynamicFolder(parentDir, "Output_", true);

		// Generate unique bug links file
		String bugLinksFile = getUniqueBugLinksWithSheetNames(executionSuiteFile, resultDir, bugLinkFileSheetName);
		System.out.println("Unique bug links written to: " + bugLinksFile);
		processSheetsForNAAndFail(executionSuiteFile, resultDir);

		// Handle file operations
		handleFile(executionSuiteFile, resultDir, false);

		// Load EX1 and EX2 files
		Workbook ex1Workbook = new XSSFWorkbook(new FileInputStream(bugLinksFile));
		Workbook ex2Workbook = new XSSFWorkbook(new FileInputStream(openProjectData));

		// Get sheets
		Sheet ex1Sheet = ex1Workbook.getSheet(bugLinkFileSheetName);
		Sheet ex2Sheet = ex2Workbook.getSheet(openProjectSheet);

		// Read IDs from EX1 (Column A) into a Set
		Set<String> ex1Ids = getIdsFromSheet(ex1Sheet, Integer.parseInt(uniqueBugsIdColumnIndex));
		System.out.println("Bug count: " + ex1Ids.size());

		// Generate dynamic file names for outputs

		String comparedFilePath = resultDir + "Compared_" + timestamp + ".xlsx";
		String missingBugDetailsFilePath = resultDir + "MissingBugDetails_" + timestamp + ".xlsx";

		compareAndAddColumn(ex2Sheet, Integer.parseInt(openProjectIdColumnIndex), ex1Ids, missingBugDetailsFilePath);

		// Write the comparison result to a file
		try (FileOutputStream comp = new FileOutputStream(comparedFilePath)) {
			ex2Workbook.write(comp);
		}

		ex1Workbook.close();
		ex2Workbook.close();

		System.out.println("Comparison Complete and Files Saved To : "+comparedFilePath);
		System.out.println("Missing Bug List In RTM Saved To : "+missingBugDetailsFilePath);
	}

	// Load configuration JSON from the specified path
	private static JsonNode loadConfig(String configFilePath) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		File configFile = new File(configFilePath);

		if (!configFile.exists()) {
			throw new FileNotFoundException("Configuration file not found at: " + configFile.getAbsolutePath());
		}

		return objectMapper.readTree(configFile);
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
		missingBugDetails.add(Arrays.asList("Bug ID", "Subject", "Status", "Author", "Link"));

		/*
        Row headerRow = sheet.getRow(0);
        if (headerRow != null) {
            // Get the number of cells in the row
            int totalCells = headerRow.getLastCellNum();
            System.out.println("Total Cells in the Row: " + totalCells);

            // Iterate over each cell in the row
            for (int cellIndex = 0; cellIndex < totalCells; cellIndex++) {
                Cell cell = headerRow.getCell(cellIndex);

                // Get the cell value as a string (use your utility if needed)
                String cellValue = (cell != null) ? getCellValueAsString(cell) : "EMPTY";

                // Print the cell index and value
                System.out.println("Cell Index: " + cellIndex + ", Value: " + cellValue);
            }
        } else {
            System.out.println("Row is null or does not exist.");
        }
		 */

		try {
			Row headerRow = sheet.getRow(0);
			int comparisonColumnIndex = headerRow.getLastCellNum();

			if (headerRow != null) {
				// Get the number of cells in the row
				int totalCells = headerRow.getLastCellNum();
				System.out.println("Total Cells in the Row: " + totalCells);

				// Iterate over each cell in the row
				for (int cellIndex = 0; cellIndex < totalCells; cellIndex++) {
					Cell cell = headerRow.getCell(cellIndex);

					// Get the cell value as a string (use your utility if needed)
					String cellValue = (cell != null) ? getCellValueAsString(cell) : "EMPTY";

					// Print the cell index and value
					System.out.println("Cell Index: " + cellIndex + ", Value: " + cellValue);
				}
			}

			headerRow.createCell(comparisonColumnIndex).setCellValue("Comparison Result");

			for (Row row : sheet) {
				if (row.getRowNum() == 0) continue;
				Cell idCell = row.getCell(idColumnIndex);
				Cell comparisonCell = row.createCell(comparisonColumnIndex);

				if (idCell != null) {
					String id = getCellValueAsString(idCell).trim();
					if (comparisonSet.contains(id)) {
						comparisonCell.setCellValue("Exist");
					} else {
						comparisonCell.setCellValue("Not Exist");
						missingBugDetails.add(Arrays.asList(id,
								getCellValueAsString(row.getCell(1)),
								getCellValueAsString(row.getCell(3)),
								getCellValueAsString(row.getCell(4)),
								"https://fintech-bs23.xyz/wp/" + id));
					}
				} else {
					comparisonCell.setCellValue("Not Executed");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			writeListToExcel(outputFilePath, "bug_details", missingBugDetails);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getCellValueAsString(Cell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue().toString();
			}
			return String.valueOf((long) cell.getNumericCellValue());
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula();
		default:
			return "";
		}
	}

	public static String getUniqueBugLinksWithSheetNames(String filePath, String outputDir, String outputFileSheetName) throws IOException {
		/* sample bug links: 
		 * https://fintech-bs23.xyz/projects/ab-revamp/work_packages/28021/activity?query_id=1460
		 * https://fintech-bs23.xyz/wp/28021
		 * https://fintech-bs23.xyz/projects/ab-revamp/work_packages/28021/activity
		 */

		Set<String> uniqueBugLinks = new LinkedHashSet<>();
		List<List<String>> outputData = new ArrayList<>();
		outputData.add(Arrays.asList("Bug ID", "Sheet Name", "Bug Link", "Status"));

		try (FileInputStream fis = new FileInputStream(new File(filePath));
				Workbook workbook = new XSSFWorkbook(fis)) {

			// Fetch the skip list string from the configuration
			String skipSheetsString = config.get("UniqueBugLinksSheetName").asText();

			// Convert the comma-separated string into a list
			List<String> sheetsToSkip = Arrays.asList(skipSheetsString.split(",\\s*"));

			for (Sheet sheet : workbook) {
				String sheetName = sheet.getSheetName();

				// Check if the sheet name is in the skip list (case-insensitive)
				if (sheetsToSkip.stream().anyMatch(skipName -> skipName.equalsIgnoreCase(sheetName))) {
					continue;
				}

				for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
					Row row = sheet.getRow(rowIndex);
					if (row == null) continue;

					Cell bugLinkCell = row.getCell(rtmBugLinkColumnIndex);
					String bugLink = getCellValueAsString(bugLinkCell);

					Cell statusCell = row.getCell(rtmStatusColumnIndex);
					String status = getCellValueAsString(statusCell);

					if (!bugLink.isEmpty()) {
						// Regex to match all bug link patterns
						Pattern urlPattern = Pattern.compile(
								"(https?://[\\w.-]+/projects/[\\w-]+/work_packages/\\d+/activity(\\?query_id=\\d+)?)|" + // Pattern 1 & 3
										"(https?://[\\w.-]+/wp/\\d+)"                                           // Pattern 2
								);

						Matcher matcher = urlPattern.matcher(bugLink);

						while (matcher.find()) {
							String extractedLink = matcher.group().trim();
							if (!extractedLink.isEmpty() && uniqueBugLinks.add(extractedLink)) {
								String bugId = getBugIdFromLink(extractedLink);
								outputData.add(Arrays.asList(bugId, sheetName, extractedLink, status));
							}
						}
					}
				}
			}
		}

		String outputFile = outputDir + "UniqueBugLinks_" + timestamp.toString() + ".xlsx";
		writeListToExcel(outputFile, outputFileSheetName, outputData);

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
		// Adjust bug ID extraction to handle both URL patterns
		if (bugLink.contains("/work_packages/")) {
			String[] parts = bugLink.split("/work_packages/");
			String idPart = parts.length > 1 ? parts[1] : "";
			return idPart.split("[/?]")[0]; // Extracts the number before "?" or "/"
		} else if (bugLink.contains("/wp/")) {
			String[] parts = bugLink.split("/wp/");
			return parts.length > 1 ? parts[1].split("[/?]")[0] : ""; // Extracts the number
		}
		return "";
	}

	public static String createDynamicFolder(String mydir, String name, Boolean dynamic) {
		String folderPath = mydir + name + (dynamic ? new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) : "") + File.separator;
		File folder = new File(folderPath);
		if (!folder.exists() && !folder.mkdirs()) {
			throw new RuntimeException("Failed to create directory: " + folderPath);
		}
		return folderPath;
	}

	public static void handleFile(String sourceFilePath, String destinationDirPath, boolean move) throws IOException {
		File sourceFile = new File(sourceFilePath);
		File destinationDir = new File(destinationDirPath);

		if (!sourceFile.exists() || !sourceFile.isFile()) {
			throw new IOException("Source file not found: " + sourceFile.getAbsolutePath());
		}

		if (!destinationDir.exists() && !destinationDir.mkdirs()) {
			throw new IOException("Failed to create destination directory: " + destinationDir.getAbsolutePath());
		}

		Path destinationPath = new File(destinationDir, sourceFile.getName()).toPath();

		if (move) {
			Files.move(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
		} else {
			Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
		}
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

					Cell statusCell = row.getCell(rtmStatusColumnIndex); // Column I
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
							Cell valueComment = row.getCell(rtmCommentColumnIndex); // Column J
							if (valueComment != null) {
								extractedValue = getCellValueAsString(valueComment);
							}
						} else if ("Fail".equalsIgnoreCase(statusValue)) {
							matchedType = "Fail";
							Cell valueBugs = row.getCell(rtmBugLinkColumnIndex); // Column K
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
		String outputFileNaFail = outputDir + "NA_and_Fail_Report_"+timestamp.toString() + ".xlsx";
		writeListToExcel(outputFileNaFail, "AnalyticalReport ", outputData);

		System.out.println("NA and Fail Report written to: " + outputFileNaFail);
	}
}

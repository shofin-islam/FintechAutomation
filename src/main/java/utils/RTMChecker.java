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

        System.out.println("Executing RTM Checker Version: " + version);

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

        System.out.println("Comparison Complete and Files Saved To : " + comparedFilePath);
        System.out.println("Missing Bug List In RTM Saved To : " + missingBugDetailsFilePath);
    }

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
        if (sheetName == null) throw new IllegalArgumentException("Sheet does not exist.");

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

        Row headerRow = sheet.getRow(0);
        int comparisonColumnIndex = headerRow.getLastCellNum();
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
                    missingBugDetails.add(Arrays.asList(
                            id,
                            getCellValueAsString(row.getCell(1)),
                            getCellValueAsString(row.getCell(3)),
                            getCellValueAsString(row.getCell(4)),
                            "https://fintech-bs23.xyz/wp/" + id));
                }
            }
        }

        // Write missing bug details to output
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            Sheet outputSheet = workbook.createSheet("Missing Bugs");

            for (int i = 0; i < missingBugDetails.size(); i++) {
                Row row = outputSheet.createRow(i);
                List<String> rowData = missingBugDetails.get(i);
                for (int j = 0; j < rowData.size(); j++) {
                    row.createCell(j).setCellValue(rowData.get(j));
                }
            }

            workbook.write(fos);
        }
    }

    // Placeholder methods — update with real implementations
    private static String dynamicDateTime() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    private static String createDynamicFolder(String base, String name, boolean appendTimestamp) throws IOException {
        String folderName = base + name + (appendTimestamp ? dynamicDateTime() : "") + File.separator;
        Files.createDirectories(Paths.get(folderName));
        return folderName;
    }

    private static String getUniqueBugLinksWithSheetNames(String executionSuiteFile, String resultDir, String sheetName) {
        // Placeholder
        return resultDir + "UniqueBugLinks.xlsx";
    }

    private static void processSheetsForNAAndFail(String executionSuiteFile, String resultDir) {
        // Placeholder
    }

    private static void handleFile(String executionSuiteFile, String resultDir, boolean b) {
        // Placeholder
    }

    private static String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            default: return "";
        }
    }
}

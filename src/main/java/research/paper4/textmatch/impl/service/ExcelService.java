package research.paper4.textmatch.impl.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ExcelService {

    public void createExcel(String fileName, Map<String, String> dataMap) {

        try {
            File file = new File(fileName);
            Workbook workbook;
            Sheet sheet;
            String[] rowData = dataMap.values().toArray(new String[0]);

            if (file.exists()) {
                // Open existing file
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheetAt(0);
                fis.close();
            } else {
                // Create new file
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet(fileName);
                // Create a header row
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("TITLE");
                header.createCell(1).setCellValue("DESCRIPTION");
                header.createCell(2).setCellValue("RESPONSIBILITIES");
                header.createCell(3).setCellValue("EDUCATION");
                header.createCell(4).setCellValue("SKILLS");
                header.createCell(5).setCellValue("EXPERIENCE");
                header.createCell(6).setCellValue("ADDITIONAL_INFO");
                header.createCell(7).setCellValue("INPUT_FULL_TEXT");

            }
            // Append new row at the end
            int lastRowNum = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRowNum + 1);

            for (int i = 0; i < rowData.length; i++) {
                newRow.createCell(i).setCellValue(rowData[i]);
            }

            // Write back to file
            FileOutputStream fos = new FileOutputStream(fileName);
            workbook.write(fos);
            fos.close();
            workbook.close();

            System.out.println("Row added successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, String>> readExcel(String filename){
        List<Map<String, String>> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filename);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext())
                return dataList;

            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }

            DataFormatter formatter = new DataFormatter();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, String> rowMap = new LinkedHashMap<>();

                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String cellValue = formatter.formatCellValue(cell);
                    rowMap.put(headers.get(i), cellValue);
                }

                dataList.add(rowMap);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    public void createResultsExcel(String fileName, List<Map<String, String>> dataList){
        try {
            File file = new File(fileName);
            Workbook workbook;
            Sheet sheet;

            if (file.exists()) {
                // Open existing file
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheetAt(0);
                fis.close();
            }
            else {
                // Create new file
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet(fileName);
                // Create a header row
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Match action");
                header.createCell(1).setCellValue("ResumeId");
                header.createCell(2).setCellValue("Resume Title");
                header.createCell(3).setCellValue("JobPostId");
                header.createCell(4).setCellValue("JobPost Title");
                header.createCell(5).setCellValue("Gemma Overall");
                header.createCell(6).setCellValue("Nomic Overall");
                header.createCell(7).setCellValue("Gemma Title");
                header.createCell(8).setCellValue("Nomic Title");
                header.createCell(9).setCellValue("Gemma Desc");
                header.createCell(10).setCellValue("Nomic Desc");
                header.createCell(11).setCellValue("Gemma Resp");
                header.createCell(12).setCellValue("Nomic Resp");
                header.createCell(13).setCellValue("Gemma Edu");
                header.createCell(14).setCellValue("Nomic Edu");
                header.createCell(15).setCellValue("Gemma Exp");
                header.createCell(16).setCellValue("Nomic Exp");
                header.createCell(17).setCellValue("Gemma Skills");
                header.createCell(18).setCellValue("Nomic Skills");
                header.createCell(19).setCellValue("Gemma AddInfo");
                header.createCell(20).setCellValue("Nomic AddInfo");
            }

            // Append new row at the end
            int lastRowNum = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRowNum + 1);

            for(Map<String, String> dataMap : dataList){
                String[] rowData = dataMap.values().toArray(new String[0]);
                for (int i = 0; i < rowData.length; i++) {
                    newRow.createCell(i).setCellValue(rowData[i]);
                }

                lastRowNum = sheet.getLastRowNum();
                newRow = sheet.createRow(lastRowNum + 1);
            }

            // Write back to file
            FileOutputStream fos = new FileOutputStream(fileName);
            workbook.write(fos);
            fos.close();
            workbook.close();
            log.info("Rows added to {}", fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


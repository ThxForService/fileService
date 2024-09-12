package com.thxforservice.exelfile.services;

import com.thxforservice.exelfile.entities.Excel1;
import com.thxforservice.exelfile.repositories.Excel1Repository;
import com.thxforservice.global.exceptions.CommonException;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


import static javax.swing.UIManager.getString;

@Service
@RequiredArgsConstructor
public class ExcelUploadService {

    private final Excel1Repository excel1Repository;

 @Transactional
    public void excelToDb() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/files/test.xlsx")){
            if (inputStream == null) {
                throw new FileNotFoundException("Excel file not found.");
            }

             try( BufferedInputStream bis = new BufferedInputStream(inputStream);
             OPCPackage opcPackage = OPCPackage.open(bis)) {

            Workbook workbook = new XSSFWorkbook(opcPackage);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue; // 빈 행이 있으면 건너뜀
                }
                Cell numCell = row.getCell(0);  // No. 열 (첫 번째 열)
                Cell qCell = row.getCell(1);    // Q 열 (두 번째 열)

                // 각 셀 값이 null이 아닌지 확인
                if (numCell == null || qCell == null) {
                    continue; // 셀이 비어 있으면 건너뜀
                }

                // 각 셀 값을 가져옴
                Long num = getLong(numCell);  // No. 값
                String q = getString(qCell);  // Q 값

                Excel1 excel1 = new Excel1();
                excel1.setNum(num);
                excel1.setQ(q);

                try {
                    excel1Repository.saveAndFlush(excel1);
                    System.out.println("Inserted: " + num + ", " + q);
                } catch (Exception e) {
                    throw new CommonException("Failed to save Excel data to database.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

                 workbook.close(); // workbook 자원 닫기
             } catch (InvalidFormatException e) {
                 throw new CommonException("Invalid format of the Excel file.", HttpStatus.BAD_REQUEST);
             } catch (IOException e) {
                 throw new CommonException("Failed to read the Excel file.", HttpStatus.INTERNAL_SERVER_ERROR);
             }
        } catch (IOException e) {
            throw new CommonException("Failed to access the Excel file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
 }

    private Long getLong(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC || DateUtil.isCellDateFormatted(cell)) {
            return null;
        }
        return (long) cell.getNumericCellValue();
    }

    private String getString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }
}
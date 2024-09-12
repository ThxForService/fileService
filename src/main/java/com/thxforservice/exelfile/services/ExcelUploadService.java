package com.thxforservice.exelfile.services;

import com.thxforservice.exelfile.entities.Excel1;
import com.thxforservice.exelfile.repositories.Excel1Repository;
import com.thxforservice.global.exceptions.CommonException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.*;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;

@Service
@RequiredArgsConstructor
public class ExcelUploadService {

    private final Excel1Repository excel1Repository;

    @Transactional
    public void excelToDb() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/files/test.xlsx")) {
            if (inputStream == null) {
                throw new CommonException("Excel 파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
            }

            try (BufferedInputStream bis = new BufferedInputStream(inputStream);
                 OPCPackage opcPackage = OPCPackage.open(bis);
                 Workbook workbook = new XSSFWorkbook(opcPackage)) {

                Sheet sheet = workbook.getSheetAt(0);

                for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 첫 번째 행은 헤더라고 가정하고 건너뜁니다.
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue; // 빈 행을 건너뜁니다.
                    }
                    Cell numCell = row.getCell(0);  // 첫 번째 열 (No. 열)
                    Cell qCell = row.getCell(1);    // 두 번째 열 (Q 열)

                    // 셀 값이 null인지 확인
                    if (numCell == null || qCell == null) {
                        continue; // 빈 셀을 건너뜁니다.
                    }

                    // 셀 값 가져오기
                    Long num = getLong(numCell);  // No. 값
                    String q = getString(qCell);  // Q 값

                    Excel1 excel1 = new Excel1();
                    excel1.setNum(num);
                    excel1.setQ(q);

                    workbook.close(); // 워크북 자원 닫기

                    try {
                        excel1Repository.save(excel1); // 엔티티 저장
                        System.out.println("저장됨: " + num + ", " + q);
                    } catch (Exception e) {
                        // 예외 세부 사항 로그 출력
                        e.printStackTrace();
                        throw new CommonException("Excel 데이터를 데이터베이스에 저장하는데 실패했습니다. 오류: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                }


            } catch (InvalidFormatException e) {
                throw new CommonException("Excel 파일의 형식이 잘못되었습니다.", HttpStatus.BAD_REQUEST);
            } catch (IOException e) {
                throw new CommonException("Excel 파일을 읽는 데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            throw new CommonException("Excel 파일에 접근하는 데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
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
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}

//km 지쨩 버전
//                 try {
//                     excel1Repository.save(excel1); // 엔티티 저장
//                     System.out.println("Inserted: " + num + ", " + q);
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                     throw new RuntimeException("Failed to save entity: " + excel1, e); // 예외를 명시적으로 던지기
//                 }
//             }
//         } catch (InvalidFormatException e) {
//             e.printStackTrace();
//             throw new RuntimeException("Invalid format in the Excel file.", e); // 예외를 명시적으로 던지기
//         } catch (IOException e) {
//             e.printStackTrace();
//             throw new RuntimeException("Failed to process Excel file.", e); // 예외를 명시적으로 던지기
//         }
//     } catch (FileNotFoundException e) {
//         e.printStackTrace();
//         throw new RuntimeException("Excel file not found.", e); // 예외를 명시적으로 던지기
//     }
// }
//
//    private Long getLong(Cell cell) {
//        if (cell == null) {
//            return null;
//        }
//
//        if (cell.getCellType() == CellType.NUMERIC) {
//            if (DateUtil.isCellDateFormatted(cell)) {
//                throw new IllegalArgumentException("Date format not supported for Long.");
//            }
//            return (long) cell.getNumericCellValue();
//        }
//
//        return null;
//    }
//
//    private String getString(Cell cell) {
//        if (cell == null) {
//            return "";
//        }
//
//        switch (cell.getCellType()) {
//            case STRING:
//                return cell.getStringCellValue();
//            case NUMERIC:
//                return String.valueOf(cell.getNumericCellValue());
//            case BOOLEAN:
//                return String.valueOf(cell.getBooleanCellValue());
//            default:
//                return "";
//        }
//    }
//}
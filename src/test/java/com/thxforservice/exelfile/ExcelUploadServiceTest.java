package com.thxforservice.exelfile;


import com.thxforservice.exelfile.entities.Excel1;
import com.thxforservice.exelfile.repositories.Excel1Repository;
import com.thxforservice.exelfile.services.ExcelUploadService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class ExcelUploadServiceTest {

    @Autowired
    private ExcelUploadService excelUploadService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private Excel1Repository excel1Repository;


    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("cors.allow.origins", () -> "http://localhost:3000"); // 테스트용 도메인 설정
    }


    @Test
    @Transactional
    void testExcelToDb() {
        try {
        // 실제 데이터베이스에 Excel 데이터를 삽입
        excelUploadService.excelToDb();

        // 트랜잭션 커밋을 강제
        entityManager.flush();

        // 데이터베이스에 저장된 결과를 검증
        Iterable<Excel1> excel1s = excel1Repository.findAll();
        assertNotNull(excel1s);
        assertTrue(excel1s.iterator().hasNext(), "데이터베이스에 데이터가 삽입되지 않았습니다.");
        for (Excel1 excel1 : excel1s) {
            System.out.println("Saved entity: " + excel1);
        }
    } catch (Exception e) {
        e.printStackTrace();
        fail("테스트 도중 예외가 발생했습니다: " + e.getMessage());
    }
}}

package com.thxforservice.exelfile;


import com.thxforservice.exelfile.repositories.Excel1Repository;
import com.thxforservice.exelfile.services.ExcelUploadService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

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


    @Test
    @Transactional
    @Commit
    void testExcelToDb() {

            excelUploadService.excelToDb();

  }
}
package kr.hhplus.be.support.dummy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;

//@ActiveProfiles("dummy")
@SpringBootTest
class DummyDataServiceTest {

    @Autowired
    DummyDataService dummyDataService;

    @Test
    @Commit
    void initializeData() throws InterruptedException, SQLException {
        dummyDataService.initializeData();
    }
}

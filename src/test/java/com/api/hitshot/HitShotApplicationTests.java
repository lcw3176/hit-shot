package com.api.hitshot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.data.mongodb.uri=mongodb://localhost:27017/hitshot_test",
        "spring.data.mongodb.auto-index-creation=false"
})
class HitShotApplicationTests {

    @Test
    void contextLoads() {
    }

}

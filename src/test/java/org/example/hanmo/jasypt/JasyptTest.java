
package org.example.hanmo.jasypt;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JasyptTest {

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    StringEncryptor stringEncryptor;

    @Test
    void encrypt() {
        String encrypt = stringEncryptor.encrypt("secret-key");
        System.out.println("encrypt = " + encrypt);
    }
}
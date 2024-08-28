package board.myboard.global.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class SecurityConfigTest {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    private static String PASSWORD = "수민SUMIN";

    @Test
    public void 패스워드_암호화() throws Exception {
        //given

        //when
        String encodePassword = bCryptPasswordEncoder.encode(PASSWORD);

        //then
        assertThat(encodePassword).isNotEqualTo(PASSWORD);
    }

    @Test
    public void 패스워드_랜덤_암호화() throws Exception {
        //given

        //when
        String encode1 = bCryptPasswordEncoder.encode(PASSWORD);
        String encode2 = bCryptPasswordEncoder.encode(PASSWORD);

        //then
        assertThat(encode1).isNotEqualTo(encode2);
    }

    @Test
    public void 암호화된_비밀번호_매치() throws Exception {
        //given

        //when
        String encodePassword = bCryptPasswordEncoder.encode(PASSWORD);

        //then
        assertThat(bCryptPasswordEncoder.matches(PASSWORD, encodePassword)).isTrue();
    }

}
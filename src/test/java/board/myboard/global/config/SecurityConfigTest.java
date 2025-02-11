package board.myboard.global.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class SecurityConfigTest {

    @Autowired
    PasswordEncoder bCryptPasswordEncoder;

    private static final String PASSWORD = "수민SUMIN";

    @Test
    public void 패스워드_암호화() {
        String encodePassword = bCryptPasswordEncoder.encode(PASSWORD);
        assertThat(encodePassword).isNotEqualTo(PASSWORD);
    }

    @Test
    public void 패스워드_랜덤_암호화() {
        String encode1 = bCryptPasswordEncoder.encode(PASSWORD);
        String encode2 = bCryptPasswordEncoder.encode(PASSWORD);
        assertThat(encode1).isNotEqualTo(encode2);
    }

    @Test
    public void 암호화된_비밀번호_매치() {
        String encodePassword = bCryptPasswordEncoder.encode(PASSWORD);
        assertThat(bCryptPasswordEncoder.matches(PASSWORD, encodePassword)).isTrue();
    }
//
    @Test
    public void 잘못된_비밀번호_매치_실패() {
        String encodePassword = bCryptPasswordEncoder.encode(PASSWORD);
        String wrongPassword = "wrongPassword";
        assertThat(bCryptPasswordEncoder.matches(wrongPassword, encodePassword)).isFalse();
    }

    @Test
    public void 빈_문자열_비밀번호_테스트() {
        String emptyPassword = "";
        String encodePassword = bCryptPasswordEncoder.encode(emptyPassword);
        assertThat(bCryptPasswordEncoder.matches(emptyPassword, encodePassword)).isTrue();
    }

    @Test
    public void null_비밀번호_예외처리() {
        assertThatThrownBy(() -> bCryptPasswordEncoder.encode(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
//
//    @Test
//    public void 긴_문자열_비밀번호_암호화_및_매칭() {
//        String longPassword = "ThisIsAVeryLongPasswordWithMultipleCharacters1234567890!@#";
//        String encodePassword = bCryptPasswordEncoder.encode(longPassword);
//        assertThat(bCryptPasswordEncoder.matches(longPassword, encodePassword)).isTrue();
//    }
//}

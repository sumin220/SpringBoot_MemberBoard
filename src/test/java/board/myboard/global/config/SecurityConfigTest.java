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

    @Test
    public void 긴_문자열_비밀번호_암호화_및_매칭() {
        String longPassword = "ThisIsAVeryLongPasswordWithMultipleCharacters1234567890!@#";
        String encodePassword = bCryptPasswordEncoder.encode(longPassword);
        assertThat(bCryptPasswordEncoder.matches(longPassword, encodePassword)).isTrue();
    }

    @Test
    public void 공백이_포함된_비밀번호_테스트() {
        String passwordWithSpaces = "  spaceTest  ";
        String encodePassword = bCryptPasswordEncoder.encode(passwordWithSpaces);
        assertThat(bCryptPasswordEncoder.matches(passwordWithSpaces, encodePassword)).isTrue();
    }

    @Test
    public void 특수문자_비밀번호_테스트() {
        String specialCharPassword = "!@#$%^&*()_+-=";
        String encodePassword = bCryptPasswordEncoder.encode(specialCharPassword);
        assertThat(bCryptPasswordEncoder.matches(specialCharPassword, encodePassword)).isTrue();
    }

    @Test
    public void 숫자만_포함된_비밀번호_테스트() {
        String numericPassword = "1234567890";
        String encodePassword = bCryptPasswordEncoder.encode(numericPassword);
        assertThat(bCryptPasswordEncoder.matches(numericPassword, encodePassword)).isTrue();
    }

    @Test
    public void 대소문자_구분_테스트() {
        String upperCasePassword = "PASSWORD";
        String lowerCasePassword = "password";
        String encodePassword = bCryptPasswordEncoder.encode(upperCasePassword);
        assertThat(bCryptPasswordEncoder.matches(lowerCasePassword, encodePassword)).isFalse();
    }

    @Test
    public void 같은_비밀번호_여러번_암호화_다른_결과_확인() {
        String encode1 = bCryptPasswordEncoder.encode(PASSWORD);
        String encode2 = bCryptPasswordEncoder.encode(PASSWORD);
        assertThat(encode1).isNotEqualTo(encode2);
    }
//
//    @Test
//    public void 암호화된_비밀번호의_길이_확인() {
//        String encodePassword = bCryptPasswordEncoder.encode(PASSWORD);
//        assertThat(encodePassword.length()).isGreaterThan(50); // BCrypt는 일반적으로 60자 이상
//    }
//
//    @Test
//    public void 짧은_비밀번호_암호화_및_매칭() {
//        String shortPassword = "pw";
//        String encodePassword = bCryptPasswordEncoder.encode(shortPassword);
//        assertThat(bCryptPasswordEncoder.matches(shortPassword, encodePassword)).isTrue();
//    }
//
//    @Test
//    public void UTF8_문자열_비밀번호_테스트() {
//        String utf8Password = "パスワード安全";
//        String encodePassword = bCryptPasswordEncoder.encode(utf8Password);
//        assertThat(bCryptPasswordEncoder.matches(utf8Password, encodePassword)).isTrue();
//    }
//
//    @Test
//    public void 연속된_숫자_비밀번호_테스트() {
//        String sequentialNumbers = "123456789";
//        String encodePassword = bCryptPasswordEncoder.encode(sequentialNumbers);
//        assertThat(bCryptPasswordEncoder.matches(sequentialNumbers, encodePassword)).isTrue();
//    }
//
//    @Test
//    public void 해시된_비밀번호를_직접_비교하면_실패() {
//        String encodePassword = bCryptPasswordEncoder.encode(PASSWORD);
//        assertThat(PASSWORD.equals(encodePassword)).isFalse();
//    }

}

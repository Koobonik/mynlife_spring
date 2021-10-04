package xyz.pwmw.mynlife.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class PasswordEncoding implements PasswordEncoder {
   private final PasswordEncoder passwordEncoder;

   public PasswordEncoding() {
      this.passwordEncoder = new BCryptPasswordEncoder();
   }

//   public PasswordEncoding(PasswordEncoder passwordEncoder) {
//      this.passwordEncoder = passwordEncoder;
//   }

   @Override
   public String encode(CharSequence rawPassword) {
      return passwordEncoder.encode(rawPassword);
   }

   @Override
   public boolean matches(CharSequence rawPassword, String encodedPassword) {
      return passwordEncoder.matches(rawPassword, encodedPassword);
   }
}
package ru.netology.cloudstorage;

import lombok.AllArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.repository.UserRepository;

import javax.transaction.Transactional;

import static ru.netology.cloudstorage.enums.ApplicationUserRole.ADMIN;
import static ru.netology.cloudstorage.enums.ApplicationUserRole.USER;

@SpringBootApplication
@AllArgsConstructor
public class CloudStorageDiplomaApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CloudStorageDiplomaApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run();
    }

    @Override
    @Transactional
    public void run(String... args) {
        userRepository.save(
                UserEntity.builder().username("user")
                        .password(passwordEncoder.encode("user"))
                        .role(USER)
                        .grantedAuthorities(USER.getGrantedAuthorities())
                        .build()
        );

        userRepository.save(
                UserEntity.builder().username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .role(ADMIN)
                        .grantedAuthorities(ADMIN.getGrantedAuthorities())
                        .build()
        );
    }
}
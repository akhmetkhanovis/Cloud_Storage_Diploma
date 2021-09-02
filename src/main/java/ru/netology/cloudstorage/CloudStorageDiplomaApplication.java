package ru.netology.cloudstorage;

import lombok.AllArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.repository.UserRepository;

import javax.transaction.Transactional;

import static ru.netology.cloudstorage.enums.ApplicationUserRole.*;

@SpringBootApplication
@Component
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
                new UserEntity("user",
                        passwordEncoder.encode("password"),
                        USER,
                        USER.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true));

        userRepository.save(new UserEntity("admin",
                passwordEncoder.encode("admin"),
                ADMIN,
                ADMIN.getGrantedAuthorities(),
                true,
                true,
                true,
                true));
    }
}
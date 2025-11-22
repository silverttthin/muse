package org.siwoong.muse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MuseApplication {

    public static void main(String[] args) {
        SpringApplication.run(MuseApplication.class, args);
    }

}

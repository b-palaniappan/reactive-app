package io.c12.bala.react;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@Log4j2
@SpringBootApplication
public class ReactiveAppApplication {

    // Enable BlockHound... Java agent to detect blocking calls from non-blocking threads.
    // Only works with JDK 11.
    static {
        BlockHound.install();
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveAppApplication.class, args);
    }

}

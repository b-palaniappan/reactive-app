package io.c12.bala.react;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

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

package org.reactor.demo;

import org.reactor.demo.models.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        Flux<Users> names = Flux.just("Santiago Armijos", "Diana Samaniego", "Valentina Castro", "Xavier Caicedo", "Alex Manga", "Maria Perez", "Juan Condoy")
                .map(name -> new Users(name.split(" ")[0].toUpperCase(), name.split(" ")[1].toUpperCase()))
                .filter(userName -> userName.getName().equalsIgnoreCase("santiago"))
                .doOnNext(users -> {
                    if (users.getName().isEmpty()) {
                        throw new RuntimeException("Empty name");
                    }
                    System.out.println(users.toString());
                }).map(user -> {
                    user.setName(user.getName().toLowerCase());
                    return user;
                });
        //Runable es una operación que se puede realizar al finalizar la operacion con hilos
        names.subscribe(user -> log.info(user.toString()), error -> log.error(error.getMessage()), new Runnable() {
            @Override
            public void run() {
                log.info("Se ha finalizado el programa");
            }
        });
    }
}

package org.reactor.demo;

import org.reactor.demo.models.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        exampleCollectList();
    }

    public void exampleSubscribe() throws Exception {
        Flux<String> names = Flux.just("Santiago Armijos", "Diana Samaniego", "Valentina Castro", "Xavier Caicedo", "Alex Manga", "Maria Perez", "Juan Condoy");
        Flux<Users> usersFlux = names
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
        usersFlux.subscribe(user -> log.info(user.toString()), error -> log.error(error.getMessage()), new Runnable() {
            @Override
            public void run() {
                log.info("Se ha finalizado el programa");
            }
        });
    }

    public void exampleFlatMap() throws Exception {
        List<String> usersList = new ArrayList<>();
        usersList.add("John  Doe");
        usersList.add("Santiago Armijos");
        usersList.add("Diana Samaniego");
        usersList.add("Valentina Castro");
        usersList.add("Xavier Caicedo");
        usersList.add("Alex Manga");
        usersList.add("Maria Perez");
        usersList.add("Juan Condoy");
        Flux<String> names = Flux.fromIterable(usersList);
        Flux<Users> usersFlux = names
                .map(name -> new Users(name.split(" ")[0].toUpperCase(), name.split(" ")[1].toUpperCase()))
                .flatMap(users -> {
                    if (users.getName().equalsIgnoreCase("Santiago")) {
                        return Mono.just(users);
                    } else {
                        return Mono.empty();
                    }
                })
                .map(user -> {
                    user.setName(user.getName().toLowerCase());
                    return user;
                });
        //Runable es una operación que se puede realizar al finalizar la operacion con hilos
        usersFlux.subscribe(user -> log.info(user.toString()));
    }

    public void exampleFlatMapUser() throws Exception {
        List<Users> usersList = new ArrayList<>();
        usersList.add(new Users("John", "Doe"));
        usersList.add(new Users("Santiago", "Armijos"));
        usersList.add(new Users("Diana", "Samaniego"));
        usersList.add(new Users("Valentina", "Castro"));
        usersList.add(new Users("Xavier", "Caicedo"));
        usersList.add(new Users("Alex", "Manga"));
        usersList.add(new Users("Maria", "Perez"));
        usersList.add(new Users("Juan", "Condoy"));

        Flux<Users> usersFlux = Flux.fromIterable(usersList)
                .map(name -> new Users(name.getName().toUpperCase(), name.getLastName().toUpperCase()))
                .flatMap(users -> {
                    if (users.getName().equalsIgnoreCase("Santiago")) {
                        return Mono.just(users);
                    } else {
                        return Mono.empty();
                    }
                })
                .map(user -> {
                    user.setName(user.getName().toLowerCase());
                    return user;
                });
        //Runable es una operación que se puede realizar al finalizar la operacion con hilos
        usersFlux.subscribe(user -> log.info(user.toString()));
    }

    public void exampleCollectList() throws Exception {
        List<Users> usersList = new ArrayList<>();
        usersList.add(new Users("John", "Doe"));
        usersList.add(new Users("Santiago", "Armijos"));
        usersList.add(new Users("Diana", "Samaniego"));
        usersList.add(new Users("Valentina", "Castro"));
        usersList.add(new Users("Xavier", "Caicedo"));
        usersList.add(new Users("Alex", "Manga"));
        usersList.add(new Users("Maria", "Perez"));
        usersList.add(new Users("Juan", "Condoy"));

        Flux.fromIterable(usersList)
                .collectList().subscribe(listUser -> {
                    listUser.forEach(l -> log.info(l.toString()));
                });
    }
}

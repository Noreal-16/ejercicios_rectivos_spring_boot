package org.reactor.demo;

import org.reactor.demo.models.CommentUser;
import org.reactor.demo.models.Comments;
import org.reactor.demo.models.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        exampleCreateTimer();
    }

    public void exampleCreateTimer() {
        Flux.create(emitter -> {
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        private Integer counter = 0;

                        @Override
                        public void run() {
                            emitter.next(++counter);
                            if (counter == 10) {
                                emitter.isCancelled();
                                emitter.complete();
                            }
                        }
                    }, 1000, 1000);
                })
                .subscribe(message -> log.info(message.toString()),
                        error -> log.error(error.toString()),
                        () -> log.info("Finalizado"));
        ;
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

    public void commentUser() throws Exception {
        Mono<Users> usersMono = Mono.fromCallable(() -> new Users("John", "Doe"));
        Mono<Comments> commentsMono = Mono.fromCallable(() -> {
            Comments comment = new Comments();
            comment.addComment("Hola este es el primer comentario");
            comment.addComment("Hola este es el segundo comentario");
            comment.addComment("Hola este es el tercer comentario");
            return comment;
        });

        usersMono.flatMap(user -> commentsMono.map(comments -> new CommentUser(user, comments)))
                .subscribe(commentUser -> log.info(commentUser.toString()));
    }

    public void commentUserZipWith() throws Exception {
        Mono<Users> usersMono = Mono.fromCallable(() -> new Users("John", "Doe"));
        Mono<Comments> commentsMono = Mono.fromCallable(() -> {
            Comments comment = new Comments();
            comment.addComment("Hola este es el primer comentario");
            comment.addComment("Hola este es el segundo comentario");
            comment.addComment("Hola este es el tercer comentario");
            return comment;
        });

        Mono<CommentUser> commentUserMono = usersMono.zipWith(commentsMono, CommentUser::new);
        commentUserMono.subscribe(user -> log.info(user.toString()));
    }

    public void commentUserZipWith2() throws Exception {
        Mono<Users> usersMono = Mono.fromCallable(() -> new Users("John", "Doe"));
        Mono<Comments> commentsMono = Mono.fromCallable(() -> {
            Comments comment = new Comments();
            comment.addComment("Hola este es el primer comentario");
            comment.addComment("Hola este es el segundo comentario");
            comment.addComment("Hola este es el tercer comentario");
            return comment;
        });

        Mono<CommentUser> commentUserMono = usersMono.zipWith(commentsMono).map(commentUsr -> {
            Users user = commentUsr.getT1();
            Comments comment = commentUsr.getT2();
            return new CommentUser(user, comment);
        });
        commentUserMono.subscribe(user -> log.info(user.toString()));
    }

    public void commentRangeZipWith() throws Exception {
        Flux.just(1, 2, 3, 4, 5)
                .map(item -> item * 2)
                .zipWith(Flux.range(0, 5),
                        (one, thou) -> String.format("Primer valor %d, segundo valor %d", one, thou))
                .subscribe(log::info);
    }

    public void commentRangeDelay() throws Exception {
        Flux<Integer> range = Flux.range(1, 12);
        Flux<Long> delay = Flux.interval(Duration.ofSeconds(1));
        range.zipWith(delay, (ra, de) -> ra)
                .doOnNext(message -> log.info(message.toString()))
                .blockLast();
    }

    public void commentRangeDelay2() throws Exception {
        Flux<Integer> range = Flux.range(1, 12)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(message -> log.info(message.toString()));

        range.subscribe();
        Thread.sleep(12000);
    }

    public void commentRangeRetry() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux.interval(Duration.ofSeconds(1))
                .doOnTerminate(countDownLatch::countDown)
                .flatMap(duration -> {
                    if (duration >= 5) {
                        return Flux.error(new InterruptedException("Solo se puede llegar hasta 5"));
                    }
                    return Flux.just(duration);
                }).map(message -> "Hola " + message.toString())
                .retry(2)
                .subscribe(log::info, error -> log.error(error.getMessage()));

        countDownLatch.await();
    }
}

package com.example.demo;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
//@Validated
public class BookController {

    private final List<Book> books = new ArrayList<>();
    private final Sinks.Many<Book> sink = Sinks.many().multicast().onBackpressureBuffer();

    @QueryMapping
    public List<Book> getBooks() {
        return books;
    }

    @MutationMapping
    public Book addBook(@Argument CreateBookInput createBookInput) {
        Book newBook = new Book(UUID.randomUUID().toString(), createBookInput.getTitle(), createBookInput.getAuthor());
        books.add(newBook);
        sink.tryEmitNext(newBook);
        return newBook;
    }

    @SubscriptionMapping
    public Flux<Book> subscribeToBooks(@Argument String authorFilter) {
        return sink.asFlux()
                .filter(book -> book.getAuthor().contains(authorFilter));
    }
}

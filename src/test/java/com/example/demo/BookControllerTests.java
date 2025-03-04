package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@GraphQlTest(BookController.class)
@ImportAutoConfiguration(GraphQLValidationConfig.class)
public class BookControllerTests {

    @Autowired
    private GraphQlTester graphQlTester;

    @Test
    void testAddBookMutation() {
        String mutation = """
                    mutation {
                        addBook(createBookInput: { title: "War of the Words", author: "H.G. Wells" }) {
                            id
                            title
                            author
                        }
                    }
                """;
        graphQlTester.document(mutation)
                .execute()
                .path("addBook")
                .entity(Book.class)
                .satisfies(book -> {
                    assertThat(book.getId()).isNotNull();
                    assertThat(book.getTitle()).isEqualTo("War of the Words");
                    assertThat(book.getAuthor()).isEqualTo("H.G. Wells");
                });

        String query = """
                    query {
                        getBooks {
                            id
                            title
                            author
                        }
                    }
                """;
        graphQlTester.document(query)
                .execute()
                .path("getBooks")
                .entityList(Book.class)
                .hasSize(1) // Assuming the addBookMutation test has run and added a book
                .satisfies(books -> {
                    assert books.get(0).getTitle().equals("War of the Words");
                    assert books.get(0).getAuthor().equals("H.G. Wells");
                });

    }

    @Test
    public void testAddBookWithShortTitle() {
        String mutation = """
            mutation {
                addBook(createBookInput: { title: "1984", author: "George Orwell" }) {
                    id
                    title
                    author
                }
            }
        """;

        graphQlTester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors.size()).isEqualTo(1);
                    assertThat(errors.get(0).getMessage()).isEqualTo("/addBook/createBookInput/title size must be between 5 and 50");
                });
    }

    @Test
    public void testSubscribeToBooks() {
        // First, add a book to ensure there is data to subscribe to
        String addBookMutation = """
                    mutation {
                        addBook(createBookInput: { title: "Some book title", author: "Some Author" }) {
                            id
                            title
                            author
                        }
                    }
                """;

        graphQlTester.document(addBookMutation)
                .execute()
                .path("addBook")
                .entity(Book.class)
                .satisfies(book -> {
                    assert book.getTitle().equals("Some book title");
                    assert book.getAuthor().equals("Some Author");
                });

        // Now, test the subscription
        String subscription = """
                    subscription {
                        subscribeToBooks(authorFilter: "Some Author") {
                            id
                            title
                            author
                        }
                    }
                """;

        graphQlTester.document(subscription)
                .executeSubscription()
                .toFlux("subscribeToBooks", Book.class)
                .take(1)
                .doOnNext(book -> {
                    assert book.getTitle().equals("Some book title");
                    assert book.getAuthor().equals("Some Author");
                })
                .blockLast();
    }

    @Test
    public void testSubscribeToBooksWithShortFilter() {

        // First, add a book to ensure there is data to subscribe to
        String addBookMutation = """
                    mutation {
                        addBook(createBookInput: { title: "Some book title", author: "Some Author" }) {
                            id
                            title
                            author
                        }
                    }
                """;

        graphQlTester.document(addBookMutation)
                .execute()
                .path("addBook")
                .entity(Book.class)
                .satisfies(book -> {
                    assert book.getTitle().equals("Some book title");
                    assert book.getAuthor().equals("Some Author");
                });

        String subscription = """
            subscription {
                subscribeToBooks(authorFilter: "Some") {
                    id
                    title
                    author
                }
            }
        """;

        graphQlTester.document(subscription)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors.size()).isEqualTo(1);
                    assertThat(errors.get(0).getMessage()).contains("/subscribeToBooks/authorFilter size must be between 5 and 50");
                });

    }
}

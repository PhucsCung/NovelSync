package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AuthorTestSamples.*;
import static com.mycompany.myapp.domain.BookTestSamples.*;
import static com.mycompany.myapp.domain.CategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Book.class);
        Book book1 = getBookSample1();
        Book book2 = new Book();
        assertThat(book1).isNotEqualTo(book2);

        book2.setId(book1.getId());
        assertThat(book1).isEqualTo(book2);

        book2 = getBookSample2();
        assertThat(book1).isNotEqualTo(book2);
    }

    @Test
    void categoryTest() {
        Book book = getBookRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        book.setCategory(categoryBack);
        assertThat(book.getCategory()).isEqualTo(categoryBack);

        book.category(null);
        assertThat(book.getCategory()).isNull();
    }

    @Test
    void authorTest() {
        Book book = getBookRandomSampleGenerator();
        Author authorBack = getAuthorRandomSampleGenerator();

        book.setAuthor(authorBack);
        assertThat(book.getAuthor()).isEqualTo(authorBack);

        book.author(null);
        assertThat(book.getAuthor()).isNull();
    }
}

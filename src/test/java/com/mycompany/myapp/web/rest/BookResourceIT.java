package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.BookAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Author;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.Category;
import com.mycompany.myapp.repository.BookRepository;
import com.mycompany.myapp.service.BookService;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BookResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BookResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_ISBN = "AAAAAAAAAA";
    private static final String UPDATED_ISBN = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(0 - 1);

    private static final Integer DEFAULT_STOCK_QUANTITY = 0;
    private static final Integer UPDATED_STOCK_QUANTITY = 1;
    private static final Integer SMALLER_STOCK_QUANTITY = 0 - 1;

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/books";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookRepository bookRepository;

    @Mock
    private BookRepository bookRepositoryMock;

    @Mock
    private BookService bookServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookMockMvc;

    private Book book;

    private Book insertedBook;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createEntity() {
        return new Book()
            .title(DEFAULT_TITLE)
            .isbn(DEFAULT_ISBN)
            .price(DEFAULT_PRICE)
            .stockQuantity(DEFAULT_STOCK_QUANTITY)
            .imageUrl(DEFAULT_IMAGE_URL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createUpdatedEntity() {
        return new Book()
            .title(UPDATED_TITLE)
            .isbn(UPDATED_ISBN)
            .price(UPDATED_PRICE)
            .stockQuantity(UPDATED_STOCK_QUANTITY)
            .imageUrl(UPDATED_IMAGE_URL);
    }

    @BeforeEach
    void initTest() {
        book = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBook != null) {
            bookRepository.delete(insertedBook);
            insertedBook = null;
        }
    }

    @Test
    @Transactional
    void createBook() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Book
        var returnedBook = om.readValue(
            restBookMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Book.class
        );

        // Validate the Book in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBookUpdatableFieldsEquals(returnedBook, getPersistedBook(returnedBook));

        insertedBook = returnedBook;
    }

    @Test
    @Transactional
    void createBookWithExistingId() throws Exception {
        // Create the Book with an existing ID
        book.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        book.setTitle(null);

        // Create the Book, which fails.

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        book.setPrice(null);

        // Create the Book, which fails.

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStockQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        book.setStockQuantity(null);

        // Create the Book, which fails.

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBooks() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].stockQuantity").value(hasItem(DEFAULT_STOCK_QUANTITY)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBooksWithEagerRelationshipsIsEnabled() throws Exception {
        when(bookServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bookServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBooksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bookServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bookRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBook() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get the book
        restBookMockMvc
            .perform(get(ENTITY_API_URL_ID, book.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(book.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.isbn").value(DEFAULT_ISBN))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.stockQuantity").value(DEFAULT_STOCK_QUANTITY))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL));
    }

    @Test
    @Transactional
    void getBooksByIdFiltering() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        Long id = book.getId();

        defaultBookFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBookFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBookFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where title equals to
        defaultBookFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where title in
        defaultBookFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where title is not null
        defaultBookFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where title contains
        defaultBookFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where title does not contain
        defaultBookFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByIsbnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn equals to
        defaultBookFiltering("isbn.equals=" + DEFAULT_ISBN, "isbn.equals=" + UPDATED_ISBN);
    }

    @Test
    @Transactional
    void getAllBooksByIsbnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn in
        defaultBookFiltering("isbn.in=" + DEFAULT_ISBN + "," + UPDATED_ISBN, "isbn.in=" + UPDATED_ISBN);
    }

    @Test
    @Transactional
    void getAllBooksByIsbnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn is not null
        defaultBookFiltering("isbn.specified=true", "isbn.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByIsbnContainsSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn contains
        defaultBookFiltering("isbn.contains=" + DEFAULT_ISBN, "isbn.contains=" + UPDATED_ISBN);
    }

    @Test
    @Transactional
    void getAllBooksByIsbnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where isbn does not contain
        defaultBookFiltering("isbn.doesNotContain=" + UPDATED_ISBN, "isbn.doesNotContain=" + DEFAULT_ISBN);
    }

    @Test
    @Transactional
    void getAllBooksByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where price equals to
        defaultBookFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where price in
        defaultBookFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where price is not null
        defaultBookFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where price is greater than or equal to
        defaultBookFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where price is less than or equal to
        defaultBookFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where price is less than
        defaultBookFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where price is greater than
        defaultBookFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByStockQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where stockQuantity equals to
        defaultBookFiltering("stockQuantity.equals=" + DEFAULT_STOCK_QUANTITY, "stockQuantity.equals=" + UPDATED_STOCK_QUANTITY);
    }

    @Test
    @Transactional
    void getAllBooksByStockQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where stockQuantity in
        defaultBookFiltering(
            "stockQuantity.in=" + DEFAULT_STOCK_QUANTITY + "," + UPDATED_STOCK_QUANTITY,
            "stockQuantity.in=" + UPDATED_STOCK_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllBooksByStockQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where stockQuantity is not null
        defaultBookFiltering("stockQuantity.specified=true", "stockQuantity.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByStockQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where stockQuantity is greater than or equal to
        defaultBookFiltering(
            "stockQuantity.greaterThanOrEqual=" + DEFAULT_STOCK_QUANTITY,
            "stockQuantity.greaterThanOrEqual=" + UPDATED_STOCK_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllBooksByStockQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where stockQuantity is less than or equal to
        defaultBookFiltering(
            "stockQuantity.lessThanOrEqual=" + DEFAULT_STOCK_QUANTITY,
            "stockQuantity.lessThanOrEqual=" + SMALLER_STOCK_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllBooksByStockQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where stockQuantity is less than
        defaultBookFiltering("stockQuantity.lessThan=" + UPDATED_STOCK_QUANTITY, "stockQuantity.lessThan=" + DEFAULT_STOCK_QUANTITY);
    }

    @Test
    @Transactional
    void getAllBooksByStockQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where stockQuantity is greater than
        defaultBookFiltering("stockQuantity.greaterThan=" + SMALLER_STOCK_QUANTITY, "stockQuantity.greaterThan=" + DEFAULT_STOCK_QUANTITY);
    }

    @Test
    @Transactional
    void getAllBooksByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where imageUrl equals to
        defaultBookFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBooksByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where imageUrl in
        defaultBookFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBooksByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where imageUrl is not null
        defaultBookFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where imageUrl contains
        defaultBookFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBooksByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        // Get all the bookList where imageUrl does not contain
        defaultBookFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBooksByCategoryIsEqualToSomething() throws Exception {
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            bookRepository.saveAndFlush(book);
            category = CategoryResourceIT.createEntity();
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        em.persist(category);
        em.flush();
        book.setCategory(category);
        bookRepository.saveAndFlush(book);
        Long categoryId = category.getId();
        // Get all the bookList where category equals to categoryId
        defaultBookShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the bookList where category equals to (categoryId + 1)
        defaultBookShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    @Test
    @Transactional
    void getAllBooksByAuthorIsEqualToSomething() throws Exception {
        Author author;
        if (TestUtil.findAll(em, Author.class).isEmpty()) {
            bookRepository.saveAndFlush(book);
            author = AuthorResourceIT.createEntity();
        } else {
            author = TestUtil.findAll(em, Author.class).get(0);
        }
        em.persist(author);
        em.flush();
        book.setAuthor(author);
        bookRepository.saveAndFlush(book);
        Long authorId = author.getId();
        // Get all the bookList where author equals to authorId
        defaultBookShouldBeFound("authorId.equals=" + authorId);

        // Get all the bookList where author equals to (authorId + 1)
        defaultBookShouldNotBeFound("authorId.equals=" + (authorId + 1));
    }

    private void defaultBookFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBookShouldBeFound(shouldBeFound);
        defaultBookShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookShouldBeFound(String filter) throws Exception {
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].stockQuantity").value(hasItem(DEFAULT_STOCK_QUANTITY)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)));

        // Check, that the count call also returns 1
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookShouldNotBeFound(String filter) throws Exception {
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBook() throws Exception {
        // Get the book
        restBookMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBook() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the book
        Book updatedBook = bookRepository.findById(book.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBook are not directly saved in db
        em.detach(updatedBook);
        updatedBook
            .title(UPDATED_TITLE)
            .isbn(UPDATED_ISBN)
            .price(UPDATED_PRICE)
            .stockQuantity(UPDATED_STOCK_QUANTITY)
            .imageUrl(UPDATED_IMAGE_URL);

        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBook.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBookToMatchAllProperties(updatedBook);
    }

    @Test
    @Transactional
    void putNonExistingBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        book.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(put(ENTITY_API_URL_ID, book.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        book.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        book.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(book)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookWithPatch() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        partialUpdatedBook.stockQuantity(UPDATED_STOCK_QUANTITY);

        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBook, book), getPersistedBook(book));
    }

    @Test
    @Transactional
    void fullUpdateBookWithPatch() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        partialUpdatedBook
            .title(UPDATED_TITLE)
            .isbn(UPDATED_ISBN)
            .price(UPDATED_PRICE)
            .stockQuantity(UPDATED_STOCK_QUANTITY)
            .imageUrl(UPDATED_IMAGE_URL);

        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookUpdatableFieldsEquals(partialUpdatedBook, getPersistedBook(partialUpdatedBook));
    }

    @Test
    @Transactional
    void patchNonExistingBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        book.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(patch(ENTITY_API_URL_ID, book.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(book)))
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        book.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        book.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(book)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBook() throws Exception {
        // Initialize the database
        insertedBook = bookRepository.saveAndFlush(book);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the book
        restBookMockMvc
            .perform(delete(ENTITY_API_URL_ID, book.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bookRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Book getPersistedBook(Book book) {
        return bookRepository.findById(book.getId()).orElseThrow();
    }

    protected void assertPersistedBookToMatchAllProperties(Book expectedBook) {
        assertBookAllPropertiesEquals(expectedBook, getPersistedBook(expectedBook));
    }

    protected void assertPersistedBookToMatchUpdatableProperties(Book expectedBook) {
        assertBookAllUpdatablePropertiesEquals(expectedBook, getPersistedBook(expectedBook));
    }
}

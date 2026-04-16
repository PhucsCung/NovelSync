package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.repository.BookRepository;
import com.mycompany.myapp.service.BookService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Book}.
 */
@Service
@Transactional
public class BookServiceImpl implements BookService {

    private static final Logger LOG = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        LOG.debug("Request to save Book : {}", book);
        return bookRepository.save(book);
    }

    @Override
    public Book update(Book book) {
        LOG.debug("Request to update Book : {}", book);
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> partialUpdate(Book book) {
        LOG.debug("Request to partially update Book : {}", book);

        return bookRepository
            .findById(book.getId())
            .map(existingBook -> {
                if (book.getTitle() != null) {
                    existingBook.setTitle(book.getTitle());
                }
                if (book.getIsbn() != null) {
                    existingBook.setIsbn(book.getIsbn());
                }
                if (book.getPrice() != null) {
                    existingBook.setPrice(book.getPrice());
                }
                if (book.getStockQuantity() != null) {
                    existingBook.setStockQuantity(book.getStockQuantity());
                }
                if (book.getImageUrl() != null) {
                    existingBook.setImageUrl(book.getImageUrl());
                }

                return existingBook;
            })
            .map(bookRepository::save);
    }

    public Page<Book> findAllWithEagerRelationships(Pageable pageable) {
        return bookRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findOne(Long id) {
        LOG.debug("Request to get Book : {}", id);
        return bookRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Book : {}", id);
        bookRepository.deleteById(id);
    }
}

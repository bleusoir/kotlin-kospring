package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTests @Autowired constructor(
    private val bookService: BookService,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    @AfterEach
    fun clean() {
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("책 저장 정상")
    fun saveBookTest() {
        // given
        val request = BookRequest("TEST BOOK")

        // when
        bookService.saveBook(request)

        // then
        val books = bookRepository.findAll()
        assertThat(books).hasSize(1)
        assertThat(books[0].name).isEqualTo("TEST BOOK")
    }

    @Test
    @DisplayName("책 대출 정상")
    fun loanBookTest() {
        // given
        bookRepository.save(Book("TEST BOOK"))
        val savedUser = userRepository.save(User("A", null, mutableListOf(), null))
        val req = BookLoanRequest("A", "TEST BOOK")

        // when
        bookService.loanBook(req)

        // then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].bookName).isEqualTo("TEST BOOK")
        assertThat(results[0].user.id).isEqualTo(savedUser.id)
        assertThat(results[0].isReturn).isFalse
    }

    @Test
    @DisplayName("책 대출시, 신규 대출 실패")
    fun loanBookFailTest() {

        // given
        bookRepository.save(Book("TEST BOOK"))
        val savedUser = userRepository.save(User("A", null, mutableListOf(), null))
        userLoanHistoryRepository.save(UserLoanHistory(savedUser, "TEST BOOK", false))
        val req = BookLoanRequest("A", "TEST BOOK")

        // when & then
        val message = assertThrows<IllegalArgumentException> {
            bookService.loanBook(req)
        }.message
        assertThat(message).isEqualTo("진작 대출되어 있는 책입니다")
    }

    @Test
    @DisplayName("책 반납 정상 동작")
    fun returnBookTest() {

        // given
        bookRepository.save(Book("TEST BOOK"))
        val savedUser = userRepository.save(User("A", null, mutableListOf(), null))
        userLoanHistoryRepository.save(UserLoanHistory(savedUser, "TEST BOOK", false))
        val req = BookReturnRequest("A", "TEST BOOK")

        // when
        bookService.returnBook(req)

        // when
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].isReturn).isTrue
    }
}
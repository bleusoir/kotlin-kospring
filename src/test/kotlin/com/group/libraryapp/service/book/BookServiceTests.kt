package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.book.BookType
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.dto.book.response.BookStatResponse
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTests @Autowired constructor(
    private val bookService: BookService,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @AfterEach
    fun clean() {
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("책 저장 정상")
    fun saveBookTest() {

        // given
        val request = BookRequest("TEST BOOK", BookType.COMPUTER)

        // when
        bookService.saveBook(request)

        // then
        val books = bookRepository.findAll()
        assertThat(books).hasSize(1)
        assertThat(books[0].name).isEqualTo("TEST BOOK")
        assertThat(books[0].type).isEqualTo(BookType.COMPUTER)
    }

    @Test
    @DisplayName("책 대출 정상")
    fun loanBookTest() {

        // given
        bookRepository.save(Book.fixture("TEST BOOK"))
        val savedUser = userRepository.save(User("A", null, mutableListOf(), null))
        val req = BookLoanRequest("A", "TEST BOOK")

        // when
        bookService.loanBook(req)

        // then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].bookName).isEqualTo("TEST BOOK")
        assertThat(results[0].user.id).isEqualTo(savedUser.id)
        assertThat(results[0].status).isEqualTo(UserLoanStatus.LOANED)
    }

    @Test
    @DisplayName("책 대출시, 신규 대출 실패")
    fun loanBookFailTest() {

        // given
        bookRepository.save(Book.fixture("TEST BOOK"))
        val savedUser = userRepository.save(User("A", null, mutableListOf(), null))
        userLoanHistoryRepository.save(UserLoanHistory.fixture(savedUser, "TEST BOOK"))
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
        bookRepository.save(Book.fixture("TEST BOOK"))
        val savedUser = userRepository.save(User("A", null, mutableListOf(), null))
        userLoanHistoryRepository.save(UserLoanHistory.fixture(savedUser, "TEST BOOK"))
        val req = BookReturnRequest("A", "TEST BOOK")

        // when
        bookService.returnBook(req)

        // then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].status).isEqualTo(UserLoanStatus.RETURNED)
    }

    @Test
    @DisplayName("책 대여 권수 정상 확인")
    fun countLoanedBookTest() {

        // given
        val savedUser = userRepository.save(User("A", 20))
        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(savedUser, "A_BOOK"),
                UserLoanHistory.fixture(savedUser, "B_BOOK", UserLoanStatus.RETURNED),
                UserLoanHistory.fixture(savedUser, "C_BOOK", UserLoanStatus.RETURNED),
            )
        )

        // when
        val result = bookService.countLoanedBook()

        // then
        assertThat(result).isEqualTo(1)
    }

    @Test
    @DisplayName("분야별 책 통계 정상 동작")
    fun getBookStatisticsTest() {

        // given
        bookRepository.save(Book.fixture("TEST#1", BookType.SCIENCE))
        bookRepository.save(Book.fixture("TEST#2", BookType.COMPUTER))
        bookRepository.save(Book.fixture("TEST#3", BookType.COMPUTER))

        // when
        val results = bookService.getBookStatistics()

        // then
        assertThat(results).hasSize(2)
        assertCount(results, BookType.SCIENCE, 1L)
        assertCount(results, BookType.COMPUTER, 2L)
    }

    private fun assertCount(results: List<BookStatResponse>, type: BookType, count: Long) {
        assertThat(results.first { it.type == type }.count).isEqualTo(count)
    }
}
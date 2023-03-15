package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class UserServiceTests @Autowired constructor(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @AfterEach
    fun clean() {
        userRepository.deleteAll()
    }

    @Test
    @Transactional
    @DisplayName("유저 저장 정상 동작")
    open fun saveUserTest() {

        // given
        val req = UserCreateRequest("JY", null)

        // when
        userService.saveUser(req)

        // then
        val results = userRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].age).isNull()
    }

    @Test
    @DisplayName("사용자 조회")
    fun getUsersTest() {

        // given
        userRepository.saveAll(
            listOf(
                User("A", 20, mutableListOf(), null),
                User("B", null, mutableListOf(), null),
            )
        )

        // when
        val results = userService.getUsers()

        // then
        assertThat(results).hasSize(2)
        assertThat(results).extracting("name").containsExactlyInAnyOrder("A", "B")
        assertThat(results).extracting("age").containsExactlyInAnyOrder(20, null)
    }

    @Test
    @DisplayName("사용자 이름 수정")
    fun updateUserNameTest() {
        // given
        val savedUser = userRepository.save(User("A", null, mutableListOf(), null))
        val req = UserUpdateRequest(savedUser.id!!, "B")

        // when
        userService.updateUserName(req)

        // then
        val result = userRepository.findAll()[0]
        assertThat(result.name).isEqualTo("B")
    }

    @Test
    @DisplayName("유저 삭제 정상 동작")
    fun deleteUserTest() {

        // given
        userRepository.save(User("A", null, mutableListOf(), null))

        // when
        userService.deleteUser("A")

        // then
        assertThat(userRepository.findAll()).isEmpty()
    }

    @Test
    @DisplayName("대출 기록이 없는 사용자의 응답이 정상 동작")
    fun getEmptyUserLoanHistoryTest() {

        // given
        userRepository.save(User("A", null))

        // when
        val results = userService.getUserLoanHistories()

        // then
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("A")
        assertThat(results[0].books).isEmpty()
    }

    @Test
    @DisplayName("대출 기록이 많은 사용자의 응답이 정상 동작")
    fun getManyUserLoanHistoriesTest() {

        // given
        val savedUsers = userRepository.saveAll(listOf(User("B", null), User("C", null)))
        val savedUser = savedUsers.first { it.name == "B" }
        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(savedUser, "BOOK#1", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUser, "BOOK#2", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUser, "BOOK#3", UserLoanStatus.RETURNED),
                UserLoanHistory.fixture(savedUser, "BOOK#4", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUser, "BOOK#5", UserLoanStatus.RETURNED),
            )
        )

        // when
        val results = userService.getUserLoanHistories()

        // then
        assertThat(results).hasSize(2)
        assertThat(results[0].name).isEqualTo("B")
        assertThat(results[0].books).hasSize(5)
        assertThat(results[0].books).extracting("name")
            .containsExactlyInAnyOrder("BOOK#1", "BOOK#2", "BOOK#3", "BOOK#4", "BOOK#5")
        assertThat(results[0].books).extracting("isReturn")
            .containsExactlyInAnyOrder(false, false, true, false, true)
    }
}
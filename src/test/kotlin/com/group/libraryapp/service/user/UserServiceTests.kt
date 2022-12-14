package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class UserServiceTests @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService,
) {

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
}
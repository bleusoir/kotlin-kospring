package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.dto.user.request.UserCreateRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
open class UserServiceTests @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService,
) {

    @Test
    @Transactional
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
}
package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

 public class UserMapperTest {

     @Test
     void userToDto() {
         User user = new User();
         user.setId(1L);
         user.setName("John");
         user.setEmail("john.doe@mail.com");
         UserDto userDto = UserMapper.userToDto(user);
         Assertions.assertNotNull(userDto);
         Assertions.assertEquals(user.getId(), userDto.getId());
         Assertions.assertEquals(user.getName(), userDto.getName());
         Assertions.assertEquals(user.getEmail(), userDto.getEmail());
     }

     @Test
     void dtoToUser() {
         UserDto userDto = new UserDto();
         userDto.setId(1L);
         userDto.setName("John");
         userDto.setEmail("john.doe@mail.com");
         User user = UserMapper.dtoToUser(userDto);
         Assertions.assertEquals(userDto.getId(), user.getId());
         Assertions.assertEquals(userDto.getName(), user.getName());
         Assertions.assertEquals(userDto.getEmail(), user.getEmail());
     }
 }
package no.war.habr.service.dto;

import no.war.habr.persist.model.User;

public interface UserMapper {

    UserDto fromUser(User user);
}

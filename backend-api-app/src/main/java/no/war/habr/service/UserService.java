package no.war.habr.service;

import no.war.habr.persist.model.EUserCondition;
import no.war.habr.service.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;

import java.util.Optional;

public interface UserService {

    Page<UserDto> findAll(Optional<String> username,
                          Optional<String> firstName,
                          Optional<String> lastName,
                          Optional<EUserCondition> condition,
                          Integer page,
                          Integer size,
                          String sortField,
                          Direction direction);

    Optional<UserDto> findById(long id);

    Optional<UserDto> findByUsername(String username);

    void deleteById(Long id);

    UserDto save(UserDto user);
}

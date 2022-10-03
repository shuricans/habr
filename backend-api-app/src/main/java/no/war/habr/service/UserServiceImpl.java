package no.war.habr.service;

import lombok.RequiredArgsConstructor;
import no.war.habr.persist.model.EUserCondition;
import no.war.habr.persist.model.User;
import no.war.habr.persist.repository.UserRepository;
import no.war.habr.persist.specification.UserSpecification;
import no.war.habr.service.dto.UserDto;
import no.war.habr.service.dto.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static no.war.habr.util.SpecificationUtils.combineSpec;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Page<UserDto> findAll(Optional<String> username,
                                 Optional<String> firstName,
                                 Optional<String> lastName,
                                 Optional<EUserCondition> condition,
                                 Integer page,
                                 Integer size,
                                 String sortField,
                                 Direction direction) {
        Specification<User> spec = null;

        if (username.isPresent() && !username.get().isBlank()) {
            spec = Specification.where(UserSpecification.usernameLike(username.get()));
        }
        if (firstName.isPresent() && !firstName.get().isBlank()) {
            spec = combineSpec(spec, UserSpecification.firstNameLike(firstName.get()));
        }
        if (lastName.isPresent() && !lastName.get().isBlank()) {
            spec = combineSpec(spec, UserSpecification.lastNameLike(lastName.get()));
        }
        if (condition.isPresent()) {
            spec = combineSpec(spec, UserSpecification.condition(condition.get()));
        }
        spec = combineSpec(spec, Specification.where(null));

        return userRepository
                .findAll(spec, PageRequest.of(page, size, Sort.by(direction, sortField)))
                .map(userMapper::fromUser);
    }

    @Override
    public Optional<UserDto> findById(long id) {
        return userRepository.findById(id).map(userMapper::fromUser);
    }

    @Override
    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::fromUser);
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public UserDto save(UserDto user) {
        return null;
    }
}

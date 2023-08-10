package com.github.jonataslaet.laetcatalog.services.exceptions;

import com.github.jonataslaet.laetcatalog.controllers.dtos.UserInsertDTO;
import com.github.jonataslaet.laetcatalog.entities.User;
import com.github.jonataslaet.laetcatalog.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserInsertValid ann) {
    }

    @Override
    public boolean isValid(UserInsertDTO userInsertDTO, ConstraintValidatorContext context) {

        List<CustomFieldError> customFieldErrors = new ArrayList<>();

        User user = userRepository.findByEmail(userInsertDTO.getEmail());
        if (Objects.nonNull(user)) {
            customFieldErrors.add(new CustomFieldError("email", "O email precisa de ser único"));
        }

        for (CustomFieldError e : customFieldErrors) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getName())
                    .addConstraintViolation();
        }
        return customFieldErrors.isEmpty();
    }
}

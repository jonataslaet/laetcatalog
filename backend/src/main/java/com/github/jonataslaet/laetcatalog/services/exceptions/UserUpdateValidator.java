package com.github.jonataslaet.laetcatalog.services.exceptions;

import com.github.jonataslaet.laetcatalog.controllers.dtos.UserUpdateDTO;
import com.github.jonataslaet.laetcatalog.entities.User;
import com.github.jonataslaet.laetcatalog.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserUpdateValid ann) {
    }

    @Override
    public boolean isValid(UserUpdateDTO userUpdateDTO, ConstraintValidatorContext context) {

        List<CustomFieldError> customFieldErrors = new ArrayList<>();

        User user = userRepository.findByEmail(userUpdateDTO.getEmail());
        if (Objects.nonNull(user) && !Objects.equals(user.getId(), getUserIdFromRequest())) {
            customFieldErrors.add(new CustomFieldError("email", "O email precisa de ser único"));
        }

        for (CustomFieldError e : customFieldErrors) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getName())
                    .addConstraintViolation();
        }
        return customFieldErrors.isEmpty();
    }

    private Long getUserIdFromRequest() {
        var uriVars = (Map<String, String>) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long userId = Long.parseLong(uriVars.get("id"));
        return userId;
    }
}

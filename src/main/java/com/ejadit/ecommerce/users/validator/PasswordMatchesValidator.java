package com.ejadit.ecommerce.users.validator;

import com.ejadit.ecommerce.users.dto.UserRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRequestDto> {

    @Override
    public boolean isValid(UserRequestDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        String password = dto.getPassword();
        String confirm = dto.getConfirmPassword();

        // Let other validators handle null/blank
        if (password == null || confirm == null) {
            return true;
        }

        boolean matches = password.equals(confirm);
        if (!matches) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{user.password.mismatch}")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return matches;
    }
}

package com.ejadit.ecommerce.users.dto;

import com.ejadit.ecommerce.users.entity.UserType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRequestDto {

    @JsonCreator
    public UserRequestDto(
            @JsonProperty("name") String name,
            @JsonProperty("userName") String userName,
            @JsonProperty("password") String password,
            @JsonProperty("confirmPassword") String confirmPassword,
            @JsonProperty("email") String email,
            @JsonProperty("userType") UserType userType,
            @JsonProperty("mobileNumber") String mobileNumber) {
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.email = email;
        this.userType = userType;
        this.mobileNumber = mobileNumber;
    }

    @NotBlank(message = "الاسم مطلوب ولا يمكن أن يكون فارغاً")
    private final String name;

    @NotBlank(message = "اسم المستخدم مطلوب ولا يمكن أن يكون فارغاً")
    private final String userName;

    @NotBlank(message = "كلمة المرور مطلوبة ولا يمكن أن تكون فارغة")
    private final String password;

    @NotBlank(message = "تأكيد كلمة المرور مطلوب ولا يمكن أن يكون فارغاً")
    private final String confirmPassword;

    @NotBlank(message = "البريد الإلكتروني مطلوب ولا يمكن أن يكون فارغاً")
    @Email(message = "يرجى إدخال بريد إلكتروني صحيح")
    private final String email;

    private final UserType userType;

    @NotBlank(message = "رقم الهاتف مطلوب ولا يمكن أن يكون فارغاً")
    private final String mobileNumber;
}

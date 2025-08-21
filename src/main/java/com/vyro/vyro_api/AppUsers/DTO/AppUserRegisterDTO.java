package com.vyro.vyro_api.AppUsers.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppUserRegisterDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public boolean isAllFieldsNotNull()
    {
        return (this.firstName != null) && (this.lastName != null) && (this.email != null) && (this.password != null);
    }
}

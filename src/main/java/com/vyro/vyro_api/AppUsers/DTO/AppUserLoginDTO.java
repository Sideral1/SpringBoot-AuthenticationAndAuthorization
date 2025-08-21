package com.vyro.vyro_api.AppUsers.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppUserLoginDTO {
    private String email;
    private String password;

    public boolean isAllFieldsNotNull()
    {
        return (this.email != null) && (this.password != null);
    }
}

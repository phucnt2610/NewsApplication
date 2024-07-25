package hcmute.kltn.backend.dto;

import lombok.Data;

import hcmute.kltn.backend.entity.enum_entity.Role;
import java.util.Date;

@Data
public class UserDTO {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Date dob;
    private String avatar;
    private Role role;
}

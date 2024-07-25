package hcmute.kltn.backend.dto.response;

import hcmute.kltn.backend.dto.UserDTO;
import lombok.Data;

@Data
public class JwtAuthResponse {
    private UserDTO user;
    private String token;
}

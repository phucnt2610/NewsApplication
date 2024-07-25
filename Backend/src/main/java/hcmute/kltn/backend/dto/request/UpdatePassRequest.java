package hcmute.kltn.backend.dto.request;

import lombok.Data;

@Data
public class UpdatePassRequest {
    private String oldPassword;
    private String newPassword;
    private String reEnterPassword;
}

package hcmute.kltn.backend.service;

import hcmute.kltn.backend.dto.response.JwtAuthResponse;
import hcmute.kltn.backend.dto.request.SignInRequest;
import hcmute.kltn.backend.dto.request.SignUpRequest;
import hcmute.kltn.backend.entity.User;

public interface AuthService {
    User signUp(SignUpRequest signUpRequest);
    JwtAuthResponse signIn(SignInRequest signInRequest);

}

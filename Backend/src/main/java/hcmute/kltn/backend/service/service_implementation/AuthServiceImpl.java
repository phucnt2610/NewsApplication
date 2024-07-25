package hcmute.kltn.backend.service.service_implementation;

import hcmute.kltn.backend.dto.UserDTO;
import hcmute.kltn.backend.dto.response.JwtAuthResponse;
import hcmute.kltn.backend.dto.request.SignInRequest;
import hcmute.kltn.backend.dto.request.SignUpRequest;
import hcmute.kltn.backend.entity.enum_entity.Role;
import hcmute.kltn.backend.entity.User;
import hcmute.kltn.backend.repository.UserRepo;
import hcmute.kltn.backend.service.AuthService;
import hcmute.kltn.backend.service.JwtService;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    @Value("${DEFAULT_AVATAR}")
    private String defaultAvatar;

    @Override
    public User signUp(SignUpRequest signUpRequest) {
        User user = new User();

        if (userRepo.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email đã tồn tại trên hệ thống.");
        } else {
            user.setFirstname(signUpRequest.getFirstname());
            user.setLastname(signUpRequest.getLastname());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setDob(signUpRequest.getDob());
            user.setAvatar(signUpRequest.getAvatar());
            user.setRole(Role.USER);
            user.setAvatar(defaultAvatar);
        }

        return userRepo.save(user);
    }

    @Override
    public JwtAuthResponse signIn(SignInRequest signInRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(),
                    signInRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new RuntimeException("Email hoặc mật khẩu không hợp lệ.");
        }

        var user = userRepo.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không hợp lệ."));


        var jwtToken = jwtService.generateToken(user);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setUser(userDTO);
        jwtAuthResponse.setToken(jwtToken);
        return jwtAuthResponse;
    }


}

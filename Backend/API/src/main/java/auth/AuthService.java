package auth;

import com.example.localhistory.user.UserRepository;
import com.example.localhistory.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public AuthService(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    // TODO: Add logic for sign up, login, logout ( invalidate tokens ), and refresh login

}

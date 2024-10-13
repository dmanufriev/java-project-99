package hexlet.code.service;

import hexlet.code.util.JWTUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public String getToken(String username, String password) {
        var authentication = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authentication);
        var token = jwtUtils.generateToken(username);
        return token;
    }

}

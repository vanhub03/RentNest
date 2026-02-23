package com.example.rentnest.controller;


import com.example.rentnest.enums.Role;
import com.example.rentnest.model.User;
import com.example.rentnest.model.dto.request.LoginRequest;
import com.example.rentnest.model.dto.request.SignupRequest;
import com.example.rentnest.model.dto.response.JwtResponse;
import com.example.rentnest.model.dto.response.MessageResponse;
import com.example.rentnest.security.JwtUtils;
import com.example.rentnest.security.TokenBlacklistService;
import com.example.rentnest.security.UserDetailsImpl;
import com.example.rentnest.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        //xac thuc tai khoan, mat khau
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //tao jwt token
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getAuthorities().toString()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest){
        if(userService.existsByUsername(signupRequest.getUsername())){
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if(userService.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body("Email already exists");
        }
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        String strRole = signupRequest.getRole();
        if("LANDLORD".equals(strRole)){
            user.setRole(Role.LANDLORD);
        }else {
            user.setRole(Role.TENANT);
        }
        userService.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");
        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")){
            String jwt = headerAuth.substring(7);
            long expirationMs = jwtUtils.getExpirationDateFromJwtToken(jwt);
            if(expirationMs > 0){
                tokenBlacklistService.blacklistToken(jwt, expirationMs);
            }
        }
        return ResponseEntity.ok(new MessageResponse("User logged out successfully"));
    }
}

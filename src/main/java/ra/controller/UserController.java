package ra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ra.jwt.JwtTokenProvider;
import ra.model.entity.ERole;
import ra.model.entity.Roles;
import ra.model.entity.Users;
import ra.model.service.RoleService;
import ra.model.service.UserService;
import ra.payload.reponse.JwtResponse;
import ra.payload.reponse.MessageResponse;
import ra.payload.request.LoginRequest;
import ra.payload.request.SignupRequest;
import ra.sercurity.CustomUserDetails;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/v1/auth")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder encoder;
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if (userService.existsByUserName(signupRequest.getUserName())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Usermame is already"));
        }
        if (userService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already"));
        }
        Users user = new Users();
        user.setUserName(signupRequest.getUserName());
        user.setUserPassword(encoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        user.setPhone(signupRequest.getPhone());
        user.setUserStatus(true);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dateNow = new Date();
        String strNow = sdf.format(dateNow);
        try {
            user.setCreatedDate(sdf.parse(strNow));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        Set<String> strRoles = signupRequest.getListRoles();
        Set<Roles> listRoles = new HashSet<>();
        if (strRoles==null){
            //User quyen mac dinh
            Roles userRole = roleService.findByRoleName(ERole.ROLE_USER).orElseThrow(()->new RuntimeException("Error: Role is not found"));
            listRoles.add(userRole);
//            System.out.println(listRoles);
        }else {
            strRoles.forEach(role->{
                switch (role){
                    case "admin":
                        Roles adminRole = roleService.findByRoleName(ERole.ROLE_ADMIN)
                                .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                        listRoles.add(adminRole);
                        break;
                    case "moderator":
                        Roles modRole = roleService.findByRoleName(ERole.ROLE_MODERATOR)
                                .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                        listRoles.add(modRole);
                        break;
                    case "user":
                        Roles userRole = roleService.findByRoleName(ERole.ROLE_USER)
                                .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                        listRoles.add(userRole);
                        break;
                }
            });
        }
        user.setListRoles(listRoles);
        userService.saveOrUpdate(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }
    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(),loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails customUserDetail = (CustomUserDetails) authentication.getPrincipal();
        //Sinh JWT tra ve client
        String jwt = tokenProvider.generateToken(customUserDetail);
        //Lay cac quyen cua user
        List<String> listRoles = customUserDetail.getAuthorities().stream()
                .map(item->item.getAuthority()).collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt,customUserDetail.getUsername(),customUserDetail.getEmail(),
                customUserDetail.getPhone(),listRoles));
    }
}

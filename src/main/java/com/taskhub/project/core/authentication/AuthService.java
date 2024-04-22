package com.taskhub.project.core.authentication;

import com.taskhub.project.aspect.exception.ServerException;
import com.taskhub.project.aspect.exception.model.ErrorsData;
import com.taskhub.project.comon.service.model.ServiceResult;
import com.taskhub.project.core.authentication.dtos.DetailsAppUserDTO;
import com.taskhub.project.core.authentication.entities.ConfirmToken;
import com.taskhub.project.core.authentication.model.*;
import com.taskhub.project.core.authentication.repo.ConfirmTokenRepo;
import com.taskhub.project.core.board.repo.WorkSpaceMemberRepo;
import com.taskhub.project.core.email.EmailSender;
import com.taskhub.project.core.helper.validator.ValidatorService;
import com.taskhub.project.core.user.constans.UserStatus;
import com.taskhub.project.core.user.entities.AppUser;
import com.taskhub.project.core.user.repo.UserRepo;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private static final int CONFIRM_TOKEN_LENGTH = 6;
    private static final int TOKEN_EXPIRED_MINUTE = 15;
    private final ConfirmTokenRepo tokenRepo;
    private final UserRepo userRepo;
    private final WorkSpaceMemberRepo boardMemberRepo;

    private final JWTService jwtService;
    private final ValidatorService validator;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailSender emailSender;


    public AuthenticationResponse register(RegisterRequest request) throws AuthenticationException {
        validator.tryValidate(request)
                .withConstraint(
                        () -> userRepo.existByUsername(request.getUsername()),
                        ErrorsData.of("username", "username.exists", "Username already exists")
                )
                .withConstraint(
                        () -> userRepo.existByEmail(request.getEmail()),
                        ErrorsData.of("email", "email.used", "Email has been use")
                )
                .message("AuthService.register")
                .throwIfFails();


        // ValidateExceptionBuilder exceptionBuilder = new ValidateExceptionBuilder();
        //
        // exceptionBuilder.addFieldError(ValidatorUtils.validate(request));
        //
        // if (userRepo.existByUsername(request.getUsername())) {
        //     exceptionBuilder.addFieldError("username", "username.exists", "Username already exists");
        // }
        // if (userRepo.existByEmail(request.getEmail())) {
        //     exceptionBuilder.addFieldError("email", "email.used", "Email has been use");
        // }
        //
        // if (!exceptionBuilder.isEmptyError()) {
        //     throw exceptionBuilder
        //             .message("AuthService.register")
        //             .build();
        // }

        AppUser user = AppUser.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.Inactive.name())
                .build();

        ConfirmToken token = generateConfirmToken(user);
        log.info(token.getToken());

        try {
            userRepo.save(user);
            tokenRepo.save(token);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        var userId = userRepo.getIdByUsername(request.getUsername()).getId();

        return generateJWTToken(userId);
    }

    @Transactional
    public void sendConfirmToken(String userID) {
        ConfirmToken dbToken = tokenRepo.findByAppUser_Id(userID).orElse(null);

        if (dbToken == null) {
            throw new ServerException("AuthService.sendConfirmToken");
        }

        tokenRepo.delete(dbToken);
        tokenRepo.flush();


        ConfirmToken token = generateConfirmToken(userRepo.getReferenceById(userID));
        tokenRepo.save(token);
        log.info("new token send: " + token.getToken());

        String email = userRepo.getEmailById(userID).getEmail();

        emailSender.send(email, token.getToken());
    }

    //TODO FIX THIS
    // @Transactional(noRollbackFor = TokenAttemptsException.class)
    public boolean validateEmailToken(String requestToken, String userID) {

        // ConfirmToken dbToken = tokenRepo.findByAppUser_Id(userID).get().orElseThrow(() -> new TokenAttemptsException("Please resend token"));
        //
        // if (!dbToken.getToken().equals(requestToken)) {
        //     if (dbToken.getAttempts() > 5) {
        //         tokenRepo.delete(dbToken);
        //         throw new TokenAttemptsException("Please resend token");
        //     }
        //     dbToken.setAttempts(dbToken.getAttempts() + 1);
        //     return false;
        // }
        //
        // userService.setUserActive(userID);
        // tokenRepo.delete(dbToken);
        return true;
    }

    private static ConfirmToken generateConfirmToken(AppUser user) {

        StringBuilder sb = new StringBuilder(CONFIRM_TOKEN_LENGTH);
        new Random().ints(CONFIRM_TOKEN_LENGTH, 0, 10).forEach(sb::append);

        return ConfirmToken.builder()
                .token(sb.toString())
                .createAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(TOKEN_EXPIRED_MINUTE))
                .attempts(0)
                .appUser(user)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws AuthenticationException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var id = userRepo.getIdByUsername(request.getUsername()).getId();

        return generateJWTToken(id);
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws MalformedJwtException {

        final var refreshToken = jwtService.decodeToken(request.getRefreshToken());

        var user = DetailsAppUserDTO.builder().id(refreshToken.getUserId()).build();

        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(request.getRefreshToken())
                .build();
    }

    private AuthenticationResponse generateJWTToken(String userId) {
        if (!userRepo.existsById(userId)) {
            throw new ServerException("AuthService.generateJWTToken");
        }

        var user = DetailsAppUserDTO.builder().id(userId).build();

        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request, String userId) throws AuthenticationException {
        var user = userRepo.findById(userId).orElse(null);

        if (user == null) {
            throw new ServerException("AuthService.changePassword");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        request.getOldPassword()
                )
        );

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepo.save(user);
    }

    public ServiceResult<BoardAuthorResponse> authorBoard(BoardAuthorRequest request) {
        var token = jwtService.decodeToken(request.getAccessToken());
        if (token == null) { // SHOULD RE MOVE ?
            throw new ServerException("AuthService.authorBoard");
        }

        var boardMember = boardMemberRepo
                .findByWorkspaceIdAndUserId(request.getBoardId(), token.getUserId())
                .orElse(null);

        if (boardMember == null) {
            throw new ServerException("AuthService.authorBoard");
        }

        // get role

        // get action

        // build role details

        // generate token


        return null;
    }
}

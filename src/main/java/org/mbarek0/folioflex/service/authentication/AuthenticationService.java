package org.mbarek0.folioflex.service.authentication;

import jakarta.validation.Valid;
import org.mbarek0.folioflex.web.exception.userExs.UserNameAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.userExs.UsernameOrPasswordInvalidException;
import org.mbarek0.folioflex.web.vm.request.RegisterVM;
import org.mbarek0.folioflex.web.vm.response.TokenVM;

public interface AuthenticationService {

    TokenVM register(@Valid RegisterVM registerVM, String clientOrigin) throws UserNameAlreadyExistsException;

    TokenVM login(String username, String password) throws UsernameOrPasswordInvalidException;

    TokenVM refresh(String refreshToken);

    void verifyEmail(String token);

    void forgotPassword(String email, String clientOrigin);

    void resetPassword(String token, String newPassword);
}
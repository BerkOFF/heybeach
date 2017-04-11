package de.daimler.heybeach.auth;

import de.daimler.heybeach.error.BackendException;
import de.daimler.heybeach.model.User;
import de.daimler.heybeach.service.UsersService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Optional;

public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private TokenService tokenService;
    private UsersService usersService;

    public UsernamePasswordAuthenticationProvider(TokenService tokenService, UsersService usersService) {
        this.tokenService = tokenService;
        this.usersService = usersService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<String> username = (Optional)authentication.getPrincipal();
        Optional<String> password = (Optional)authentication.getCredentials();

        if (!username.isPresent() || !password.isPresent()) {
            throw new BadCredentialsException("Missing User Credentials");
        }

        try {

            Optional<User> user = usersService.getUser(username.get());
            if(!user.isPresent()) {
                throw new UsernameNotFoundException("User not found: " + username.get());
            }

            if (!user.get().getPassword().equals(password.get())) {
                throw new BadCredentialsException("Invalid User Credentials");
            }

            PreAuthenticatedAuthenticationToken resultOfAuthentication =
                    new PreAuthenticatedAuthenticationToken(user.get().getId(), null,
                            AuthorityUtils.createAuthorityList(user.get().getRole().name()));
            String newToken = tokenService.generateNewToken();
            resultOfAuthentication.setDetails(newToken);
            tokenService.store(newToken, resultOfAuthentication);

            return resultOfAuthentication;
        } catch (BackendException exc) {
            throw new AuthenticationServiceException("Unexpected error occurred", exc);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

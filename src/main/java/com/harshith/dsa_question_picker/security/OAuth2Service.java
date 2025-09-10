package com.harshith.dsa_question_picker.security;

import com.harshith.dsa_question_picker.model.OAuth2Provider;
import com.harshith.dsa_question_picker.model.User;
import com.harshith.dsa_question_picker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2Service extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getName();

        String email;
        String name = null;
        String picture;

        if (provider.equals(OAuth2Provider.google.toString())) {
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
            picture = oAuth2User.getAttribute("picture");
        } else if (provider.equals(OAuth2Provider.github.toString())) {
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
            if (name == null) {
                name = oAuth2User.getAttribute("login");
            }
            picture = oAuth2User.getAttribute("avatar_url");
        } else {
            picture = null;
            email = null;
        }


        String finalName = name;
        User user = userRepository
                .findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    // create new user
                    User newUser = User.builder()
                            .id(UUID.randomUUID())
                            .email(email)
                            .name(finalName)
                            .pictureUrl(picture)
                            .provider(provider)
                            .providerId(providerId)
                            .build();
                    return userRepository.save(newUser);
                });
        user.setName(name);
        user.setEmail(email);
        user.setPictureUrl(picture);
        userRepository.save(user);

        return new CustomUserPrinciple(user, oAuth2User.getAttributes());
    }
}
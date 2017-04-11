package de.daimler.heybeach.util;

import de.daimler.heybeach.model.UserRole;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.beans.FeatureDescriptor;
import java.util.UUID;
import java.util.stream.Stream;

public class Helpers {

    public static <T> T merge(Object src, T target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
        return target;
    }

    public static <T> T copyClone(Class<T> clazz, Object src) {
        T instance = BeanUtils.instantiateClass(clazz);
        BeanUtils.copyProperties(src, instance);
        return instance;
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    public static UUID getAuthUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()) {
            return (UUID)authentication.getPrincipal();
        }
        return null;
    }

    public static boolean checkAuthzForRole(UserRole role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(role.name()));
    }
}

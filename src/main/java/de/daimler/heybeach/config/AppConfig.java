package de.daimler.heybeach.config;

import de.daimler.heybeach.persistence.PictureEntityDAO;
import de.daimler.heybeach.persistence.UserEntityDAO;
import de.daimler.heybeach.persistence.jdbc.PicturesEntityDAOImpl;
import de.daimler.heybeach.persistence.jdbc.UsersEntityDAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Autowired
    DataSource dataSource;

    @Bean
    public UserEntityDAO userEntityService() {
        UsersEntityDAOImpl entityService = new UsersEntityDAOImpl();
        entityService.setDataSource(dataSource);
        return entityService;
    }

    @Bean
    public PictureEntityDAO pictureEntityService() {
        PicturesEntityDAOImpl entityService = new PicturesEntityDAOImpl();
        entityService.setDataSource(dataSource);
        return entityService;
    }
}

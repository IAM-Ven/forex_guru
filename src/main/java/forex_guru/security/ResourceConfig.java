package forex_guru.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import javax.sql.DataSource;

/**
 * Configuration for Resource Server Filter
 */
@Configuration
@EnableResourceServer
public class ResourceConfig extends ResourceServerConfigurerAdapter {

    /**
     * Configure endpoints that require access token authorization
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            // disable cross site risk forgery (vulnerability for now)
            .csrf().disable()

                // determine security for endpoints
                .authorizeRequests()
                .antMatchers("/prices").authenticated();
    }
}

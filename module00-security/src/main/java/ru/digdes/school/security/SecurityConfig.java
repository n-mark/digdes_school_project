package ru.digdes.school.security;

import java.util.logging.Logger;

public class SecurityConfig {
    public SecurityConfig() {
        Logger logger = Logger.getLogger(SecurityConfig.class.getName());
        logger.info("SecurityConfig constructor has been accessed");
    }
}

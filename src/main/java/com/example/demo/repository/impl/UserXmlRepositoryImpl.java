package com.example.demo.repository.impl;

import com.example.demo.model.Authority;
import com.example.demo.model.UserXml;
import com.example.demo.model.UserXmlWrapper;
import com.example.demo.repository.UserXmlRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Repository
public class UserXmlRepositoryImpl implements UserXmlRepository {
    private final Path xmlPath = Path.of("users.xml");
    private final XmlMapper xmlMapper = new XmlMapper();

    private final static TypeReference<UserXmlWrapper> TYPE_REFERENCE = new TypeReference<>() {
    };

    @PostConstruct
    public void init() throws IOException {
        Files.deleteIfExists(xmlPath);
        Files.createFile(xmlPath);
        save(new UserXml(
                "admin",
                "$2a$10$uezbzRQyCde.P6A8TU2ogef4zHEGL0kDpxpqBLFVJrXPVPQdep1Z2",
                Arrays.stream(Authority.values()).toList())
        );
    }

    @Override
    public UserXml findByLogin(String login) {
        final List<UserXml> users = getUserXmlWrapper().getUsers();
        return users.stream()
                .filter(user -> login.equals(user.getLogin()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Couldn't find user with login: %s".formatted(login)));
    }

    @Override
    public void save(UserXml userXml) {
        final UserXmlWrapper userXmlWrapper = getUserXmlWrapper();
        userXmlWrapper.getUsers().add(userXml);
        try {
            xmlMapper.writeValue(xmlPath.toFile(), userXmlWrapper);
        } catch (IOException e) {
            throw new RuntimeException("Не получилось сохранить пользователя: ", e);
        }
    }

    private UserXmlWrapper getUserXmlWrapper() {
        UserXmlWrapper userXmlWrapper;
        try {
            userXmlWrapper = xmlMapper.readValue(xmlPath.toFile(), TYPE_REFERENCE);
        } catch (IOException e) {
            userXmlWrapper = new UserXmlWrapper();
        }
        return userXmlWrapper;
    }
}

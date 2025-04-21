package com.example.demo.repository;

import com.example.demo.model.UserXml;

import java.util.Optional;

public interface UserXmlRepository {
    UserXml findByLogin(String login);

    void save(UserXml userXml);
}

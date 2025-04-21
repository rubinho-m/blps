package com.example.demo.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserXml {
    private String login;

    private String hashedPassword;

    @JacksonXmlElementWrapper(localName = "authorities")
    @JacksonXmlProperty(localName = "authority")
    private List<Authority> authorities;
}

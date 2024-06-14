package com.example.cyberlarpapi.game.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home
{
    @RequestMapping("/")
    public String hello()
    {
        return "<h1> Deployed </h1>";
    }
}

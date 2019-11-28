package com.example.kpi.controller;

import java.util.HashMap;
import java.util.Map;

import com.example.kpi.service.SecurityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javassist.bytecode.ByteArray;

/**
 * SecurityController
 */

 @RestController
 @RequestMapping(value = "/api/v1")
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/security/encodePassword",
                    method = RequestMethod.POST,
                    produces = {MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                MediaType.APPLICATION_XML_VALUE})

    @ResponseBody
    public Map<String, String> encode(@RequestBody Map<String, String> data){
        Map<String, String> result = new HashMap<>();
        String password = data.get("password");
        result.put("password", securityService.encrytePassword(password));
        return result;
    }

    // @RequestMapping(value = "/security/decodePassword",
    //                 method = RequestMethod.POST,
    //                 produces = {MediaType.APPLICATION_PROBLEM_JSON_VALUE,
    //                             MediaType.APPLICATION_XML_VALUE})

    // @ResponseBody
    // public String decode(@RequestBody String password){
    //     return securityService.decode(password);
    // }

    @RequestMapping(value = "/security/generateToken",
                    method = RequestMethod.POST,
                    produces = {MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                MediaType.APPLICATION_XML_VALUE})

    @ResponseBody
    public Map<String, String> generateToken(@RequestBody Map<String, String> data){
        Map<String, String> result = new HashMap<>();
        String username = data.get("username");
        result.put("token", securityService.generateTokenLogin(username));
        return result;
    }

    @RequestMapping(value = "/security/getUsername",
                    method = RequestMethod.POST,
                    produces = {MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                MediaType.APPLICATION_XML_VALUE})

    @ResponseBody
    public Map<String, String> getUsername(@RequestBody Map<String, String> data){
        Map<String, String> result = new HashMap<>();
        String token = data.get("token");
        result.put("username", securityService.getUsernameFromToken(token));
        return result;
        
    }

    @RequestMapping(value = "/security/getSecretKey", 
                    method = RequestMethod.GET,
                    produces = {MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                MediaType.APPLICATION_XML_VALUE}
    )

    @ResponseBody
    public Map<String, String> getSecretKey(){
        Map<String, String> result = new HashMap<>();
        result.put("secretKey", new String(securityService.generateShareSecret()));
        return result;
    }

    @RequestMapping(value = "/security/validateToken",
                    method = RequestMethod.POST,
                    produces = {MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                MediaType.APPLICATION_XML_VALUE})

    @ResponseBody
    public boolean validatetoken(@RequestBody Map<String, String> data){
       
        String token = data.get("token");
       return securityService.validateTokenLogin(token);
        
    }
    
}
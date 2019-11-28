/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.kpi.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author vuduchiep
 */

@Service
public class SecurityService {
    
    @Autowired
    private Environment env; // CUng cap phuong thuc de doc thong tin trong file application.properties
    
    public static final String USERNAME = "username";
    
    public String generateTokenLogin(String username){
        
        String token = null;
//        System.out.println("hello");
        try {
            
            
            // Create HMAC singer
            JWSSigner singer = new MACSigner(generateShareSecret());
            
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
            builder.claim(USERNAME, username);
            builder.expirationTime(generateExpiretionDate());
            
            JWTClaimsSet claimsSet = builder.build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            
            // Apply HMAC protection
            signedJWT.sign(singer);
            
            token = signedJWT.serialize();
            
        } catch (JOSEException ex) {
            ex.printStackTrace();
            Logger.getLogger(SecurityService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return token;
    }
    
    private JWTClaimsSet getClaimsFromToken(String token){
        
        JWTClaimsSet claims = null;
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            
            JWSVerifier verifier = new MACVerifier(generateShareSecret());
            
            if(signedJWT.verify(verifier)){
                claims = signedJWT.getJWTClaimsSet();
            }
            
            
        } catch (ParseException ex) {
            Logger.getLogger(SecurityService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JOSEException ex) {
            Logger.getLogger(SecurityService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return claims;
    }
    
    private Date generateExpiretionDate(){
        
        int expire_time = Integer.parseInt(env.getProperty("expire_time"));
        return new Date(System.currentTimeMillis() + expire_time);
    }
    
    private Date getExpiretionDateFromToken(String token){
        Date expiration = null;
        JWTClaimsSet claims = getClaimsFromToken(token);
        expiration = claims.getExpirationTime();
        return expiration;
    }
    
    public String getUsernameFromToken(String token){
        String username = null;
            
        try {
            
            JWTClaimsSet claims = getClaimsFromToken(token);
            username = claims.getStringClaim(USERNAME);
        } catch (ParseException ex) {
            Logger.getLogger(SecurityService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return username;
    }
    
    public byte[] generateShareSecret(){
        byte[] shareSecret = new byte[32];
        String secret_key = env.getProperty("secret_key");
        shareSecret = secret_key.getBytes();
        return shareSecret;
        
    }
    
    private boolean isTokenExpired(String token){
        Date expiration = getExpiretionDateFromToken(token);
        return expiration.before(new Date());
    }
    
    public boolean validateTokenLogin(String token){
        if(token == null || token.trim().length() == 0){
            return false;
        }
        
        String username = getUsernameFromToken(token);
        if(username == null || username.isEmpty()){
            return false;
        }
        
        if(isTokenExpired(token)){
            return false;
        }
        return true;
    }

    public String encode(String pass) {
        // Encode data on your side using BASE64
        byte[] bytesEncoded = Base64.encodeBase64(pass.getBytes());
        String passEncoded = bytesEncoded.toString();
        System.out.println("encoded value is " + new String(bytesEncoded));
        return passEncoded;
    }

    public String decode(String bytesEncoded) {
        // Decode data on other side, by processing encoded data
        byte[] valueDecoded = Base64.decodeBase64(bytesEncoded);
        String pass = new String(valueDecoded);
        System.out.println("Decoded value is " + pass);
        return pass;

    }

    public String encrytePassword(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}
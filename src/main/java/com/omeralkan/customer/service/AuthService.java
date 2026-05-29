package com.omeralkan.customer.service;

import com.omeralkan.customer.dto.AuthRequest;
import com.omeralkan.customer.dto.AuthResponse;
import com.omeralkan.customer.dto.CustomerResponse;
import com.omeralkan.customer.dto.CustomerSaveRequest;
import com.omeralkan.customer.entity.Customer;
import com.omeralkan.customer.repository.CustomerRepository;
import com.omeralkan.customer.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(CustomerSaveRequest request) {
        CustomerResponse savedCustomer = customerService.saveCustomer(request);

        var userDetails = User.builder()
                .username(savedCustomer.getEmail())
                .password("")
                .roles("USER")
                .build();

        String jwtToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .customerId(savedCustomer.getId())  // YENİ ALAN
                .email(savedCustomer.getEmail())
                .ad(savedCustomer.getAd())
                .soyad(savedCustomer.getSoyad())
                .build();
    }
    public AuthResponse login(AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Customer customer = customerRepository.findByEmailAndDeleteFlagFalse(request.getEmail())
                .orElseThrow();

        var userDetails = User.builder()
                .username(customer.getEmail())
                .password(customer.getPassword())
                .roles("USER")
                .build();
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .customerId(customer.getId())  // YENİ ALAN
                .email(customer.getEmail())
                .ad(customer.getAd())
                .soyad(customer.getSoyad())
                .build();
    }
}
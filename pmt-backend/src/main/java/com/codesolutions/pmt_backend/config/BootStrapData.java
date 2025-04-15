package com.codesolutions.pmt_backend.config;

import com.codesolutions.pmt_backend.entity.Role;
import com.codesolutions.pmt_backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootStrapData implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public BootStrapData(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(new Role("ADMIN"));
        }
        if (roleRepository.findByName("MEMBER").isEmpty()) {
            roleRepository.save(new Role("MEMBER"));
        }
        if (roleRepository.findByName("OBSERVER").isEmpty()) {
            roleRepository.save(new Role("OBSERVER"));
        }
    }
}

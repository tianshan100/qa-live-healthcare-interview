package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<Map<String, Object>> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getActiveDoctors() {
        return doctorRepository.findByIsActiveTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getDoctorByUsername(String username) {
        return doctorRepository.findByUsername(username)
                .map(this::toDto)
                .orElse(null);
    }

    public Map<String, Object> login(String username, String password) {
        return doctorRepository.findByUsernameAndPassword(username, password)
                .map(this::toDto)
                .orElse(null);
    }

    private Map<String, Object> toDto(Doctor doctor) {
        return Map.of(
                "id", String.valueOf(doctor.getId()),
                "username", doctor.getUsername(),
                "name", doctor.getName(),
                "title", doctor.getTitle() != null ? doctor.getTitle() : "",
                "department", doctor.getDepartment() != null ? doctor.getDepartment() : "",
                "avatar", doctor.getAvatar() != null ? doctor.getAvatar() : "",
                "experience", doctor.getExperience() != null ? doctor.getExperience() : "",
                "specialties", doctor.getSpecialties() != null
                        ? Arrays.asList(doctor.getSpecialties().split(","))
                        : List.of(),
                "isActive", doctor.getIsActive()
        );
    }
}

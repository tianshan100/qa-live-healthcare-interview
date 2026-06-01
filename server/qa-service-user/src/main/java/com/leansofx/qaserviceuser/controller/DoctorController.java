package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public List<Map<String, Object>> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/active")
    public List<Map<String, Object>> getActiveDoctors() {
        return doctorService.getActiveDoctors();
    }

    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getDoctorByUsername(@PathVariable String username) {
        Map<String, Object> doctor = doctorService.getDoctorByUsername(username);
        if (doctor != null) {
            return ResponseEntity.ok(doctor);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        Map<String, Object> doctor = doctorService.login(
                credentials.get("username"),
                credentials.get("password")
        );
        if (doctor != null) {
            return ResponseEntity.ok(doctor);
        }
        return ResponseEntity.status(401).build();
    }
}

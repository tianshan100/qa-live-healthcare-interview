package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.entity.Patient;
import com.leansofx.qaserviceuser.exception.BusinessException;
import com.leansofx.qaserviceuser.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

@Service
public class PatientService {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,20}$");
    private static final int PASSWORD_MIN_LENGTH = 6;
    private static final int PASSWORD_MAX_LENGTH = 20;

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Map<String, Object> loginOrRegister(String username, String password) {
        validateInput(username, password);

        return patientRepository.findByUsername(username)
                .map(existing -> {
                    if (!existing.getPassword().equals(password)) {
                        throw new BusinessException(401, "用户名或密码错误");
                    }
                    return Map.<String, Object>of(
                            "code", 200,
                            "message", "登录成功",
                            "data", toDto(existing)
                    );
                })
                .orElseGet(() -> {
                    Patient newPatient = new Patient();
                    newPatient.setUsername(username);
                    newPatient.setPassword(password);
                    newPatient.setName(username);
                    newPatient = patientRepository.save(newPatient);
                    return Map.of(
                            "code", 201,
                            "message", "注册成功，已自动登录",
                            "data", toDto(newPatient)
                    );
                });
    }

    private void validateInput(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new BusinessException(400, "用户名格式不正确，需3-20位字母、数字或下划线");
        }
        if (password == null || password.isBlank()) {
            throw new BusinessException(400, "密码不能为空");
        }
        if (password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            throw new BusinessException(400, "密码长度需6-20位");
        }
    }

    private Map<String, Object> toDto(Patient patient) {
        return Map.of(
                "id", String.valueOf(patient.getId()),
                "username", patient.getUsername(),
                "name", patient.getName() != null ? patient.getName() : "",
                "phone", patient.getPhone() != null ? patient.getPhone() : "",
                "gender", patient.getGender() != null ? patient.getGender() : ""
        );
    }
}

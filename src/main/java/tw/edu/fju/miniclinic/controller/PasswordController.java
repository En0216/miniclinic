package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;

@Controller
public class PasswordController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DoctorRepository doctorRepo;

    // 1. 顯示修改密碼頁面 (GET /password)
    @GetMapping("/password")
    public String showPasswordPage(HttpSession session) {
        // 檢查登入狀態
        if (session.getAttribute("loggedInDoctorId") == null) {
            return "redirect:/login";
        }
        return "password-change";
    }

    // 2. 處理密碼修改表單 (POST /password)
    @PostMapping("/password")
    public String handlePasswordChange(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model) {

        String doctorId = (String) session.getAttribute("loggedInDoctorId");
        if (doctorId == null) return "redirect:/login";

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor == null) {
            model.addAttribute("error", "系統錯誤：找不到醫師帳號");
            return "password-change";
        }

        // 驗證舊密碼
        if (!passwordEncoder.matches(oldPassword, doctor.getPasswordHash())) {
            model.addAttribute("error", "舊密碼輸入錯誤");
            return "password-change";
        }

        // 驗證新密碼一致性
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "新密碼與確認密碼不符");
            return "password-change";
        }

        // 儲存新密碼
        doctor.setPasswordHash(passwordEncoder.encode(newPassword));
        doctorRepo.save(doctor);

        model.addAttribute("message", "密碼已成功修改！");
        return "password-change";
    }
}
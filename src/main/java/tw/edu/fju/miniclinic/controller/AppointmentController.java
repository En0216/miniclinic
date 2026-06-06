package tw.edu.fju.miniclinic.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentForm;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;

@Controller
public class AppointmentController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    // GET：顯示表單 (同時支援單數與複數路徑，避免舊連結或快取導致 404)
    @GetMapping({"/appointments/new", "/appointment/new"})
    public String newAppointmentForm(Model model) {
        model.addAttribute("form", new AppointmentForm());
        model.addAttribute("doctors", doctorRepo.findAll());
        return "appointment-new";
    }

    // POST：提交掛號 (同時支援單數與複數路徑)
    @PostMapping({"/appointments/new", "/appointment/new"})
    public String submitAppointment(
            @Valid @ModelAttribute("form") AppointmentForm form,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) { // 表單驗證失敗，回到表單頁並顯示錯誤訊息
            model.addAttribute("form", form);
            model.addAttribute("doctors", doctorRepo.findAll());
            return "appointment-new";
        }

        // 1. 取得並清理輸入資料
        String chartNo = (form.getChartNo() == null) ? "" : form.getChartNo().trim();
        String doctorId = (form.getDoctorId() == null) ? "" : form.getDoctorId().trim();

        // 2. 查詢資料庫
        Patient patient = patientRepo.findById(chartNo).orElse(null);
        Doctor  doctor  = doctorRepo.findById(doctorId).orElse(null);

        // 3. 驗證：若找不到病人或醫師，回傳錯誤訊息
        if (patient == null || doctor == null) {
            model.addAttribute("error", "查無此病歷號或醫師，請確認後重試");
            model.addAttribute("form", form);
            model.addAttribute("doctors", doctorRepo.findAll());
            return "appointment-new";   // ← 回到表單頁，不是跳轉
        }

        // 4. 驗證：日期必填
        if (form.getApptDate() == null || form.getApptDate().isBlank()) {
            model.addAttribute("error", "請選擇預約日期");
            model.addAttribute("form", form);
            model.addAttribute("doctors", doctorRepo.findAll());
            return "appointment-new";
        }

        // 5. 建立預約記錄
        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        appt.setApptDate(LocalDate.parse(form.getApptDate()));
        appt.setTimeSlot(form.getTimeSlot());
        appt.setStatus("BOOKED");

        // 6. 存檔
        Appointment saved = appointmentRepo.save(appt);

        // 7. 顯示結果頁
        model.addAttribute("appointment", saved);
        return "appointment-result";
    }

    @PostMapping("/api/appointments")
    public ResponseEntity<Appointment> createAppointment(
            @RequestBody Map<String, String> request) {

        // 從 request 取出資料
        String chartNo = request.get("chartNo");
        String doctorId = request.get("doctorId");
        LocalDate apptDate = LocalDate.parse(request.get("apptDate"));
        String timeSlot = request.get("timeSlot");

        // 查詢關聯的 Patient 與 Doctor
        Patient patient = patientRepo.findById(chartNo).orElse(null);
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);

        if (patient == null || doctor == null) {
            return ResponseEntity.badRequest().build();
        }

        // 建立 Appointment 物件
        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        appt.setApptDate(apptDate);
        appt.setTimeSlot(timeSlot);
        appt.setStatus("BOOKED");

        Appointment saved = appointmentRepo.save(appt);
        return ResponseEntity.status(201).body(saved);
    }
}

package tw.edu.fju.miniclinic.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class About {
    
    @GetMapping("/api/about")
    public Map<String, String> about() {
        return Map.of(
            "student_id", " 414570291 ",
            "student_name", " 江佳恩 ",
            "project", "MiniClinic",
            "version", "0.1.0",
            "chapter", "Ch09-A"
        );
    }
}
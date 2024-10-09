package com.example.employee_management.api.controller;

import com.example.employee_management.api.dto.EmployeeDto;
import com.example.employee_management.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/employees")
@RequiredArgsConstructor
@Validated
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/save")
    public String saveEmployee(@RequestBody @Valid EmployeeDto employeeDto){
        return employeeService.saveEmployee(employeeDto);
    }

    @GetMapping
    public List<EmployeeDto> getAllEmployees(){
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public EmployeeDto getEmployeeById(@PathVariable("id") @NotBlank(message = "Employee Id can't be blank") String id){
        return employeeService.getEmployeeById(id);
    }

    @PutMapping
    public String updateEmployee(@RequestBody EmployeeDto employeeDto){
        return employeeService.updateEmployee(employeeDto);
    }
    @DeleteMapping("{id}")
    public String deleteEmployee(@PathVariable("id") @NotBlank(message = "Employee Id can't be blank") String id){
        return employeeService.deleteEmployee(id);
    }

    @PostMapping("/upload-image")
    public String uploadImage(@RequestParam String id, @RequestParam MultipartFile profile) throws IOException {
        return employeeService.saveProfileImage(id,profile);
    }

    @PostMapping("/download-report")
    public ResponseEntity<byte[]> downloadEmployeesReport() throws JRException, IOException {
        String filename = String.format("employee_report.pdf");

        byte[] report = employeeService.downloadEmployeesReport();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(filename)
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(report);
    }

    @PostMapping("/downloadProfileImage")
    public ResponseEntity<Resource> downloadProfileImage(@RequestParam String id) throws IOException {
       return employeeService.downloadProfileImage(id);
    }

}

package com.example.employee_management.service;

import com.example.employee_management.api.dto.EmployeeDto;
import com.example.employee_management.constants.ApplicationConstants;
import com.example.employee_management.converter.EmployeeConverter;
import com.example.employee_management.entity.Employee;
import com.example.employee_management.entity.UserAuthentication;
import com.example.employee_management.entity.Users;
import com.example.employee_management.enums.UserType;
import com.example.employee_management.exception.ServiceException;
import com.example.employee_management.repository.EmployeeRepository;
import com.example.employee_management.repository.UserAuthenticationRepository;
import com.example.employee_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final UserRepository userRepository;

    private final EmployeeConverter employeeConverter;

    private final EmployeeRepository employeeRepository;
    private final UserAuthenticationRepository userAuthenticationRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${file.upload.dir}")
    private String uploadDir;

    public String saveEmployee(EmployeeDto employeeDto) {
        Employee employee = employeeConverter.convert(employeeDto);
        Users savedUser = userRepository.save(employee);

        UserAuthentication userAuthentication = new UserAuthentication();
        userAuthentication.setEmail(employeeDto.getEmail());
        userAuthentication.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        userAuthentication.setUserType(UserType.fromMappedValue(employeeDto.getUserType()));
        userAuthentication.setUserId(savedUser.getId());

        userAuthenticationRepository.save(userAuthentication);
        return "Employee saved";
    }


    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream()
                .map(employeeConverter::convertToDto)
                .toList();
    }

    //    @Scheduled(cron = "0 * * * * ?")
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateCurrentAgeInDays() {
        List<Employee> employees = employeeRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Employee employee : employees) {
            LocalDate dateOfBirth = employee.getDateOfBirth();
            long currentAgeInDays = ChronoUnit.DAYS.between(dateOfBirth, today);
            employee.setCurrentAgeInDays(currentAgeInDays);
        }

        employeeRepository.saveAll(employees);
    }

    public EmployeeDto getEmployeeById(String id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ServiceException(ApplicationConstants.EMPLOYEE_NOT_FOUND, ApplicationConstants.BAD_REQUEST, HttpStatus.BAD_REQUEST));
        return employeeConverter.convertToDto(employee);
    }

    public String updateEmployee(EmployeeDto employeeDto) {
        Employee existingEmployee = employeeRepository.findById(employeeDto.getId()).orElseThrow(() -> new ServiceException(ApplicationConstants.EMPLOYEE_NOT_FOUND, ApplicationConstants.BAD_REQUEST, HttpStatus.BAD_REQUEST));
        if (existingEmployee != null) {
            employeeConverter.convertForUpdate(existingEmployee, employeeDto);
            userRepository.save(existingEmployee);
        }

        return "Employee updated";
    }

    public String deleteEmployee(String id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ServiceException(ApplicationConstants.EMPLOYEE_NOT_FOUND,ApplicationConstants.BAD_REQUEST, HttpStatus.BAD_REQUEST));
        log.info("Employee----{}",employee);

        if (employee != null && employee.getRoles().getRoleName().equalsIgnoreCase("Admin")) {
            Optional<UserAuthentication> user = userAuthenticationRepository.findByEmail(employee.getEmail());
            userAuthenticationRepository.deleteById(user.get().getId());
            userRepository.deleteById(id);
        }
        else
            throw new ServiceException("User is not have permission to delete",ApplicationConstants.BAD_REQUEST,HttpStatus.BAD_REQUEST);

        return "Employee deleted";
    }

    public String saveProfileImage(String id, MultipartFile file) throws IOException {
        File directory = new File(uploadDir);
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ServiceException(ApplicationConstants.EMPLOYEE_NOT_FOUND,ApplicationConstants.BAD_REQUEST, HttpStatus.BAD_REQUEST));

        if (employee != null && file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();

            if (originalFilename != null) {

                String newFilename = id + getFileExtension(originalFilename);
                File destinationFile = new File(directory, newFilename);
                file.transferTo(destinationFile);
            }
            return "File uploaded successfully";
        }
        else
            throw new ServiceException("Please upload an image", ApplicationConstants.BAD_REQUEST,HttpStatus.BAD_REQUEST);


    }

    private String getFileExtension(String fileName) {
        String fileExtension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = fileName.substring(dotIndex);
        }
        return fileExtension;
    }

    public byte[] downloadEmployeesReport() throws JRException, IOException {
        JasperPrint jasperPrint = null;
        List<Employee> employees = employeeRepository.findAll();
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(employees);

        String reportFile = "templates/employee.jrxml";
        Resource resource = new ClassPathResource(reportFile);
        InputStream reportStream = resource.getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public ResponseEntity<Resource> downloadProfileImage(String id) throws IOException {
        Path directory = Paths.get(uploadDir);

        String extension = getFileExtension(id);

        String fileName = id + extension;

        Path filePath = directory.resolve(fileName);

        if (!Files.exists(filePath)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, id + ".*")) {
                Iterator<Path> iterator = stream.iterator();
                if (iterator.hasNext()) {
                    filePath = iterator.next();
                } else {
                    throw new ServiceException("File not found with id: " + id, ApplicationConstants.BAD_REQUEST, HttpStatus.BAD_REQUEST);
                }
            }
        }

        Resource resource = new UrlResource(filePath.toUri());

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName() + "\"")
                .body(resource);
    }

}

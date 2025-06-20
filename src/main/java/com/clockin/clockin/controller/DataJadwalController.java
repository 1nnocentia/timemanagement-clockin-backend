package com.clockin.clockin.controller;

import com.clockin.clockin.dto.DataJadwalDTO;
import com.clockin.clockin.dto.GroupedCountDTO;
import com.clockin.clockin.service.DataJadwalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data-jadwal")
public class DataJadwalController {

    @Autowired
    private DataJadwalService dataJadwalService;

    @PostMapping
    public ResponseEntity<DataJadwalDTO> create(@RequestBody DataJadwalDTO dto) {
    DataJadwalDTO createdDto = dataJadwalService.create(dto);
    return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
}

    
    @GetMapping("/{id}")
    public DataJadwalDTO getById(@PathVariable Long id) {
        return dataJadwalService.getById(id);
    }

    @GetMapping
    public List<DataJadwalDTO> getAll() {
        return dataJadwalService.getAll();
    }

    @PutMapping("/{id}")
    public DataJadwalDTO update(@PathVariable Long id, @RequestBody DataJadwalDTO dto) {
        return dataJadwalService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        dataJadwalService.delete(id);
    }

    // Mendapatkan jumlah tugas/event berdasarkan prioritas
    // Method: GET
    // http://localhost:8080/api/data-jadwal/reports/by-prioritas
    @GetMapping("/reports/by-prioritas")
    public ResponseEntity<List<GroupedCountDTO>> getCountsByPrioritas() {
        List<GroupedCountDTO> reports = dataJadwalService.getCountsByPrioritas();
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    // Mendapatkan jumlah tugas/event berdasarkan kategori
    // Method: GET
    // http://localhost:8080/api/data-jadwal/reports/by-kategori
    @GetMapping("/reports/by-kategori")
    public ResponseEntity<List<GroupedCountDTO>> getCountsByKategori() {
        List<GroupedCountDTO> reports = dataJadwalService.getCountsByKategori();
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    // Mendapatkan jumlah event berdasarkan periode waktu (bulan/minggu/tahun)
    // Method: GET
    // http://localhost:8080/api/data-jadwal/reports/events-by-period?periodType=MONTH
    @GetMapping("/reports/events-by-period")
    public ResponseEntity<List<GroupedCountDTO>> getEventCountsByPeriod(@RequestParam String periodType) {
        try {
            List<GroupedCountDTO> reports = dataJadwalService.getEventCountsByPeriod(periodType);
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Mendapatkan jumlah task berdasarkan periode waktu (bulan/minggu/tahun)
    // Method: GET
    // http://localhost:8080/api/data-jadwal/reports/tasks-by-period?periodType=MONTH
    @GetMapping("/reports/tasks-by-period")
    public ResponseEntity<List<GroupedCountDTO>> getTaskCountsByPeriod(@RequestParam String periodType) {
        try {
            List<GroupedCountDTO> reports = dataJadwalService.getTaskCountsByPeriod(periodType);
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

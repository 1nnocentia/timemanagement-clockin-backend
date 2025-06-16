package com.clockin.clockin.service.impl;

import com.clockin.clockin.model.Prioritas;
import com.clockin.clockin.model.User;
import com.clockin.clockin.dto.PrioritasDTO;
import com.clockin.clockin.repository.PrioritasRepository;
import com.clockin.clockin.repository.UserRepository;
import com.clockin.clockin.service.PrioritasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrioritasServiceImpl implements PrioritasService {

    private static final Logger logger = LoggerFactory.getLogger(PrioritasServiceImpl.class);

    @Autowired
    private PrioritasRepository prioritasRepository;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String usernameFromPrincipal;

        if (principal instanceof UserDetails) {
            usernameFromPrincipal = ((UserDetails) principal).getUsername();
            logger.info("Authenticated principal is UserDetails. Extracted username: {}", usernameFromPrincipal);
        } else {
            usernameFromPrincipal = principal.toString();
            logger.warn("Authenticated principal is NOT UserDetails (it's {}). Trying toString(): {}", principal.getClass().getName(), usernameFromPrincipal);
        }

        if (usernameFromPrincipal == null || usernameFromPrincipal.isEmpty()) {
            logger.error("Could not extract non-empty username from authenticated principal.");
            throw new RuntimeException("Tidak dapat mengekstrak nama pengguna dari sesi autentikasi.");
        }

        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(usernameFromPrincipal);

        if (userOptional.isEmpty()) {
            logger.error("User with username '{}' (from authenticated session) NOT found in database. This user might be missing or there's a case-sensitivity mismatch.", usernameFromPrincipal);
            throw new RuntimeException("Pengguna terautentikasi tidak ditemukan.");
        }

        return userOptional.get();
    }

    private PrioritasDTO convertToDTO(Prioritas prioritas) {
        if (prioritas == null) {
            return null;
        }
        PrioritasDTO dto = new PrioritasDTO();
        dto.setId(prioritas.getId());
        dto.setNamaPrioritas(prioritas.getNamaPrioritas());
        return dto;
    }

    private Prioritas convertToEntity(PrioritasDTO dto) {
        if (dto == null) {
            return null;
        }
        Prioritas prioritas = new Prioritas();
        prioritas.setId(dto.getId());
        prioritas.setNamaPrioritas(dto.getNamaPrioritas());
        return prioritas;
    }

    @Override
    @Transactional
    public PrioritasDTO createPrioritas(PrioritasDTO dto) {
        User authenticatedUser = getAuthenticatedUser();

        if (prioritasRepository.findByNamaPrioritasAndUser(dto.getNamaPrioritas(), authenticatedUser).isPresent()) {
            throw new RuntimeException("Prioritas dengan nama '" + dto.getNamaPrioritas() + "' sudah ada untuk pengguna ini.");
        }

        Prioritas prioritas = convertToEntity(dto);
        prioritas.setUser(authenticatedUser);
        Prioritas savedPrioritas = prioritasRepository.save(prioritas);
        return convertToDTO(savedPrioritas);
    }

    @Override
    public PrioritasDTO getPrioritasById(Long id) {
        User authenticatedUser = getAuthenticatedUser();
        Prioritas prioritas = prioritasRepository.findByIdAndUser(id, authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Prioritas tidak ditemukan atau Anda tidak memiliki akses."));
        return convertToDTO(prioritas);
    }

    @Override
    public List<PrioritasDTO> getAllPrioritas() {
        User authenticatedUser = getAuthenticatedUser();
        List<Prioritas> prioritasList = prioritasRepository.findByUser(authenticatedUser);
        return prioritasList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PrioritasDTO updatePrioritas(Long id, PrioritasDTO dto) {
        User authenticatedUser = getAuthenticatedUser();
        Prioritas existingPrioritas = prioritasRepository.findByIdAndUser(id, authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Prioritas tidak ditemukan atau Anda tidak memiliki akses."));

        if (!existingPrioritas.getNamaPrioritas().equalsIgnoreCase(dto.getNamaPrioritas()) &&
            prioritasRepository.findByNamaPrioritasAndUser(dto.getNamaPrioritas(), authenticatedUser).isPresent()) {
            throw new RuntimeException("Prioritas dengan nama '" + dto.getNamaPrioritas() + "' sudah ada untuk pengguna ini.");
        }

        existingPrioritas.setNamaPrioritas(dto.getNamaPrioritas());
        Prioritas updatedPrioritas = prioritasRepository.save(existingPrioritas);
        return convertToDTO(updatedPrioritas);
    }

    @Override
    @Transactional
    public void deletePrioritas(Long id) {
        User authenticatedUser = getAuthenticatedUser();
        if (!prioritasRepository.existsByIdAndUser(id, authenticatedUser)) {
            throw new RuntimeException("Prioritas tidak ditemukan atau Anda tidak memiliki akses untuk menghapus.");
        }
        prioritasRepository.deleteById(id);
    }
}
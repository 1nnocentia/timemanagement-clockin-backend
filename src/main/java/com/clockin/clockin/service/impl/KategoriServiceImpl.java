package com.clockin.clockin.service.impl;

import com.clockin.clockin.model.Kategori;
import com.clockin.clockin.model.User;
import com.clockin.clockin.dto.KategoriDTO;
import com.clockin.clockin.repository.KategoriRepository;
import com.clockin.clockin.repository.UserRepository;
import com.clockin.clockin.service.KategoriService;
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
public class KategoriServiceImpl implements KategoriService {

    private static final Logger logger = LoggerFactory.getLogger(KategoriServiceImpl.class);

    @Autowired
    private KategoriRepository kategoriRepository;

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

    private KategoriDTO convertToDTO(Kategori kategori) {
        if (kategori == null) {
            return null;
        }
        KategoriDTO dto = new KategoriDTO();
        dto.setId(kategori.getId());
        dto.setNamaKategori(kategori.getNamaKategori());
        dto.setColor(kategori.getColor());
        return dto;
    }

    private Kategori convertToEntity(KategoriDTO dto) {
        if (dto == null) {
            return null;
        }
        Kategori kategori = new Kategori();
        kategori.setId(dto.getId());
        kategori.setNamaKategori(dto.getNamaKategori());
        kategori.setColor(dto.getColor());
        return kategori;
    }

    @Override
    @Transactional
    public KategoriDTO createKategori(KategoriDTO dto) {
        User authenticatedUser = getAuthenticatedUser();

        if (kategoriRepository.findByNamaKategoriAndUser(dto.getNamaKategori(), authenticatedUser).isPresent()) {
            throw new RuntimeException("Kategori dengan nama '" + dto.getNamaKategori() + "' sudah ada untuk pengguna ini.");
        }

        Kategori kategori = convertToEntity(dto);
        kategori.setUser(authenticatedUser);
        Kategori savedKategori = kategoriRepository.save(kategori);
        return convertToDTO(savedKategori);
    }

    @Override
    public KategoriDTO getKategoriById(Long id) {
        User authenticatedUser = getAuthenticatedUser();
        Kategori kategori = kategoriRepository.findByIdAndUser(id, authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan atau Anda tidak memiliki akses."));
        return convertToDTO(kategori);
    }

    @Override
    public List<KategoriDTO> getAllKategori() {
        User authenticatedUser = getAuthenticatedUser();
        List<Kategori> kategoriList = kategoriRepository.findByUser(authenticatedUser);
        return kategoriList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public KategoriDTO updateKategori(Long id, KategoriDTO dto) {
        User authenticatedUser = getAuthenticatedUser();
        Kategori existingKategori = kategoriRepository.findByIdAndUser(id, authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan atau Anda tidak memiliki akses."));

        if (!existingKategori.getNamaKategori().equalsIgnoreCase(dto.getNamaKategori()) &&
            kategoriRepository.findByNamaKategoriAndUser(dto.getNamaKategori(), authenticatedUser).isPresent()) {
            throw new RuntimeException("Kategori dengan nama '" + dto.getNamaKategori() + "' sudah ada untuk pengguna ini.");
        }

        existingKategori.setNamaKategori(dto.getNamaKategori());
        existingKategori.setColor(dto.getColor());
        Kategori updatedKategori = kategoriRepository.save(existingKategori);
        return convertToDTO(updatedKategori);
    }

    @Override
    @Transactional
    public void deleteKategori(Long id) {
        User authenticatedUser = getAuthenticatedUser();
        if (!kategoriRepository.existsByIdAndUser(id, authenticatedUser)) {
             throw new RuntimeException("Kategori tidak ditemukan atau Anda tidak memiliki akses untuk menghapus.");
        }
        kategoriRepository.deleteById(id);
    }
}
package com.clockin.clockin.service;

import com.clockin.clockin.model.User;
import com.clockin.clockin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList; // Untuk koleksi kosong dari Authorities

// Anotasi @Service menandakan kelas ini adalah komponen service Spring
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Memuat detail pengguna berdasarkan username atau email.
     * Metode ini dipanggil oleh Spring Security selama proses otentikasi.
     *
     * @param usernameOrEmail Nama pengguna atau email yang akan dicari.
     * @return Objek UserDetails yang berisi informasi pengguna dan otoritasnya.
     * @throws UsernameNotFoundException jika pengguna tidak ditemukan.
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Coba cari berdasarkan username
        User user = userRepository.findByUsername(usernameOrEmail);

        // Jika tidak ditemukan, coba cari berdasarkan email
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail);
        }

        // Jika tetap tidak ditemukan, lempar exception
        if (user == null) {
            throw new UsernameNotFoundException("Pengguna tidak ditemukan dengan username atau email: " + usernameOrEmail);
        }

        // Mengembalikan objek UserDetails yang dibangun oleh Spring Security
        // Di sini kita hanya menggunakan username, password, dan daftar otoritas kosong
        // Anda bisa menambahkan peran/otoritas yang sebenarnya dari entitas User jika ada.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), // Atau email, tergantung mana yang Anda ingin gunakan sebagai "username" unik
                user.getPassword(),
                new ArrayList<>() // Daftar otoritas/peran (kosong untuk saat ini)
        );
    }
}
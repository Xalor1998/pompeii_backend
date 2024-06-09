package com.cex.vulcano.service;

import com.cex.vulcano.model.User;
import com.cex.vulcano.model.Wallet;
import com.cex.vulcano.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    public Wallet createWallet(User user, String currency) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setCurrency(currency);
        wallet.setAddress(generateAddress());
        wallet.setBalance(0.0);
        return walletRepository.save(wallet);
    }

    public List<Wallet> getWalletsByUserId(Long userId) {
        return walletRepository.findByUserId(userId);
    }

    private String generateAddress() {
        SecureRandom random = new SecureRandom();
        byte[] addressBytes = new byte[20];
        random.nextBytes(addressBytes);
        StringBuilder address = new StringBuilder();
        for (byte b : addressBytes) {
            address.append(String.format("%02x", b));
        }
        return address.toString();
    }
}

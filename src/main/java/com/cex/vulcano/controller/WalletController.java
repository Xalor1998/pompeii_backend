package com.cex.vulcano.controller;

import com.cex.vulcano.model.User;
import com.cex.vulcano.model.Wallet;
import com.cex.vulcano.model.WalletRequest;
import com.cex.vulcano.service.UserService;
import com.cex.vulcano.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(@RequestBody WalletRequest request) {
        System.out.println("bent");
        User user = userService.findByUsername(request.getUsername());
        System.out.println("User: " + user == null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        System.out.println("create wallet");
        Wallet wallet = walletService.createWallet(user, request.getCurrency());
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Wallet>> getWalletsByUserId(@PathVariable Long userId) {
        List<Wallet> wallets = walletService.getWalletsByUserId(userId);
        return ResponseEntity.ok(wallets);
    }

}
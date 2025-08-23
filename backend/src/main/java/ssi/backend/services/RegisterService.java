package ssi.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;
import ssi.backend.entities.Buyer;
import ssi.backend.entities.Seller;
import ssi.backend.repositories.BuyerRepository;
import ssi.backend.repositories.SellerRepository;
import ssi.backend.exceptions.BadRequestException;

import java.util.List;

@Service
public class RegisterService {
    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private Validator validator;

    public List<Buyer> getAllBuyers() {
        return buyerRepository.findAll();
    }

    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    public List<Buyer> getAllEmailBuyers(String email) {
        return buyerRepository.findByEmail(email);
    }

    public List<Seller> getAllEmailSellers(String email) {
        return sellerRepository.findByEmail(email);
    }

    public Buyer createBuyer(Buyer buyer) {
        if (!getAllEmailBuyers(buyer.getEmail()).isEmpty() ||
            !getAllEmailSellers(buyer.getEmail()).isEmpty()) {
            throw new BadRequestException("Email already exists: " + buyer.getEmail());
        }
        return buyerRepository.save(buyer);
    }

    public Seller createSeller(Seller seller) {
        if (!getAllEmailBuyers(seller.getEmail()).isEmpty() ||
            !getAllEmailSellers(seller.getEmail()).isEmpty()) {
            throw new BadRequestException("Email already exists: " + seller.getEmail());
        }
        return sellerRepository.save(seller);
    }
}
